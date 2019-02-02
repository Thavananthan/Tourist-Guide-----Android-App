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

import static com.example.nanthu.homeui.ProfileSetupActivity.GallaryPick;

public class profileActivity extends AppCompatActivity {

    private EditText userName,userProfName,userStatus,userCountry,userGender,userDoB;
    private Button accountUpdateButton;
    private CircleImageView userProfImage;

    private DatabaseReference SettinguserRef;
    private StorageReference UserImageProfileRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private String currentuserID;
    final static int GallaryPick=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadingBar=new ProgressDialog(this);



        mAuth=FirebaseAuth.getInstance();
        currentuserID=mAuth.getCurrentUser().getUid();
        SettinguserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);
        UserImageProfileRef= FirebaseStorage.getInstance().getReference().child("UserProfile");

        userName=(EditText)findViewById(R.id.setting_username);
        userProfName=(EditText)findViewById(R.id.setting_userfullname);
        userCountry=(EditText)findViewById(R.id.setting_country);
        userDoB=(EditText)findViewById(R.id.setting_dob);
        userGender=(EditText)findViewById(R.id.setting_gender);
        userStatus=(EditText)findViewById(R.id.setting_status);
        userProfImage=(CircleImageView)findViewById(R.id.settingProfileImage);

        accountUpdateButton=(Button)findViewById(R.id.update_account_setting_button);

        SettinguserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String myProfImage=dataSnapshot.child("ProfileImage").getValue().toString();
                    String myUsername=dataSnapshot.child("username").getValue().toString();
                    String myUserFullname=dataSnapshot.child("fullname").getValue().toString();
                    String mystatus=dataSnapshot.child("status").getValue().toString();
                    String mygender=dataSnapshot.child("gender").getValue().toString();
                    String mydob=dataSnapshot.child("dob").getValue().toString();
                    String mycountry=dataSnapshot.child("country").getValue().toString();

                    Picasso.with(profileActivity.this).load( myProfImage).placeholder(R.drawable.images).into( userProfImage);
                    userName.setText(myUsername);
                    userProfName.setText(myUserFullname);
                    userCountry.setText(mycountry);
                    userDoB.setText(mydob);
                    userGender.setText(mygender);
                    userStatus.setText(mystatus);





                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        accountUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInformation();
            }
        });


        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GallaryPick);
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
                loadingBar.setTitle("Your Profile");
                loadingBar.setMessage("please wait, while we are updating your Account...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resulituri=result.getUri();
                StorageReference filepath=UserImageProfileRef.child(currentuserID+".jpg");
                filepath.putFile(resulituri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(profileActivity.this,"Profile Image Stored success to firebase storage..",Toast.LENGTH_SHORT).show();
                            final String downloadUrl=task.getResult().getDownloadUrl().toString();
                            SettinguserRef.child("ProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent selfintent=new Intent(profileActivity.this,profileActivity.class);
                                        startActivity(selfintent);
                                        Toast.makeText(profileActivity.this,"Profile Image Stored success to firebase  databse ..",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }else{
                                        String message=task.getException().getMessage();
                                        Toast.makeText(profileActivity.this,"error :"+message,Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }
                                }
                            });


                        }
                    }
                });
            }else{
                Toast.makeText(profileActivity.this,"error :Image can be cropped.Try again",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }









    private void ValidateAccountInformation() {
        String username=userName.getText().toString();
        String status=userStatus.getText().toString();
        String fullname=userProfName.getText().toString();
        String dob=userDoB.getText().toString();
        String gender=userGender.getText().toString();
        String country=userCountry.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(profileActivity.this,"Check the username ",Toast.LENGTH_LONG).show();

        } else if(TextUtils.isEmpty(status)){
            Toast.makeText(profileActivity.this,"Check the status ",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(fullname)){
            Toast.makeText(profileActivity.this,"Check the fullname ",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(dob)){
            Toast.makeText(profileActivity.this,"Check the date of birth",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(gender)){
            Toast.makeText(profileActivity.this,"Check the gender ",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(country)){
            Toast.makeText(profileActivity.this,"Check the country ",Toast.LENGTH_LONG).show();

        }else{

            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("please wait, while we are updating your profile...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            updateAccountInfo(username,fullname,status,dob,gender,country);
        }





    }

    private void updateAccountInfo(String username, String fullname, String status, String dob, String gender, String country) {

        HashMap usermap=new HashMap();
        usermap.put("username",username);
        usermap.put("fullname",fullname);
        usermap.put("status",status);
        usermap.put("dob",dob);
        usermap.put("gender",gender);
        usermap.put("country",country);
        SettinguserRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(profileActivity.this,"Update Account Setting successfully... ",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                }
                else{
                    Toast.makeText(profileActivity.this,"Error:Occured ,while Updating  Account Setting  ",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }

            }
        });



    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(this,Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.Profile);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(profileActivity.this, SettingActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
