package com.dimits.dimitschat;

import android.app.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimits.dimitschat.adapter.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {
    //initialize variables
    TextView title;
    ImageView user_img;
    private static final int PICK_IMAGE_REQUEST = 1234;
    private Uri imageUri = null;
    AlertDialog UploadDialog;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView btn_image,statusCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //assign the variables
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        updateToken();
        title = (TextView)findViewById(R.id.title);
        user_img = (ImageView)findViewById(R.id.user_img);
        UploadDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        //check if the user have an image or not
        if (Common.currentUser.getImg().equals("Default")){
            //if not put a default image for them
            Glide.with(this).load(R.drawable.ic_person_black_24dp).into(user_img);
        }else{
            //if he have , put his image
            Glide.with(this).load(Common.currentUser.getImg()).into(user_img);
        }
        //Click listener for the profile image
        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
        //Assigning some variables
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
                //start signing out the user
                signOut();
            }
        });
        statusCircle = findViewById(R.id.status);



        status("online");


    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    private void status (String status){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Common.currentUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }




    private void updateToken(){
        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnFailureListener(e -> Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(instanceIdResult -> {
                    Common.updateToken(HomeActivity.this,instanceIdResult.getToken());
                });
    }


    private void showImageDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("Choose your Image");

        //inflate the view
        View itemView = LayoutInflater.from(this).inflate(R.layout.details_dailog, null);
        btn_image = (ImageView) itemView.findViewById(R.id.btn_image);
        Glide.with(this).load(R.drawable.ic_person_black_24dp).into(btn_image);
        //image onClickListener
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open Images in the device
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, which) -> {
            dialogInterface.dismiss();
        }).setPositiveButton("OK", (dialogInterface, which) -> {
            //initialize new user object to update the old one
            Map<String, Object> updateDate = new HashMap<>();

            if (imageUri != null) {
                //if the user selected an image
                UploadDialog.setMessage("Uploading...");
                UploadDialog.show();

                String unique_name = UUID.randomUUID().toString();
                StorageReference imageFolder = storageReference.child("images/" + unique_name);

                imageFolder.putFile(imageUri)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                UploadDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(task -> {
                    UploadDialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                        //start changing img item in the object
                        updateDate.put("img", uri.toString());
                        //update user Data
                        updateUser(updateDate);
                    });
                }).addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    UploadDialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                });
            } else {
                //update User Data with nothing new
                updateUser(updateDate);
            }
        });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUser(Map<String, Object> updateDate) {
        //Push differences to the DataBase
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

                        //make the current user equals null
                        Common.currentUser = null;
                        //start sign out action
                        FirebaseAuth.getInstance().signOut();
                        //Go to Sign in / Sign up Activity
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
                btn_image.setImageURI(imageUri);
            }
        }
    }
}