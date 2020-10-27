package com.sng.bucbuc_partnerapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryMenuVH extends RecyclerView.ViewHolder {
    TextView CategoryNameTv;

    public CategoryMenuVH(@NonNull View itemView) {
        super(itemView);

        CategoryNameTv=itemView.findViewById(R.id.categoryNameTv);

    }

}
