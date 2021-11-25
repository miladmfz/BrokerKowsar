package com.kits.brokerkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {

    private String id;
    Good gooddetail;
    APIInterface apiInterface;
    private Image_info image_info;
    private Intent intent;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    DatabaseHelper dbh;
    private ImageView img;
    Bitmap biitmap;
    ArrayList<Column> Columns;
    ArrayList<Good> imagelists;
    LinearLayoutCompat mainviewline;
    Action action;
    SliderView sliderView;
    ProgressBar prog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_t);

        intent();

        Handler handler = new Handler();
        handler.postDelayed(this::init, 100);


    }

    //*********************************************************************


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        id = data.getString("id");

    }

    public void init() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));
        image_info = new Image_info(this);
        action = new Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        Columns = dbh.GetColumns(id, "", "0");
        gooddetail = dbh.getGoodByCode(id);

        Toolbar toolbar = findViewById(R.id.DetailActivity_toolbar);
        TextView customer = findViewById(R.id.DetailActivity_customer);
        TextView sumfac = findViewById(R.id.DetailActivity_sum_factor);
        TextView customer_code = findViewById(R.id.DetailActivity_customer_code);
        mainviewline = findViewById(R.id.DetailActivity_line_property);
        prog = findViewById(R.id.DetailActivity_prog);

        Button btnbuy = findViewById(R.id.DetailActivity_btnbuy);
        setSupportActionBar(toolbar);

        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");

        } else {
            customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
            customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
        }

//
//        if (callMethod.ReadBoolan("RealAmount")) {
//            CreateView(
//                    "موجودی",
//                    String.valueOf(Integer.parseInt(gooddetail.getGoodFieldValue("Amount"))
//                            - Integer.parseInt(gooddetail.getGoodFieldValue("ReservedAmount"))
//                    )
//            );
//        } else {
//            CreateView("موجودی", gooddetail.getGoodFieldValue("Amount"));
//            CreateView("غیر قابل سفارش", gooddetail.getGoodFieldValue("ReservedAmount"));
//        }


        for (Column Column : Columns) {

            if (Integer.parseInt(Column.getColumnFieldValue("SortOrder")) > 0) {
               if(Column.getColumnFieldValue("ColumnType").equals("0")){
                   CreateView(
                           Column.getColumnFieldValue("ColumnDesc"),
                           gooddetail.getGoodFieldValue(Column.getColumnFieldValue("columnname"))
                   );
               }else {
                   CreateView(
                           Column.getColumnFieldValue("ColumnDesc"),
                           gooddetail.getGoodFieldValue(Column.getColumnFieldValue("columnname"))
                   );
               }


            }
        }

        prog.setVisibility(View.GONE);

        //img.setBackground(ContextCompat.getDrawable(this, R.drawable.no_photo));
//        if (image_info.Image_exist(gooddetail.getGoodFieldValue("GoodCode"))) {
//            String root = Environment.getExternalStorageDirectory() + "/Kowsar";
//            File imagefile = new File(root + "/" + callMethod.ReadString("EnglishCompanyNameUse") + "/" + gooddetail.getGoodFieldValue("GoodCode") + ".jpg");
//            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
//            img.setImageBitmap(myBitmap);
//        } else {
//            Call<RetrofitResponse> call2 = apiInterface.GetImage("getImage", gooddetail.getGoodFieldValue("GoodCode"), 0);
//            call2.enqueue(new Callback<RetrofitResponse>() {
//                @Override
//                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
//                    if (response.isSuccessful()) {
//                        assert response.body() != null;
//                        if (response.body().getText().equals("no_photo")) {
//                            byte[] imageByteArray1;
//                            imageByteArray1 = Base64.decode(getString(R.string.no_photo), Base64.DEFAULT);
//                            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
//
//                        } else {
//                            byte[] imageByteArray1;
//                            imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
//                            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
//                            biitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false);
//                            image_info.SaveImage(BitmapFactory.decodeByteArray(Base64.decode(response.body().getText(), Base64.DEFAULT), 0, Base64.decode(response.body().getText(), Base64.DEFAULT).length), gooddetail.getGoodFieldValue("GoodCode"));
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
//                }
//            });
//
//        }

        imagelists=dbh.GetksrImageCodes(gooddetail.getGoodFieldValue("GoodCode"));

        SliderView();
        btnbuy.setOnClickListener(view -> {

            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                action.buydialog(gooddetail.getGoodFieldValue("GoodCode"), "0");
            } else {
                intent = new Intent(DetailActivity.this, PrefactoropenActivity.class);
                intent.putExtra("fac", "0");
                startActivity(intent);
            }
        });
    }


    public void CreateView(String title, String body) {

        LinearLayoutCompat ll_1 = new LinearLayoutCompat(DetailActivity.this);
        ll_1.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_1.setWeightSum(1);

        TextView extra_TextView1 = new TextView(DetailActivity.this);
        extra_TextView1.setText(NumberFunctions.PerisanNumber(title));
        extra_TextView1.setBackgroundResource(R.color.grey_20);
        extra_TextView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.7));
        extra_TextView1.setTextSize(Integer.parseInt(callMethod.ReadString("TitleSize")));
        extra_TextView1.setPadding(2, 5, 2, 5);
        extra_TextView1.setGravity(Gravity.CENTER);
        extra_TextView1.setTextColor(getResources().getColor(R.color.grey_800));
        ll_1.addView(extra_TextView1);

        TextView extra_TextView2 = new TextView(DetailActivity.this);
        extra_TextView2.setText(NumberFunctions.PerisanNumber(body));
        extra_TextView2.setBackgroundResource(R.color.white);
        extra_TextView2.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT, (float) 0.3));
        extra_TextView2.setTextSize(Integer.parseInt(callMethod.ReadString("BodySize")));
        extra_TextView2.setPadding(2, 2, 2, 2);
        extra_TextView2.setGravity(Gravity.CENTER);
        extra_TextView2.setTextColor(getResources().getColor(R.color.grey_1000));
        ll_1.addView(extra_TextView2);

        ViewPager extra_ViewPager = new ViewPager(DetailActivity.this);
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
                intent = new Intent(DetailActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
            } else {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    intent = new Intent(DetailActivity.this, SearchActivity.class);
                    intent.putExtra("scan", "");
                    intent.putExtra("id", "0");
                    intent.putExtra("title", "جستجوی کالا");

                } else {
                    Toast.makeText(this, "سبد خرید خالی می باشد", Toast.LENGTH_SHORT).show();
                    intent = new Intent(DetailActivity.this, PrefactoropenActivity.class);
                }
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//
//    public void imageview(String code) {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.image_zoom);
//        final ImageView imageView = dialog.findViewById(R.id.image_zoom_view);
//        if (image_info.Image_exist(code)) {
//            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//            File imagefile = new File(root + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/" + code + ".jpg");
//            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
//            imageView.setImageBitmap(myBitmap);
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagefile.getAbsolutePath()), BitmapFactory.decodeFile(imagefile.getAbsolutePath()).getWidth() * 4, BitmapFactory.decodeFile(imagefile.getAbsolutePath()).getHeight() * 4, false));
//        } else {
//            imageView.setBackground(ContextCompat.getDrawable(DetailActivity.this, R.drawable.no_photo));
//        }
//        dialog.show();
//        imageView.setOnClickListener(view -> dialog.dismiss());
//    }

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











