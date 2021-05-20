package com.example.android.tvshowsandmovieslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class mainPage extends AppCompatActivity implements View.OnClickListener {

    private Button logout, registerMP;
    private TextView userNameShow;
    private FirebaseUser user;
    private DatabaseReference reference;

    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        userNameShow = (TextView) findViewById(R.id.userNameShow);
        logout = (Button) findViewById(R.id.logOut);
        registerMP = (Button) findViewById(R.id.registerbtnMP);
        registerMP.setOnClickListener(this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(mainPage.this, MainActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userid = user.getUid();

        final TextView userName = (TextView) findViewById(R.id.userNameShow);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (signInAccount != null) {
                    userNameShow.setText(signInAccount.getDisplayName());
                    registerMP.setVisibility(View.INVISIBLE);
                } else {
                    User userN = snapshot.getValue(User.class);
                    if (userN != null && signInAccount == null) {
                        String userNamee = userN.fullname;
                        userNameShow.setText(userNamee);
                        registerMP.setVisibility(View.INVISIBLE);}
                    else {
                        userName.setText("Anonymous");
                    }
            } }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mainPage.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerbtnMP:
                startActivity(new Intent(this, Register.class));
                break;
        }
    }
    //proba
}