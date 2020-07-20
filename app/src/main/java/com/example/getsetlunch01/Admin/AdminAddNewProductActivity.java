package com.example.getsetlunch01.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.getsetlunch01.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String categoryName,description,price,pname,saveCurrentDate,saveCurrentTime;
   private Button add_new_product;
  private   EditText product_name,product_description ,product_price;
   private ImageView select_product_image;
   private static final  int GalleryPick=1;
   private Uri ImageUri;
   private  String ProductRamdomKey,downloadImageUrl;
   //private StorageReference
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        categoryName=getIntent().getExtras().get("category").toString();
       // Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();
        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("ProductImages");
        ProductsRef=FirebaseDatabase.getInstance().getReference().child("Products");


        add_new_product=findViewById(R.id.add_new_product);
        product_name=findViewById(R.id.product_name);
        product_description=findViewById(R.id.product_description);
        product_price=findViewById(R.id.product_price);
        select_product_image=findViewById(R.id.select_product_image);
        loadingBar=new ProgressDialog(this);


        select_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });


        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

    }
  private void OpenGallery()
  {
      Intent galleryIntent=new Intent();
      galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
      galleryIntent.setType("image/*");
      startActivityForResult(galleryIntent,GalleryPick);


  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            select_product_image.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData()
    {
        description=product_description.getText().toString();
        price=product_price.getText().toString();
        pname=product_name.getText().toString();


        if(ImageUri==null)
        {
            Toast.makeText(this, "Please select product image", Toast.LENGTH_SHORT).show();
        }
        else if(description.length()==0)
        {
            product_description.setError("Description is Required");
            product_description.requestFocus();

        }
        else if(pname.length()==0)
        {
            product_name.setError("Name is required");
            product_name.requestFocus();

        }

        else if(price.length()==0)
        {
            product_price.setError("Price is required");
            product_price.requestFocus();

        }
        else {
            StoreProductInformation();
        }

    }




    private void StoreProductInformation() {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        ProductRamdomKey= saveCurrentDate +  saveCurrentTime;

       // final StorageReference filepath=ProductImagesRef.child(ImageUri.getLastPathSegment() +ProductRamdomKey);

        //final UploadTask uploadTask=filepath.putFile(ImageUri);

        final StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment() + ProductRamdomKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(ImageUri);





        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image Aded Succesfully", Toast.LENGTH_SHORT).show();


                    Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                            if(!task.isSuccessful())
                            {
                                throw task.getException();

                            }
                            downloadImageUrl=filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {

                                downloadImageUrl=task.getResult().toString();
                                Toast.makeText(AdminAddNewProductActivity.this, "Product Image Added To Database !", Toast.LENGTH_SHORT).show();
                                SaveProductInfoToDatabase();
                            }

                        }
                    });
            }
        });
    }





    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", ProductRamdomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", pname);

        ProductsRef.child(ProductRamdomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent i=new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(i);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product Added Succesfully", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            loadingBar.dismiss();
                            String message= task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }
}
