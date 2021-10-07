package com.dimits.dimitschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.adapter.MessagesAdapter;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.common.CommonAgr;
import com.dimits.dimitschat.model.ChatModel;
import com.dimits.dimitschat.model.FCMResponse;
import com.dimits.dimitschat.model.FCMSendData;
import com.dimits.dimitschat.model.TokenModel;
import com.dimits.dimitschat.model.UserModel;
import com.dimits.dimitschat.remote.IFCMService;
import com.dimits.dimitschat.remote.RetrofitFCMClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MessagesActivity extends AppCompatActivity {
    //initializing variables
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IFCMService ifcmService;
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
    private DatabaseReference reference;
    MessagesAdapter messagesAdapter;
    List<ChatModel> mChat;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
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
        sec_user = (TextView)findViewById(R.id.sec_user);
        sec_img = (ImageView)findViewById(R.id.sec_img);
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
                if (!MESSAGE.isEmpty())
                    sendMessage(SENDER,RECEIVER,MESSAGE);
                else
                    Toast.makeText(MessagesActivity.this, "Empty Message !", Toast.LENGTH_SHORT).show();
                //reset the edit text
                edt_message.setText("");
            }
        });
        //getting started to get the second user's data form the firebase
        downloadData();

    }

    private void sendMessage(String sender,String receiver,String message) {
        //initializing the Message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", sender);
        messageData.put("receiver", receiver);
        messageData.put("message", message);
        //push the message object to the Database
        submitMessageToFireBase(messageData, RECEIVER, message);

    }

    private void submitMessageToFireBase(Map<String, Object> message, String secondUser, String content) {
        //push data assigned to the DataBase
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();
        chatRef.child("Chats").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                FirebaseDatabase.getInstance().getReference("Tokens").child(secondUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            TokenModel tokenModel = dataSnapshot.getValue(TokenModel.class);
                            Map<String, String> notiData = new HashMap<>();
                            notiData.put(CommonAgr.NOTI_TITLE, Common.currentUser.getName());
                            notiData.put(CommonAgr.NOTI_CONTENT, content);

                            FCMSendData sendData = new FCMSendData(tokenModel.getToken(), notiData);

                            compositeDisposable.add(
                                    ifcmService.sendNotification(sendData)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(fcmResponse -> {

                                    }, throwable -> {
                                        Toast.makeText(MessagesActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                    })

                            );
                            Toast.makeText(MessagesActivity.this, "Notification Sent to" + tokenModel.getPhone() + " ,Token : " + tokenModel.getToken(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void downloadData() {
        //Getting the data of the second user
        ref = FirebaseDatabase.getInstance().getReference("Users").child(RECEIVER);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    secondUser = dataSnapshot.getValue(UserModel.class);
                    sec_user.setText(secondUser.getName());
                    if (secondUser.getImg().equals("Default"))
                        Glide.with(MessagesActivity.this).load(R.drawable.ic_person_black_24dp).into(sec_img);
                    else
                        Glide.with(MessagesActivity.this).load(secondUser.getImg()).into(sec_img);
                }
                downloadMessages(Common.currentUser.getUid(),RECEIVER,secondUser.getImg());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void downloadMessages(String myId, String userId, String Imageurl){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ChatModel chat = dataSnapshot1.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getSender().equals(myId) && chat.getReceiver().equals(userId)){
                        mChat.add(chat);
                    }
                    messagesAdapter = new MessagesAdapter(mChat,MessagesActivity.this,Imageurl);
                    recyclerView.setAdapter(messagesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
