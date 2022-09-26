package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.model.NumberFunctions;

import java.util.ArrayList;

public class GroupLableAdapter extends RecyclerView.Adapter<GroupLableAdapter.GoodGroupViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<GoodGroup> GoodGroups;
    private Intent intent;
    private final DatabaseHelper dbh;

    public GroupLableAdapter(ArrayList<GoodGroup> GoodGroups, Context mContext) {
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


        holder.grpname.setText(NumberFunctions.PerisanNumber(GoodGroups.get(position).getGoodGroupFieldValue("Name")));


        if (Integer.parseInt(GoodGroups.get(position).getGoodGroupFieldValue("ChildNo"))>0) {
            holder.img.setVisibility(View.VISIBLE);
        } else
            holder.img.setVisibility(View.GONE);



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

        private final TextView grpname;
        private final ImageView img;
        FrameLayout rltv;

        GoodGroupViewHolder(View itemView) {
            super(itemView);
            grpname = itemView.findViewById(R.id.grp_vlist_detail_name);
            rltv = itemView.findViewById(R.id.grp_vlist_detail);
            img = itemView.findViewById(R.id.grp_vlist_detail_image);
        }
    }
}