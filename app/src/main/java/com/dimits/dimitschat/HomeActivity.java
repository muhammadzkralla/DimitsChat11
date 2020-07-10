package com.dimits.dimitschat;

import android.app.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimits.dimitschat.ui.main.tabstuff.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    TextView title;
    ImageView user_img;
    private static final int PICK_IMAGE_REQUEST = 1234;
    private Uri imageUri = null;
    AlertDialog dialog;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        title = (TextView)findViewById(R.id.title);
        user_img = (ImageView)findViewById(R.id.user_img);
        if (Common.currentUser.getImg() == "Default"){
            Glide.with(this).load(R.drawable.ic_person_black_24dp).into(user_img);
        }else{
            Glide.with(this).load(Common.currentUser.getImg()).into(user_img);
        }
        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                Map<String, Object> updateDate = new HashMap<>();
                if (imageUri != null) {

                    // firebase Storage upload image
                    dialog.setMessage("Uploading...");
                    dialog.show();

                    String unique_name = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("images/" + unique_name);

                    imageFolder.putFile(imageUri)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnCompleteListener(task -> {
                        dialog.dismiss();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateDate.put("img", uri.toString());
                            updateUser(updateDate);
                        });
                    }).addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                    });
                } else {
                    updateUser(updateDate);
                }

            }
        });
        title.setText(Common.currentUser.getName());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void updateUser(Map<String, Object> updateDate) {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(Common.currentUser.getUid())
                .updateChildren(updateDate)
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
                    onStart();
                });
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Signout")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Common.currentUser = null;

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                user_img.setImageURI(imageUri);
            }
        }
    }
}