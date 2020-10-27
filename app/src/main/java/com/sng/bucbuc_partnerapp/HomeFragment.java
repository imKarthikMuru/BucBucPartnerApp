package com.sng.bucbuc_partnerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    RecyclerView HomeRecyclerView;
    DatabaseReference reference;
    FirebaseRecyclerAdapter adapter;
    FirebaseRecyclerOptions<OrderModelCLass> options;
    String uid;
    LottieAnimationView Loading;

    LoadingView loadingView=LoadingView.getInstance();

    FloatingActionButton FilterFAB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        HomeRecyclerView=(RecyclerView)view.findViewById(R.id.homeRecycler);
        Loading=(LottieAnimationView)view.findViewById(R.id.loading);
        FilterFAB=(FloatingActionButton)view.findViewById(R.id.fabFilter);

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference=FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("Orders");

        options=new FirebaseRecyclerOptions.Builder<OrderModelCLass>()
                .setQuery(reference,OrderModelCLass.class).build();

        adapter=new FirebaseRecyclerAdapter<OrderModelCLass,OrdersViewHolder>(options) {
            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycler, parent, false);
                return  new OrdersViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final OrdersViewHolder holder, int position, @NonNull final OrderModelCLass model) {

                Loading.setVisibility(View.GONE);

                final DatabaseReference ref=getRef(position);
                final String post=ref.getKey();

                holder.CustomeName.setText(model.getName());
                holder.AreaNameTV.setText(model.getAddress());
                holder.RateTV.setText("₹"+model.getToPay());
                holder.RateTV.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

                final List<String> list=new ArrayList<>();
                final StringBuilder builder = new StringBuilder();

                reference.child(post).child("Products").orderByChild("ProductName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot data:dataSnapshot.getChildren()){

                            list.add(data.child("ProductName").getValue()+" x "+data.child("Quantity").getValue());
                            Log.d(TAG, "onBindViewHolder: ::::::::::::"+list.size()+"::::::::;"+list.toString());

                        }

                        for (int i=0;i<list.size();i++){
                            if (i < list.size() - 1) {
                                builder.append(list.get(i).toString()+", ");
                            } else {
                                builder.append(list.get(i).toString());
                            }

                        }

                        holder.OrderDetailsTV.setText(builder.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.Show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(),OrderDetailsActivity.class)
                        .putExtra("PostKey",post));
                    }
                });

                holder.OrderedDateTime.setText(model.getOrderedDate()+" "+model.getTime());

                switch (model.getOrderStatus()) {
                    case "Accepted":
                        holder.Accept.setText("Order Accepted");
                        holder.Accept.setEnabled(false);
                        break;
                    case "Prepared":
                        holder.Accept.setText("Order Preparing");
                        holder.Accept.setEnabled(false);
                        break;
                    case "Shipped":
                        holder.Accept.setText("Order Shipped");
                        holder.Accept.setEnabled(false);
                        break;
                    case "Delivered":
                        holder.Accept.setText("Delivered");
                        holder.Accept.setEnabled(false);
                        break;
                    default:
                        holder.Accept.setText("Accept Order");
                        holder.Accept.setEnabled(true);
                        break;
                }

                holder.Accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        loadingView.ShowProgress(getContext(),"Hold Tight! ",false);

                        final Map<String, Object> Accept = new HashMap<>();
                        Accept.put("OrderStatus","Accepted");

                        reference.child(post).updateChildren(Accept).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    FirebaseDatabase.getInstance().getReference("Users").child(model.getUserID())
                                            .child("MyOrders").child(post).updateChildren(Accept).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                holder.Accept.setText("Order Accepted");
                                                holder.Accept.setEnabled(false);
                                            }else {
                                                holder.Accept.setText("Try Again");
                                                Toast.makeText(getContext(), "Oh Snap! Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }else {
                                    Toast.makeText(getContext(), "Oh Snap! Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                                loadingView.hideProgress();
                            }
                        });
                    }
                });
            }
        };

        LinearLayoutManager manager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
        manager.setStackFromEnd(true);
        HomeRecyclerView.setAdapter(adapter);
        HomeRecyclerView.setLayoutManager(manager);
        adapter.startListening();
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
