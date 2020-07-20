package com.example.getsetlunch01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class PaymentOptionsAcivity extends AppCompatActivity {
    TextView txttitle;
    RadioGroup btnGroup;
    RadioButton rdCOD, rdUPI;
    Button btnPlaceOrder, btnNext;
    private String totalAmount="";
    private String pname="",Name,Phone,Address,City;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options_acivity);

        totalAmount=getIntent().getStringExtra("Total Price");
        pname=getIntent().getStringExtra("Pname");
        Name=getIntent().getStringExtra("Name");
        Phone=getIntent().getStringExtra("Phone");
        Address=getIntent().getStringExtra("Address");
        City=getIntent().getStringExtra("City");






        txttitle=findViewById(R.id.txttitle);
        btnGroup=findViewById(R.id.btnGroup);
        rdCOD=findViewById(R.id.rdCOD);
        rdUPI=findViewById(R.id.rdUPI);
         btnPlaceOrder=findViewById(R.id.btnPlaceOrder);
         btnNext=findViewById(R.id.btnNext);


         btnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
         {
             @Override
             public void onCheckedChanged(RadioGroup radioGroup, int i)
             {
                 if(rdCOD.isChecked())
                 {
                     btnPlaceOrder.setVisibility(View.VISIBLE);
                 }else
                 {
                     btnNext.setVisibility(View.VISIBLE);
                 }

             }
         });

    btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
    {
        ConfirmOrder();

    }
    });
    btnNext.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent i=new Intent(PaymentOptionsAcivity.this,UPIpaymentActivity.class);
            i.putExtra("Total Price",totalAmount);
            i.putExtra("Pname",pname);
            i.putExtra("Name",Name);
            i.putExtra("Phone",Phone);
            i.putExtra("Address",Address);
            i.putExtra("City",City);

            startActivity(i);

        }
    });



}


    private void ConfirmOrder()
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar calForDate=  Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());


        final DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> orderMap=new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("pname",pname);
        orderMap.put("name",Name);
        orderMap.put("phone",Phone);
        orderMap.put("address",Address);
        orderMap.put("city",City);
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("status","Not Shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(PaymentOptionsAcivity.this, "Order Placed Succsesfully", Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent( PaymentOptionsAcivity.this,HomeActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();

                                }
                                else

                                {
                                    Toast.makeText(PaymentOptionsAcivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });




            }
        });


    }



}
