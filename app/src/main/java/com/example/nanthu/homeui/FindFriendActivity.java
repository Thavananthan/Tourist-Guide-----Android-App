package com.example.nanthu.homeui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    private ImageButton searchbtn;
    private EditText searchFindtext;
    private RecyclerView searchResultlist;

    private DatabaseReference allUsersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);


        allUsersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");

        searchResultlist=(RecyclerView)findViewById(R.id.search_result_list);
        searchResultlist.setHasFixedSize(true);
        searchResultlist.setLayoutManager(new LinearLayoutManager(this));

        searchbtn=(ImageButton) findViewById(R.id.search_people_friends_button);
        searchFindtext=(EditText)findViewById(R.id.serach_box_input);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchBoxInput=searchFindtext.getText().toString();
                SearchPeopleandFriend(searchBoxInput);
            }
        });






        toolbar();
    }

    private void SearchPeopleandFriend(String searchBoxInput) {

        Toast.makeText(this,"Searching......",Toast.LENGTH_SHORT).show();

        Query searchPeopleandFriendQuery= allUsersDatabaseRef.orderByChild("fullname").startAt(searchBoxInput).endAt(searchBoxInput+"\uf8ff");
        FirebaseRecyclerAdapter<FindFriend,FindFriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindFriend, FindFriendsViewHolder>
                (
                        FindFriend.class,
                        R.layout.all_user_layout,
                        FindFriendsViewHolder.class,
                        searchPeopleandFriendQuery


                ) {
            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriend model, final int position) {
                  viewHolder.setFullname(model.getFullname());
                  viewHolder.setStatus(model.getStatus());
                  viewHolder.setProfileImage(getApplicationContext(),model.getProfileImage());
                  viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          String vist_userId=getRef(position).getKey();
                          Intent profileintent=new Intent(FindFriendActivity.this,PersonProfileActivity.class);
                          profileintent.putExtra("visit",vist_userId);
                          startActivity(profileintent);
                      }
                  });

            }
        };

        searchResultlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setProfileImage(Context ctx,String profileImage){
            CircleImageView myImage=(CircleImageView)mView.findViewById(R.id.all_user_profile_images);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.images).into(myImage);
            }
        public void setFullname(String fullname){
            TextView myname=(TextView)mView.findViewById(R.id.all_user_profile_full_name);
            myname.setText(fullname);
            }
        public void setStatus(String status){
            TextView mystatus=(TextView)mView.findViewById(R.id.all_user_profile_status);
            mystatus.setText(status);
        }

    }


    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.findfriend);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(FindFriendActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
