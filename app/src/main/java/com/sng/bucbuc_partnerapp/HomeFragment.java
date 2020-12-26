package com.sng.bucbuc_partnerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sng.bucbuc_partnerapp.Notification.NotificationInterface;
import com.sng.bucbuc_partnerapp.Notification.NotificationRequest;
import com.sng.bucbuc_partnerapp.Notification.NotificationResponse;
import com.sng.bucbuc_partnerapp.Notification.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";

    RecyclerView HomeRecyclerView;
    DatabaseReference reference;
    HomeAdapter adapter;
    FirebaseRecyclerOptions<OrderModelCLass> options;
    String uid;
    LottieAnimationView Loading;
    SwipeRefreshLayout refreshLayout;

    LinearLayoutManager manager;
    LoadingView loadingView=LoadingView.getInstance();

    String filter;
    FloatingActionButton FilterFAB;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    String storeName,CusPushToken;

    @Override
    public void onPause() {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();
        mListState = HomeRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    HomeRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }

        HomeRecyclerView.setLayoutManager(manager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        HomeRecyclerView=(RecyclerView)view.findViewById(R.id.homeRecycler);
        Loading=(LottieAnimationView)view.findViewById(R.id.loading);
        FilterFAB=(FloatingActionButton)view.findViewById(R.id.fabFilter);
        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refresh);

        SharedPreferences prefs = getContext().getSharedPreferences("StoreData", Context.MODE_PRIVATE);
        storeName=prefs.getString("StoreName","");

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference=FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("Orders");
        
        FilterFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupFilterMenu(view);
            }
        });

            refreshLayout.setRefreshing(true);

            refreshLayout.setColorSchemeResources(R.color.colorAccent);

        options=new FirebaseRecyclerOptions.Builder<OrderModelCLass>()
                .setQuery(reference,OrderModelCLass.class).build();

        adapter=new HomeAdapter(options);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                adapter=new HomeAdapter(options);
                HomeRecyclerView.setAdapter(adapter);
                adapter.startListening();

            }
        });

         manager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
        manager.setStackFromEnd(true);
        HomeRecyclerView.setAdapter(adapter);
        HomeRecyclerView.setLayoutManager(manager);
        adapter.startListening();
        }else {
            startActivity(new Intent(getContext(),LoginActivity.class));
        }
       return view;
    }


    private void PopupFilterMenu(View view) {
        PopupMenu popup = new PopupMenu(getContext(), view, Gravity.CENTER_HORIZONTAL,0,R.style.myPopupMenuTextAppearanceColor);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.all:
                        filter="All";
                        options=new FirebaseRecyclerOptions.Builder<OrderModelCLass>()
                                .setQuery(reference,OrderModelCLass.class).build();

                        adapter=new HomeAdapter(options);

                        LinearLayoutManager manager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
                        manager.setStackFromEnd(true);
                        HomeRecyclerView.setAdapter(adapter);
                        HomeRecyclerView.setLayoutManager(manager);
                        adapter.startListening();
                        return true;

                    case R.id.placed:
                        filter="Placed";
                        SetRecycelerView(filter);
                        return true;

                    case R.id.accept:
                        filter="Accepted";
                        SetRecycelerView(filter);
                        return true;

                    case R.id.prepare:
                        filter="Prepared";
                        SetRecycelerView(filter);
                        return true;

                    case R.id.shipped:
                        filter="Shipped";
                        SetRecycelerView(filter);
                        return true;

                    case R.id.delivered:
                        filter="Delivered";
                        SetRecycelerView(filter);
                        return true;

                    case R.id.cancelled:
                        filter="Cancelled";
                        SetRecycelerView(filter);
                        return true;


                    default:
                        return false;


                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();

    }

    public void SetRecycelerView( String filter) {

        Log.d(TAG, "SetRecycelerView: ::::::::::::::"+filter);

                    Query firebaseSearchQuery=reference.orderByChild("OrderStatus").equalTo(filter);
//                            .startAt(filter)
//                            .endAt(filter + "\uf8ff");
                    
                    options = new FirebaseRecyclerOptions.Builder<OrderModelCLass>()
                                    .setQuery(firebaseSearchQuery, OrderModelCLass.class)
                                    .setLifecycleOwner(this)
                                    .build();

        adapter=new HomeAdapter(options);
        LinearLayoutManager manager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
        manager.setStackFromEnd(true);
        HomeRecyclerView.setAdapter(adapter);
        HomeRecyclerView.setLayoutManager(manager);
        adapter.startListening();
    }


public class HomeAdapter extends FirebaseRecyclerAdapter<OrderModelCLass,OrdersViewHolder>{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomeAdapter(@NonNull FirebaseRecyclerOptions<OrderModelCLass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final OrdersViewHolder holder, int position, @NonNull final OrderModelCLass model) {

        Loading.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);

        final DatabaseReference ref=getRef(position);
        final String post=ref.getKey();

        FirebaseDatabase.getInstance().getReference("Users").child(model.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CusPushToken= String.valueOf(snapshot.child("PushToken").getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
                holder.Accept.setText("Accepted");
                holder.Accept.setEnabled(false);
                break;
            case "Prepared":
                holder.Accept.setText("Preparing");
                holder.Accept.setEnabled(false);
                break;
            case "Shipped":
                holder.Accept.setText("Shipped");
                holder.Accept.setEnabled(false);
                break;
            case "Delivered":
                holder.Accept.setText("Delivered");
                holder.Accept.setEnabled(false);
                break;
            case "Cancelled":
                holder.Accept.setText("Cancelled");
                holder.Accept.setTextColor(getResources().getColor(R.color.quantum_googred));
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

                                        sendNotification("Order Status","You Order on "+storeName+" has been Accepted.","MyOrders");

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

    private void sendNotification(String order_status, String s, String myOrders) {

        NotificationRequest request=new NotificationRequest(CusPushToken,new NotificationRequest.Notification(order_status,s,myOrders));

        Log.d(TAG, "onComplete: :::::::::::::::::"+CusPushToken);

        RetrofitClient.getRetrofit(BASE_URL)
                .create(NotificationInterface.class)
                .sent(request)
                .enqueue(new retrofit2.Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                        if (response.code() == 200){
                            Log.d(TAG, "onResponse: :::::::::Message sent");
                        }else if (response.code()==400){
                            Log.d(TAG, "onResponse: :::::::::Bad request");
                        }else if (response.code()==404){
                            Log.d(TAG, "onResponse: :::::::::Not found 404");
                        }else {
                            Log.d(TAG, "onResponse: :::::::::::unknown Error"+response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationResponse> call, Throwable t) {

                        Log.d(TAG, "onFailure: ::::::::::::::"+t.getCause()+"::::::"+t.getMessage()+":::::::::");
                    }
                });
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycler, parent, false);
        return  new OrdersViewHolder(v);
    }
}

}
