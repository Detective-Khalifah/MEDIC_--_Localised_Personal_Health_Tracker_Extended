package com.blogspot.thengnet.medic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blogspot.thengnet.medic.databinding.ActivitySignUpWithEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpWithEMail extends AppCompatActivity {

    private static final String TAG = SignUpWithEMail.class.getName();
    private ActivitySignUpWithEmailBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpWithEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.loading.setVisibility(View.GONE);
        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.loading.setVisibility(View.VISIBLE);
                Log.d(TAG, ("Email: " + binding.email.getText().toString().trim() + "|" + "pwd: " + binding.password.getText().toString().trim()));
                signUp(binding.email.getText().toString().trim(), binding.email.getText().toString().trim());
            }
        });

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

    private void signUp (String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete (@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, open AlarmsActivity
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SignUpWithEMail.this, AlarmsActivity.class));
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpWithEMail.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

}