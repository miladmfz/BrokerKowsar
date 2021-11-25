package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Image_info;
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

public class GoodBuyHistoryAdapter extends RecyclerView.Adapter<GoodBuyHistoryAdapter.GoodViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<Good> goods;
    private final Context mContext;
    CallMethod callMethod;
    private final String itemposition;
    APIInterface apiInterface;
    private final Image_info image_info;

    public GoodBuyHistoryAdapter(ArrayList<Good> goods, String Itemposition, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.itemposition = Itemposition;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new Image_info(mContext);

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (itemposition.equals("0")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history_line, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history, parent, false);

        }
        return new GoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, int position) {
        Good gooddetail = goods.get(position);


        int sellprice = Integer.parseInt(goods.get(position).getGoodFieldValue("Price"));
        int fac_amount = Integer.parseInt(goods.get(position).getGoodFieldValue("FactorAmount"));
        int unit_value = Integer.parseInt(goods.get(position).getGoodFieldValue("DefaultUnitValue"));

        long price = (long) sellprice * fac_amount * unit_value;


        holder.goodnameTextView.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue("GoodName")));
        holder.priceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(goods.get(position).getGoodFieldValue("Price")))));
        holder.amount.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue("FactorAmount")));
        holder.code.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue("GoodCode")));
        holder.total.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt("" + price))));


        if (itemposition.equals("1")) {
            int maxsellprice = Integer.parseInt(gooddetail.getGoodFieldValue("MaxSellPrice"));
            long maxprice = (long) maxsellprice * fac_amount * unit_value;
            holder.maxsellpriceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(gooddetail.getGoodFieldValue("MaxSellPrice")))));
            holder.maxtotal.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt("" + maxprice))));

            if (image_info.Image_exist(gooddetail.getGoodFieldValue("KsrImageCode"))) {
                String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                File imagefile = new File(root + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/" + gooddetail.getGoodFieldValue("GoodCode") + ".jpg");
                Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
                holder.img.setImageBitmap(myBitmap);
            } else {
                Call<RetrofitResponse> call2 = apiInterface.GetImageFromKsr("GetImageFromKsr", gooddetail.getGoodFieldValue("KsrImageCode"));
                call2.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (response.body().getText().equals("no_photo")) {
                                byte[] imageByteArray1;
                                imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
                                holder.img.setImageBitmap(
                                        Bitmap.createScaledBitmap(
                                                BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                                                BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2,
                                                BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2,
                                                false)
                                );
                            } else {
                                byte[] imageByteArray1;
                                imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
                                holder.img.setImageBitmap(
                                        Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                                                BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2,
                                                BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2,
                                                false)
                                );
                                image_info.SaveImage(
                                        BitmapFactory.decodeByteArray(Base64.decode(response.body().getText(), Base64.DEFAULT), 0, Base64.decode(response.body().getText(), Base64.DEFAULT).length), gooddetail.getGoodFieldValue("KsrImageCode"));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class GoodViewHolder extends RecyclerView.ViewHolder {
        private final TextView goodnameTextView;
        private final TextView priceTextView;
        private final TextView total;
        private final TextView amount;
        private final TextView code;
        MaterialCardView rltv;

        private final TextView maxsellpriceTextView;
        private final TextView maxtotal;
        private final ImageView img;


        GoodViewHolder(View itemView) {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_buy_history_name);
            maxsellpriceTextView = itemView.findViewById(R.id.good_buy_history_maxprice);
            maxtotal = itemView.findViewById(R.id.good_buy_history_maxtotal);
            priceTextView = itemView.findViewById(R.id.good_buy_history_price);
            total = itemView.findViewById(R.id.good_buy_history_total);
            amount = itemView.findViewById(R.id.good_buy_history_amount);
            code = itemView.findViewById(R.id.good_buy_history_code);
            img = itemView.findViewById(R.id.good_buy_history_img);
            rltv = itemView.findViewById(R.id.good_buy_history);
        }
    }


}
