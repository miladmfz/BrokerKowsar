package com.kits.brokerkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.DetailActivity;
import com.kits.brokerkowsar.activity.PrefactoropenActivity;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.activity.SearchByDateActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.ImageInfo;
import com.kits.brokerkowsar.model.Column;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.viewholder.GoodItemViewHolder;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodAdapter extends RecyclerView.Adapter<GoodItemViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<Good> goods;
    private Intent intent;
    DatabaseHelper dbh;

    APIInterface apiInterface;
    private final ImageInfo image_info;
    Call<RetrofitResponse> call2;
    public boolean multi_select;
    Action action;
    ArrayList<Column> Columns;


    public GoodAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_prosearch, parent, false);
        return new GoodItemViewHolder(view);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {




        holder.mainline.removeAllViews();

        for (Column Column : Columns) {
            if (Integer.parseInt(Column.getSortOrder()) > 1) {
                TextView extra_TextView = new TextView(mContext);
                extra_TextView.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                extra_TextView.setBackgroundResource(R.color.white);
                extra_TextView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                extra_TextView.setTextSize(Integer.parseInt(callMethod.ReadString("BodySize")));
                extra_TextView.setGravity(Gravity.CENTER);
                extra_TextView.setTextColor(mContext.getColor(R.color.grey_1000));

                try {
                    if(Integer.parseInt(goods.get(position).getGoodFieldValue(Column.getColumnFieldValue("columnname")))>999) {
                        extra_TextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(goods.get(position).getGoodFieldValue(Column.getColumnFieldValue("columnname"))))));
                    }else {
                        extra_TextView.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                    }
                }catch (Exception e){
                    extra_TextView.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue(Column.getColumnFieldValue("columnname"))));
                }

                if (Column.getSortOrder().equals("2")) {
                    extra_TextView.setLines(3);
                    if (extra_TextView.getText().toString().length()>50){
                        String lowText=extra_TextView.getText().toString().substring(0,50)+"...";
                        extra_TextView.setText(lowText);
                    }
                }

                if (Column.getSortOrder().equals("3")) {
                    if (extra_TextView.getText().toString().length()>30){
                        String lowText=extra_TextView.getText().toString().substring(0,30)+"...";
                        extra_TextView.setText(lowText);
                    }
                    extra_TextView.setTextSize(14);
                    extra_TextView.setLines(2);

                }

                if (Column.getColumnName().equals("MaxSellPrice")) {

                    extra_TextView.setTextColor(getcolorresource("3"));
                }


                holder.mainline.addView(extra_TextView);
            }
        }

        if (image_info.Image_exist(goods.get(position).getGoodFieldValue("KsrImageCode"))) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    goods.get(position).getGoodFieldValue("KsrImageCode") + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.img.setImageBitmap(myBitmap);

        } else {

            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
            holder.img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));
            call2 = apiInterface.GetImageFromKsr("GetImageFromKsr", goods.get(position).getGoodFieldValue("KsrImageCode"));
            call2.enqueue(new Callback<RetrofitResponse>() {
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
                                    goods.get(position).getGoodFieldValue("KsrImageCode")
                            );

                            notifyItemChanged(position);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());

                }
            });

        }


        holder.rltv.setOnClickListener(v -> {
            if (multi_select) {
                holder.rltv.setChecked(!holder.rltv.isChecked());
                goods.get(position).setCheck(!goods.get(position).isCheck());
                if (goods.get(position).isCheck()) {

                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                        SearchActivity activity = (SearchActivity) mContext;

                        activity.good_select_function(goods.get(position));
                    }
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.Search_date_detailActivity")) {
                        SearchByDateActivity activity = (SearchByDateActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }

                } else {
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                        SearchActivity activity = (SearchActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.Search_date_detailActivity")) {
                        SearchByDateActivity activity = (SearchByDateActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }


                }

            } else {
                intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", goods.get(position).getGoodFieldValue("GoodCode"));
                intent.putExtra("ws", goods.get(position).getGoodFieldValue("Shortage"));
                mContext.startActivity(intent);
            }

        });


        holder.rltv.setChecked(goods.get(position).isCheck());

        holder.rltv.setOnLongClickListener(view -> {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                multi_select = true;

                holder.rltv.setChecked(!holder.rltv.isChecked());
                goods.get(position).setCheck(!goods.get(position).isCheck());

                if (goods.get(position).isCheck()) {
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                        SearchActivity activity = (SearchActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.Search_date_detailActivity")) {
                        SearchByDateActivity activity = (SearchByDateActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }

                } else {
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                        SearchActivity activity = (SearchActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }
                    if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.Search_date_detailActivity")) {
                        SearchByDateActivity activity = (SearchByDateActivity) mContext;
                        activity.good_select_function(goods.get(position));
                    }


                }
            } else {

                intent = new Intent(mContext, PrefactoropenActivity.class);
                intent.putExtra("fac", "0");
                mContext.startActivity(intent);

            }

            return true;
        });


        holder.btnadd.setOnClickListener(view -> {


            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    action.buydialog(goods.get(position).getGoodFieldValue("GoodCode"), "0");

            } else {

                intent = new Intent(mContext, PrefactoropenActivity.class);
                intent.putExtra("fac", "0");
                mContext.startActivity(intent);

            }
        });





    }

    public int getcolorresource(String colortarget) {
        int intcolor;
        switch(colortarget)
        {
            case ("2"):
                intcolor=mContext.getColor(R.color.colorAccent);
                break;
            case ("3"):
                intcolor=mContext.getColor(R.color.color_red);
                break;
            case ("4"):
                intcolor=mContext.getColor(R.color.color_sky);
                break;
            case ("5"):
                intcolor=mContext.getColor(R.color.color_green);
                break;
            case ("6"):
                intcolor=mContext.getColor(R.color.color_yellow);
                break;
            case ("7"):
                intcolor=mContext.getColor(R.color.color_pink);
                break;
            case ("8"):
                intcolor=mContext.getColor(R.color.color_indigo);
                break;
            case ("9"):
                intcolor=mContext.getColor(R.color.color_brown);
                break;
            case ("10"):
                intcolor=mContext.getColor(R.color.color_purple);
                break;
            case ("11"):
                intcolor=mContext.getColor(R.color.color_blue);
                break;
            case ("12"):
                intcolor=mContext.getColor(R.color.color_orange);
                break;

            default:
                intcolor=mContext.getColor(R.color.color_black);

                break;
        }


        return intcolor;
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
