package com.example.nanthu.homeui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView  userName,userProfName,userStatus,userCountry,userGender,userDoB;
    private CircleImageView userProfImage;

    private DatabaseReference ProfileUserRef,FriendRef,PostsRef;
    private FirebaseAuth mAuth;
    private Button myposts,myFriends;

    private String currentuserId;
    private int countfriend=0,countPost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mAuth=FirebaseAuth.getInstance();
        currentuserId=mAuth.getCurrentUser().getUid();
        ProfileUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserId);
        FriendRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        PostsRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        userName=(TextView) findViewById(R.id.my_username);
        userProfName=(TextView) findViewById(R.id.my_profile_fullname);
        userCountry=(TextView) findViewById(R.id.my_country);
        userDoB=(TextView) findViewById(R.id.my_profile_dob);
        userGender=(TextView) findViewById(R.id.my_profile_gender);
        userStatus=(TextView) findViewById(R.id.my_profile_status);
        userProfImage=(CircleImageView)findViewById(R.id.my_profile_pic);
        myFriends=(Button)findViewById(R.id.my_friend_button_btn);
        myposts=(Button)findViewById(R.id.my_post_button_btn);

        myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewProfileActivity.this,Friendsctivity.class);
                startActivity(intent);
                Toast.makeText(ViewProfileActivity.this,"friends",Toast.LENGTH_LONG).show();
            }
        });

        myposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewProfileActivity.this,MypostsViewActivity.class);
                startActivity(intent);
                Toast.makeText(ViewProfileActivity.this,"My posts",Toast.LENGTH_LONG).show();
            }
        });


        ProfileUserRef.addValueEventListener(new ValueEventListener() {
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


                    Picasso.with(ViewProfileActivity.this).load( myProfImage).placeholder(R.drawable.images).into( userProfImage);
                    userName.setText("@"+myUsername);
                    userProfName.setText(myUserFullname);
                    userCountry.setText("Country: "+mycountry);
                    userDoB.setText("DOB: "+mydob);
                    userGender.setText("Gender: "+mygender);
                    userStatus.setText(mystatus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FriendRef.child(currentuserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    countfriend=(int)dataSnapshot.getChildrenCount();
                    myFriends.setText(Integer.toString(countfriend)+" Friends");
                }else{
                    myFriends.setText("0 Friends");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PostsRef.orderByChild("uid").startAt(currentuserId).endAt(currentuserId+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                     countPost=(int)dataSnapshot.getChildrenCount();
                     myposts.setText(Integer.toString(countPost)+"  MyPosts");
                }else{
                    myposts.setText("0 MyPosts");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar();
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.Profile);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(ViewProfileActivity.this, Home.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
