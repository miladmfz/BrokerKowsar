package com.kits.brokerkowsar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.adapters.CustomerAdapter;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
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


public class CustomerActivity extends AppCompatActivity {
    APIInterface apiInterface;

    private String factor_target = "0";
    private String edit = "0";
    DatabaseHelper dbh;
    private RecyclerView rc_customer;
    ArrayList<Customer> customers = new ArrayList<>();
    ArrayList<Customer> citys = new ArrayList<>();
    CustomerAdapter adapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutCompat li_search, li_new;
    Replication replication;
    String srch = "";
    String id = "0";
    Spinner spinner;
    Intent intent;
    EditText edtsearch;
    Button Customer_new, kodemeli_check, customer_reg_btn;
    ArrayList<String> city_array = new ArrayList<>();
    TextView kodemeli_statu;
    EditText ekodemelli;
    EditText ename;
    EditText efamily;
    EditText eaddress;
    EditText ephone;
    EditText emobile;
    EditText eemail;
    EditText epostcode;
    EditText ezipcode;
    String kodemelli, citycode = "", name, family, address, phone, mobile, email, postcode, zipcode;
    boolean activecustomer = true;
    CallMethod callMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        intent();
        Config();
        try {
            init();
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }


    }

//*****************************************************************************************


    public void Config() {
        callMethod = new CallMethod(this);
        replication = new Replication(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

        rc_customer = findViewById(R.id.Customer_R1);
        edtsearch = findViewById(R.id.Customer_edtsearch);
        Customer_new = findViewById(R.id.Customer_new_btn);
        li_search = findViewById(R.id.customer_search_line);
        li_new = findViewById(R.id.customer_new_line);

        ekodemelli = findViewById(R.id.customer_new_kodemelli);
        kodemeli_check = findViewById(R.id.customer_new_kodemelli_check);
        kodemeli_statu = findViewById(R.id.customer_new_kodemelli_status);

        spinner = findViewById(R.id.customer_city_spinner);
        ename = findViewById(R.id.customer_new_name);
        efamily = findViewById(R.id.customer_new_family);
        eaddress = findViewById(R.id.customer_new_address);
        ephone = findViewById(R.id.customer_new_phone);
        emobile = findViewById(R.id.customer_new_mobile);
        eemail = findViewById(R.id.customer_new_email);
        epostcode = findViewById(R.id.customer_new_postcode);
        ezipcode = findViewById(R.id.customer_new_zipcode);
        customer_reg_btn = findViewById(R.id.customer_new_register_btn);

        Toolbar toolbar = findViewById(R.id.CustomerActivity_toolbar);
        setSupportActionBar(toolbar);

    }

    public void init() {

        switch (id){
            case "0":
                Customer_search();
                break;
            case "1":
                Customer_new();
                break;
        }

    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        edit = data.getString("edit");
        factor_target = data.getString("factor_code");
        id = data.getString("id");

    }

    public void Customer_search() {
        li_search.setVisibility(View.VISIBLE);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                srch = NumberFunctions.EnglishNumber(editable.toString());
                allCustomer();
            }
        });


        Customer_new.setOnClickListener(v -> {
            intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "1");
            startActivity(intent);
        });

        final SwitchMaterial mySwitch_activestack = findViewById(R.id.customerActivityswitch);


        mySwitch_activestack.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                activecustomer = true;
                mySwitch_activestack.setText("فعال");

            } else {
                activecustomer = false;
                mySwitch_activestack.setText("فعال -غیرفعال");
            }
            allCustomer();
        });
        allCustomer();

    }

    public void Customer_new() {
        li_new.setVisibility(View.VISIBLE);
       // replication.replicate_customer();


        citys = dbh.city();
        for (Customer citycustomer : citys) {
            city_array.add(citycustomer.getCustomerFieldValue("CityName"));
        }

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, city_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setSelection(0);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citycode = citys.get(position).getCustomerFieldValue("CityCode");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kodemeli_check.setOnClickListener(v -> {

            if (dbh.Customer_check(ekodemelli.getText().toString()) > 0) {

                kodemeli_statu.setText("کد ملی ثبت شده است");
                kodemeli_statu.setTextColor(getResources().getColor(R.color.red_300));
            } else {

                kodemeli_statu.setText("کد ملی ثبت نشده است");
                kodemeli_statu.setTextColor(getResources().getColor(R.color.green_900));

            }
        });

        customer_reg_btn.setOnClickListener(v -> {

            if (dbh.Customer_check(ekodemelli.getText().toString()) > 0) {
                kodemeli_statu.setText("کد ملی ثبت شده است");
                kodemeli_statu.setTextColor(getResources().getColor(R.color.red_300));
            } else {

                UserInfo auser = dbh.LoadPersonalInfo();
                if (Integer.parseInt(auser.getBrokerCode()) > 0) {
                    kodemelli = NumberFunctions.EnglishNumber(ekodemelli.getText().toString());
                    name = NumberFunctions.EnglishNumber(ename.getText().toString());
                    family = NumberFunctions.EnglishNumber(efamily.getText().toString());
                    address = NumberFunctions.EnglishNumber(eaddress.getText().toString());
                    phone = NumberFunctions.EnglishNumber(ephone.getText().toString());
                    mobile = NumberFunctions.EnglishNumber(emobile.getText().toString());
                    email = NumberFunctions.EnglishNumber(eemail.getText().toString());
                    postcode = NumberFunctions.EnglishNumber(epostcode.getText().toString());
                    zipcode = NumberFunctions.EnglishNumber(ezipcode.getText().toString());

                    Call<RetrofitResponse> call = apiInterface.customer_insert("CustomerInsert", auser.getBrokerCode(), citycode, kodemelli, name, family, address, phone, mobile, email, postcode, zipcode);
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                ArrayList<Customer> Customes = response.body().getCustomers();
                                callMethod.showToast(Customes.get(0).getCustomerFieldValue("ErrDesc"));
                                intent = new Intent(App.getContext(), CustomerActivity.class);
                                intent.putExtra("edit", "0");
                                intent.putExtra("factor_code", "0");
                                intent.putExtra("id", "0");
                                startActivity(intent);
                            }


                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            callMethod.ErrorLog(t.getMessage());
                        }
                    });

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
        rc_customer.setLayoutManager(gridLayoutManager);
        rc_customer.setAdapter(adapter);
        rc_customer.setItemAnimator(new DefaultItemAnimator());
    }


}
