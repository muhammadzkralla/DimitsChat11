package com.dimits.dimitschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimits.dimitschat.adapter.GlobalAdapter;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.common.CommonAgr;
import com.dimits.dimitschat.model.FCMService.FCMSendData;
import com.dimits.dimitschat.model.GlobalChatModel;
import com.dimits.dimitschat.model.UserModel;
import com.dimits.dimitschat.remote.IFCMService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.luolc.emojirain.EmojiRainLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;

public class globalActivity extends AppCompatActivity {
    //initializing variables

    int INTENT_CODE = 5;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    IFCMService ifcmService;
    private  Uri imageurl ;
    private StorageTask uploadTask;
    private EmojiRainLayout emojiRainLayout;
    EditText edt_message;
    ImageView send_btn,send_image;
    Intent intent;
    UserModel otherUsers;
    String SENDER;
    String RECEIVER;
    String MESSAGE;
    private DatabaseReference ref;
    private StorageReference reference1;
    private DatabaseReference reference;
    GlobalAdapter messagesAdapter;
    List<GlobalChatModel> mChat;
    RecyclerView recyclerView;

    @SuppressLint("ResourceType")
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
        emojiRainLayout = (EmojiRainLayout)findViewById(R.id.activity_main);
        recyclerView = findViewById(R.id.recycler_messages);
        edt_message = (EditText)findViewById(R.id.edt_message);
        send_btn = (ImageView)findViewById(R.id.send_btn);
        send_image = (ImageView)findViewById(R.id.send_image);
        //set send message click listener and some assigning
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        reference1 = FirebaseStorage.getInstance().getReference("uploads");
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the message
                MESSAGE = edt_message.getText().toString();
                //send the message
                if (!MESSAGE.isEmpty()){
                        sendMessage(SENDER,Common.currentUser.getImg(),MESSAGE);}
                else {
                    sendMessage(SENDER,Common.currentUser.getImg(),"👍");
//                    emojiRainLayout.addEmoji(R.drawable.emoji_1_3);
//                    emojiRainLayout.addEmoji(R.drawable.emoji_2_3);
//                    emojiRainLayout.addEmoji(R.drawable.emoji_3_3);
//                    emojiRainLayout.addEmoji(R.drawable.emoji_4_3);
//                    emojiRainLayout.addEmoji(R.drawable.emoji_5_3);

                    emojiRainLayout.addEmoji(R.drawable.fui_ic_twitter_bird_white_24dp);

                    emojiRainLayout.stopDropping();
                    emojiRainLayout.setPer(10);
                    emojiRainLayout.setDuration(7200);
                    emojiRainLayout.setDropDuration(2400);
                    emojiRainLayout.setDropFrequency(500);
                    emojiRainLayout.startDropping();
                    //reset the edit text
                    edt_message.setText("");
                }
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,INTENT_CODE);
            }
        });


        //getting started to get the second user's data form the firebase
        downloadMessages();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_CODE && resultCode == RESULT_OK){
            imageurl = data.getData();
            uploadimage();
        }else {
            Toast.makeText(globalActivity.this,"something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadimage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageurl != null){
            StorageReference reference = reference1.child(System.currentTimeMillis()
                    + "."+ getFileExtention(imageurl));

            uploadTask = reference.putFile(imageurl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String myUri = downloadUri.toString();

                        /**DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("GlobalChats");
                        String commentid = reference1.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        reference1.child(commentid).setValue(hashMap);**/

                        sendMessage1(SENDER,Common.currentUser.getImg(),myUri);


                        pd.dismiss();
                    }else {
                        Toast.makeText(globalActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(globalActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(globalActivity.this,"no image selected ",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage1(String sender, String img, String myUri) {
        //initializing the Message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", sender);
        messageData.put("img", img);
        messageData.put("message", null);
        messageData.put("imageurl",myUri);
        //push the message object to the Database
        submitMessageToFireBase(messageData);

    }

    private void sendMessage(String sender, String image, String message) {
        //initializing the Message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", sender);
        messageData.put("img", image);
        messageData.put("message", message);
        //push the message object to the Database
        submitMessageToFireBase(messageData);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
           // GlobalChatModel globalChatModel = messagesAdapter.getItemAtposition();
           // deletefromfirebase(globalChatModel);
           Toast.makeText(globalActivity.this,"deleted",Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
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
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
