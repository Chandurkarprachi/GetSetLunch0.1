package com.example.getsetlunch01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsetlunch01.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
private Button ProceedToPay;
private TextView txtPname;

private String totalAmount="";
String pname="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);


        totalAmount=getIntent().getStringExtra("Total Price");
        pname=getIntent().getStringExtra("pname");

        ProceedToPay=findViewById(R.id.confirm_order_btn);
        nameEditText=findViewById(R.id.shippment_name);
        phoneEditText=findViewById(R.id.shippment_phone);
        addressEditText=findViewById(R.id.shippment_address);
        cityEditText=findViewById(R.id.shippment_city);
        txtPname=findViewById(R.id.txt1);


        ProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
        txtPname.setText("Dear Cutomer You are placing order for "+pname);

    }

    private void Check()
    {
        if(nameEditText.length()==0)
        {
            nameEditText.setError("Enter Name.");
            nameEditText.requestFocus();

        }
        else if(phoneEditText.length()==0)
        {
            phoneEditText.setError("Enter Phone Number");
            phoneEditText.requestFocus();

        }
        else if(addressEditText.length()==0)
        {
            addressEditText.setError("Enter Address");
            addressEditText.requestFocus();

        }
        else if(cityEditText.length()==0)
        {
            cityEditText.setError("Enter City");
            cityEditText.requestFocus();
        }
        else{
            Intent i= new Intent(ConfirmOrderActivity.this,PaymentOptionsAcivity.class);
            i.putExtra("Total Price",totalAmount);
            i.putExtra("Pname",pname);
            i.putExtra("Name",nameEditText.getText().toString());
            i.putExtra("Phone",phoneEditText.getText().toString());
            i.putExtra("Address",addressEditText.getText().toString());
            i.putExtra("City",cityEditText.getText().toString());
            startActivity(i);

        }

    }


}
