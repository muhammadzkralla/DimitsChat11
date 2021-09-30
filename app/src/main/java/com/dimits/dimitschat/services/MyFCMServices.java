package com.dimits.dimitschat.services;

import androidx.annotation.NonNull;

import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.common.CommonAgr;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quintus.labs.notification.NotificationHelper;

import java.util.Map;
import java.util.Random;

public class MyFCMServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String,String> dataRecv = remoteMessage.getData();
        if(dataRecv != null) {
            Common.showNotification(this, new Random().nextInt(),
                    dataRecv.get(CommonAgr.NOTI_TITLE),
                    dataRecv.get(CommonAgr.NOTI_CONTENT),
                    null);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Common.updateToken(this,s);
    }
}
