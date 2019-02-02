package com.example.nanthu.homeui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton SendMessageButton,SendImageFileButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    private List<Message>messageslist=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private String messageReceiverId,messageReceiverUsername,MegsenderID,savecurrentdate, savecurrentTime;

    private TextView receiverUsername,userLastSeen;
    private CircleImageView receiverImage;
    private DatabaseReference RootRef,UsersRef;
    private FirebaseAuth mAuth;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
     //   toolbar();

         RootRef= FirebaseDatabase.getInstance().getReference();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
         mAuth=FirebaseAuth.getInstance();
         MegsenderID=mAuth.getCurrentUser().getUid();
        messageReceiverId=getIntent().getExtras().get("visit").toString();
        messageReceiverUsername=getIntent().getExtras().get("userName").toString();


        intailizeFieldes();
        DisplayRecevInfrom();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchmessages();
    }

    private void fetchmessages() {

        RootRef.child("Message").child(MegsenderID).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    Message message=dataSnapshot.getValue(Message.class);
                    messageslist.add(message);
                    messageAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {

        UpdateUserStatus("online");

        String TextMEssage=userMessageInput.getText().toString();
        if(TextUtils.isEmpty(TextMEssage)){
            Toast.makeText(this,"please type a message",Toast.LENGTH_LONG).show();
        }
        else{
            String message_sender_Ref="Message/"+MegsenderID+"/"+messageReceiverId;
            String message_recever_Ref="Message/"+messageReceiverId+"/"+MegsenderID;

            DatabaseReference user_message_key=RootRef.child("Messages").child(MegsenderID).child(messageReceiverId).push();
            String message_push_id=user_message_key.getKey();


            Calendar calForeDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yyyy");
            savecurrentdate=currentDate.format(calForeDate.getTime());

            Calendar calForeTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
            savecurrentTime=currentTime.format(calForeDate.getTime());

            Map messageTextBody= new HashMap();
            messageTextBody.put("message",TextMEssage);
            messageTextBody.put("time",savecurrentTime);
            messageTextBody.put("date",savecurrentdate);
            messageTextBody.put("type","Message");
            messageTextBody.put("from",MegsenderID);

            Map messagebodydetails= new HashMap();
            messagebodydetails.put(message_sender_Ref+"/"+message_push_id ,messageTextBody);
            messagebodydetails.put(message_recever_Ref+"/"+message_push_id ,messageTextBody);

            RootRef.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this,"sent message successfully",Toast.LENGTH_LONG).show();
                        userMessageInput.setText("");

                    }
                    else{
                        String errormeg=task.getException().getMessage();
                        Toast.makeText(ChatActivity.this,"Error"+errormeg,Toast.LENGTH_LONG).show();
                        userMessageInput.setText("");

                    }

                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateUserStatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateUserStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateUserStatus("offline");
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

        UsersRef.child(MegsenderID).child("UserState").updateChildren(currentstatus);


    }


    private void DisplayRecevInfrom() {

       receiverUsername.setText(messageReceiverUsername);

        RootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                   final String myImage=dataSnapshot.child("ProfileImage").getValue().toString();
                   final String mytype=dataSnapshot.child("UserState").child("Type").getValue().toString();
                   final String date=dataSnapshot.child("UserState").child("Date").getValue().toString();
                    final String time=dataSnapshot.child("UserState").child("time").getValue().toString();

                    if(mytype.equals("online")){
                        userLastSeen.setText("online");
                    }else{
                        userLastSeen.setText("offline: "+time+" "+date);

                    }

                    Picasso.with(ChatActivity.this).load(myImage).placeholder(R.drawable.images).into(receiverImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void intailizeFieldes() {

        SendMessageButton=(ImageButton)findViewById(R.id.sent_message_button);
        SendImageFileButton=(ImageButton)findViewById(R.id.sent_image_file_button);
        userMessageInput=(EditText)findViewById(R.id.input_message);





        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_chat=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_chat);

        receiverUsername=(TextView)action_bar_chat.findViewById(R.id.custom_chat_usernames);
        receiverImage=(CircleImageView)action_bar_chat.findViewById(R.id.customchat_profile_image);
        userLastSeen=(TextView)action_bar_chat.findViewById(R.id.custom_user_lastseen);


        messageAdapter=new MessageAdapter(messageslist);
        userMessagesList=(RecyclerView)findViewById(R.id.message_lists_users);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        UpdateUserStatus("online");



    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.Profile);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(ChatActivity.this, Home.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
