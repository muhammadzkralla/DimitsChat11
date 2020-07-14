package com.dimits.dimitschat.ui.main.chats;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dimits.dimitschat.callback.IUserCallbackListener;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.ChatModel;
import com.dimits.dimitschat.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChatsViewModel extends ViewModel implements IUserCallbackListener {
    private MutableLiveData<List<UserModel>> userMutableLiveData;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IUserCallbackListener iUserCallbackListener;
    private List<String> usersIds;

    public ChatsViewModel(){
        iUserCallbackListener = this;
    }

    public MutableLiveData<List<UserModel>> getUserList() {
        if(userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            getChats();
        }
        return userMutableLiveData;
    }

    private void getChats() {
        usersIds = new ArrayList<>();
        DatabaseReference chats = FirebaseDatabase.getInstance().getReference("Chats");
        chats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersIds.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ChatModel chats = dataSnapshot1.getValue(ChatModel.class);
                    if (chats.getReceiver().equals(Common.currentUser.getUid())){
                        usersIds.add(chats.getSender());
                    }
                    if (chats.getSender().equals(Common.currentUser.getUid())){
                        usersIds.add(chats.getReceiver());
                    }
                }

                loadUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void loadUsers() {
        List<UserModel> tempList = new ArrayList<>();
        HashSet<String> stringHashSet = new HashSet<>(usersIds);
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempList.clear();
                for (DataSnapshot userSnapShot:dataSnapshot.getChildren()){
                    UserModel userModel = userSnapShot.getValue(UserModel.class);
                    for (String userId : stringHashSet){
                        if (userModel.getUid().equals(userId)){
                            tempList.add(userModel);
                        }
                    }
                }
                iUserCallbackListener.onUserLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iUserCallbackListener.onUserLoadFailed(databaseError.getMessage());
            }
        });
    }


    public MutableLiveData<String> getMessageError() {
        return messageError;
    }


    @Override
    public void onUserLoadSuccess(List<UserModel> userModels) {
        userMutableLiveData.setValue(userModels);
    }

    @Override
    public void onUserLoadFailed(String message) {
        messageError.setValue(message);
    }
}
