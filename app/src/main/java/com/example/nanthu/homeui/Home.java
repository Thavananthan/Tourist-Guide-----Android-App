package com.example.nanthu.homeui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

private FirebaseAuth mAuth;
private DatabaseReference UserRef,PostsRef,likeRef;
private StorageReference PostsImageRef;
private CircleImageView navImageView;
private TextView navProfileUsername;
private ImageButton addimagebutton;
String currentuserId;

Boolean LikesChecker=false;

private RecyclerView postlist;
    private  static final String TAG="MapActivity";
    private static final int Error_Dialog_request=9001;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PostsImageRef= FirebaseStorage.getInstance().getReference();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentuserId=mAuth.getCurrentUser().getUid();
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        PostsRef=FirebaseDatabase.getInstance().getReference().child("Posts");








        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        postlist=(RecyclerView)findViewById(R.id.recycler_view);
        postlist.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);



        navImageView=(CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.nav_profileImage);

        navProfileUsername=(TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_full_name);

        UserRef.child(currentuserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("fullname")){
                        String  Fullname=dataSnapshot.child("fullname").getValue().toString();
                        navProfileUsername.setText(Fullname);
                        }
                     if(dataSnapshot.hasChild("ProfileImage"))  {
                         String  image=dataSnapshot.child("ProfileImage").getValue().toString();

                         Picasso.with(Home.this).load(image).placeholder(R.drawable.images).into(navImageView);
                     }else{
                        Toast.makeText(Home.this,"Profile name do not exists..",Toast.LENGTH_SHORT).show();
                        // SendUserToLoginActivity();
                         //sendUsersetupProfile();


                     }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addimagebutton=(ImageButton)findViewById(R.id.newPost);
        addimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home.this,ImageUpload.class);
                startActivity(intent);
                Toast.makeText(Home.this,"Upload Image",Toast.LENGTH_LONG).show();
            }
        });

     /* ViewPage vp= new ViewPage();
      FragmentManager fm=getSupportFragmentManager();
      fm.beginTransaction().replace(R.id.main,vp).commit();*/

        DisplayAllUsersPosts();




    }

    private void DisplayAllUsersPosts() {

        Query sortPostIndesendingOrder=PostsRef.orderByChild("counter");



        FirebaseRecyclerAdapter<Upload,PostsViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Upload, PostsViewHolder>
                (Upload.class,
                  R.layout.image_item,
                    PostsViewHolder.class,
                        sortPostIndesendingOrder
                ) {
            @Override
            protected void populateViewHolder(PostsViewHolder viewHolder, Upload model, int position) {

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
                            Intent postclickIntent=new Intent(Home.this,ClickPostActivity.class);
                            postclickIntent.putExtra("postkey",PostKey);
                            startActivity(postclickIntent);
                        }
                    });
                    viewHolder.commentPostbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent commentIntent=new Intent(Home.this,CommentsActivity.class);
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
                                        if(dataSnapshot.child(PostKey).hasChild(currentuserId)){
                                            likeRef.child(PostKey).child(currentuserId).removeValue();
                                            LikesChecker=false;
                                        }else{
                                            likeRef.child(PostKey).child(currentuserId).setValue(true);
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
        postlist.setAdapter(firebaseRecyclerAdapter);

        UpdateUserStatus("offline");
        }



    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        ImageButton LikePostButton,commentPostbutton;
        TextView noOflikes;
        int countLikes;
        String currentUserID;
        DatabaseReference LikeRef;

        public PostsViewHolder(@NonNull View itemView) {
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
        public void setporfile(Context ctx,String porfile){
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){

            SendUserToLoginActivity();

        }
        else{

            checkUserExistence();
        }
    }
    private void sendUsersetupProfile() {
        Intent intentlogin=new Intent(this,ProfileSetupActivity.class);
        intentlogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentlogin);
        finish();
    }
   private void checkUserExistence() {
        final String Current_User_ID=mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(Current_User_ID)){
                    sendUsersetupProfile();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void SendUserToLoginActivity() {
        Intent intentlogin=new Intent(this,LoginActivity.class);
        intentlogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentlogin);
        finish();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        UserRef.child(currentuserId).child("UserState").updateChildren(currentstatus);


    }


  //  public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //   getMenuInflater().inflate(R.menu.activity_home_drawer, menu);
       // MenuItem item = menu.findItem(R.id.action_search);
        //s= (SearchView) MenuItemCompat.getActionView(item);
        //s.setQueryHint(getString(R.string.action_search));
       /* s.setOnCloseListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/

      //  return true;
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fm;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            UpdateUserStatus("offline");
           /* fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.main , new ViewPage()).commit();
            getSupportActionBar().setTitle("TourGuid");*/
            Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
        }else if(id==R.id.nav_profile){
            Intent intent=new Intent(Home.this,ViewProfileActivity.class);
            startActivity(intent);
            Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();

        } else if(id==R.id.nav_findfriend){
            Intent intent=new Intent(this,FindFriendActivity.class);
            startActivity(intent);
            Toast.makeText(this,"Findfriend",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_search) {

           Intent intent=new Intent(this,Search.class);
            startActivity(intent);
            Toast.makeText(this,"search",Toast.LENGTH_LONG).show();

            /* fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.main , new Camfrag()).commit();
            getSupportActionBar().setTitle("SERACH VIEW");
            Toast.makeText(this,"search",Toast.LENGTH_LONG).show();*/


        } else if (id == R.id.nav_settting) {
           Intent intent=new Intent(Home.this,SettingActivity.class);
           startActivity(intent);
            Toast.makeText(this,"setting",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_location) {
            fm=getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.main , new Location()).commit();
            Toast.makeText(this,"map",Toast.LENGTH_LONG).show();


        }else if (id == R.id.nav_about) {
           Intent intent=new Intent(this,AboutUs_Activity.class);
           startActivity(intent);
            Toast.makeText(this,"About us ",Toast.LENGTH_LONG).show();

            }else if (id == R.id.nav_fd) {
            Intent intent=new Intent(this,FeedbackActivity.class);
            startActivity(intent);
            Toast.makeText(this,"FeedBack",Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_friend) {
            Intent intent=new Intent(this,Friendsctivity.class);
            startActivity(intent);
            Toast.makeText(this,"friends ",Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_message) {
            Intent intent=new Intent(this,Friendsctivity.class);
            startActivity(intent);
            Toast.makeText(this,"Message",Toast.LENGTH_LONG).show();

        }else if(id==R.id.nav_logOut){
            mAuth.signOut();
            UpdateUserStatus("offline");
            SendUserToLoginActivity();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }


}
