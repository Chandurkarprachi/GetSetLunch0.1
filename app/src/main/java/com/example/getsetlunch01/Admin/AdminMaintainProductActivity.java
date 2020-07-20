package com.example.getsetlunch01.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.getsetlunch01.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    private Button applyChangesBtn,deleteBtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productID="";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);



        productID=getIntent().getStringExtra("pid");
        productsRef= FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applyChangesBtn=findViewById(R.id.apply_changes_btn);
        name=findViewById(R.id.product_name_maintain);
        deleteBtn=findViewById(R.id.delete_product_btn);
        price=findViewById(R.id.product_price_maintain);
        description=findViewById(R.id.product_description_maintain);
        imageView=findViewById(R.id.product_image_maintain);

        displayProductInfo();


        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view)
            {
              applyChanges();

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CharSequence options[]=new CharSequence[]
                        {
                                "Yes",
                                "No"
                        };

                AlertDialog.Builder builder=new AlertDialog.Builder(AdminMaintainProductActivity.this);
                builder.setTitle("Are u sure want to delete product ?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if(i==0)
                        {
                            deleteProduct();
                        }else
                        {
                           // Intent intent= new Intent(AdminMaintainProductActivity.this,AdminMaintainProductActivity.class);
                            //startActivity(intent);

                        }

                    }
                });
                builder.show();
            }
        });

    }

    private void deleteProduct()
    {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Intent i= new Intent(AdminMaintainProductActivity.this, AdminCategoryActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(AdminMaintainProductActivity.this, "Product Deleted Succesfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChanges()
    {
        String pName=name.getText().toString();
        String pPrice=price.getText().toString();
        String pDescription=description.getText().toString();

        if(pName.length()==0)
        {
            name.setError("Name Cannot be empty.");
            name.requestFocus();
        }

       else if(pPrice.length()==0)
        {
            price.setError("Price Cannot be empty.");
            price.requestFocus();
        }
       else if(pDescription.length()==0)
        {
            description.setError("Description Cannot be empty.");
            description.requestFocus();
        }
       else
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                   if(task.isSuccessful())
                   {
                       Toast.makeText(AdminMaintainProductActivity.this, "Changes Apply Successfully", Toast.LENGTH_SHORT).show();
                       Intent i= new Intent(AdminMaintainProductActivity.this,AdminCategoryActivity.class);
                       startActivity(i);
                       finish();
                   }

                }
            });
        }
    }

    private void displayProductInfo()

    {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String pName=dataSnapshot.child("pname").getValue().toString();
                    String pPrice=dataSnapshot.child("price").getValue().toString();
                    String pDescription=dataSnapshot.child("description").getValue().toString();
                    String pImage=dataSnapshot.child("image").getValue().toString();
                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
