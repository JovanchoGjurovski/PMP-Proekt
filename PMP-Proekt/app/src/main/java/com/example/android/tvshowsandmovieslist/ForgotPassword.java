package com.example.android.tvshowsandmovieslist;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText enterEmail;
    private Button resetPasswordbtn;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        enterEmail = (EditText) findViewById(R.id.enterEmail);

        resetPasswordbtn = (Button) findViewById(R.id.resetPassword);

        auth = FirebaseAuth.getInstance();
        resetPasswordbtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        resetPasswordbtn();
    }

    private void resetPasswordbtn() {

        String email = enterEmail.getText().toString().trim();

        if (email.isEmpty()) {
            enterEmail.setError("Email is empty");
            enterEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterEmail.setError("Invalid email address");
            enterEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Email Sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPassword.this, "Error", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}