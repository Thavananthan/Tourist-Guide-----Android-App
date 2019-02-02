package com.example.nanthu.homeui;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName,userProfName,userStatus,userCountry,userGender,userDoB;
    private CircleImageView userProfImage;
    private Button friendRequsetButton,decilnefriendReustButton;
    private DatabaseReference   FriendRequestRef,UserRef,FriendRef;
    private FirebaseAuth mAuth;
    private String sentuserID,receverUserID,CURRENT_STATE, savecurrentdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);
        mAuth=FirebaseAuth.getInstance();

        sentuserID=mAuth.getCurrentUser().getUid();
        receverUserID=getIntent().getExtras().get("visit").toString();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef=FirebaseDatabase.getInstance().getReference().child("FriendsRequests");
        FriendRef=FirebaseDatabase.getInstance().getReference().child("Friends");

        UserRef.child(receverUserID).addValueEventListener(new ValueEventListener() {
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


                    Picasso.with(PersonProfileActivity.this).load( myProfImage).placeholder(R.drawable.images).into( userProfImage);
                    userName.setText("@"+myUsername);
                    userProfName.setText(myUserFullname);
                    userCountry.setText("Country: "+mycountry);
                    userDoB.setText("DOB: "+mydob);
                    userGender.setText("Gender: "+mygender);
                    userStatus.setText(mystatus);

                    MaintananceofButtons();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        IntializeFields();
        decilnefriendReustButton.setVisibility(View.INVISIBLE);
        decilnefriendReustButton.setEnabled(false);

        if(!sentuserID.equals(receverUserID)){
            friendRequsetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendRequsetButton.setEnabled(false);
                    if(CURRENT_STATE.equals("not_friends")){
                        SendFirendRequestToaPerson();
                    }
                    if(CURRENT_STATE.equals("request_send")){
                        CancelFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnfriendAnExitingFriend();
                    }
                }
            });

        }else{
            decilnefriendReustButton.setVisibility(View.INVISIBLE);
            friendRequsetButton.setVisibility(View.INVISIBLE);

        }

    }

    private void UnfriendAnExitingFriend() {
        FriendRef.child(sentuserID).child(receverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRef.child(receverUserID).child(sentuserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequsetButton.setEnabled(true);
                                CURRENT_STATE="not_friends";
                                friendRequsetButton.setText("Sent Friend Request");

                                decilnefriendReustButton.setVisibility(View.INVISIBLE);
                                decilnefriendReustButton.setEnabled(false);

                            }

                        }
                    });
                }
            }
        });


    }


    private void AcceptFriendRequest() {
        Calendar calForeDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yyyy");
        savecurrentdate=currentDate.format(calForeDate.getTime());

        FriendRef.child(sentuserID).child(receverUserID).child("date").setValue(savecurrentdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){


                    FriendRef.child(receverUserID).child(sentuserID ).child("date").setValue(savecurrentdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                FriendRequestRef.child(sentuserID).child(receverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FriendRequestRef.child(receverUserID).child(sentuserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        friendRequsetButton.setEnabled(true);
                                                        CURRENT_STATE="friends";
                                                        friendRequsetButton.setText("Unfriend this Person");

                                                        decilnefriendReustButton.setVisibility(View.INVISIBLE);
                                                        decilnefriendReustButton.setEnabled(false);

                                                    }

                                                }
                                            });
                                        }
                                    }
                                });

                            }

                        }
                    });
                }

            }
        });



    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(sentuserID).child(receverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRequestRef.child(receverUserID).child(sentuserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequsetButton.setEnabled(true);
                                CURRENT_STATE="not_friends";
                                friendRequsetButton.setText("Sent Friend Request");

                                decilnefriendReustButton.setVisibility(View.INVISIBLE);
                                decilnefriendReustButton.setEnabled(false);

                            }

                        }
                    });
                }
            }
        });

    }

    private void MaintananceofButtons() {
        FriendRequestRef.child(sentuserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receverUserID)){
                    String request_type=dataSnapshot.child(receverUserID).child("request_type").getValue().toString();

                    if(request_type.equals("sent")){
                        CURRENT_STATE="request_send";
                        friendRequsetButton.setText("Cancel Friend Request");
                        decilnefriendReustButton.setVisibility(View.INVISIBLE);
                        decilnefriendReustButton.setEnabled(false);
                        }
                        else if(request_type.equals("received")){
                          CURRENT_STATE="request_received";
                          friendRequsetButton.setText("Accept Friend Request");

                          decilnefriendReustButton.setVisibility(View.VISIBLE);
                          decilnefriendReustButton.setEnabled(true);

                          decilnefriendReustButton.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  CancelFriendRequest();
                              }
                          });



                    }
                }else{
                    FriendRef.child(sentuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receverUserID)){
                                CURRENT_STATE="friends";
                                friendRequsetButton.setText("Unfriend this Person");

                                decilnefriendReustButton.setVisibility(View.INVISIBLE);
                                decilnefriendReustButton.setEnabled(false);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendFirendRequestToaPerson() {
        FriendRequestRef.child(sentuserID).child(receverUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRequestRef.child(receverUserID).child(sentuserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequsetButton.setEnabled(true);
                                CURRENT_STATE="request_send";
                                friendRequsetButton.setText("Cancel Friend Request");

                                decilnefriendReustButton.setVisibility(View.INVISIBLE);
                                decilnefriendReustButton.setEnabled(false);

                            }

                        }
                    });
                }
            }
        });


    }

    private void IntializeFields() {
        userName=(TextView) findViewById(R.id.Person_username);
        userProfName=(TextView) findViewById(R.id.Person_profile_fullname);
        userCountry=(TextView) findViewById(R.id.Person_country);
        userDoB=(TextView) findViewById(R.id.Person_profile_dob);
        userGender=(TextView) findViewById(R.id.Person_profile_gender);
        userStatus=(TextView) findViewById(R.id.Person_profile_status);
        userProfImage=(CircleImageView)findViewById(R.id.Person_profile_pic);


        friendRequsetButton=(Button)findViewById(R.id.Person_friend_request_btn);
        decilnefriendReustButton=(Button)findViewById(R.id.Person_friend_request_cancle_btn);

        CURRENT_STATE="not_friends";


    }

}
