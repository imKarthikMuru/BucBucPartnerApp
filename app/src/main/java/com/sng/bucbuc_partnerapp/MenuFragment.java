package com.sng.bucbuc_partnerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";

    FloatingActionButton AddNewItemFab;
    RecyclerView recyclerView,CategoryRV;
    String uid;
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerOptions<ProductListModelClass> options2;
    CoordinatorLayout mRoot;
    LoadingView loadingView;
    String post_key,post;

    FirebaseRecyclerOptions<SectionHeader> options;
    FirebaseRecyclerAdapter<SectionHeader, HeaderViewHolder> adapter;
    FirebaseRecyclerAdapter<SectionHeader,CategoryMenuVH> catagoryAdapter;
    FirebaseRecyclerAdapter<ProductListModelClass, MenuListViewHolder> adapter2;
    RecyclerView.LayoutManager manager;
    ItemTouchHelper helper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        AddNewItemFab=view.findViewById(R.id.AddItemFAB);
        recyclerView=view.findViewById(R.id.menuRV);
        mRoot=view.findViewById(R.id.parentRelative);
        loadingView=LoadingView.getInstance();
        CategoryRV=view.findViewById(R.id.productCategoryRv);

        SwipeActions();

        AddNewItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),AddNewProductActivity.class));
            }
        });

        return view;
    }

    private void SwipeActions() {


         helper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                direction=viewHolder.getAdapterPosition();

                int position=viewHolder.getAdapterPosition();

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        adapter2.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    adapter2.startListening();
//                                    Toast.makeText(getContext(), "Deleted successfully.", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(mRoot,"Deleted.",Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                        break;

//                    case ItemTouchHelper.RIGHT:
////                        post_key=adapter.getRef(position).getKey();
//                        Intent intent = new Intent(getContext(),AddNewProductActivity.class);
//                        intent.putExtra("Post",post_key);
//                        intent.putExtra("PostKey",post);
//                        startActivity(intent);
//                        break;
                }


            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.quantum_vanillared700))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Swipe to Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

//                        .addSwipeRightActionIcon(R.drawable.ic_edit)
//                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.holo_green_dark))
//                        .addSwipeRightLabel("Swipe to Edit")
//                        .setSwipeRightLabelColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
//                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("Products");
        loadingView.ShowProgress(getContext(), "Loading your Products", true);

        CategoryFilterRv();
        PopulateNestedRecyclerView();
    }

    private void PopulateNestedRecyclerView() {


        adapter = new FirebaseRecyclerAdapter<SectionHeader, HeaderViewHolder>(options) {
            @NonNull
            @Override
            public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_headers, parent, false);
                return new HeaderViewHolder(v1);
            }

            @Override
            protected void onBindViewHolder(@NonNull final HeaderViewHolder holder, final int position, @NonNull final SectionHeader model) {

                loadingView.hideProgress();

                //just add the ExpansionLayout (with findViewById) to the expansionsCollection
//                expansionsCollection.add(holder.getExpansionLayout());

                holder.HeaderTv.setText(getRef(position).getKey());

                post_key=getRef(position).getKey();

                holder.expansionLayout.addListener(new ExpansionLayout.Listener() {
                    @Override
                    public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {

                    }
                });

                FirebaseRecyclerOptions<ProductListModelClass> options2 = new FirebaseRecyclerOptions.Builder<ProductListModelClass>()
                        .setQuery(reference.child(getRef(position).getKey()), ProductListModelClass.class)
                        .build();

                adapter2 = new FirebaseRecyclerAdapter<ProductListModelClass, MenuListViewHolder>(options2) {
                    @NonNull
                    @Override
                    public MenuListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v2 = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.menulist_recycler, parent, false);
                        return new MenuListViewHolder(v2);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final MenuListViewHolder productViewHolder, final int position, @NonNull final ProductListModelClass model) {

                        productViewHolder.ProductNameTv.setText(model.getProductName());

                        productViewHolder.PriceTv.setText(String.format("₹%s × %s %s", model.getPrice(), model.getSize(), model.getQtyIn()));

                        if (!model.getProductStatus().equals("Available")){
                            productViewHolder.StatusSwitch.setChecked(false);
                        }else {
                            productViewHolder.StatusSwitch.setChecked(true);
                        }


                        DatabaseReference ref=getRef(position);
                        final String post=ref.getKey();



                        productViewHolder.StatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                Log.d(TAG, "onCheckedChanged: ::::::::::::::::::::"+post+":::::::::::::"+post_key);

                                Map<String, Object> StockStatus = new HashMap<>();
                                if (isChecked){
                                    StockStatus.put("ProductStatus","Available");
                                }else {
                                    StockStatus.put("ProductStatus","Out of Stock");
                                }

                                FirebaseDatabase.getInstance().getReference("Stores").child(uid)
                                        .child("Products").child(model.CategoryType).child(model.getProductId()).updateChildren(StockStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });

                            }
                        });

                        if (model.getProductImage().isEmpty()||model.getProductImage()==null||model.getProductImage()==""){
                            Picasso.get().load(R.drawable.logotext).into(productViewHolder.ProductPreview);
                        }else{
                            Picasso.get().load(model.getProductImage()).networkPolicy(NetworkPolicy.OFFLINE).into(productViewHolder.ProductPreview, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(model.getProductImage()).into(productViewHolder.ProductPreview);
                                }
                            });
                        }



                        String vegMark=model.getVegMark();
                        if (vegMark==null){
                            productViewHolder.VegMarkIV.setVisibility(View.GONE);
                        }
                        else {
                            productViewHolder.VegMarkIV.setVisibility(View.VISIBLE);
                            if (!vegMark.equals("Veg")){
                                productViewHolder.VegMarkIV.setColorFilter(ContextCompat.getColor(getContext(),R.color.quantum_vanillared700),  android.graphics.PorterDuff.Mode.SRC_IN);
                            }
                        }

                        productViewHolder.CategoryTv.setText(model.getCategoryType());

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getContext(),AddNewProductActivity.class);
                                intent.putExtra("Post",model.getProductId());
                                intent.putExtra("PostKey",post_key);
                                startActivity(intent);
                            }
                        });

                    }

                };
                adapter2.notifyDataSetChanged();
                adapter2.startListening();
                holder.ProductsRecyclerView.setAdapter(adapter2);
                helper.attachToRecyclerView(holder.ProductsRecyclerView);
            }

        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


    }

    private void CategoryFilterRv() {


        options = new FirebaseRecyclerOptions.Builder<SectionHeader>()
                .setQuery(reference, SectionHeader.class)
                .build();

        catagoryAdapter=new FirebaseRecyclerAdapter<SectionHeader, CategoryMenuVH>(options) {
            @NonNull
            @Override
            public CategoryMenuVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v2 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.filter_menu, parent, false);
                return new CategoryMenuVH(v2);
            }

            @Override
            protected void onBindViewHolder(@NonNull final CategoryMenuVH holder, final int position, @NonNull final SectionHeader model) {

                loadingView.hideProgress();
                holder.CategoryNameTv.setText(getRef(position).getKey());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            recyclerView.smoothScrollToPosition(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });

            }


        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        CategoryRV.setLayoutManager(linearLayoutManager);
        catagoryAdapter.notifyDataSetChanged();
        catagoryAdapter.startListening();
        CategoryRV.setAdapter(catagoryAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }
}
