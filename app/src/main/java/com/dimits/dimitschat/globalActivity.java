package com.dimits.dimitschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.adapter.GlobalAdapter;
import com.dimits.dimitschat.adapter.MessagesAdapter;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.ChatModel;
import com.dimits.dimitschat.model.GlobalChatModel;
import com.dimits.dimitschat.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class globalActivity extends AppCompatActivity {
    //initializing variables

    EditText edt_message;
    ImageView send_btn;
    Intent intent;
    UserModel otherUsers;
    String SENDER;
    String RECEIVER;
    String MESSAGE;
    private DatabaseReference ref;
    private DatabaseReference reference;
    GlobalAdapter messagesAdapter;
    List<GlobalChatModel> mChat;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        //getting the intent from the UsersAdapter
        intent = getIntent();
        //assigning the uid of the user clicked on from the extra string of the intent
        String SECOND_USER_ID = intent.getStringExtra("second_user");
        //initialize the sender
        SENDER = Common.currentUser.getUid().toString();
        //initialize the receiver
        RECEIVER = SECOND_USER_ID;
        //assigning variables
        recyclerView = findViewById(R.id.recycler_messages);
        edt_message = (EditText)findViewById(R.id.edt_message);
        send_btn = (ImageView)findViewById(R.id.send_btn);
        //set send message click listener and some assigning
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the message
                MESSAGE = edt_message.getText().toString();
                //send the message
                sendMessage(SENDER,Common.currentUser.getImg(),MESSAGE);
                //reset the edit text
                edt_message.setText("");
            }
        });
        //getting started to get the second user's data form the firebase
        downloadMessages();

    }



    private void sendMessage(String sender,String image,String message) {
        //initializing the Message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", sender);
        messageData.put("img", image);
        messageData.put("message", message);
        //push the message object to the Database
        submitMessageToFireBase(messageData);

    }

    private void submitMessageToFireBase(Map<String, Object> message) {
        //push data assigned to the DataBase
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();
        chatRef.child("GlobalChats").push().setValue(message);

    }
    private void downloadMessages() {
        mChat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GlobalChats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    GlobalChatModel globalChatModel = dataSnapshot1.getValue(GlobalChatModel.class);
                    mChat.add(globalChatModel);
                    //for each single chat item in the list
                    for (GlobalChatModel singleChat : mChat){
                        //update the adapter with the coming data
                        messagesAdapter = new GlobalAdapter(mChat,globalActivity.this);
                        recyclerView.setAdapter(messagesAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
