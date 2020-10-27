package com.sng.bucbuc_partnerapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

class MenuListViewHolder extends RecyclerView.ViewHolder {

    ImageView ProductPreview,VegMarkIV;
    TextView ProductNameTv,PriceTv,CategoryTv,SizeTv;

    SwitchCompat StatusSwitch;

    public MenuListViewHolder(@NonNull View itemView) {
        super(itemView);

        PriceTv=itemView.findViewById(R.id.priceTv);
        ProductNameTv=itemView.findViewById(R.id.productNameTv);
        CategoryTv=itemView.findViewById(R.id.categoryTv);
//        SizeTv=itemView.findViewById(R.id.GramsTv);
        ProductPreview=itemView.findViewById(R.id.productPreview);
        VegMarkIV=itemView.findViewById(R.id.vegMark);
        StatusSwitch=itemView.findViewById(R.id.statusSwitch);
    }
}
