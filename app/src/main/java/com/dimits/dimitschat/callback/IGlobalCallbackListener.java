package com.dimits.dimitschat.callback;

import com.dimits.dimitschat.model.GlobalChatModel;
import com.dimits.dimitschat.model.UserModel;

import java.util.List;

public interface IGlobalCallbackListener {
    void onUserLoadSuccess(List<GlobalChatModel> globalChatModels);
    void onUserLoadFailed(String message);
}
