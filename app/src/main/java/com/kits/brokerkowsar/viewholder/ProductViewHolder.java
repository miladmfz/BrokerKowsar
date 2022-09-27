 package com.kits.brokerkowsar.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.Product;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

 public class ProductViewHolder extends ChildViewHolder {
     private final TextView mtextView;
     private final ImageView image;

     public ProductViewHolder(View itemView) {
         super(itemView);
         mtextView=itemView.findViewById(R.id.item2_tv);
         image=itemView.findViewById(R.id.item2_image);
     }

     public void bind(Product product){
         mtextView.setText(NumberFunctions.PerisanNumber(product.name));
     }

     public void bindimage (Bitmap myBitmap){

         image.setImageBitmap(myBitmap);


     }
     public void intent(final Product product,final  Context mContext){

         mtextView.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", String.valueOf(product.id));
            intent.putExtra("title",product.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(intent);

         });
     }

 }
