package com.example.turtogacrudfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    TextView loginEmailText, loginPasswordTextView;
    Button loginButton;

    private ProgressDialog progressDialog;
    FirebaseAuth fauth;
    private FirebaseAuth mAuth;

    private long backPressedTime;
    private Toast backToast;
    private View view;
    private Dialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, MenuActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailText = findViewById(R.id.loginEmailText);
        loginPasswordTextView = findViewById(R.id.loginPasswordTextView);
        loginButton = findViewById(R.id.loginButton);

        fauth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        dialog = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alreadyRegisteredText:
                startActivity(new Intent(LoginActivity.this, registerActivity.class));
                break;

            case R.id.loginButton:
                login();
                break;

            case R.id.forgotText:
                forgotpassword();
                break;
        }
    }

    private void forgotpassword() {
        dialog.setContentView(R.layout.forgotpassword);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText emailText = dialog.findViewById(R.id.forgotEmail);
        Button send = dialog.findViewById(R.id.forgotButton);
        dialog.show();

        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error! Reset link not sent.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void login() {
        String email = loginEmailText.getText().toString().trim();
        String pass = loginPasswordTextView.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmailText.setError("Email is Required!");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            loginPasswordTextView.setError("Password is Required!");
            return;
        }

        if (pass.length() < 6) {
            loginPasswordTextView.setError("Password must have 6 or more than characters");
        } else {
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
            fauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {

                        Toast.makeText(LoginActivity.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "ERROR!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}