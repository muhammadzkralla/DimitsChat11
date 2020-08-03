package com.dimits.dimitschat.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.Interface.ItemClickListener;
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
    UserModel currentUser;
    private ItemClickListener itemClickListener;

    public GlobalAdapter(List<GlobalChatModel> chatModels, Context context) {
        this.chatModels = chatModels;
        this.context = context;
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
        holder.txt_message.setText(chatModels.get(position).getMessage());
        //put every single image in the chat item for its sender
        if (chatModels.get(position).getImg().equals("Default"))
            Glide.with(context).load(R.drawable.ic_person_black_24dp).into(holder.receiver_img);
        else
            Glide.with(context).load(chatModels.get(position).getImg()).into(holder.receiver_img);
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }
    public GlobalChatModel getItemAtposition(int position){
        return chatModels.get(position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHodler extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener {
        TextView txt_message;
        ImageView receiver_img;


        public MyViewHodler(@NonNull View itemView) {
            super(itemView);
            txt_message = itemView.findViewById(R.id.txt_message);
            receiver_img = itemView.findViewById(R.id.receiver_img);
            itemView.setOnCreateContextMenuListener( this);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("do you want to delete this message");
            menu.add(0,0,getAdapterPosition(),Common.DELETE);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
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
