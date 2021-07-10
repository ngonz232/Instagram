package com.example.instagram.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    Button btnSignup;
    TextView etUsernameSignUp;
    TextView etPasswordSignUp;
    TextView etEmail;
    TextView etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignup = findViewById(R.id.btnSignUp);
        etUsernameSignUp = findViewById(R.id.etUsernameSignUp);
        etPasswordSignUp = findViewById(R.id.etPasswordSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
// Set core properties
                user.setUsername(etUsernameSignUp.getText().toString());
                user.setPassword(etPasswordSignUp.getText().toString());
                user.setEmail(etEmail.getText().toString());
// Set custom properties
                user.put("phoneNumber", etPhoneNumber.getText().toString());
// Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(),"Sign Up Successful!",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Sign Up Error. Check ParseException Log",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                finish();
            }
        });
    }
}