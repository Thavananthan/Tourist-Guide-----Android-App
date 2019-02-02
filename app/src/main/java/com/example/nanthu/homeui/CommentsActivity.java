package com.example.nanthu.homeui;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView commentList;
    private ImageButton PostCommentButton;
    private EditText commentInputText;
    private String Post_Key,currentUserId;
    private FirebaseAuth mAuth;
    private LinearLayout clickcomments;
    private DatabaseReference UserRef,postsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key=getIntent().getExtras().get("postkey").toString();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        commentList=(RecyclerView)findViewById(R.id.comment_list);
        commentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentList.setLayoutManager(linearLayoutManager);

        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef= FirebaseDatabase.getInstance().getReference("Posts").child(Post_Key).child("comments");

        clickcomments=(LinearLayout)findViewById(R.id.all_comments) ;
        commentInputText=(EditText)findViewById(R.id.comments_input);
        PostCommentButton=(ImageButton)findViewById(R.id.post_commentsbtn);




        PostCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String username=dataSnapshot.child("username").getValue().toString();

                            validatecomment(username);

                            commentInputText.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Comments,CommentsViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>
                (
                        Comments.class,
                        R.layout.all_commentview_layout,
                        CommentsViewHolder.class,
                        postsRef
                ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

                }
        };

        commentList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setComment(String comment){
            TextView myComments=(TextView)mView.findViewById(R.id.displaycommentext);
            myComments.setText(comment);
            }
        public void setDate(String date){
            TextView mydate=(TextView)mView.findViewById(R.id.simplecommentdate);
            mydate.setText(" Date:"+date);
        }
        public void setTime(String time){
            TextView mytime=(TextView)mView.findViewById(R.id.simplecommenttime);
            mytime.setText(" Time:"+time);
            }
        public void setUsername(String username){
            TextView myusername=(TextView)mView.findViewById(R.id.comment_username);
            myusername.setText("@"+username +"  ");
            }
    }






    private void validatecomment(String username) {

        String commenttext=commentInputText.getText().toString();

        if(TextUtils.isEmpty(commenttext)){
            Toast.makeText(this,"Please Write comment....",Toast.LENGTH_SHORT).show();
        }else{
            Calendar calForeDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yyyy");
          final String savecurrentdate=currentDate.format(calForeDate.getTime());

            Calendar calForeTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss");
           final String savecurrentTime=currentTime.format(calForeDate.getTime());
           final String postRandom= currentUserId + savecurrentdate + savecurrentTime;

            HashMap commentMap=new HashMap();
            commentMap.put("uid",currentUserId);
            commentMap.put("comment",commenttext);
            commentMap.put("date",savecurrentdate);
            commentMap.put("time",savecurrentTime);
            commentMap.put("username",username);

            postsRef.child(postRandom).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentsActivity.this,"You have commented successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CommentsActivity.this,"Error Occured: Try again.... ",Toast.LENGTH_SHORT).show();

                }
                }
            });



        }
    }
}
