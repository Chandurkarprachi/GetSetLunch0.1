package com.example.getsetlunch01;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.getsetlunch01.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;
    private Button securityQuestionBtn;

    private Uri imageUri;
    private  String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private  String checker="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        profileImageView=findViewById(R.id.settings_profile_image);

        fullNameEditText=findViewById(R.id.settings_full_name);
        userPhoneEditText=findViewById(R.id.settings_phone_number);
        addressEditText=findViewById(R.id.settings_address);


        profileChangeTextBtn=findViewById(R.id.profile_image_change_btn);
        closeTextBtn=findViewById(R.id.close_settings_btn);
        saveTextButton=findViewById(R.id.update_account_settings_btn);
        securityQuestionBtn=findViewById(R.id.security_qtn);

        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEditText);




        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(SettingsActivity.this,HomeActivity.class);
                startActivity(i);
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();

                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker="clicked";

                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(SettingsActivity. this);

            }
        });

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i= new Intent(SettingsActivity.this,ResetPasswordActivity.class);
                i.putExtra("check","settings");
                startActivity(i);

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profileImageView.setImageURI(imageUri);

        }
        else {
            Toast.makeText(this, "Error Try Again Later", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }



    private void updateOnlyUserInfo()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap< String,Object>  userMap= new HashMap<>();
        userMap. put("Name", fullNameEditText.getText().toString());
        userMap. put("Address", addressEditText.getText().toString());
        userMap. put("phoneOrder", userPhoneEditText.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();


    }



    private void userInfoSaved()
    {
        if( fullNameEditText.length()==0)
        {
           fullNameEditText.setError("Name Cannot be empty");
           fullNameEditText.requestFocus();
        }
        else if(addressEditText.length()==0)
        {
            addressEditText.setError("Addres is Required");
            addressEditText.requestFocus();

        }
        else if(userPhoneEditText.length()==0)
        {
            userPhoneEditText.setError("Phone Number is Required!");
            userPhoneEditText.requestFocus();

        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }



    private void uploadImage()
    {

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Wait While We Are Uploaing Your Account Information ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri !=null)
        {
            final StorageReference fileRef =storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone()+".jpg");

            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(! task.isSuccessful())
                    {
                        throw task.getException();

                    }
                    return fileRef.getDownloadUrl();
                }
            })
             .addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task <Uri>task) {
                     if(task.isSuccessful())
                     {
                         Uri downLoadUrl=task.getResult();
                        myUrl=downLoadUrl.toString();
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");

                         HashMap< String,Object>  userMap= new HashMap<>();
                         userMap. put("Name", fullNameEditText.getText().toString());
                         userMap. put("Address", addressEditText.getText().toString());
                         userMap. put("phoneOrder", userPhoneEditText.getText().toString());
                         userMap. put("Image", myUrl);

                         ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                         progressDialog.dismiss();

                         startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                         Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                         finish();
                     }
                     else
                     {
                         progressDialog.dismiss();
                         Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                     }



                 }
             });
        }

        else {
            Toast.makeText(this, "Image Is not selected", Toast.LENGTH_SHORT).show();
        }
    }






    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, EditText addressEditText) {

        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if (dataSnapshot.child("Image").exists())
                    {
                        String Image=dataSnapshot.child("Image").getValue().toString();
                        String Name=dataSnapshot.child("Name").getValue().toString();
                        String Phone=dataSnapshot.child("Phone").getValue().toString();
                        String Address=dataSnapshot.child("Address").getValue().toString();

                        Picasso.get().load(Image).into(profileImageView);

                        fullNameEditText.setText(Name);
                        userPhoneEditText.setText(Phone);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
