package com.example.nanthu.homeui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetupActivity extends AppCompatActivity {
    private Button btnsave;
    private EditText mUsername,mFullname,mCountry;
    private CircleImageView profileimage;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private StorageReference UserImageProfileRef;
    String current_userId;
    private ProgressDialog loadingBar;
    final static int GallaryPick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        mUsername=(EditText)findViewById(R.id.profile_setup_username);
        mFullname=(EditText)findViewById(R.id.profile_setup_fullname);
        mCountry=(EditText)findViewById(R.id.profileSetup_country);
        profileimage=(CircleImageView)findViewById(R.id.profile_setup_Image);
        btnsave=(Button)findViewById(R.id.btn_save);
        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        UserImageProfileRef= FirebaseStorage.getInstance().getReference().child("UserProfile");

        current_userId=mAuth.getCurrentUser().getUid();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_userId);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSetupInformatino();
            }
        });
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GallaryPick);
            }
        });
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("ProfileImage")){
                        String image=dataSnapshot.child("ProfileImage").getValue().toString();
                        Picasso.with(ProfileSetupActivity.this).load(image).placeholder(R.drawable.images).into( profileimage);
                    }else{
                        Toast.makeText(ProfileSetupActivity.this,"please select the Image frist..",Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GallaryPick&&resultCode==RESULT_OK&&data!=null){
            Uri imageuri=data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("please wait, while we are updating your profile...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resulituri=result.getUri();
                StorageReference filepath=UserImageProfileRef.child(current_userId+".jpg");
                filepath.putFile(resulituri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileSetupActivity.this,"Profile Image Stored success to firebase storage..",Toast.LENGTH_SHORT).show();
                            final String downloadUrl=task.getResult().getDownloadUrl().toString();
                            mUserRef.child("ProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent selfintent=new Intent(ProfileSetupActivity.this,ProfileSetupActivity.class);
                                        startActivity(selfintent);
                                        Toast.makeText(ProfileSetupActivity.this,"Profile Image Stored success to firebase  databse ..",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }else{
                                        String message=task.getException().getMessage();
                                        Toast.makeText(ProfileSetupActivity.this,"error :"+message,Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }
                                }
                            });


                        }
                    }
                });
            }else{
                Toast.makeText(ProfileSetupActivity.this,"error :Image can be cropped.Try again",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SaveSetupInformatino() {
        loadingBar.setTitle("Saving Information");
        loadingBar.setMessage("please wait, while we are saving your information");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);


        String username=mUsername.getText().toString();
        String Fullname=mFullname.getText().toString();
        String country=mCountry.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"please Enter the Username",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Fullname)){
            Toast.makeText(this,"please Enter the Fullname",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(country)){
            Toast.makeText(this,"please Enter the country name",Toast.LENGTH_SHORT).show();

        }else{
            HashMap userMap=new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",Fullname);
            userMap.put("country",country);
            userMap.put("status","I Love Travel");
            userMap.put("dob","none");
            userMap.put("gender","none");

            mUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){

                        Toast.makeText(ProfileSetupActivity.this,"Your account is create sucessfuly",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        sendLoginActivity();


                    }else{
                        String message=task.getException().getMessage();
                        Toast.makeText(ProfileSetupActivity.this,"error :"+message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                }
            });


        }
    }

    private void sendLoginActivity() {
        Intent intent=new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserTomainActivity() {
        Intent intent=new Intent(this,Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.ProfilesetUp);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(ProfileSetupActivity.this, Home.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
