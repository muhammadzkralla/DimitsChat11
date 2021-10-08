package com.dimits.dimitschat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.MessagesActivity;
import com.dimits.dimitschat.R;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        holder.userphon.setText(new StringBuffer(userModels.get(position).getPhone()));
        if (userModels.get(position).getImg().equals("Default")){
            Glide.with(context).load(R.drawable.ic_person_black_24dp).into(holder.userimage);
        }else{
            Glide.with(context).load(userModels.get(position).getImg()).into(holder.userimage);
        }

        //ClickListener for each item in the adapter with putting extra the uid of the user clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("second_user",userModels.get(position).getUid());
                context.startActivity(intent);
                Toast.makeText(context, "" + userModels.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        if(userModels.get(position).getStatus() != null){
            if(userModels.get(position).getStatus().equals("online")){
                holder.statusCircle.setVisibility(View.VISIBLE);
            }
        }


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
        ImageView userimage;
        TextView userphon;
        ImageView statusCircle;
        public MyViewHodler(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            userimage = itemView.findViewById(R.id.user_image);
            userphon = itemView.findViewById(R.id.user_phon);
            statusCircle = itemView.findViewById(R.id.status);
        }
    }
}
