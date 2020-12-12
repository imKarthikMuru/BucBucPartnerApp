package com.sng.bucbuc_partnerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StatsFragment extends Fragment {

    TextView TotalEarn,CompletedOrders,PendingOrders,TotalRating,CancelledOrder,TotalOrder,AvergedUsers;
    DatabaseReference reference;
    Query query,pendingQuery;
    String uid;
    double totalEarn=0;
    int completedOrders=0;
    int pending=0,Total=0,totalPending=0;

    double total = 0.0;
    double count = 0.0;
    double average = 0.0;
    int rating=0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        TotalEarn=(TextView)view.findViewById(R.id.totalEarn);
        CompletedOrders=(TextView)view.findViewById(R.id.completedOrder);
        PendingOrders=(TextView)view.findViewById(R.id.pendingOrder);
        TotalRating=(TextView)view.findViewById(R.id.totalRating);
        TotalOrder=(TextView)view.findViewById(R.id.totalorders);
        CancelledOrder=(TextView)view.findViewById(R.id.cancelOrder);
        AvergedUsers=(TextView)view.findViewById(R.id.ratedusers);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference= FirebaseDatabase.getInstance().getReference("Stores").child(uid);

        reference.child("Orders").orderByChild("ToPay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Total=Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                Log.d("Total", "onDataChange: ::::::::Total "+Total);

                TotalOrder.setText(""+Total);

                for (final DataSnapshot dataSnapshot:snapshot.getChildren()) {

                    switch(String.valueOf(dataSnapshot.child("OrderStatus").getValue())) {

                        case "Delivered":{
                            totalEarn += Double.parseDouble(String.valueOf(dataSnapshot.child("ToPay").getValue()));
                            TotalEarn.setText("₹ " + totalEarn);

                            query = reference.child("Orders").orderByChild("OrderStatus").equalTo("Delivered");
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                                    completedOrders = Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

                                    CompletedOrders.setText("" + completedOrders);

                                    totalPending = Total - completedOrders;

                                    Log.d("TAG", "onDataChange: ::::::::::Completed  " + dsnapshot.getChildrenCount());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("TAG", "onCancelled: :::::::" + error);
                                }
                            });
                    }
                        case "Placed":
                        {
                            pendingQuery = reference.child("Orders").orderByChild("OrderStatus").equalTo("Placed");

                            pendingQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                                    pending = Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

                                    Log.d("Placed", "onDataChange: ::::::::::Placed  " + (pending));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("TAG", "onCancelled: :::::::" + error);
                                }
                            });
                    }
                        case "Accepted":{
                            pendingQuery = reference.child("Orders").orderByChild("OrderStatus").equalTo("Accepted");

                            pendingQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                                    pending += Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

                                    Log.d("Accepted", "onDataChange: ::::::::::Placed  " + (pending));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("TAG", "onCancelled: :::::::" + error);
                                }
                            });
                    }
                        case "Prepared":
                        {
                            pendingQuery = reference.child("Orders").orderByChild("OrderStatus").equalTo("Prepared");

                            pendingQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                                    pending += Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

                                    Log.d("Prepared", "onDataChange: ::::::::::Prepared  " + pending);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("TAG", "onCancelled: :::::::" + error);
                                }
                            });
                    }
                        case "Shipped":{
                            pendingQuery = reference.child("Orders").orderByChild("OrderStatus").equalTo("Shipped");

                            pendingQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                                    pending += Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));
                                    PendingOrders.setText(""+pending);
                                    Log.d("Shipped", "onDataChange: ::::::::::Shipped  " + (pending));

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("TAG", "onCancelled: :::::::" + error);
                                }
                            });
                    }
                        case "Cancelled": {
                            pendingQuery = reference.child("Orders").orderByChild("OrderStatus").equalTo("Cancelled");

                            pendingQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                                    int cancel = Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

//                                    pending-=cancel;
                                    CancelledOrder.setText(""+cancel);

                                    Log.d("Cancel", "onDataChange: ::::::::::Cancelled  " + (pending));
//                                    Log.d("Total", "onDataChange: :::::::Total Pending:::"+(totalPending-pending));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("TAG", "onCancelled: :::::::" + error);
                                }
                            });

                        }

                    }


//
//                    if (String.valueOf(dataSnapshot.child("OrderStatus").getValue()).equals("Delivered")) {
//
//                        totalEarn+=Double.parseDouble(String.valueOf(dataSnapshot.child("ToPay").getValue()));
//
//                        TotalEarn.setText("₹ "+totalEarn);
//
//                        query=reference.child("Orders").orderByChild("OrderStatus").equalTo("Delivered");
//
//                        query.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
//
//                                    completedOrders = Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));
//
//                                    CompletedOrders.setText("" + completedOrders);
//
//                                    Log.d("TAG", "onDataChange: ::::::::::Completed  " + dsnapshot.getChildrenCount());
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.d("TAG", "onCancelled: :::::::"+error);
//                            }
//                        });
//
//                    }
//
//                    if (String.valueOf(dataSnapshot.child("OrderStatus").getValue()).equals("Placed")){
//
//                        pendingQuery=reference.child("Orders").orderByChild("OrderStatus").equalTo("Placed");
//
//                        pendingQuery.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
//
//                                pending = Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));
//                                totalPending=pending-completedOrders;
//
//                                Log.d("Placed", "onDataChange: ::::::::::Placed  " + (totalPending));
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.d("TAG", "onCancelled: :::::::"+error);
//                            }
//                        });
//                    }
//
//                    if (String.valueOf(dataSnapshot.child("OrderStatus").getValue()).equals("Prepared")){
//
//                        pendingQuery=reference.child("Orders").orderByChild("OrderStatus").equalTo("Prepared");
//
//                        pendingQuery.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
//
//                                totalPending+= Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));
//
//                                Log.d("Prepared", "onDataChange: ::::::::::Prepared  " + totalPending);
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.d("TAG", "onCancelled: :::::::"+error);
//                            }
//                        });
//                    }
//
//                    if (String.valueOf(dataSnapshot.child("OrderStatus").getValue()).equals("Shipped")){
//
//                        pendingQuery=reference.child("Orders").orderByChild("OrderStatus").equalTo("Shipped");
//
//                        pendingQuery.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
//
//                                totalPending+= Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));
//
//                                Log.d("Shipped", "onDataChange: ::::::::::Shipped  " + (totalPending));
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.d("TAG", "onCancelled: :::::::"+error);
//                            }
//                        });
//                    }
//
//                    if (String.valueOf(dataSnapshot.child("OrderStatus").getValue()).equals("Cancelled")){
//
//                        pendingQuery=reference.child("Orders").orderByChild("OrderStatus").equalTo("Cancelled");
//
//                        pendingQuery.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
//
//                                int cancel= Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));
//
//                                totalPending-=cancel;
//
//                                Log.d("TAG", "onDataChange: ::::::::::Cancelled  " + (totalPending));
//                                Log.d("Total", "onDataChange: :::::::Total Pending:::"+(Total-totalPending-completedOrders));
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.d("TAG", "onCancelled: :::::::"+error);
//                            }
//                        });
//                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Ratings").orderByChild("StoreID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                    if (String.valueOf(dataSnapshot1.child("StoreID").getValue()).equals(uid)){

                        rating = Integer.parseInt(String.valueOf(dataSnapshot1.child("Rating").getValue()));

                        total = total + rating;
                        count = count + 1;
                        average = total / count;

                    }
                    TotalRating.setText(String.format("%.1f", Float.parseFloat(String.valueOf(average))));

                    pendingQuery = FirebaseDatabase.getInstance().getReference("Ratings")
                            .orderByChild("StoreID").equalTo(uid);

                    pendingQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dsnapshot) {

                            int reviewedUser = Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

                            AvergedUsers.setText("Averaged from "+reviewedUser+"+ ratings");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: :::::::" + error);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;    }

}
