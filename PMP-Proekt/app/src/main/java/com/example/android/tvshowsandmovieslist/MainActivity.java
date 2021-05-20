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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText mainEmail, mainPassword;
    private Button login, anonymous, googleSI;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) findViewById(R.id.RegisterMain);
        register.setOnClickListener(this);

        forgotPassword = (TextView) findViewById(R.id.ForgotPassword);
        forgotPassword.setOnClickListener(this);

        login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(this);

        googleSI = (Button) findViewById(R.id.btnGoogle);
        googleSI.setOnClickListener(this);

        anonymous = (Button) findViewById(R.id.Anonymous);
        anonymous.setOnClickListener(this);

        mainEmail = (EditText) findViewById(R.id.EmailMain);
        mainPassword = (EditText) findViewById(R.id.PasswordMain);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.RegisterMain:
                 startActivity(new Intent(this, Register.class));
                 break;
            case R.id.Login:
                 userLogin();
                 break;
            case R.id.ForgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
            case R.id.Anonymous:
                 signInAnonymously();
                 break;
            case R.id.btnGoogle:
                 signIn();
                 break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, mainPage.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void userLogin() {
        String email = mainEmail.getText().toString().trim();
        String password = mainPassword.getText().toString().trim();

        if (email.isEmpty()) {
            mainEmail.setError("Email is empty");
            mainEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mainEmail.setError("Invalid email address");
            mainEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mainPassword.setError("Field is empty");
            mainPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            mainPassword.setError("Password is too short");
            mainPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, mainPage.class));
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Verify email", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(MainActivity.this, "Wrong Credentials", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, mainPage.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}