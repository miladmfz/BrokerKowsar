package com.kits.brokerkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.viewholder.GoodItemViewHolder;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Response;

public class GoodAdapter extends RecyclerView.Adapter<GoodItemViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    DatabaseHelper dbh;

    APIInterface apiInterface;
    final ImageInfo image_info;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_item_cardview, parent, false);
//        GoodItemCardviewBinding binding = GoodItemCardviewBinding.inflate(
//                LayoutInflater.from(parent.getContext())
//        );
//        return new GoodItemViewHolder(binding);
        return new GoodItemViewHolder(view,mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.bind(Columns, goods.get(position), mContext, callMethod);

        holder.Action(goods.get(position), multi_select);
        holder.callimage(goods.get(position));

        holder.rltv.setOnClickListener(v -> {

            if (multi_select) {
                if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                    holder.rltv.setChecked(!holder.rltv.isChecked());
                    goods.get(position).setCheck(!goods.get(position).isCheck());
                    if (goods.get(position).isCheck()) {
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchByDateActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                    } else {
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchByDateActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                    }
                } else {
                    callMethod.showToast("این کالا غیر فعال می باشد");
                }
            } else {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", goods.get(position).getGoodFieldValue("GoodCode"));
                intent.putExtra("ws", goods.get(position).getGoodFieldValue("Shortage"));
                mContext.startActivity(intent);
            }

        });


        holder.rltv.setChecked(goods.get(position).isCheck());

        holder.rltv.setOnLongClickListener(view ->
        {
            if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    multi_select = true;
                    holder.rltv.setChecked(!holder.rltv.isChecked());
                    goods.get(position).setCheck(!goods.get(position).isCheck());

                    if (goods.get(position).isCheck()) {
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchByDateActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }

                    } else {
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.brokerkowsar.activity.SearchByDateActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }


                    }
                } else {

                    Intent intent = new Intent(mContext, PrefactoropenActivity.class);
                    intent.putExtra("fac", "0");
                    mContext.startActivity(intent);

                }
            } else {
                callMethod.showToast("این کالا غیر فعال می باشد");
            }

            return true;
        });


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call.isExecuted()) {
            holder.call.cancel();

        }
    }
}
