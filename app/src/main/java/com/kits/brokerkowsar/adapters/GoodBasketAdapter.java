package com.kits.brokerkowsar.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.BuyActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.ImageInfo;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.viewholder.GoodBasketViewHolder;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodBasketAdapter extends RecyclerView.Adapter<GoodBasketViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final APIInterface apiInterface;
    private final ImageInfo image_info;
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    private long sum = 0;
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



        int maxsellprice = Integer.parseInt(goods.get(position).getGoodFieldValue("MaxSellPrice"));
        int sellprice = Integer.parseInt(goods.get(position).getGoodFieldValue("Price"));
        int fac_amount = Integer.parseInt(goods.get(position).getGoodFieldValue("FactorAmount"));
        int unit_value = Integer.parseInt(goods.get(position).getGoodFieldValue("DefaultUnitValue"));


        long maxprice = (long) maxsellprice * fac_amount * unit_value;
        final long price = (long) sellprice * fac_amount * unit_value;
        sum = sum + price;
        int ws = Integer.parseInt(goods.get(position).getGoodFieldValue("Shortage"));


        holder.goodnameTextView.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue("GoodName")));
        holder.amount.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodFieldValue("FactorAmount")));

        holder.maxsellpriceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(goods.get(position).getGoodFieldValue("MaxSellPrice")))));
        holder.priceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(goods.get(position).getGoodFieldValue("Price")))));
        holder.total.setText(NumberFunctions.PerisanNumber(decimalFormat.format(price)));
        holder.maxtotal.setText(NumberFunctions.PerisanNumber(decimalFormat.format(maxprice)));

            if(goods.get(position).getGoodFieldValue("SellPriceType").equals("0")) {
                holder.offer.setText("");
            }else {
                holder.offer.setText(NumberFunctions.PerisanNumber((100 - ((sellprice * 100) / maxsellprice)) + " درصد تخفیف "));
            }


        if (image_info.Image_exist(goods.get(position).getGoodFieldValue("KsrImageCode"))) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    goods.get(position).getGoodFieldValue("KsrImageCode") + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.img.setImageBitmap(myBitmap);

        } else {
            Call<RetrofitResponse> call2 = apiInterface.GetImageFromKsr(
                    "GetImageFromKsr",
                    goods.get(position).getGoodFieldValue("KsrImageCode")
            );
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getText().equals("no_photo")) {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(mContext.getString(R.string.no_photo), Base64.DEFAULT);
                            holder.img.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length), BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2, BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2, false));

                        } else {
                           image_info.SaveImage(BitmapFactory.decodeByteArray(Base64.decode(response.body().getText(), Base64.DEFAULT), 0, Base64.decode(response.body().getText(), Base64.DEFAULT).length), goods.get(position).getGoodFieldValue("KsrImageCode"));
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


        if (ws == 1) {
            holder.good_buy_shortage_f1.getLayoutParams().height = 30;
        } else {
            holder.good_buy_shortage_f1.getLayoutParams().height = 1;
        }

        if (ws == 2) {
            holder.good_buy_shortage_f2.getLayoutParams().height = 30;
        } else {
            holder.good_buy_shortage_f2.getLayoutParams().height = 1;
        }


        holder.btndlt.setOnClickListener(view ->

                new AlertDialog.Builder(mContext)
                .setTitle("توجه")
                .setMessage("آیا کالا از لیست حذف گردد؟")
                .setPositiveButton("بله", (dialogInterface, i) -> {

                    dbh.DeletePreFactorRow(callMethod.ReadString("PreFactorCode"), goods.get(position).getGoodFieldValue("PreFactorRowCode"));
                    callMethod.showToast( "از سبد خرید حذف گردید");
                    intent = new Intent(mContext, BuyActivity.class);
                    intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                    intent.putExtra("showflag", "2");
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                })
                .setNegativeButton("خیر", (dialogInterface, i) -> {

                })
                .show());


        holder.amount.setOnClickListener(view ->
                action.buydialog(
                        goods.get(position).getGoodFieldValue("GoodCode"),
                        goods.get(position).getGoodFieldValue("PrefactorRowCode")
                )
        );


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
