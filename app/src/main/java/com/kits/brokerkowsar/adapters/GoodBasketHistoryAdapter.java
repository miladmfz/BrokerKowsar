package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import com.kits.brokerkowsar.application.ImageInfo;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.viewholder.GoodBasketHistoryViewHolder;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GoodBasketHistoryAdapter extends RecyclerView.Adapter<GoodBasketHistoryViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<Good> goods;
    private final Context mContext;
    CallMethod callMethod;
    private final String itemposition;
    APIInterface apiInterface;
    private final ImageInfo image_info;
    private long sum = 0;
    private final DatabaseHelper dbh;
    Action action;




    public GoodBasketHistoryAdapter(ArrayList<Good> goods, String Itemposition, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.itemposition = Itemposition;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);

    }

    @NonNull
    @Override
    public GoodBasketHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (itemposition.equals("0")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history_line, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history, parent, false);

        }
        return new GoodBasketHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodBasketHistoryViewHolder holder, int position) {
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



}
