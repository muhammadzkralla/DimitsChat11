package com.dimits.dimitschat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dimits.dimitschat.R;
import com.dimits.dimitschat.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.MyViewHodler> {
    private List<UserModel> userModels = new ArrayList<>();
    Context context;
    public UserAdapter(Context context, List<UserModel> userModels) {
        this.context =context;
        this.userModels= userModels;
    }

    @NonNull
    @Override
    public MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHodler holder, int position) {
        holder.username.setText(new StringBuffer(userModels.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }


    public List<UserModel> getListUser() {
        return userModels;
    }

    public class MyViewHodler extends RecyclerView.ViewHolder {
        TextView username;
        public MyViewHodler(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
        }
    }
}
