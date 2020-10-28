package com.sng.bucbuc_partnerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailsActivity";
    
    String post;
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

    int toPay=0,deliveryFee=0,taxes=0,total=0;

    FirebaseRecyclerAdapter adapter;
    FirebaseRecyclerOptions<OrderModelCLass> options;

    LoadingView loadingView=LoadingView.getInstance();

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

        manager=new LinearLayoutManager(OrderDetailsActivity.this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        loadingView.ShowProgress(OrderDetailsActivity.this,"Loading Order Details..",false);

        UpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopupMenu(v);
            }
        });

        reference= FirebaseDatabase.getInstance().getReference("Stores").child(uid);
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

                            String status=String.valueOf(snapshot.child("OrderStatus").getValue());

                            UpdateStatus.setText(status+" (UPDATE STATUS)");

                            userId=String.valueOf(snapshot.child("UserID").getValue());

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

                            Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE).into(holder.ProductBanner, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(img).into(holder.ProductBanner);
                                }
                            });

                        }else{
                            Picasso.get().load(R.drawable.logotext).into(holder.ProductBanner);
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
                        UpdateOrderStatus(Orderstatus);
                        return true;

                    case R.id.delivered:
                        Orderstatus="Delivered";
                        UpdateOrderStatus(Orderstatus);
                        return true;

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
                               UpdateStatus.setText(status+" (UPDATE STATUS)");
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

    public void Back(View view) {
        finish();
    }
}