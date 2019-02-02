package com.example.nanthu.homeui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Search extends AppCompatActivity {

    private ImageButton searchbtn;
    private EditText searchFindtext;
    private RecyclerView searchResultlist;

    private DatabaseReference allUsersDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");
        allUsersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        searchResultlist=(RecyclerView)findViewById(R.id.search_result_list_location);
        searchResultlist.setHasFixedSize(true);
        searchResultlist.setLayoutManager(new LinearLayoutManager(this));

        searchbtn=(ImageButton) findViewById(R.id.search_location_button);
        searchFindtext=(EditText)findViewById(R.id.serach_box_input_location);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput=searchFindtext.getText().toString();
                SearchPeopleandFriend(searchBoxInput);
            }
        });



    }

    private void SearchPeopleandFriend(String searchBoxInput) {

        Toast.makeText(this,"Searching......",Toast.LENGTH_SHORT).show();

        Query searchPeopleandFriendQuery= allUsersDatabaseRef.orderByChild("description").startAt(searchBoxInput).endAt(searchBoxInput+"\uf8ff");
        FirebaseRecyclerAdapter<  FindLocation,  Search.FindFriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindLocation,Search.FindFriendsViewHolder>
                (
                        FindLocation.class,
                        R.layout.row,
                        Search.FindFriendsViewHolder.class,
                        searchPeopleandFriendQuery


                ){
            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindLocation model, final int position) {

                viewHolder.setDescription(model.getDescription());
                viewHolder.setPostImage(getApplicationContext(),model.getPostImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String vist_userId=getRef(position).getKey();

                        Intent profileintent=new Intent(Search.this,ClickPostActivity.class);
                        profileintent.putExtra("postkey",vist_userId);
                        startActivity(profileintent);
                    }
                });
            }

       /*     @Override
            protected void populateViewHolder(FindFriendActivity.FindFriendsViewHolder viewHolder, FindFriend model, final int position) {
                viewHolder.setFullname(model.getFullname());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileImage(getApplicationContext(),model.getProfileImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String vist_userId=getRef(position).getKey();
                        Intent profileintent=new Intent(Search.this,PersonProfileActivity.class);
                        profileintent.putExtra("visit",vist_userId);
                        startActivity(profileintent);
                    }
                });

            }*/
        };

        searchResultlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setPostImage(Context ctx,String postImage){
            CircleImageView myImage=(CircleImageView)mView.findViewById(R.id.all_user_location_images);
            Picasso.with(ctx).load(postImage).placeholder(R.drawable.images).into(myImage);
        }
        public void setDescription(String description){
            TextView myname=(TextView)mView.findViewById(R.id.all_user_profile_location_name);
            myname.setText(description);
        }


    }


}



