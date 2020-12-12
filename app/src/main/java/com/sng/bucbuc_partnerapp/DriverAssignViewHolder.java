package com.sng.bucbuc_partnerapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DriverAssignViewHolder extends RecyclerView.ViewHolder {

    TextView DriverName,DriverETA;
    Button AssignDriver;

    public DriverAssignViewHolder(@NonNull View itemView) {
        super(itemView);

        DriverName=itemView.findViewById(R.id.driverName);
        DriverETA=itemView.findViewById(R.id.driverETA);
        AssignDriver=itemView.findViewById(R.id.btnAssign);
    }
}
