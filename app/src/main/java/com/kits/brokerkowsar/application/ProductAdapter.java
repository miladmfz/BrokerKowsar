package com.kits.brokerkowsar.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kits.brokerkowsar.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class ProductAdapter extends ExpandableRecyclerViewAdapter<com.kits.brokerkowsar.application.CategoryViewHolder, com.kits.brokerkowsar.application.ProductViewHolder> {

    Context mContext;

    public ProductAdapter(List<? extends ExpandableGroup> groups, Context mContext) {
        super(groups);
        this.mContext=mContext;
    }

    @Override
    public com.kits.brokerkowsar.application.CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new com.kits.brokerkowsar.application.CategoryViewHolder(v);
    }

    @Override
    public com.kits.brokerkowsar.application.ProductViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2,parent,false);
        return new com.kits.brokerkowsar.application.ProductViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(com.kits.brokerkowsar.application.ProductViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final Product product = (Product) group.getItems().get(childIndex);
        holder.bind(product);
        holder.intent(product,App.getContext());


    }

    @Override
    public void onBindGroupViewHolder(com.kits.brokerkowsar.application.CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Category company = (Category) group;
        holder.bind(company);
        holder.intent(company, App.getContext());
        holder.hide(company);

    }
}
