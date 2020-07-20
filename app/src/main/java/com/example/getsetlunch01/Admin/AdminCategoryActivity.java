package com.example.getsetlunch01.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.getsetlunch01.HomeActivity;
import com.example.getsetlunch01.MainActivity;
import com.example.getsetlunch01.R;

public class AdminCategoryActivity extends AppCompatActivity {
    TextView txtthali,txtsalad,txtdezerts;
    ImageView thali,salad,dezerts;
    private Button LogoutBtn,checkOrdersBtn,maintainProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        txtthali=findViewById(R.id.txtthali);
        txtsalad=findViewById(R.id.txtsalad);
        txtdezerts=findViewById(R.id.txtdezerts);

        LogoutBtn=findViewById(R.id.admin_logout);
        checkOrdersBtn=findViewById(R.id.check_orders_btn);
        maintainProductsBtn=findViewById(R.id.maintain_btn);

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i= new Intent(AdminCategoryActivity.this, HomeActivity.class);
                i.putExtra("Admin","Admin");
                startActivity(i);

            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    Intent i= new Intent(AdminCategoryActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();

            }
        });
        checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i= new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(i);
            }
        });

        thali=findViewById(R.id.thali);
       salad=findViewById(R.id.salad);
        dezerts=findViewById(R.id.dezerts);


        thali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category","thali");
                startActivity(i);

            }
        });
        salad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","salad");
                startActivity(i);

            }
        });
        dezerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(AdminCategoryActivity.this,AdminAddNewProductActivity.class);
                i.putExtra("category","dezerts");
                startActivity(i);

            }
        });
    }
}
