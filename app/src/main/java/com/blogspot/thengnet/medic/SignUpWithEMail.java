package com.blogspot.thengnet.medic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blogspot.thengnet.medic.databinding.ActivitySignUpWithEmailBinding;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpWithEMail extends AppCompatActivity {

    private ActivitySignUpWithEmailBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpWithEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupWithPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(SignUpWithEMail.this, SignUpWithPhoneNumber.class));
            }
        });

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(SignUpWithEMail.this, SignInActivity.class));
            }
        });
    }
}