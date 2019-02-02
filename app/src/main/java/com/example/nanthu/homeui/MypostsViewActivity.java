package com.example.nanthu.homeui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MypostsViewActivity extends AppCompatActivity {

    private RecyclerView myposts;
    private FirebaseAuth mAuth;
    private DatabaseReference PostsRef,UserRef,likeRef;
    private String current_userID;
    Boolean LikesChecker=false;
    private  static final String TAG="MypostsViewActivity";
    private static final int Error_Dialog_request=9001;
    Button btn1;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts_view);

        PostsRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth=FirebaseAuth.getInstance();
        current_userID=mAuth.getCurrentUser().getUid();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");


        myposts=(RecyclerView)findViewById(R.id.my_all_posts_view);
        myposts.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myposts.setLayoutManager(linearLayoutManager);

        DisplayMyallPosts();

        toolbar();
        if(isServicesOK()){
            init();
        }
    }

    private void DisplayMyallPosts()
    {
        Query Myquery=PostsRef.orderByChild("uid").startAt(current_userID).endAt(current_userID+"\uf8ff");


        FirebaseRecyclerAdapter<Upload,MyPostsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Upload, MyPostsViewHolder>
                (Upload.class,
                  R.layout.image_item,
                  MyPostsViewHolder.class,
                        Myquery
                ) {
            @Override
            protected void populateViewHolder(MyPostsViewHolder viewHolder, Upload model, int position) {

                final  String PostKey=getRef(position).getKey();

                viewHolder.setFullname(model.getFullname());
                viewHolder.setdescription(model.getdescription());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setPostImage(getApplicationContext(),model.getPostImage());
                viewHolder.setporfile(getApplicationContext(),model.getporfile());

                viewHolder.setLikeButtonStatus(PostKey);

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent postclickIntent=new Intent(MypostsViewActivity.this,ClickPostActivity.class);
                        postclickIntent.putExtra("postkey",PostKey);
                        startActivity(postclickIntent);
                    }
                });
                viewHolder.commentPostbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent=new Intent(MypostsViewActivity.this,CommentsActivity.class);
                        commentIntent.putExtra("postkey",PostKey);
                        startActivity(commentIntent);
                    }
                });

                viewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LikesChecker=true;

                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(LikesChecker.equals(true)){
                                    if(dataSnapshot.child(PostKey).hasChild(current_userID)){
                                        likeRef.child(PostKey).child(current_userID).removeValue();
                                        LikesChecker=false;
                                    }else{
                                        likeRef.child(PostKey).child(current_userID).setValue(true);
                                        LikesChecker=false;

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        myposts.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder{

        View mview;
        ImageButton LikePostButton,commentPostbutton;
        TextView noOflikes;
        int countLikes;
        String currentUserID;
        DatabaseReference LikeRef;

        public MyPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            LikePostButton=(ImageButton)mview.findViewById(R.id.likes_button);
            commentPostbutton=(ImageButton)mview.findViewById(R.id.comment__button);
            noOflikes=(TextView)mview.findViewById(R.id.display_no_of_likes);

            LikeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String PostKey){
            LikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentUserID)){

                        countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.like);
                        noOflikes.setText((Integer.toString(countLikes)+(" Likes")));

                    }else{
                        countLikes=(int)dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.dislike);
                        noOflikes.setText((Integer.toString(countLikes)+(" Likes")));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setFullname(String fullname){
            TextView username=(TextView)mview.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }
        public void setporfile(Context ctx, String porfile){
            CircleImageView userImage=(CircleImageView) mview.findViewById(R.id.post__profile_image);
            Picasso.with(ctx).load(porfile).into(userImage);
        }
        public void setDate(String date){
            TextView Post_date=(TextView)mview.findViewById(R.id.post_date);
            Post_date.setText(" "+date);
        }
        public void setTime(String time){
            TextView Post_time=(TextView)mview.findViewById(R.id.post_time);
            Post_time.setText(" " +time);

        }

        public void setdescription(String description){
            TextView Description=(TextView)mview.findViewById(R.id.post_description);
            Description.setText(description);

        }
        public void setPostImage(Context ctx,String PostImage){
            ImageView image=(ImageView)mview.findViewById(R.id.post_image);
            Picasso.with(ctx).load(PostImage).into(image);

        }
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.Posts);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(MypostsViewActivity.this, SettingActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("WrongViewCast")
    private void init(){
        btn1=(Button)findViewById(R.id.map);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MypostsViewActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean isServicesOK(){
        Log.d(TAG,"isServiesOK:check in google service version");
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MypostsViewActivity.this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServiesOK:check in google map working good");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServiesOK: Error");
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MypostsViewActivity.this,available,Error_Dialog_request);
            dialog.show();

        }else{
            Toast.makeText(this,"we can't make map request",Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
