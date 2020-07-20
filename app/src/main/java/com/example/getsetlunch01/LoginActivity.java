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
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsetlunch01.Admin.AdminCategoryActivity;
import com.example.getsetlunch01.Model.Users;
import com.example.getsetlunch01.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText etphone,etpassword;

   private Button btnLlogin;
    private ProgressDialog loadingbar;
    com.rey.material.widget.CheckBox  remember_me_chb;
    private String parent="Users";
   private TextView admin_link,not_admin,forgot_password_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etphone=findViewById(R.id.etphone);
        etpassword=findViewById(R.id.etpassword);
        remember_me_chb=findViewById(R.id.remember_me_chb);
        admin_link=findViewById(R.id.admin_link);
        not_admin=findViewById(R.id.not_admin);
        btnLlogin=findViewById(R.id.btnLlogin);
        forgot_password_link=findViewById(R.id.forgot_password_link);

        Paper.init(this);

        loadingbar=new ProgressDialog(this);
        btnLlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginUser();
            }

            });


        admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLlogin.setText("Admin Login");
                admin_link.setVisibility(View.INVISIBLE);
                not_admin.setVisibility(View.VISIBLE);
                //remember_me_chb.setVisibility(View.INVISIBLE);
                parent="Admins";
            }
        });
        not_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLlogin.setText("Login");
                admin_link.setVisibility(View.VISIBLE);
                not_admin.setVisibility(View.INVISIBLE);
                //remember_me_chb.setVisibility(View.VISIBLE);
                parent="Users";
            }
        });

        forgot_password_link.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent i= new Intent(LoginActivity.this,ResetPasswordActivity.class);
                i.putExtra("check","login");
                startActivity(i);

            }
        });
    }
    private void LoginUser()
    {
        final String phone=etphone.getText().toString();
        final String password=etpassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {

            etphone.setError("Phone Number Cannot Be Empty");
            etphone.requestFocus();

         }
        else if(TextUtils.isEmpty(password))
        {
            etpassword.setError("Password Cannot Be Empty");
            etpassword.requestFocus();
        }
        else {
            loadingbar.setTitle("Creat account");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.setMessage("Checking your credintials Please Wait!");
            loadingbar.show();

            AlloaAccessToAccount(phone,password);
        }



    }

    private void AlloaAccessToAccount(final String phone, final String password) {

        if(remember_me_chb.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);

        }
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parent).child(phone).exists())
                {
                    Users usersData=dataSnapshot.child(parent).child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(parent.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welcome Admin u Are Login Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent i=new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(i);
                                finish();

                            }
                            else if (parent.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(i);
                                finish();

                            }
                        }
                        else
                        {
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else
                    {
                        loadingbar.dismiss();
                        Toast.makeText(LoginActivity.this, "Account with this "+phone+" does not exists ",Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }







}
