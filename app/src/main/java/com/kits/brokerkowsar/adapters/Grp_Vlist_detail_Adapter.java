package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.GoodGroup;

import java.util.ArrayList;

public class Grp_Vlist_detail_Adapter extends RecyclerView.Adapter<Grp_Vlist_detail_Adapter.GoodGroupViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<GoodGroup> GoodGroups;
    private Intent intent;
    private final DatabaseHelper dbh;

    public Grp_Vlist_detail_Adapter(ArrayList<GoodGroup> GoodGroups, Context mContext) {
        this.mContext = mContext;
        this.GoodGroups = GoodGroups;
        this.callMethod = new CallMethod(mContext);

        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));


    }

    @NonNull
    @Override
    public GoodGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_v_list_detail, parent, false);
        return new GoodGroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GoodGroupViewHolder holder, int position) {
        final GoodGroup GoodGroupdetail = GoodGroups.get(position);


        holder.grpname.setText(GoodGroupdetail.getGoodGroupFieldValue("Name"));


        if (dbh.getAllGroups(String.valueOf(GoodGroupdetail.getGoodGroupFieldValue("GroupCode"))).size() == 0) {
            holder.grpname.setIconSize(1);

        } else
            holder.grpname.setIconSize(50);


        holder.grpname.setOnClickListener(v -> {

            intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"));
            intent.putExtra("title", GoodGroups.get(position).getGoodGroupFieldValue("Name"));
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }

    static class GoodGroupViewHolder extends RecyclerView.ViewHolder {

        private final MaterialButton grpname;
        MaterialCardView rltv;

        GoodGroupViewHolder(View itemView) {
            super(itemView);
            grpname = itemView.findViewById(R.id.grp_vlist_detail_name);
            rltv = itemView.findViewById(R.id.grp_vlist_detail);
        }
    }
}
