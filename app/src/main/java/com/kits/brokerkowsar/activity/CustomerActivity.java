package com.kits.brokerkowsar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.CustomerAdapter;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.databinding.ActivityCustomerBinding;
import com.kits.brokerkowsar.model.Customer;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.model.UserInfo;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Response;


public class CustomerActivity extends AppCompatActivity {
    private APIInterface apiInterface;
    private String factor_target = "0";
    private String edit = "0";
    private DatabaseHelper dbh;
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Customer> citys = new ArrayList<>();
    private CustomerAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private Replication replication;
    private String srch = "";
    private String id = "0";
    private Intent intent;
    private ArrayList<String> city_array = new ArrayList<>();
    private String kodemelli, citycode = "", name, family, address, phone, mobile, email, postcode, zipcode;
    private boolean activecustomer = true;
    private CallMethod callMethod;
    private ActivityCustomerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        intent();
        Config();
        try {
            init();
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }

//*****************************************************************************************


    private void Config() {
        callMethod = new CallMethod(this);
        replication = new Replication(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        setSupportActionBar(binding.CustomerActivityToolbar);
    }




    public void init() {

        switch (id) {
            case "0":
                Customer_search();
                break;
            case "1":
                Customer_new();
                break;
        }

    }

    private void intent() {
        Bundle data = getIntent().getExtras();
        edit = data.getString("edit");
        factor_target = data.getString("factor_code");
        id = data.getString("id");
        assert data != null;
    }
    private void Customer_search() {
        binding.customerSearchLine.setVisibility(View.VISIBLE);
        binding.CustomerEdtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                srch = NumberFunctions.EnglishNumber(editable.toString());
                allCustomer();
            }
        });


        binding.customerNewRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "1");
            startActivity(intent);
        });

        SwitchMaterial mySwitchActivestack = findViewById(R.id.customerActivityswitch);
        mySwitchActivestack.setOnCheckedChangeListener((compoundButton, b) -> {
            activecustomer = b;
            mySwitchActivestack.setText(b ? "فعال" : "فعال -غیرفعال");
            allCustomer();
        });
        allCustomer();

    }

    public void Customer_new() {
        binding.customerNewLine.setVisibility(View.VISIBLE);
        // replication.replicate_customer();


        citys = dbh.city();
        for (Customer citycustomer : citys) {
            city_array.add(citycustomer.getCustomerFieldValue("CityName"));
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, city_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.customerCitySpinner.setAdapter(spinner_adapter);
        binding.customerCitySpinner.setSelection(0);


        binding.customerCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citycode = citys.get(position).getCustomerFieldValue("CityCode");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.customerNewKodemelliCheck.setOnClickListener(v -> {

            if (dbh.Customer_check(binding.customerNewKodemelli.getText().toString()) > 0) {

                binding.customerNewKodemelliStatus.setText("کد ملی ثبت شده است");
                binding.customerNewKodemelliStatus.setTextColor(getResources().getColor(R.color.red_300));
            } else {

                binding.customerNewKodemelliStatus.setText("کد ملی ثبت نشده است");
                binding.customerNewKodemelliStatus.setTextColor(getResources().getColor(R.color.green_900));

            }
        });

        binding.customerNewRegisterBtn.setOnClickListener(v -> {

            if (dbh.Customer_check(binding.customerNewKodemelli.getText().toString()) > 0) {
                binding.customerNewKodemelliStatus.setText("کد ملی ثبت شده است");
                binding.customerNewKodemelliStatus.setTextColor(getResources().getColor(R.color.red_300));
            } else {

                if (Integer.parseInt(dbh.ReadConfig("BrokerCode")) > 0) {
                    kodemelli = NumberFunctions.EnglishNumber(binding.customerNewKodemelli.getText().toString());
                    name = NumberFunctions.EnglishNumber(binding.customerNewName.getText().toString());
                    family = NumberFunctions.EnglishNumber(binding.customerNewFamily.getText().toString());
                    address = NumberFunctions.EnglishNumber(binding.customerNewAddress.getText().toString());
                    phone = NumberFunctions.EnglishNumber(binding.customerNewPhone.getText().toString());
                    mobile = NumberFunctions.EnglishNumber(binding.customerNewMobile.getText().toString());
                    email = NumberFunctions.EnglishNumber(binding.customerNewEmail.getText().toString());
                    postcode = NumberFunctions.EnglishNumber(binding.customerNewPostcode.getText().toString());
                    zipcode = NumberFunctions.EnglishNumber(binding.customerNewZipcode.getText().toString());
//
//                    Call<RetrofitResponse> call = apiInterface.customer_insert( auser.getBrokerCode(), citycode, kodemelli, name, family, address, phone, mobile, email, postcode, zipcode);
//                    call.enqueue(new Callback<RetrofitResponse>() {
//                        @Override
//                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
//                            if (response.isSuccessful()) {
//                                assert response.body() != null;
//                                ArrayList<Customer> Customes = response.body().getCustomers();
//                                callMethod.showToast(Customes.get(0).getCustomerFieldValue("ErrDesc"));
//                                intent = new Intent(App.getContext(), CustomerActivity.class);
//                                intent.putExtra("edit", "0");
//                                intent.putExtra("factor_code", "0");
//                                intent.putExtra("id", "0");
//                                startActivity(intent);
//                            }
//
//
//                        }
//
//                        @Override
//                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                            callMethod.ErrorLog(t.getMessage());
//                        }
//                    });

                } else {
                    intent = new Intent(this, ConfigActivity.class);
                    callMethod.showToast("کد بازاریاب را وارد کنید");
                    startActivity(intent);

                }
            }

        });


    }


    public void allCustomer() {
        customers = dbh.AllCustomer(srch, activecustomer);
        adapter = new CustomerAdapter(customers, this, edit, factor_target);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.CustomerR1.setLayoutManager(gridLayoutManager);
        binding.CustomerR1.setAdapter(adapter);
        binding.CustomerR1.setItemAnimator(new DefaultItemAnimator());
    }


}
