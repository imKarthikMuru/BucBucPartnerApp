package com.sng.bucbuc_partnerapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListViewHolder extends RecyclerView.ViewHolder {

    TextView ProductName,PriceTv,SubTotalTV,Quantity;
    ImageView ProductBanner;

    public OrderListViewHolder(@NonNull View itemView) {
        super(itemView);

        ProductName=itemView.findViewById(R.id.productName);
        PriceTv=itemView.findViewById(R.id.price);
        SubTotalTV=itemView.findViewById(R.id.subTotal);
        Quantity=itemView.findViewById(R.id.qty);
        ProductBanner=itemView.findViewById(R.id.product_image);
    }
}
