package com.example.nanthu.homeui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ImageUpload extends AppCompatActivity {
    private  static final int PICK_IMAGE_REQUEST=1;

    private ImageView mButtonChooseImage;
    private Button mButtonUploadImage;
     private TextView mTextViewShowUpload;
    private EditText mEditTextFilename;
    private ProgressDialog loadingbar;
    private long countPost=0;

  //  private ProgressBar mProgressbar;
    private EditText mTextViewDecs;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef,postsRef;
    private FirebaseAuth mAuth;

    String   Descreption,Heading,current_user_id;
    String savecurrentdate,savecurrentTime,postRandomName,downloadUrl;

    private StorageTask mUploadtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();

        mButtonChooseImage=(ImageView) findViewById(R.id.Choose_file);
        mButtonUploadImage=findViewById(R.id.button_upload);
        mTextViewShowUpload=findViewById(R.id.text_view_show_upload);
        mEditTextFilename=findViewById(R.id.edit_text_file_name);

       // mProgressbar=findViewById(R.id.progress_bar);
        loadingbar=new ProgressDialog(this);
        mTextViewDecs=findViewById(R.id.DeceText);


        mStorageRef= FirebaseStorage.getInstance().getReference();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Users");
        postsRef= FirebaseDatabase.getInstance().getReference("Posts");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

        mButtonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadtask!=null && mUploadtask.isInProgress()){
                    Toast.makeText(ImageUpload.this,"Upload in progress",Toast.LENGTH_LONG).show();

                }else{
                uploadfile();
                }

            }
        });
        mTextViewShowUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK &&data!=null&&data.getData()!=null ){
            mImageUri=data.getData();
            //Picasso.with(this).load(mImageUri).into(mImageview);
           // mImageview.setImageURI(mImageUri);
            mButtonChooseImage.setImageURI(mImageUri);
        }

    }

  /*  private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }*/

    private void uploadfile(){

         Descreption=mEditTextFilename.getText().toString();
         Heading=mTextViewDecs.getText().toString();

         if(mImageUri==null){
             Toast.makeText(this,"Please add images..",Toast.LENGTH_SHORT).show();
         }
       else if(TextUtils.isEmpty(Descreption)){
            Toast.makeText(this,"Enter the Descreption..",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Heading)){
            Toast.makeText(this,"Enter the Heading..",Toast.LENGTH_SHORT).show();
        }
        else{
             loadingbar.setTitle("Add New Post ");
             loadingbar.setMessage("please wait, while we are updating  your post...");
             loadingbar.show();
             loadingbar.setCanceledOnTouchOutside(true);
             StroingImageFirebase();
         }

       /* if(mImageUri!=null){
            StorageReference fileReference=mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
          mUploadtask=fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressbar.setProgress(0);
                        }
                    },500);
                    Toast.makeText(ImageUpload.this,"Upload sucessful",Toast.LENGTH_LONG).show();
                    Upload upload=new Upload(mEditTextFilename.getText().toString(),taskSnapshot.getDownloadUrl().toString(),mTextViewDecs.getText().toString());

                    String uploadId=mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageUpload.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progrees=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mProgressbar.setProgress((int)progrees);
                }
            });


        }else{
            Toast.makeText(this,"NO FILE SELECTED",Toast.LENGTH_LONG).show();
        }
            */
    }

    private void StroingImageFirebase() {
        Calendar calForeDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yyyy");
        savecurrentdate=currentDate.format(calForeDate.getTime());

        Calendar calForeTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        savecurrentTime=currentTime.format(calForeDate.getTime());
        postRandomName=savecurrentdate + savecurrentTime;


        StorageReference filePath=mStorageRef.child("PostImage").child(mImageUri.getLastPathSegment()+ postRandomName +".jpg");
        filePath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    downloadUrl=task.getResult().getDownloadUrl().toString();
                    Toast.makeText(ImageUpload.this, "Uploaded successfully", Toast.LENGTH_LONG).show();
                    savingPostInformationtoDatabase();
                }else{
                    String message=task.getException().getMessage();
                    Toast.makeText(ImageUpload.this, "error occured:"+message, Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void savingPostInformationtoDatabase() {

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   countPost=dataSnapshot.getChildrenCount();
               }else{
                   countPost=0;
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userfullname=dataSnapshot.child("fullname").getValue().toString();
                    String userprofileImage=dataSnapshot.child("ProfileImage").getValue().toString();

                    HashMap Postmap=new HashMap();
                    Postmap.put("uid",current_user_id);
                    Postmap.put("date",savecurrentdate);
                    Postmap.put("time",savecurrentTime);
                    Postmap.put("description",Descreption);
                    Postmap.put("heading",Heading);
                    Postmap.put("PostImage",downloadUrl);
                    Postmap.put("porfile",userprofileImage);
                    Postmap.put("fullname",userfullname);
                    Postmap.put("counter",countPost);
                    postsRef.child( current_user_id + postRandomName ).updateChildren(Postmap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                usersendMainactivity();
                                Toast.makeText(ImageUpload.this, "Post is Update successfully", Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();;

                            }else{
                                Toast.makeText(ImageUpload.this, "Error occured while updating your post", Toast.LENGTH_LONG).show();
                                loadingbar.dismiss();
                            }

                        }
                    });



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void usersendMainactivity(){

        Intent intent=new Intent(this,Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
       /* Viewpage v1=new Viewpage();
        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.main ,v1).commit();
        Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();*/
    }
}
