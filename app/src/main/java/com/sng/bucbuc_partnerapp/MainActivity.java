package com.sng.bucbuc_partnerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String uid;

    private static final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    String StoreAddress=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instance for Bottom Navigation
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);


        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //Do nothing for avoid recreation that fragment again..
            }
        });

        //For default page Fragment Set
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new HomeFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        try {
           uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //bottom Nav click
    BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment fragment=null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            fragment=new HomeFragment();
                            break;
                        case R.id.nav_menu:
                            fragment=new MenuFragment();
                            break;
                        case R.id.nav_orderHistory:
                            fragment=new OrderHistoryFragment();
                            break;
                        case R.id.nav_profile:
                            fragment=new ProfileFragment();
                            break;
                    }
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left);
                    fragmentTransaction.replace(R.id.fragmentContainer,fragment).commit();
                    return true;
                }
            };


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof HomeFragment) {

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
        else {
            // just replace container with fragment as you normally do;
//            Toast.makeText(this, "Click BACK again to exit app.", Toast.LENGTH_SHORT).show();
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);//clear backstackfirst and then you can exit the app onbackpressed from home fr
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragmentContainer, new HomeFragment());
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        } else {

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("StoreDetails").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        StoreAddress = String.valueOf(snapshot.child("Address").getValue());
                        Geocoder coder = new Geocoder(getApplicationContext());
                        try {
                            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(StoreAddress, 5);
                            for (Address add : adresses) {
                                if (adresses != null) {//Controls to ensure it is right address such as country etc.
                                    double longitude = add.getLongitude();
                                    double latitude = add.getLatitude();
                                    if (!snapshot.child("StoreLatLng").exists()){
                                        Map<String, Object> StoreLatLng = new HashMap<>();
                                        StoreLatLng.put("Lat",latitude);
                                        StoreLatLng.put("Lng",longitude);

                                        FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("StoreDetails").updateChildren(StoreLatLng);
                                    }else {
                                        Log.d(TAG, "onDataChange: :::::::::::::LatLng already exists.");
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}