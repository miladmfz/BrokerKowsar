package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.viewholder.GroupLabelViewHolder;

import java.util.ArrayList;

public class GroupLableAdapter extends RecyclerView.Adapter<GroupLabelViewHolder> {

    Context mContext;
    CallMethod callMethod;
    ArrayList<GoodGroup> GoodGroups;


    public GroupLableAdapter(ArrayList<GoodGroup> GoodGroups, Context mContext) {
        this.mContext = mContext;
        this.GoodGroups = GoodGroups;
        this.callMethod = new CallMethod(mContext);

    }

    @NonNull
    @Override
    public GroupLabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_v_list_detail, parent, false);
        return new GroupLabelViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupLabelViewHolder holder, int position) {

        holder.bind(GoodGroups.get(position), mContext);


    }

    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }


}
