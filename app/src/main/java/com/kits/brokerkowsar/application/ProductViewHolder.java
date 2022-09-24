 package com.kits.brokerkowsar.application;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

 public class ProductViewHolder extends ChildViewHolder {
     private final TextView mtextView;

     public ProductViewHolder(View itemView) {
         super(itemView);
         mtextView=itemView.findViewById(R.id.item2_tv);
     }

     public void bind(Product product){
         mtextView.setText(product.name);
     }

     public void intent(final Product product,final  Context mContext){

         mtextView.setOnClickListener(v -> {



             Log.e("test11_pro_pro.id",product.id+"");
             Log.e("test11_pro_pro.name",product.name);

            Intent intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", String.valueOf(product.id));
            intent.putExtra("title",product.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(intent);









         });
     }

 }
