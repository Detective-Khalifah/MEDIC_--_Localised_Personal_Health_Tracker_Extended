package com.blogspot.thengnet.medic;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blogspot.thengnet.medic.databinding.ActivityViewProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {

    ActivityViewProfileBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser mSignedInUser;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mSignedInUser = mAuth.getCurrentUser();

        if (mSignedInUser != null) {
            binding.userDeets.setText("Display name: " + mSignedInUser.getDisplayName() + "\n" +
                    "E-mail: " + mSignedInUser.getEmail() + "\n" +
                    "Phone number: " + mSignedInUser.getPhoneNumber() + "\n" +
                    "Photo url: " + mSignedInUser.getPhotoUrl() + "\n" +
                    "Uui: " + mSignedInUser.getUid() + "\n" +
                    "User anonymous: " + mSignedInUser.isAnonymous() + "\n" +
                    "E-mail verified: " + mSignedInUser.isEmailVerified());

        } else {
            binding.userDeets.setText("Not signed in!");
        }

        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                signOut();
            }
        });
    }

    private void signOut () {
        mAuth.signOut();
        finish();
    }


    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out)
            signOut();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.view_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
}