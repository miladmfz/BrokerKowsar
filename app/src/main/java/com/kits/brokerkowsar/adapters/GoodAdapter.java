package com.kits.brokerkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.ImageInfo;
import com.kits.brokerkowsar.model.Column;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.viewholder.GoodItemViewHolder;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

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


        holder.bind(Columns, goods.get(position), mContext, callMethod);

        holder.Action(goods.get(position)
                , mContext
                , dbh
                , callMethod
                , action
                , image_info
                , multi_select
        );


        if (!image_info.Image_exist(goods.get(position).getGoodFieldValue("KsrImageCode"))) {

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


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
