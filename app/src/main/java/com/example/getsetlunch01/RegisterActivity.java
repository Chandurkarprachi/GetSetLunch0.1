package com.example.getsetlunch01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
   private EditText etregistername,etregisterphone,etregisterpass;
    private Button btnRregister;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etregistername=findViewById(R.id.etregistername);
        etregisterphone=findViewById(R.id.etregisterphone);
        etregisterpass=findViewById(R.id.etregisterpass);
        btnRregister=findViewById(R.id.btnRregister);
        loadingbar=new ProgressDialog(this);

        btnRregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAcount();
            }
        });
    }




  private void  createAcount()
  {
       final String name=etregistername.getText().toString();
       final String phone=etregisterphone.getText().toString();
        final String password=etregisterpass.getText().toString();


        if(TextUtils.isEmpty(name))
        {
            etregistername.setError("Name Cannot Be Empty");
            etregistername.requestFocus();

        }
     else if(TextUtils.isEmpty(phone))
      {
          etregistername.setError("Phone Number Cannot Be Empty");
          etregisterphone.requestFocus();

      }
      else if(TextUtils.isEmpty(password))
      {
          etregistername.setError("Password Cannot Be Empty");
          etregisterpass.requestFocus();

      }
      else {
            loadingbar.setTitle("Creat account");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.setMessage("Checking your credintials Please Wait!");
            loadingbar.show();

            ValidatePhoneNumber(name,phone,password);
        }
  }

    private void ValidatePhoneNumber(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String,Object>userDataMap=new HashMap<>();

                    userDataMap.put("Phone",phone);
                    userDataMap.put("Name",name);
                    userDataMap.put("Password",password);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Account Created Succesfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(i);


                                    }
                                    else

                                    {
                                        Toast.makeText(RegisterActivity.this, "Network Issue", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }
                else {
                    Toast.makeText(RegisterActivity.this, "This Phone Number Alredy Exists\nPlease Try Again With Another Phone Number", Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                   // Toast.makeText(RegisterActivity.this, " ", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(i);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
