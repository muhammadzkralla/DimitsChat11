package com.dimits.dimitschat.ui.main.users;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dimits.dimitschat.callback.IUserCallbackListener;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel implements IUserCallbackListener {
    private MutableLiveData<List<UserModel>> userMutableLiveData;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IUserCallbackListener iUserCallbackListener;

    public UsersViewModel(){
        iUserCallbackListener = this;
    }

    public MutableLiveData<List<UserModel>> getUserList() {
        if(userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadUsers();
        }
        return userMutableLiveData;
    }

    private void loadUsers() {
        List<UserModel> tempList = new ArrayList<>();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempList.clear();
                for (DataSnapshot userSnapShot:dataSnapshot.getChildren()){
                    UserModel userModel = userSnapShot.getValue(UserModel.class);
                    userModel.setUid(userSnapShot.getKey());
                    tempList.add(userModel);
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
