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
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.viewholder.GoodBasketViewHolder;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class GoodBasketAdapter extends RecyclerView.Adapter<GoodBasketViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final APIInterface apiInterface;
    private final ImageInfo image_info;
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    private final DatabaseHelper dbh;
    Intent intent;
    Action action;

    public GoodBasketAdapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);
    }

    @NonNull
    @Override
    public GoodBasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy, parent, false);
        return new GoodBasketViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodBasketViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.bind(goods.get(position));
        holder.Action(goods.get(position), mContext, dbh, callMethod, action, image_info);


        if (!image_info.Image_exist(goods.get(position).getGoodFieldValue("KsrImageCode"))) {
            Call<RetrofitResponse> call2 = apiInterface.GetImageFromKsr("GetImageFromKsr",
                    goods.get(position).getGoodFieldValue("KsrImageCode")
            );
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull retrofit2.Response<RetrofitResponse> response) {
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
