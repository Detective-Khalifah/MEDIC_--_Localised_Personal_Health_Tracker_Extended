package com.blogspot.thengnet.medic;

import android.os.Bundle;
import android.view.View;

import com.blogspot.thengnet.medic.databinding.ActivityAlarmsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmsActivity extends AppCompatActivity {

    private ActivityAlarmsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser mSignedInUser;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mSignedInUser = mAuth.getCurrentUser();

        binding.userDeets.setText("Display name: " + mSignedInUser.getDisplayName() + "\n" +
                "E-mail: " + mSignedInUser.getEmail() + "\n" +
                "Phone number: " + mSignedInUser.getPhoneNumber() + "\n" +
                "Photo url: " + mSignedInUser.getPhotoUrl() + "\n" +
                "Uui: " + mSignedInUser.getUid() + "\n" +
                "User anonymous: " + mSignedInUser.isAnonymous() + "\n" +
                "E-mail verified: " + mSignedInUser.isEmailVerified());

        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                signOut();
            }
        });

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Snackbar.make(view, "Phase I task 3: Add scheduled alarm", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void signOut () {
        mAuth.signOut();
        finish();
    }
}