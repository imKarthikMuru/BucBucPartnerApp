package com.sng.bucbuc_partnerapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    TextView StoreNameTV,OwnerNameTv,AddressTv,EmailTv,ContactNoTV,TotalProducts;
    CircleImageView StoreImage;
    TextView LogoutBTN;
    String store_name,owner_name,address,email,store_image,contact,offer,coupon;

    LoadingView loadingView=LoadingView.getInstance();

    String uid;
    DatabaseReference reference;

    SwitchCompat OnlineSwitch;
    TextView CouponTV,OfferTV;

    BottomSheetDialog couponDialog,offerDialog;
    EditText couponET,offerET;
    Button couponUpdate,offerUpdate;
    TextView ongoingCoupon,ongoingOffer,EarningsTV;

    Map<String, Object> discount=new HashMap<>();
    Map<String, Object> StoreStatus=new HashMap<>();

    DatabaseReference payoutRef;

    int products=0;
    private String OrderedDate;
    private String Time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_profile, container, false);

        StoreNameTV=view.findViewById(R.id.storename);
        OwnerNameTv=view.findViewById(R.id.ownerNameTv);
        AddressTv=view.findViewById(R.id.addressTv);
        EmailTv=view.findViewById(R.id.emailTv);
        StoreImage=view.findViewById(R.id.store_image);
        LogoutBTN = (TextView) view.findViewById(R.id.BTNLogout);
        ContactNoTV=view.findViewById(R.id.contactTv);

        OnlineSwitch=(SwitchCompat)view.findViewById(R.id.StatusSwitch);
        CouponTV=(TextView)view.findViewById(R.id.coupon);
        OfferTV=(TextView)view.findViewById(R.id.offer);
        TotalProducts=(TextView)view.findViewById(R.id.totalProducts);

        couponDialog=new BottomSheetDialog(getContext());
        couponDialog.setContentView(R.layout.coupon_dialog);
        couponDialog.setCancelable(true);
        couponDialog.setCanceledOnTouchOutside(true);

        couponET=(EditText)couponDialog.findViewById(R.id.couponet);
        couponUpdate=couponDialog.findViewById(R.id.couponbtn);
        ongoingCoupon=couponDialog.findViewById(R.id.coupontv);

        offerDialog=new BottomSheetDialog(getContext());
        offerDialog.setContentView(R.layout.offer_dialog);
        offerDialog.setCancelable(true);
        offerDialog.setCanceledOnTouchOutside(true);

        offerET=(EditText)offerDialog.findViewById(R.id.offeret);
        offerUpdate=offerDialog.findViewById(R.id.offerbtn);
        ongoingOffer=offerDialog.findViewById(R.id.offer);

        EarningsTV=(TextView)view.findViewById(R.id.earnings);

        payoutRef=FirebaseDatabase.getInstance().getReference("PayoutRequests");

        loadingView.ShowProgress(getContext(),"Loading your profile.",true);

        LogoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.ShowProgress(getContext(),"Logging out",false);

                AuthUI.getInstance()
                        .signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getContext(),LoginActivity.class));
                            Toast.makeText(getContext(), "Successfully signed out.", Toast.LENGTH_SHORT).show();
             }
                        loadingView.hideProgress();

                    }
                });
            }
        });

        CouponTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                couponDialog.show();
            }
        });

        OfferTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offerDialog.show();
            }
        });




        OnlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    StoreStatus.put("Status","Online");
                }
                else {
                    StoreStatus.put("Status","Offline");
                }
                reference.updateChildren(StoreStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Status updated.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "Oops..something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        return view;
    }

    private void InitView() {

        try {
            StoreNameTV.setText(""+store_name);
            OwnerNameTv.setText(""+owner_name);
            AddressTv.setText(""+address);
            EmailTv.setText(""+email);
            ContactNoTV.setText(""+contact);

            Picasso.get().load(store_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_spinner)
                    .noFade().into(StoreImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(store_image).into(StoreImage);
                }
            });

            loadingView.hideProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference= FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("StoreDetails");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (uid!=null){

            FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("Products").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Query query;

                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        query=FirebaseDatabase.getInstance().getReference("Stores").child(uid)
                                .child("Products").child(dataSnapshot.getRef().getKey());

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                products+=Integer.parseInt(String.valueOf(dsnapshot.getChildrenCount()));

                                TotalProducts.setText(""+products);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            store_name = String.valueOf(snapshot.child("StoreName").getValue());
                            owner_name = String.valueOf(snapshot.child("OwnerName").getValue());
                            address = String.valueOf(snapshot.child("Address").getValue());
                            email = String.valueOf(snapshot.child("Email").getValue());
                            store_image = String.valueOf(snapshot.child("StoreLogo").getValue());
                            contact=String.valueOf(snapshot.child("ContactNo").getValue());

                            InitView();

                            if (snapshot.child("Coupon").exists()){
                                coupon=String.valueOf(snapshot.child("Coupon").getValue());
                                CouponTV.setText(coupon);
                                ongoingCoupon.setText(coupon);
                            }else {
                                ongoingCoupon.setText("Add Coupon");
                                CouponTV.setText("Add Coupon");
                            }

                            if (snapshot.child("Offer").exists()){
                                offer=String.valueOf(snapshot.child("Offer").getValue());
                                OfferTV.setText(offer+" OFF");
                                ongoingOffer.setText(offer+"% OFF");
                            }else {
                                ongoingOffer.setText("Add offer");
                                OfferTV.setText("Add Offer");
                            }

                            if (snapshot.child("Payout").exists()){
                                EarningsTV.setText(String.valueOf(snapshot.child("Payout").getValue()));
                            }else {
                                EarningsTV.setText("00.00");
                            }

                            final DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                            OrderedDate = date.format(Calendar.getInstance().getTime());

                            final DateFormat time = new SimpleDateFormat("hh:mm a");
                            Time = time.format(Calendar.getInstance().getTime());


                            AlertDialog alertDialog=new AlertDialog.Builder(getContext())
                                    .setIcon(R.drawable.logotext)
                                    .setMessage("You want to payout your earnings money?")
                                    .setTitle("Are you sure?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Map<String, Object> payoutReq = new HashMap<>();
                                            payoutReq.put("StoreID",uid);
                                            payoutReq.put("ReqAmount",snapshot.child("Payout").getValue());
                                            payoutReq.put("Date",OrderedDate);
                                            payoutReq.put("Time",Time);

                                            Map<String, Object> resetPayout = new HashMap<>();
                                            resetPayout.put("Payout",0);

                                            if (Integer.parseInt(String.valueOf(snapshot.child("Payout").getValue()))!=0){
                                                payoutRef.child("Stores").push().setValue(payoutReq).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            reference.updateChildren(resetPayout);
                                                        }else {
                                                            Toast.makeText(getContext(), "Oops! something went wrong, Try later.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }else {
                                                Toast.makeText(getContext(), "your earning money value is 0.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).setCancelable(true)
                                    .create();


                            EarningsTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.show();
                                }
                            });

                            couponUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String changedCoupon=couponET.getText().toString().trim();
                                    if (changedCoupon.isEmpty()){
                                        reference.child("Coupon").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   Toast.makeText(getContext(), "Coupon deleted.", Toast.LENGTH_SHORT).show();
                                               }else{
                                                   Toast.makeText(getContext(), "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
                                               }
                                            }
                                        });
                                    }else {
                                        loadingView.ShowProgress(getContext(),"Updating Coupons..",false);
                                        discount.put("Coupon",changedCoupon);
                                        reference.updateChildren(discount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingView.hideProgress();
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getContext(), "Coupon changed.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(), "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            offerUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String changedOffer=offerET.getText().toString().trim();
                                    if (changedOffer.isEmpty()){
                                        reference.child("Offer").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getContext(), "Offer deleted.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(), "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        discount.put("Offer",changedOffer);
                                        loadingView.ShowProgress(getContext(),"Updating Offers..",false);
                                        reference.updateChildren(discount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingView.hideProgress();
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getContext(), "Offer changed.", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(), "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            String status=String.valueOf(snapshot.child("Status").getValue());
                            if (status.equals("Online")){
                                OnlineSwitch.setChecked(true);
                            }else {
                                OnlineSwitch.setChecked(false);
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
