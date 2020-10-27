package com.sng.bucbuc_partnerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewProductActivity extends AppCompatActivity {

    ImageView ImagePreview;
    EditText ProductNameEt,DescriptionEt,PriceET,QuantityEt;
    Button AddProductBTN;
    Spinner CategorySpinner,ProductSizeSpinner;
    TextView ProductCategoryTv;
    String uid;
    String product_img;
    View parentLayout;
    RadioGroup radioGroup;
    RadioButton VEgRb,NonVegRB;
    int selectedRb_id;
    String productId;
    TextView QtyTypeTv;
    String SelectedRb;
    String sizeSpinnerValue;

    Map<String, Object > ProductInfo = new HashMap<>();
    String[] items = new String[]{
            "Fruits & Vegetables",
            "Dairy, Eggs & Bakery",
            "Kitchen Staples",
            "Packaged Food & Snacks",
            "Instant & Ready to Eat",
            "Beverages" ,
            "Frozen Foods",
            "Meats",
            "Household Care",
            "Baby Care",
            "Personal Care",
            "Health & Wellness",
            "Other"};
    String vegMark;
    String sizeType;
    String[] sizes=new String[]{"kg","g","ml","L","piece"};

    private DatabaseReference databaseReference;
    private Uri imageUri;
    ImageView UpdateVegMark;
    private StorageReference storageReference;

    private static final int PERMISSION_FILE = 23;
    private static final int ACCESS_FILE = 43;
     String productName;
    String description;
    String price;
    String quantity;
    String categoryType;
     
     String Post,Ref;

     LoadingView loadingView=LoadingView.getInstance();

    private static final String TAG = "AddNewProductActivity";
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        findViewByID();


        ImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoosePreviewImage();
            }
        });


        AddProductBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProduct();
            }
        });


        CategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryType=CategorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ProductSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sizeSpinnerValue=ProductSizeSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void AddProduct() {

        productName=ProductNameEt.getText().toString();
        description=DescriptionEt.getText().toString();
        price=PriceET.getText().toString();
        quantity=QuantityEt.getText().toString();

        if (productName.isEmpty()){
            ProductNameEt.setError("Required.");
        }else if (price.isEmpty()){
            PriceET.setError("Required.");
        }else if (quantity.isEmpty()){
            QuantityEt.setError("Required.");
        }else if (categoryType.isEmpty()){
            Toast.makeText(this, "Select Category Type", Toast.LENGTH_SHORT).show();
        }
//        else if(imageUri==null){
//            Toast.makeText(this, "Select Product image", Toast.LENGTH_SHORT).show();   }
        else {
            if (imageUri != null) {
                loadingView.ShowProgress(AddNewProductActivity.this,"Uploading your Product",false);

                final StorageReference fileReference = storageReference.child(productName+"_"+quantity+ ".jpg");

                fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                Picasso.get().load(uri).into(ImagePreview);
                                final String url = uri.toString();

                                if (sizeSpinnerValue==null){
                                    sizeSpinnerValue=sizeType;
                                }

                                ProductInfo.put("QtyIn",sizeSpinnerValue);
                                ProductInfo.put("ProductImage", url);
                                ProductInfo.put("ProductName",productName);
                                if (description.isEmpty()){
                                    ProductInfo.put("Description","");
                                }else {
                                    ProductInfo.put("Description",description);
                                }

                                if (radioGroup.getCheckedRadioButtonId()!=-1) {
                                    selectedRb_id = radioGroup.getCheckedRadioButtonId();
                                    VEgRb = (RadioButton) findViewById(selectedRb_id);
                                    SelectedRb = VEgRb.getText().toString();

                                }else if (vegMark!=null){
                                    SelectedRb=vegMark;
                                }
                                else {
                                    SelectedRb=null;
                                }
                                productId=productName.concat("_"+quantity).replace(" ","");
                                ProductInfo.put("ProductId",productId);

                                ProductInfo.put("VegMark",SelectedRb);
                                ProductInfo.put("Price",price);
                                ProductInfo.put("Size",quantity);
                                ProductInfo.put("CategoryType",categoryType);
                                ProductInfo.put("ProductStatus","Available");

                                if(Post!=null){
                                    UpdateProduct();
                                }else{
                                    Add();
                                }

                                loadingView.hideProgress();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }else{

                if (sizeSpinnerValue==null){
                    sizeSpinnerValue=sizeType;
                }

                ProductInfo.put("QtyIn",sizeSpinnerValue);
                ProductInfo.put("ProductImage", "");
                ProductInfo.put("ProductName",productName);
                if (description.isEmpty()){
                    ProductInfo.put("Description","");
                }else {
                    ProductInfo.put("Description",description);
                }

                if (radioGroup.getCheckedRadioButtonId()!=-1) {
                    selectedRb_id = radioGroup.getCheckedRadioButtonId();
                    VEgRb = (RadioButton) findViewById(selectedRb_id);
                    SelectedRb = VEgRb.getText().toString();

                }else if (vegMark!=null){
                 SelectedRb=vegMark;
                }
                else {
                    SelectedRb=null;
                }
                productId=productName.concat("_"+quantity).replace(" ","").replace(".","");
                ProductInfo.put("ProductId",productId);

                ProductInfo.put("VegMark",SelectedRb);
                ProductInfo.put("Price",price);
                ProductInfo.put("Size",quantity);
                ProductInfo.put("CategoryType",categoryType);

                if(Post!=null){
                    UpdateProduct();
                }else{
                    Add();
                }

                loadingView.hideProgress();
            }
        }

    }

    private void UpdateProduct() {

        databaseReference.child(Ref).child(Post).updateChildren(ProductInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    finish();
                    Toast.makeText(AddNewProductActivity.this, "Updated Successfully.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddNewProductActivity.this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void Add() {
        databaseReference.child(categoryType).child(productId).setValue(ProductInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Snackbar.make(parentLayout,"Added Successfully.",Snackbar.LENGTH_LONG).show();
                    finish();
                }else{
                    Snackbar.make(parentLayout,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();

                    Toast.makeText(AddNewProductActivity.this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void ChoosePreviewImage() {
        if (ContextCompat.checkSelfPermission(AddNewProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewProductActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_FILE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "select Image"), ACCESS_FILE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCESS_FILE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setFixAspectRatio(true)
                    .start(AddNewProductActivity.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri=resultUri;
                ImagePreview.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void findViewByID() {

        ImagePreview=(ImageView)findViewById(R.id.imagePreview);
        ProductNameEt=(EditText)findViewById(R.id.productNameET);
        DescriptionEt=(EditText)findViewById(R.id.DescriptionET);
        PriceET=(EditText)findViewById(R.id.PriceET);
        QuantityEt=(EditText)findViewById(R.id.quantityET);
        AddProductBTN=(Button)findViewById(R.id.btnAddProduct);
        CategorySpinner=(Spinner)findViewById(R.id.typeSpinner);
        ProductCategoryTv=(TextView)findViewById(R.id.categoryTv);
         parentLayout = findViewById(android.R.id.content);
         radioGroup=findViewById(R.id.radioGroup);
         VEgRb=findViewById(R.id.vegRb);
         NonVegRB=findViewById(R.id.NonVegRB);
         UpdateVegMark=findViewById(R.id.img_veg_mark);
         ProductSizeSpinner=findViewById(R.id.sizeSpinner);
         QtyTypeTv=findViewById(R.id.qtyInTv);

         ArrayAdapter<String> sizeAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,sizes);
         ProductSizeSpinner.setAdapter(sizeAdapter);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        CategorySpinner.setAdapter(adapter);

        try {
            uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        databaseReference=FirebaseDatabase.getInstance().getReference("Stores").child(uid).child("Products");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProductBanners").child(uid);

        try {
            Intent intent=getIntent();
            Post=intent.getStringExtra("Post");
            Ref=intent.getStringExtra("PostKey");
            Log.d(TAG, "findViewByID: ::::::::::::::"+Post+"::::::::::::::::::"+Ref);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Post!=null){
            AddProductBTN.setText("Update Product");
            UpdateView();

        }
    }

    private void UpdateView() {
        databaseReference.child(Ref).child(Post).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    productName= String.valueOf(snapshot.child("ProductName").getValue());
                    ProductNameEt.setText(productName);

                    description= String.valueOf(snapshot.child("Description").getValue());
                    DescriptionEt.setText(description);

                    price= String.valueOf(snapshot.child("Price").getValue());
                    PriceET.setText(price);

                    quantity= String.valueOf(snapshot.child("Size").getValue());
                    sizeType=String.valueOf(snapshot.child("QtyIn").getValue());
                    QtyTypeTv.setVisibility(View.VISIBLE);
                    QtyTypeTv.setText(sizeType);

                    QuantityEt.setText(quantity);

                    product_img= String.valueOf(snapshot.child("ProductImage").getValue());
                    if (!product_img.isEmpty()){
                        Picasso.get().load(product_img).resize(100, 100).centerCrop().into(ImagePreview);
                    }

                    CategorySpinner.setVisibility(View.GONE);
                    ProductSizeSpinner.setVisibility(View.GONE);

                    categoryType= String.valueOf(snapshot.child("CategoryType").getValue());
                    ProductCategoryTv.setText(categoryType);

                    VEgRb.setVisibility(View.GONE);
                    NonVegRB.setVisibility(View.GONE);

                    UpdateVegMark.setVisibility(View.VISIBLE);

                     vegMark = String.valueOf(snapshot.child("VegMark").getValue());
                    SelectedRb=vegMark;
                    if (vegMark == null) {
                        UpdateVegMark.setVisibility(View.GONE);
                    } else {
                        UpdateVegMark.setVisibility(View.VISIBLE);
                        if (!vegMark.equals("Veg")) {
                            UpdateVegMark.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.quantum_vanillared700), android.graphics.PorterDuff.Mode.SRC_IN);
                        }
                    }

                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


}