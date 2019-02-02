package com.example.nanthu.homeui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friendsctivity extends AppCompatActivity {

    private RecyclerView my_friend_list;
    private DatabaseReference FriendRef,UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsctivity);

        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        FriendRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        my_friend_list=(RecyclerView)findViewById(R.id.friends_list);
        my_friend_list.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        my_friend_list.setLayoutManager(linearLayoutManager);

        DisplayAllFriend();
        }

    private void DisplayAllFriend() {

        FirebaseRecyclerAdapter<Friends,friendsViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Friends, friendsViewHolder>
                (
                        Friends.class,
                        R.layout.all_user_layout,
                        friendsViewHolder.class,
                        FriendRef
                ) {
            @Override
            protected void populateViewHolder(final friendsViewHolder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());
                final String usersIDs=getRef(position).getKey();
                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final String userName=dataSnapshot.child("fullname").getValue().toString();
                            final String ProfileImage=dataSnapshot.child("ProfileImage").getValue().toString();
                            final String type;

                            if(dataSnapshot.hasChild("UserState")){
                                type=dataSnapshot.child("UserState").child("Type").getValue().toString();

                                if(type.equals("online")){
                                    viewHolder.onlineStatusView.setVisibility(View.VISIBLE);
                                }else{
                                    viewHolder.onlineStatusView.setVisibility(View.INVISIBLE);
                                }
                            }

                            viewHolder.setFullname(userName);
                            viewHolder.setProfileImage(getApplicationContext(),ProfileImage);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence option[]=new CharSequence[]{userName+"'s Profile","Send Message" };
                                    AlertDialog.Builder builder=new AlertDialog.Builder(Friendsctivity.this);
                                    builder.setTitle("Select Option");

                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0){
                                                Intent profileIntent=new Intent(Friendsctivity.this,PersonProfileActivity.class);
                                                profileIntent.putExtra("visit",usersIDs);
                                                startActivity(profileIntent);

                                            }
                                            if(which==1){
                                                Intent chatntent=new Intent(Friendsctivity.this,ChatActivity.class);
                                                chatntent.putExtra("visit",usersIDs);
                                                chatntent.putExtra("userName",userName);
                                                startActivity(chatntent);
                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            });



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        };

        my_friend_list.setAdapter(firebaseRecyclerAdapter);
    }






    public void UpdateUserStatus(String state){

        String SaveCurretdate,SaveCurrentTime;

        Calendar calForeDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yyyy");
        SaveCurretdate=currentDate.format(calForeDate.getTime());

        Calendar calForetime=Calendar.getInstance();
        SimpleDateFormat currenttime=new SimpleDateFormat("hh:mm a");
        SaveCurrentTime=currenttime.format( calForetime.getTime());


        Map currentstatus=new HashMap();
        currentstatus.put("time",SaveCurrentTime);
        currentstatus.put("Date",SaveCurretdate);
        currentstatus.put("Type",state);

        UsersRef.child(online_user_id).child("UserState").updateChildren(currentstatus);


    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateUserStatus("online");
    }




    public static class friendsViewHolder extends RecyclerView.ViewHolder{

            View mView;
            ImageView onlineStatusView;
      public friendsViewHolder(@NonNull View itemView) {
          super(itemView);
          mView=itemView;
          onlineStatusView=(ImageView)itemView.findViewById(R.id.all_user_online);
      }

        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView myImage=(CircleImageView)mView.findViewById(R.id.all_user_profile_images);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.images).into(myImage);
        }
        public void setFullname(String fullname){
            TextView myname=(TextView)mView.findViewById(R.id.all_user_profile_full_name);
            myname.setText(fullname);
        }
        public void setDate(String date){
            TextView mydate=(TextView)mView.findViewById(R.id.all_user_profile_status);
            mydate.setText("friends Since: "+date);
        }
  }
}
