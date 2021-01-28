package com.sng.bucbuc_partnerapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;
import com.sng.bucbuc_partnerapp.Notification.Config;

public class DatabaseUtil extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Config.id, Config.description, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(Config.description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
