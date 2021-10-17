package com.blogspot.thengnet.medic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blogspot.thengnet.medic.databinding.ActivitySignInBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(SignInActivity.this, SignUpWithEMail.class));
            }
        });
    }
}