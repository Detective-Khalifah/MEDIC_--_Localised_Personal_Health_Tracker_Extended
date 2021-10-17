package com.blogspot.thengnet.medic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blogspot.thengnet.medic.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getName();
    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        binding.loading.setVisibility(View.GONE);
        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.loading.setVisibility(View.VISIBLE);
                Log.d(TAG, ("Email: " + binding.username.getText().toString().trim() + "|" + "pwd: " + binding.password.getText().toString().trim()));
                signIn(binding.username.getText().toString().trim(), binding.password.getText().toString().trim());
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(SignInActivity.this, SignUpWithEMail.class));
            }
        });
    }

    private void signIn (String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete (@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, open AlarmsActivity
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null)
                                startActivity(new Intent(SignInActivity.this, AlarmsActivity.class));
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
}