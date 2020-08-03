package com.dimits.dimitschat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.GlobalChatModel;
import com.dimits.dimitschat.model.UserModel;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quintus.labs.notification.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

import static com.dimits.dimitschat.App.CHANNEL_ID;

public class ExampleService extends Service {
    UserModel senderUser;
    String sender;
    String title;
    String message;


    @Override
    public void onCreate() {
        super.onCreate();
        loadSenderUser();
    }

    private void loadSenderUser() {
        List<GlobalChatModel> tempList = new ArrayList<>();
        DatabaseReference firebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference("GlobalChats");

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GlobalChatModel chat = snapshot.getValue(GlobalChatModel.class);
                    tempList.add(chat);
                }
                message = tempList.get(tempList.size()-1).getMessage();
                sender = tempList.get(tempList.size()-1).getSender();
                loadUser(sender);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadUser(String sender) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user.getUid().equals(sender)){
                        title = user.getName();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("GlobalChats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(com.dimits.dimitschat.ExampleService.this,HomeActivity.class);
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                notificationHelper.createNotification(title,message,R.drawable.ic_announcement_black_24dp,intent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_send_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
