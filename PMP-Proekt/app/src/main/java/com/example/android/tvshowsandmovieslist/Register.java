package com.example.android.tvshowsandmovieslist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {



    private TextView logo, register;
    private EditText etFullName, etEmail, etPassowrd, etConfirmPassword;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        logo = (TextView) findViewById(R.id.Registertv);
        logo.setOnClickListener(this);

        register = (Button) findViewById(R.id.Register);
        register.setOnClickListener(this);

        etFullName = (EditText) findViewById(R.id.FullName);
        etEmail = (EditText) findViewById(R.id.Email);
        etPassowrd = (EditText) findViewById(R.id.PasswordR);
        etConfirmPassword = (EditText) findViewById(R.id.ConfirmPassword);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Registertv:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.Register:
                register();
                break;
                
        }
    }

    private void register() {

        String fullname = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassowrd.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullname.isEmpty()) {
            etFullName.setError("Field is empty");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Field is empty");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassowrd.setError("Field is empty");
            etPassowrd.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassowrd.setError("Password is too short");
            etPassowrd.requestFocus();
            return;
        }

        if (!confirmPassword.equals(password)) {
            etConfirmPassword.setError("Passwords don't match");
            etConfirmPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    User user = new User(fullname, email);

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Successful Register", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Register.this, "Failed Register", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(Register.this, "Failed Register", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}