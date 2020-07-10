package com.dimits.dimitschat.callback;

import com.dimits.dimitschat.model.UserModel;

import java.util.List;

public interface IUserCallbackListener {

    void onUserLoadSuccess(List<UserModel> userModels);
    void onUserLoadFailed(String message);
}
