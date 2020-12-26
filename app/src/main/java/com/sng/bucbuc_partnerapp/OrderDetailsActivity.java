package com.sng.bucbuc_partnerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sng.bucbuc_partnerapp.Notification.NotificationInterface;
import com.sng.bucbuc_partnerapp.Notification.NotificationRequest;
import com.sng.bucbuc_partnerapp.Notification.NotificationResponse;
import com.sng.bucbuc_partnerapp.Notification.RetrofitClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailsActivity";
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";

    String post,status;
    String uid;
    DatabaseReference reference;
    TextView OrderedDateTV,NameTV,MobileTV,AddressTV,PaymentTV,CallCustomerTV;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    TextView ItemTotal,DeliveryFee,Taxes,ToPay
            ,Note;
    Button UpdateStatus;

    String Orderstatus;
    String userId;
    String note=null;
    String storeID;
    String userToken;

    int toPay=0,deliveryFee=0,taxes=0,total=0;

    FirebaseRecyclerAdapter adapter,driverAdapter;
    FirebaseRecyclerOptions<OrderModelCLass> options;
    FirebaseRecyclerOptions<DriverModelClass> driverOptions;

    Button AssignDriver;

    DatabaseReference DriverRef;

    LoadingView loadingView=LoadingView.getInstance();

    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    RecyclerView DriversRecyclerView;

    LatLng StoreLatLng,DriverLatLng;
    double storeLat,StoreLng,DriverLat,DriverLng;
    SharedPreferences prefs;
     String OrderedDate;
     String Time;
     String storeName;
     String driverToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = getIntent();
        post=intent.getStringExtra("PostKey");

        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "onCreate: ::::::::::::::"+post);
        OrderedDateTV=(TextView)findViewById(R.id.date);
        NameTV=(TextView)findViewById(R.id.cusName);
        MobileTV=(TextView)findViewById(R.id.mobile);
        AddressTV=(TextView)findViewById(R.id.address);
        PaymentTV=(TextView)findViewById(R.id.paymentType);
        CallCustomerTV=(TextView)findViewById(R.id.callTV);

        UpdateStatus=(Button)findViewById(R.id.updateStatus);

        ItemTotal=(TextView)findViewById(R.id.itemTotal);
        DeliveryFee=(TextView)findViewById(R.id.deliveryFee);
        Taxes=(TextView)findViewById(R.id.taxes);
        ToPay=(TextView)findViewById(R.id.toPay);
        Note=(TextView)findViewById(R.id.noteTv);

        recyclerView=(RecyclerView)findViewById(R.id.orderRecycler);

        AssignDriver=(Button)findViewById(R.id.assignDriver);
        Context context;
        dialogBuilder=new AlertDialog.Builder(this);

        LayoutInflater inflater=this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.driver_list_dialog, null);

        DriversRecyclerView=(RecyclerView)dialogView.findViewById(R.id.driverRecyclerview);

        final DateFormat date = new SimpleDateFormat("dd-MMMM-yyyy");
        OrderedDate = date.format(Calendar.getInstance().getTime());

        final DateFormat time = new SimpleDateFormat("hh:mm a");
        Time = time.format(Calendar.getInstance().getTime());


        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);

        alertDialog=dialogBuilder.create();
        alertDialog.setCancelable(true);

        manager=new LinearLayoutManager(OrderDetailsActivity.this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        loadingView.ShowProgress(OrderDetailsActivity.this,"Loading Order Details..",false);

        UpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopupMenu(v);
            }
        });

        DriverRef=FirebaseDatabase.getInstance().getReference("Drivers");

         prefs = getApplicationContext().getSharedPreferences("StoreData", MODE_PRIVATE);
         storeLat= Double.parseDouble(prefs.getString("Lat","0"));
         StoreLng=Double.parseDouble(prefs.getString("Long","0"));
         storeID=FirebaseAuth.getInstance().getCurrentUser().getUid();
         storeName=prefs.getString("StoreName","");

         StoreLatLng=new LatLng(storeLat,StoreLng );

        reference= FirebaseDatabase.getInstance().getReference("Stores").child(uid);

        driverOptions=new FirebaseRecyclerOptions.Builder<DriverModelClass>()
                .setQuery(DriverRef,DriverModelClass.class).build();

        driverAdapter=new FirebaseRecyclerAdapter<DriverModelClass,DriverAssignViewHolder>(driverOptions) {
            @NonNull
            @Override
            public DriverAssignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list, parent, false);
               return  new DriverAssignViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final DriverAssignViewHolder holder, int position, @NonNull final DriverModelClass model) {

                DatabaseReference ref=getRef(position);
                final String driverKey=ref.getKey();

                holder.DriverName.setText(model.getName());

                DriverLat=Double.parseDouble(String.valueOf(model.getLat()));
                DriverLng=Double.parseDouble(String.valueOf(model.getLng()));
                driverToken=String.valueOf(model.getPushToken());

                Log.d(TAG, "onBindViewHolder: :::::::::::::"+storeLat+":::::"+StoreLng+":::::::::::Drive::::"+DriverLat+"::::"+DriverLng);

                DriverLatLng=new LatLng(DriverLat,DriverLng );

                    if (StoreLatLng == null || DriverLatLng == null) {
                        Toast.makeText(OrderDetailsActivity.this, "Unable to get Drivers Location.", Toast.LENGTH_SHORT).show();
                    } else {


                        Routing routing = new Routing.Builder()
                                .travelMode(AbstractRouting.TravelMode.DRIVING)
                                .withListener(new RoutingListener() {
                                    @Override
                                    public void onRoutingFailure(RouteException e) {
                                        holder.DriverETA.setText("Cannot find any possible of ETA.");
                                        holder.AssignDriver.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onRoutingStart() {
                                        holder.AssignDriver.setVisibility(View.GONE);
                                        holder.DriverETA.setText("...");
                                    }

                                    @Override
                                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

                                        for (int j = 0; j < arrayList.size(); j++) {
                                            String eta = String.valueOf(arrayList.get(j).getDurationText());
                                            holder.DriverETA.setText(eta);
//                                            holder.AssignDriver.setVisibility(View.VISIBLE);

                                            DriverRef.child(driverKey)
                                                    .child("Pending").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    Log.d(TAG, "onDataChange: ::::::::::::::::"+snapshot.getChildrenCount());

                                                    if (snapshot.getChildrenCount()<=0){
                                                        holder.AssignDriver.setVisibility(View.VISIBLE);
                                                    }else {
                                                        holder.AssignDriver.setVisibility(View.GONE);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }


                                    }

                                    @Override
                                    public void onRoutingCancelled() {

                                    }
                                })
                                .waypoints(DriverLatLng, StoreLatLng)
                                .key(getString(R.string.Google_Api_Key))
                                .build();
                        routing.execute();
                    }

                    final Map<String, Object> assignDriver = new HashMap<>();
                    assignDriver.put("StoreID",storeID);
                    assignDriver.put("OrderID",post);
                    assignDriver.put("Date",OrderedDate);
                    assignDriver.put("Time",Time);

                    final Map<String, Object> Driver = new HashMap<>();
                    Driver.put("DriverID",driverKey);


                    holder.AssignDriver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DriverRef.child(driverKey)
                                    .child("Pending")
                                    .child(post)
                                    .setValue(assignDriver)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                holder.AssignDriver.setText("Assigned");
                                                holder.AssignDriver.setEnabled(false);
                                                AssignDriver.setText(model.getName());

                                                sendNotification("New Ride!","You have assigned for new Order. Check Now.","OrderDetails",driverToken);

                                                reference.child("Orders")
                                                        .child(post)
                                                        .updateChildren(Driver);
                                                FirebaseDatabase.getInstance().getReference("Users").child(userId)
                                                        .child("MyOrders")
                                                        .child(post)
                                                        .updateChildren(Driver);
                                            }else {
                                                holder.AssignDriver.setEnabled(true);
                                                Toast.makeText(OrderDetailsActivity.this, "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });
                }

        };

        DriversRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        DriversRecyclerView.setAdapter(driverAdapter);
        driverAdapter.startListening();

        AssignDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });

        reference.child("Orders")
        .child(post).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        loadingView.hideProgress();

                        if (snapshot.exists()){

                            OrderedDateTV.setText("Ordered on: "+snapshot.child("OrderedDate").getValue()+" "+snapshot.child("Time").getValue());

                            NameTV.setText(String.valueOf(snapshot.child("Name").getValue()));

                            MobileTV.setText(String.valueOf(snapshot.child("Mobile").getValue()));

                            AddressTV.setText(String.valueOf("("+snapshot.child("AddressType").getValue()+") "+snapshot.child("Address").getValue()));

                            String paymentMethod=String.valueOf(snapshot.child("PaymentMethod").getValue());

                            if (paymentMethod.equals("Pay Online")){

                                PaymentTV.setText("Paid Online");

                            }else {

                                PaymentTV.setText("C.O.D");

                            }

                            status=String.valueOf(snapshot.child("OrderStatus").getValue());

                            if (snapshot.child("DriverID").exists()){
                                DriverRef.child(String.valueOf(snapshot.child("DriverID").getValue())).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        AssignDriver.setText(String.valueOf(dataSnapshot.child("Name").getValue()));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }else {
                                AssignDriver.setText("Assign Driver");
                            }

                            if (status.equals("Delivered")||status.equals("Cancelled")){
                                AssignDriver.setVisibility(View.GONE);
                            }

                            UpdateStatus.setText(status);

                            userId=String.valueOf(snapshot.child("UserID").getValue());

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    userToken= String.valueOf(snapshot.child("PushToken").getValue());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            toPay=Integer.parseInt(String.valueOf(snapshot.child("ToPay").getValue()));
                            Log.d(TAG, "onDataChange: :::::::::::"+toPay);

                            note= String.valueOf(snapshot.child("Suggestion").getValue());

                            if (note!="-"){
                                Note.setText("Note from Customer : "+note);
                            }else {
                                Note.setText("-");
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        options=new FirebaseRecyclerOptions.Builder<OrderModelCLass>()
                .setQuery(reference.child("Orders").child(post).child("Products"),OrderModelCLass.class).build();

        adapter=new FirebaseRecyclerAdapter<OrderModelCLass,OrderListViewHolder>(options) {
            @NonNull
            @Override
            public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_recycler, parent, false);
               return  new OrderListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final OrderListViewHolder holder, int position, @NonNull final OrderModelCLass model) {
                holder.ProductName.setText(model.getProductName());
                holder.SubTotalTV.setText("₹ "+model.getPrice());
                holder.Quantity.setText(""+model.getQuantity());

                total+=model.getPrice();

                ItemTotal.setText("₹ "+total);

                deliveryFee=toPay-total;
                DeliveryFee.setText("₹ "+deliveryFee);
                ToPay.setText("₹ "+toPay);

                reference.child("Products").child(model.getCategory()).child(model.getProductID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        holder.PriceTv.setText("₹ "+snapshot.child("Price").getValue());

                        final String img=String.valueOf(snapshot.child("ProductImage").getValue());

                        Log.d(TAG, "onDataChange: :::::::::::"+img);

                        if (!img.isEmpty()){

//                            Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE).into(holder.ProductBanner, new Callback() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onError(Exception e) {
//                                    Picasso.get().load(img).into(holder.ProductBanner);
//                                }
//                            });

                        }else{
//                            Picasso.get().load(R.drawable.logotext).into(holder.ProductBanner);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void ShowPopupMenu(View v) {
        Context wrapper = new ContextThemeWrapper(this, R.style.myPopupMenuTextAppearanceColor);
         PopupMenu popup = new PopupMenu(wrapper, v);
         popup.setGravity(Gravity.RIGHT);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MenuItem item = popup.getMenu().findItem(R.id.cancel);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(OrderDetailsActivity.this, R.color.BannerOrangeDark)), 0, s.length(), 0);
        s.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setTitle(s);


        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.accept:
                        Orderstatus="Accepted";
                        UpdateOrderStatus(Orderstatus);
                        return true;

                    case R.id.prepare:
                        Orderstatus="Prepared";
                        UpdateOrderStatus(Orderstatus);
                        return true;

                    case R.id.shipped:
                        Orderstatus="Shipped";
                        alertDialog.show();
                        UpdateOrderStatus(Orderstatus);
                        return true;

//                    case R.id.delivered:
//                        Orderstatus="Delivered";
//                        UpdateOrderStatus(Orderstatus);
//                        return true;

                    case R.id.cancel:
                        Orderstatus="Cancelled";
                        UpdateOrderStatus(Orderstatus);
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

    private void UpdateOrderStatus(final String status) {
        loadingView.ShowProgress(OrderDetailsActivity.this,"Hold Tight! ",false);

        final Map<String, Object> Accept = new HashMap<>();
        Accept.put("OrderStatus",status);

        reference.child("Orders").child(post).updateChildren(Accept).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    FirebaseDatabase.getInstance().getReference("Users").child(userId)
                            .child("MyOrders").child(post).updateChildren(Accept).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                               UpdateStatus.setText(status);

                               sendNotification("Order Status","You Order on "+storeName+" has been "+status,"MyOrders",userToken);

                            }else {
                               UpdateStatus.setText("Update Order Status");
                                Toast.makeText(OrderDetailsActivity.this, "Oh Snap! Something went wrong.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }else {
                    Toast.makeText(OrderDetailsActivity.this, "Oh Snap! Something went wrong.", Toast.LENGTH_SHORT).show();
                }
                loadingView.hideProgress();
            }
        });
    }

    private void sendNotification(String order_status, String s, String orders,String token) {

        NotificationRequest request=new NotificationRequest(token,new NotificationRequest.Notification(order_status,s,orders));

        Log.d(TAG, "onComplete: :::::::::::::::::"+token);

        RetrofitClient.getRetrofit(BASE_URL)
                .create(NotificationInterface.class)
                .sent(request)
                .enqueue(new retrofit2.Callback<NotificationResponse>() {
                    @Override
                    public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                        if (response.code() == 200){
                            Toast.makeText(OrderDetailsActivity.this, "message send", Toast.LENGTH_SHORT).show();
                        }else if (response.code()==400){
                            Toast.makeText(OrderDetailsActivity.this, "Bad request", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(OrderDetailsActivity.this, "404", Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "onResponse: :::::::::::Error"+response.code()+":::::::::"+call.request());
                    }

                    @Override
                    public void onFailure(Call<NotificationResponse> call, Throwable t) {

                        Log.d(TAG, "onFailure: ::::::::::::::"+t.getCause()+"::::::"+t.getMessage()+":::::::::");
                    }
                });

    }

    public void Back(View view) {
        finish();
    }
}