package com.sng.bucbuc_partnerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    TextView StoreNameTV,OwnerNameTv,AddressTv,EmailTv,ContactNoTV;
    CircleImageView StoreImage;
    TextView LogoutBTN;
    String store_name,owner_name,address,email,store_image,contact;

    LoadingView loadingView=LoadingView.getInstance();

    String uid;
    DatabaseReference reference;

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

                        }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }




    }

}
