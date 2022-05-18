package com.kits.brokerkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.adapters.SliderAdapter;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Image_info;
import com.kits.brokerkowsar.model.Column;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;




public class DetailActivity extends AppCompatActivity {

    private String id;
    Good gooddetail;
    APIInterface apiInterface;
    private Intent intent;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    DatabaseHelper dbh;
    ArrayList<Column> Columns;
    ArrayList<Good> imagelists;
    LinearLayoutCompat mainviewline;
    Action action;
    SliderView sliderView;
    ProgressBar prog;

    Toolbar toolbar;
    TextView tv_customer;
    TextView tv_sumfac;
    TextView tv_customer_code;
    Button btnbuy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_t);

        intent();
        Config();
        try {

            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }



    }

    //*********************************************************************


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        id = data.getString("id");

    }

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        action = new Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        Columns = dbh.GetColumns(id, "", "0");
        gooddetail = dbh.getGoodByCode(id);

        toolbar = findViewById(R.id.DetailActivity_toolbar);
        tv_customer = findViewById(R.id.DetailActivity_customer);
        tv_sumfac = findViewById(R.id.DetailActivity_sum_factor);
        tv_customer_code = findViewById(R.id.DetailActivity_customer_code);
        mainviewline = findViewById(R.id.DetailActivity_line_property);
        prog = findViewById(R.id.DetailActivity_prog);

        btnbuy = findViewById(R.id.DetailActivity_btnbuy);
        setSupportActionBar(toolbar);


    }


    public void init() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            tv_customer.setText("فاکتوری انتخاب نشده");
            tv_sumfac.setText("0");

        } else {
            tv_customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            tv_sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
            tv_customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
        }

        for (Column Column : Columns) {
            if (Integer.parseInt(Column.getColumnFieldValue("SortOrder")) > 0) {
                CreateView(
                        Column.getColumnFieldValue("ColumnDesc"),
                        gooddetail.getGoodFieldValue(Column.getColumnFieldValue("columnname"))
                );
            }
        }

        prog.setVisibility(View.GONE);
        imagelists=dbh.GetksrImageCodes(gooddetail.getGoodFieldValue("GoodCode"));
        SliderView();
        btnbuy.setOnClickListener(view -> {

            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                action.buydialog(gooddetail.getGoodFieldValue("GoodCode"), "0");
            } else {
                intent = new Intent(this, PrefactoropenActivity.class);
                intent.putExtra("fac", "0");
                startActivity(intent);
            }
        });
    }


    public void CreateView(String title, String body) {

        LinearLayoutCompat ll_1 = new LinearLayoutCompat(this);
        ll_1.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_1.setWeightSum(1);

        TextView extra_TextView1 = new TextView(this);
        extra_TextView1.setText(NumberFunctions.PerisanNumber(title));
        extra_TextView1.setBackgroundResource(R.color.grey_20);
        extra_TextView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.7));
        extra_TextView1.setTextSize(Integer.parseInt(callMethod.ReadString("TitleSize")));
        extra_TextView1.setPadding(2, 5, 2, 5);
        extra_TextView1.setGravity(Gravity.CENTER);
        extra_TextView1.setTextColor(getResources().getColor(R.color.grey_800));

        ll_1.addView(extra_TextView1);

        TextView extra_TextView2 = new TextView(this);
        try {
            Log.e("test",body);
            if(Integer.parseInt(body)>999) {
                extra_TextView2.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(body))));
            }else {
                extra_TextView2.setText(NumberFunctions.PerisanNumber(body));
            }
        }catch (Exception e){
            extra_TextView2.setText(NumberFunctions.PerisanNumber(body));
        }

        extra_TextView2.setBackgroundResource(R.color.white);
        extra_TextView2.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.3));
        extra_TextView2.setTextSize(Integer.parseInt(callMethod.ReadString("BodySize")));
        extra_TextView2.setPadding(2, 2, 2, 2);
        extra_TextView2.setGravity(Gravity.CENTER);
        extra_TextView2.setTextColor(getResources().getColor(R.color.grey_1000));
        ll_1.addView(extra_TextView2);

        ViewPager extra_ViewPager = new ViewPager(this);
        extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 3));
        extra_ViewPager.setBackgroundResource(R.color.grey_40);

        mainviewline.addView(ll_1);
        mainviewline.addView(extra_ViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
            } else {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("scan", "");
                    intent.putExtra("id", "0");
                    intent.putExtra("title", "جستجوی کالا");

                } else {
                    callMethod.showToast( "سبد خرید خالی می باشد");
                    intent = new Intent(this, PrefactoropenActivity.class);
                }
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void SliderView() {

        sliderView = findViewById(R.id.DetailActivity_imageSlider);
        SliderAdapter adapter = new SliderAdapter(imagelists,true,this );
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SCALE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

}











