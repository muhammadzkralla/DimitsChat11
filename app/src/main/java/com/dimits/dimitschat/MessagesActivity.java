package com.dimits.dimitschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {
    //initializing variables
    TextView sec_user;
    ImageView sec_img;
    EditText edt_message;
    ImageView send_btn;
    Intent intent;
    UserModel secondUser;
    String SENDER;
    String RECEIVER;
    String MESSAGE;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        //getting the intent from the UsersAdapter
        intent = getIntent();
        //assigning the uid of the user clicked on from the extra string of the intent
        String SECOND_USER_ID = intent.getStringExtra("second_user");
        //initialize the sender
        SENDER = Common.currentUser.getUid().toString();
        //initialize the receiver
        RECEIVER = SECOND_USER_ID;
        //assigning variables
        sec_user = (TextView)findViewById(R.id.sec_user);
        sec_img = (ImageView)findViewById(R.id.sec_img);
        edt_message = (EditText)findViewById(R.id.edt_message);
        send_btn = (ImageView)findViewById(R.id.send_btn);
        //set send message click listener
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the message
                MESSAGE = edt_message.getText().toString();
                //send the message
                sendMessage(SENDER,RECEIVER,MESSAGE);
                //reset the edit text
                edt_message.setText("");
            }
        });
        //getting started to get the second user's data form the firebase
        ref = FirebaseDatabase.getInstance().getReference("Users").child(SECOND_USER_ID);
        downloadData();

    }

    private void sendMessage(String sender,String receiver,String message) {
        //initializing the Message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("message", message);
        //push the message object to the Database
        submitMessageToFireBase(messageData);

    }

    private void submitMessageToFireBase(Map<String, Object> message) {
        //push data assigned to the DataBase
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();
        chatRef.child("Chats").push().setValue(message);

    }

    private void downloadData() {
        //Getting the data of the second user
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    secondUser = dataSnapshot.getValue(UserModel.class);
                    sec_user.setText(secondUser.getName());
                    if (secondUser.getImg() == "Default")
                        Glide.with(MessagesActivity.this).load(R.drawable.ic_person_black_24dp).into(sec_img);
                    else
                        Glide.with(MessagesActivity.this).load(secondUser.getImg()).into(sec_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
