package com.example.nanthu.homeui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView Posttext,descText;
    private Button editbtn,deletebtn;
    private String postKey,currentUserId,databaseUserId,Description,image,heading;
    private DatabaseReference clickpostRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        postKey=getIntent().getExtras().get("postkey").toString();
        clickpostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        PostImage=(ImageView)findViewById(R.id.click_post_image);
        Posttext=(TextView)findViewById(R.id.post_text);
        descText=(TextView)findViewById(R.id.post_text_dece);

        editbtn=(Button)findViewById(R.id.post_editbtn);
        deletebtn=(Button)findViewById(R.id.post_deletebtn);

        editbtn.setVisibility(View.INVISIBLE);
        deletebtn.setVisibility(View.INVISIBLE);

        clickpostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    Description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("PostImage").getValue().toString();
                    heading=dataSnapshot.child("heading").getValue().toString();


                    databaseUserId = dataSnapshot.child("uid").getValue().toString();
                    descText.setText(heading);
                    Posttext.setText(Description);

                    Picasso.with(ClickPostActivity.this).load(image).into(PostImage);


                    if (currentUserId.equals(databaseUserId)) {
                        editbtn.setVisibility(View.VISIBLE);
                        deletebtn.setVisibility(View.VISIBLE);

                        descText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditDescription(heading);
                            }
                        });


                    }

                    editbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditcurrentPost( Description);
                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletecurrentPost();
            }
        });
    }

    private void EditcurrentPost(String description) {
        AlertDialog.Builder builder=new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post :");

        final EditText inputField=new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clickpostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this,"Post has been Updated successful",Toast.LENGTH_LONG).show();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);
    }

    private void EditDescription(String heading){
        AlertDialog.Builder builder=new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Description :");

        final EditText inputField=new EditText(ClickPostActivity.this);
        inputField.setText(heading);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clickpostRef.child("heading").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this,"Post has been Updated successful",Toast.LENGTH_LONG).show();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_light);


    }

    private void DeletecurrentPost() {
        clickpostRef.removeValue();
        SendUserToMainActivity();
        Toast.makeText(this,"Post has been Deleted",Toast.LENGTH_LONG).show();
    }
    private void SendUserToMainActivity() {
        Intent intent=new Intent(this,Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
