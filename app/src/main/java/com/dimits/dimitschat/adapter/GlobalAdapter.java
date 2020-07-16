package com.dimits.dimitschat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.R;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.ChatModel;
import com.dimits.dimitschat.model.GlobalChatModel;
import com.dimits.dimitschat.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class GlobalAdapter extends  RecyclerView.Adapter<GlobalAdapter.MyViewHodler> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private List<GlobalChatModel> chatModels = new ArrayList<>();
    Context context;
    private String imageurl;
    UserModel currentUser;

    public GlobalAdapter(List<GlobalChatModel> chatModels, Context context, String imageurl) {
        this.chatModels = chatModels;
        this.context = context;
        this.imageurl = imageurl;
    }


    @NonNull
    @Override
    public GlobalAdapter.MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            return new GlobalAdapter.MyViewHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_from_right, parent, false));
        }else{
            return new GlobalAdapter.MyViewHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_from_left, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHodler holder, int position) {
        GlobalChatModel chatModel = chatModels.get(position);
        holder.txt_message.setText(chatModel.getMessage());
        if (imageurl == "Default"){
            Glide.with(context).load(R.drawable.ic_person_black_24dp).into(holder.receiver_img);
        }else{
            Glide.with(context).load(imageurl).into(holder.receiver_img);
        }
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }


    public class MyViewHodler extends RecyclerView.ViewHolder {
        TextView txt_message;
        ImageView receiver_img;

        public MyViewHodler(@NonNull View itemView) {
            super(itemView);
            txt_message = itemView.findViewById(R.id.txt_message);
            receiver_img = itemView.findViewById(R.id.receiver_img);

        }
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = Common.currentUser;
        if (chatModels.get(position).getSender().equals(currentUser.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}
