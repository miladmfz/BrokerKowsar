package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
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

public class GoodBuyHistoryAdapter extends RecyclerView.Adapter<GoodBuyHistoryAdapter.GoodViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<Good> goods;
    private final Context mContext;
    CallMethod callMethod;
    private final String itemposition;
    APIInterface apiInterface;
    private final Image_info image_info;
    private long sum = 0;
    private final DatabaseHelper dbh;
    Action action;




    public GoodBuyHistoryAdapter(ArrayList<Good> goods, String Itemposition, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.itemposition = Itemposition;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new Image_info(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("UseSQLiteURL"));
        
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);

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


        }

        if (image_info.Image_exist(gooddetail.getGoodFieldValue("KsrImageCode"))) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    gooddetail.getGoodFieldValue("KsrImageCode") + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.img.setImageBitmap(myBitmap);

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
