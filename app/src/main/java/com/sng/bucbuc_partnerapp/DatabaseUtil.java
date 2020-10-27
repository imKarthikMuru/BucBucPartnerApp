package com.sng.bucbuc_partnerapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtil extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
