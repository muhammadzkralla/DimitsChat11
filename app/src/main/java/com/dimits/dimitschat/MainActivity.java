package com.dimits.dimitschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dimits.dimitschat.common.Common;
import com.dimits.dimitschat.model.UserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    //initialize the variables
    private static int APP_REQUEST_CODE = 9696;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> providers;
    private static final int PICK_IMAGE_REQUEST = 1234;
    private Uri imageUri = null;
    android.app.AlertDialog dialog;
    FirebaseStorage storage;
    StorageReference storageReference;



    @Override
    protected void onStart() {
        super.onStart();
        //Check for connection
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            firebaseAuth.addAuthStateListener(listener);
        }
        else{
            Toast.makeText(this, "Your are offline, please connect to the Internet", Toast.LENGTH_SHORT).show();
            showConnectionDialog();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startConnection();
    }

    private void startConnection() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                checkUserFromFirebase(user);
            } else {
                Login();
            }

        };
    }

    private void Login() {
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers).build(),
                APP_REQUEST_CODE);
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserModel usersModel = dataSnapshot.getValue(UserModel.class);
                            if (usersModel.getBanned().equals("1"))
                                showBannedDialog();
                            else
                                goToHomeActivity(usersModel);
                        } else {
                            showRegisterDialog(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //dialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog(FirebaseUser user) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please fill information");
        //Inflate the view
        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        //Find the Views
        EditText etName =  itemView.findViewById(R.id.et_name);
        EditText etPhone = itemView.findViewById(R.id.et_phone);
        //ImageView img = itemView.findViewById(R.id.img);
        //set Data
        etPhone.setText(user.getPhoneNumber());
        //Glide.with(this).load(R.drawable.ic_person_black_24dp).into(img);

        /*Set Event
        img.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
        */
        //Action
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("REGISTER", (dialogInterface, i) -> {
            UserModel userModel = new UserModel();
            if (TextUtils.isEmpty(etName.getText().toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }
            /*if (imageUri != null) {

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
                                Toast.makeText(MainActivity.this, "err", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {

                        userModel.setImg(uri.toString());
                        Glide.with(this).load(uri).into(img);

                    });
                }).addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                });
            } else {
                Glide.with(this).load(R.drawable.common_full_open_on_phone).into(img);
            }

             */
            //Fill the DataBase
            userModel.setUid(user.getUid());
            userModel.setBanned("0");
            userModel.setImg("Default");
            userModel.setName(etName.getText().toString());
            userModel.setPhone(etPhone.getText().toString());
            userRef.child(user.getUid()).setValue(userModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Congratulation! Register Successfully", Toast.LENGTH_SHORT).show();
                            goToHomeActivity(userModel);
                        }
                    });
        });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void goToHomeActivity(UserModel usersModel) {
        Common.currentUser = usersModel; //Important,you need always assign value for it before use
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

    private void showBannedDialog() {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE)
        {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }else{
                Toast.makeText(this,"Failed sign in!!",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showConnectionDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Internet Connection error");
        alertDialog.setMessage("choose what to do please :");
        alertDialog.setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onStart();
            }
        });
        alertDialog.setNegativeButton("Close the app", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

}
