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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.activity.BuyActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Image_info;
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


public class Good_buy_Adapter extends RecyclerView.Adapter<Good_buy_Adapter.GoodViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private APIInterface apiInterface;
    private Image_info image_info;
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    private long sum = 0;
    private final DatabaseHelper dbh;
    Intent intent;
    Action action;

    public Good_buy_Adapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new Image_info(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy, parent, false);
        return new GoodViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodViewHolder holder, int position) {
        final Good gooddetail = goods.get(position);


        int maxsellprice = Integer.parseInt(gooddetail.getGoodFieldValue("MaxSellPrice"));
        int sellprice = Integer.parseInt(gooddetail.getGoodFieldValue("Price"));
        int fac_amount = Integer.parseInt(gooddetail.getGoodFieldValue("FactorAmount"));
        int unit_value = Integer.parseInt(gooddetail.getGoodFieldValue("DefaultUnitValue"));


        long maxprice = (long) maxsellprice * fac_amount * unit_value;
        final long price = (long) sellprice * fac_amount * unit_value;
        sum = sum + price;
        int ws = Integer.parseInt(gooddetail.getGoodFieldValue("Shortage"));


        holder.goodnameTextView.setText(NumberFunctions.PerisanNumber(gooddetail.getGoodFieldValue("GoodName")));
        holder.amount.setText(NumberFunctions.PerisanNumber(gooddetail.getGoodFieldValue("FactorAmount")));

        holder.maxsellpriceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(gooddetail.getGoodFieldValue("MaxSellPrice")))));
        holder.priceTextView.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(gooddetail.getGoodFieldValue("Price")))));
        holder.total.setText(NumberFunctions.PerisanNumber(decimalFormat.format(price)));
        holder.maxtotal.setText(NumberFunctions.PerisanNumber(decimalFormat.format(maxprice)));

            if(gooddetail.getGoodFieldValue("SellPriceType").equals("0")) {
                holder.offer.setText("");
            }else {
                holder.offer.setText(NumberFunctions.PerisanNumber((100 - ((sellprice * 100) / maxsellprice)) + " ???????? ?????????? "));
            }


        if (image_info.Image_exist(gooddetail.getGoodFieldValue("KsrImageCode"))) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    gooddetail.getGoodFieldValue("KsrImageCode") + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.img.setImageBitmap(myBitmap);

        } else {
            Call<RetrofitResponse> call2 = apiInterface.GetImageFromKsr(
                    "GetImageFromKsr",
                    gooddetail.getGoodFieldValue("KsrImageCode")
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
                           image_info.SaveImage(BitmapFactory.decodeByteArray(Base64.decode(response.body().getText(), Base64.DEFAULT), 0, Base64.decode(response.body().getText(), Base64.DEFAULT).length), gooddetail.getGoodFieldValue("KsrImageCode"));
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
                .setTitle("????????")
                .setMessage("?????? ???????? ???? ???????? ?????? ??????????")
                .setPositiveButton("??????", (dialogInterface, i) -> {

                    dbh.DeletePreFactorRow(callMethod.ReadString("PreFactorCode"), gooddetail.getGoodFieldValue("PreFactorRowCode"));
                    callMethod.showToast( "???? ?????? ???????? ?????? ??????????");
                    intent = new Intent(mContext, BuyActivity.class);
                    intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                    intent.putExtra("showflag", "2");
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                })
                .setNegativeButton("??????", (dialogInterface, i) -> {

                })
                .show());


        holder.amount.setOnClickListener(view ->
                action.buydialog(
                        gooddetail.getGoodFieldValue("GoodCode"),
                        gooddetail.getGoodFieldValue("PrefactorRowCode")
                )
        );


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }

    static class GoodViewHolder extends RecyclerView.ViewHolder {
        private final TextView goodnameTextView;
        private final TextView maxsellpriceTextView;
        private final TextView priceTextView;
        private final TextView total;
        private final TextView maxtotal;
        private final TextView amount;
        private final TextView good_buy_shortage_f1;
        private final TextView good_buy_shortage_f2;
        private final TextView offer;
        private final Button btndlt;

        private final ImageView img;
        MaterialCardView rltv;

        GoodViewHolder(View itemView) {
            super(itemView);
            goodnameTextView = itemView.findViewById(R.id.good_buy_name);
            maxsellpriceTextView = itemView.findViewById(R.id.good_buy_maxprice);
            priceTextView = itemView.findViewById(R.id.good_buy_price);
            amount = itemView.findViewById(R.id.good_buy_amount);
            good_buy_shortage_f1 = itemView.findViewById(R.id.good_buy_shortage_false1);
            good_buy_shortage_f2 = itemView.findViewById(R.id.good_buy_shortage_false2);
            total = itemView.findViewById(R.id.good_buy_total);
            maxtotal = itemView.findViewById(R.id.good_buy_maxtotal);
            img = itemView.findViewById(R.id.good_buy_img);
            btndlt = itemView.findViewById(R.id.good_buy_btndlt);
            offer = itemView.findViewById(R.id.good_buy_offer);


            rltv = itemView.findViewById(R.id.good_buy);
        }
    }


}
