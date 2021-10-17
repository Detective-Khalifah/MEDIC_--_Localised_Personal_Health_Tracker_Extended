package com.blogspot.thengnet.medic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blogspot.thengnet.medic.databinding.ActivitySignUpWithPhoneNumberBinding;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpWithPhoneNumber extends AppCompatActivity {

    private ActivitySignUpWithPhoneNumberBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpWithPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupWithEMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(SignUpWithPhoneNumber.this, SignUpWithEMail.class));
            }
        });

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(SignUpWithPhoneNumber.this, SignInActivity.class));
            }
        });
    }
}