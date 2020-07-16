package com.dimits.dimitschat.ui.main.global;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dimits.dimitschat.callback.IGlobalCallbackListener;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.GlobalChatModel;
import com.dimits.dimitschat.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GlobalViewModel extends ViewModel implements IGlobalCallbackListener{
    private MutableLiveData<List<GlobalChatModel>> modelMutableLiveDataGlobal;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IGlobalCallbackListener iGlobalCallbackListener;

    public GlobalViewModel(){
       iGlobalCallbackListener = this;
    }

    public MutableLiveData<List<GlobalChatModel>> getmessages() {
        if(modelMutableLiveDataGlobal == null) {
            modelMutableLiveDataGlobal = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadmessages();
        }
        return modelMutableLiveDataGlobal;
    }

    private void loadmessages() {
        List<GlobalChatModel> tempList = new ArrayList<>();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("GlobalChats");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempList.clear();
                for (DataSnapshot messageSnapShot:dataSnapshot.getChildren()){
                    GlobalChatModel userModel = messageSnapShot.getValue(GlobalChatModel.class);
                    userModel.setSender(Common.currentUser.getName());
                    userModel.setSender(Common.currentUser.getUid());
                    tempList.add(userModel);
                }
                iGlobalCallbackListener.onUserLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iGlobalCallbackListener.onUserLoadFailed(databaseError.getMessage());
            }
        });
    }
    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onUserLoadSuccess(List<GlobalChatModel> globalChatModels) {
       modelMutableLiveDataGlobal.setValue(globalChatModels);
    }

    @Override
    public void onUserLoadFailed(String message) {

    }
}
