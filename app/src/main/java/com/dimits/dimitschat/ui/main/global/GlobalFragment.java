package com.dimits.dimitschat.ui.main.global;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimits.dimitschat.ExampleService;
import com.dimits.dimitschat.MessagesActivity;
import com.dimits.dimitschat.R;
import com.dimits.dimitschat.adapter.GlobalAdapter;
import com.dimits.dimitschat.callback.IGlobalCallbackListener;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.globalActivity;
import com.dimits.dimitschat.model.GlobalChatModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Unbinder;

public class GlobalFragment extends Fragment{
    GlobalViewModel globalViewModel;
    EditText editText;
    ImageView sendText;
    private Unbinder unbinder;
    RecyclerView recyclerView;
    Button intent;
    Button service;
    Button serviceOff;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel.class);
        View root =  inflater.inflate(R.layout.fragment_global, container, false);
        intent = (Button)root.findViewById(R.id.intent);
        intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent global = new Intent(getActivity(), globalActivity.class);
                startActivity(global);
            }
        });

        service = (Button)root.findViewById(R.id.service);
        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity(), ExampleService.class);
                ContextCompat.startForegroundService(getContext(), serviceIntent);
            }
        });

        serviceOff = (Button)root.findViewById(R.id.serviceOff);
        serviceOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getActivity(), ExampleService.class);
                getActivity().stopService(serviceIntent);
            }
        });




       /* recyclerView =(RecyclerView)root.findViewById(R.id.recycler_global_chat);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
        recyclerView.setLayoutManager(layoutManager);

        sendText = (ImageView)root.findViewById(R.id.send_btn);
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText =(EditText)root.findViewById(R.id.edt_message);
                Map<String, Object> globalChatModel = new HashMap<>();
                globalChatModel.put("sender", Common.currentUser.getName());
                globalChatModel.put("message", editText.getText().toString());
                //submittofirebase((List<GlobalChatModel>) globalChatModel);

            }
        });

        globalViewModel.getmessages().observe(this, new Observer<List<GlobalChatModel>>() {
            @Override
            public void onChanged(List<GlobalChatModel> globalChatModels) {
                GlobalAdapter globalAdapter = new GlobalAdapter(globalChatModels, getContext());
                recyclerView.setAdapter(globalAdapter);
            }
        });*/

        return root;
    }


/*
    private void submittofirebase(List<GlobalChatModel> globalChatModel) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();
        chatRef.child("GlobalChats").push().setValue(globalChatModel);
    }*/
}
