package com.dimits.dimitschat.ui.main.global;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dimits.dimitschat.R;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.globalActivity;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Unbinder;

public class GlobalFragment extends Fragment{
    GlobalViewModel globalViewModel;
    EditText editText;
    ImageView sendText;
    private Unbinder unbinder;
    RecyclerView recyclerView;
    Button intent, on, off;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel.class);
        View root =  inflater.inflate(R.layout.fragment_global, container, false);
        intent = (Button)root.findViewById(R.id.intent);
        on = (Button)root.findViewById(R.id.notifications_on);
        off = (Button)root.findViewById(R.id.notifications_off);

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Tokens").child(Common.currentUser.getUid())
                        .child("status").setValue("1");

                Toast.makeText(getContext(), "Notifications Turned On", Toast.LENGTH_SHORT).show();
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Tokens").child(Common.currentUser.getUid())
                        .child("status").setValue("0");

                Toast.makeText(getContext(), "Notifications Turned Off", Toast.LENGTH_SHORT).show();
            }
        });

        intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent global = new Intent(getActivity(), globalActivity.class);
                startActivity(global);
            }
        });

        return root;
    }
}
