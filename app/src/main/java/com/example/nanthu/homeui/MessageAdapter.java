package com.example.nanthu.homeui;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHoldder> {

    private List<Message> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;

    public  MessageAdapter(List<Message> userMessageList){
        this.userMessageList=userMessageList;
    }


    public class MessageViewHoldder extends RecyclerView.ViewHolder {
        public TextView senderTextmessage,receiverTextmessage;
        public CircleImageView receiverProfileImage;

        public MessageViewHoldder(@NonNull View itemView) {
            super(itemView);

            senderTextmessage=(TextView)itemView.findViewById(R.id.sender_messageText);
            receiverTextmessage=(TextView)itemView.findViewById(R.id.recevier_messageText);

            receiverProfileImage=(CircleImageView)itemView.findViewById(R.id.profile_image_message);



        }


    }

    @NonNull
    @Override
    public MessageViewHoldder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View V= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout_of_user,viewGroup,false);

        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHoldder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHoldder messageViewHoldder, int i)
    {
        String messagesendrId=mAuth.getCurrentUser().getUid();
        Message message=userMessageList.get(i);

        String formUseId=message.getFrom();
        String formMessageType=message.getType();
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users").child(formUseId);
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String Image=dataSnapshot.child("ProfileImage").getValue().toString();
                    Picasso.with(messageViewHoldder.receiverProfileImage.getContext()).load(Image).placeholder(R.drawable.images)
                            .into(messageViewHoldder.receiverProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(formMessageType.equals("Message")){

            messageViewHoldder.receiverTextmessage.setVisibility(View.INVISIBLE);
            messageViewHoldder.receiverProfileImage.setVisibility(View.INVISIBLE);

            if(formUseId.equals(messagesendrId)){

                messageViewHoldder.senderTextmessage.setBackgroundResource(R.drawable.send_message_backgroud_color);
                messageViewHoldder.senderTextmessage.setTextColor(Color.WHITE);
                messageViewHoldder.senderTextmessage.setGravity(Gravity.LEFT);
                messageViewHoldder.senderTextmessage.setText(message.getMessage());
            }
            else{
                messageViewHoldder.senderTextmessage.setVisibility(View.INVISIBLE);
                messageViewHoldder.receiverTextmessage.setVisibility(View.VISIBLE);
                messageViewHoldder.receiverProfileImage.setVisibility(View.VISIBLE);

                messageViewHoldder.receiverTextmessage.setBackgroundResource(R.drawable.receiver_message_backgroudcolor);
                messageViewHoldder.receiverTextmessage.setTextColor(Color.WHITE);
                messageViewHoldder.receiverTextmessage.setGravity(Gravity.LEFT);
                messageViewHoldder.receiverTextmessage.setText(message.getMessage());
            }

        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }
}
