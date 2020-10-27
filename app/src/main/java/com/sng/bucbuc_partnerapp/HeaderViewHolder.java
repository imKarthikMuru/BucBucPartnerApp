package com.sng.bucbuc_partnerapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.ExpansionLayout;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    TextView HeaderTv;
    RecyclerView ProductsRecyclerView;
    LinearLayoutManager manager;
    ExpansionLayout expansionLayout;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.VERTICAL,false);
        HeaderTv = itemView.findViewById(R.id.section);
        ProductsRecyclerView = itemView.findViewById(R.id.productRV);
        ProductsRecyclerView.setLayoutManager(manager);
        ViewCompat.setNestedScrollingEnabled(ProductsRecyclerView, false);
        expansionLayout = itemView.findViewById(R.id.expansionLayout);
    }

    public  ExpansionLayout getExpansionLayout(){
        return expansionLayout;
    }
}
