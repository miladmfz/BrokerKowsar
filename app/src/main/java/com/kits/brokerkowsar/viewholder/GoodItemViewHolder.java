package com.kits.brokerkowsar.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.PrefactoropenActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.ImageInfo;
import com.kits.brokerkowsar.model.Column;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodItemViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final LinearLayoutCompat mainline;
    private final ImageView img;
    public MaterialCardView rltv;
    public final Button btnadd;

    boolean multi_select1;


    private final Context mContext;
    CallMethod callMethod;

    DatabaseHelper dbh;

    APIInterface apiInterface;
    private final ImageInfo image_info;
    public Call<RetrofitResponse> call;
    Action action;
    ArrayList<Column> Columns;


    public GoodItemViewHolder(View itemView,Context context) {
        super(itemView);

        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        mainline = itemView.findViewById(R.id.prosearch_mainline);
        img = itemView.findViewById(R.id.good_prosearch_img);
        rltv = itemView.findViewById(R.id.good_prosearch);
        btnadd = itemView.findViewById(R.id.good_prosearch_btn);
    }


    public void bind(ArrayList<Column> Columns, Good good, Context mContext, CallMethod callMethod) {



        mainline.removeAllViews();

        for (Column Column : Columns) {
            if (Integer.parseInt(Column.getSortOrder()) > 1) {
                TextView extra_TextView = new TextView(mContext);
                extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                extra_TextView.setBackgroundResource(R.color.white);
                extra_TextView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                extra_TextView.setTextSize(Integer.parseInt(callMethod.ReadString("BodySize")));
                extra_TextView.setGravity(Gravity.CENTER);
                extra_TextView.setTextColor(mContext.getColor(R.color.grey_1000));

                try {
                    if (Integer.parseInt(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))) > 999) {
                        extra_TextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))))));
                    } else {
                        extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                    }
                } catch (Exception e) {
                    extra_TextView.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                }

                if (Column.getSortOrder().equals("2")) {
                    extra_TextView.setLines(3);
                    if (extra_TextView.getText().toString().length() > 50) {
                        String lowText = extra_TextView.getText().toString().substring(0, 50) + "...";
                        extra_TextView.setText(lowText);
                    }
                }

                if (Column.getColumnName().equals("MaxSellPrice")) {

                    extra_TextView.setTextColor(getcolorresource("3", mContext));
                }
                mainline.addView(extra_TextView);
            }
        }
    }

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForColorStateLists"})
    public void Action(Good good, boolean multi_select) {

        this.multi_select1 = multi_select;




        if (good.getGoodFieldValue("ActiveStack").equals("1")){
            btnadd.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.green_600));
        }else{
            btnadd.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.grey_700));
        }



        btnadd.setOnClickListener(view -> {
            if (good.getGoodFieldValue("ActiveStack").equals("1")) {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    action.buydialog(good.getGoodFieldValue("GoodCode"), "0");
                } else {
                    Intent intent = new Intent(mContext, PrefactoropenActivity.class);
                    intent.putExtra("fac", "0");
                    mContext.startActivity(intent);
                }
            }else{
                callMethod.showToast("این کالا غیر فعال می باشد");
            }
        });


    }

    public void callimage(Good good){
        String imagecode = dbh.GetLastksrImageCode(good.getGoodFieldValue("GoodCode"));

        if (image_info.Image_exist(imagecode)) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    imagecode + ".jpg"
            );
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            img.setImageBitmap(myBitmap);

        } else {

            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
            img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

        }



        call = apiInterface.GetImageFromKsr("GetImageFromKsr", good.getGoodFieldValue("KsrImageCode"));
        if (!image_info.Image_exist(imagecode)) {


            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    imagecode
                            );
                            callimage(good);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());

                }
            });
        }


    }

    public int getcolorresource(String colortarget, Context mContext) {
        int intcolor;
        switch (colortarget) {
            case ("2"):
                intcolor = mContext.getColor(R.color.colorAccent);
                break;
            case ("3"):
                intcolor = mContext.getColor(R.color.color_red);
                break;
            case ("4"):
                intcolor = mContext.getColor(R.color.color_sky);
                break;
            case ("5"):
                intcolor = mContext.getColor(R.color.color_green);
                break;
            case ("6"):
                intcolor = mContext.getColor(R.color.color_yellow);
                break;
            case ("7"):
                intcolor = mContext.getColor(R.color.color_pink);
                break;
            case ("8"):
                intcolor = mContext.getColor(R.color.color_indigo);
                break;
            case ("9"):
                intcolor = mContext.getColor(R.color.color_brown);
                break;
            case ("10"):
                intcolor = mContext.getColor(R.color.color_purple);
                break;
            case ("11"):
                intcolor = mContext.getColor(R.color.color_blue);
                break;
            case ("12"):
                intcolor = mContext.getColor(R.color.color_orange);
                break;

            default:
                intcolor = mContext.getColor(R.color.color_black);

                break;
        }


        return intcolor;
    }

}