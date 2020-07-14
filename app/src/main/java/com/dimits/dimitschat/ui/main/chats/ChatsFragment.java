package com.dimits.dimitschat.ui.main.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimits.dimitschat.R;
import com.dimits.dimitschat.adapter.UserAdapter;
import com.dimits.dimitschat.model.UserModel;
import com.dimits.dimitschat.ui.main.users.UsersViewModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatsFragment extends Fragment {
    private ChatsViewModel chatsViewModel;
    private List<UserModel> userModelsList;
    RecyclerView recyclerView;
    UserAdapter adapter;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.chats_fragment, container, false);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        unbinder = ButterKnife.bind(this,root);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);

        chatsViewModel.getMessageError().observe(getViewLifecycleOwner(), s
                -> Toast.makeText(getContext(),""+s,Toast.LENGTH_SHORT).show());


        chatsViewModel.getUserList().observe(getViewLifecycleOwner(), userModels -> {
            adapter = new UserAdapter(getContext(),userModels);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        return root;

    }
}
