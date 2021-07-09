package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvSignUp = findViewById(R.id.tvSignUp);

        if (ParseUser.getCurrentUser() != null) goMainActivity();

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUp.class);
                startActivity(i);
            }
        });

    }


    public void loginOnClick(View v) {
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();
        loginUser(username, password);
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Toast.makeText(this, "Username or password is incorrect.", Toast.LENGTH_SHORT).show();
                return;
            }
            goMainActivity();
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }



}

