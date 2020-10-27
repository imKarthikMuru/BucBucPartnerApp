package com.sng.bucbuc_partnerapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrdersViewHolder extends RecyclerView.ViewHolder {

    TextView CustomeName,AreaNameTV,RateTV,OrderDetailsTV,OrderedDateTime;
    Button Accept,Show;

    public OrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        CustomeName=itemView.findViewById(R.id.customerName);
        AreaNameTV=itemView.findViewById(R.id.areaName);
        RateTV=itemView.findViewById(R.id.price);
        OrderDetailsTV=itemView.findViewById(R.id.orderDetails);
        OrderedDateTime=itemView.findViewById(R.id.orderDate);
        Accept=itemView.findViewById(R.id.accept);
        Show=itemView.findViewById(R.id.show);

    }
}
