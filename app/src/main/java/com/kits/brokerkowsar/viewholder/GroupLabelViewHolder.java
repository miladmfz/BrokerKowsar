package com.kits.brokerkowsar.viewholder;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.model.NumberFunctions;

public class GroupLabelViewHolder extends RecyclerView.ViewHolder {

    private final TextView grpname;
    private final ImageView img;
    private final FrameLayout rltv;

    public GroupLabelViewHolder(View itemView) {
        super(itemView);

        grpname = itemView.findViewById(R.id.grp_vlist_detail_name);
        rltv = itemView.findViewById(R.id.grp_vlist_detail);
        img = itemView.findViewById(R.id.grp_vlist_detail_image);
    }


    public void bind(GoodGroup goodGroup, Context mContext) {
        grpname.setText(NumberFunctions.PerisanNumber(goodGroup.getGoodGroupFieldValue("Name")));


        if (Integer.parseInt(goodGroup.getGoodGroupFieldValue("ChildNo")) > 0) {
            img.setVisibility(View.VISIBLE);
        } else
            img.setVisibility(View.GONE);


        grpname.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", goodGroup.getGoodGroupFieldValue("GroupCode"));
            intent.putExtra("title", goodGroup.getGoodGroupFieldValue("Name"));
            mContext.startActivity(intent);
        });
    }


}