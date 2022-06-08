package com.example.turtogacrudfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class registerActivity extends AppCompatActivity implements View.OnClickListener {

    EditText regEmail,regPass, regConfirmPass;
    FirebaseAuth mAuth;
    Button regButton;
    TextView signHereText;

    FirebaseDatabase database;
    DatabaseReference reference;
    User value;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        signHereText = findViewById(R.id.signHereText);
        signHereText.setOnClickListener(this);

        regButton = findViewById(R.id.regButton);
        regButton.setOnClickListener(this);

        regEmail = findViewById(R.id.regEmail);
        regPass = findViewById(R.id.regPass);
        regConfirmPass = findViewById(R.id.regConfirmPass);

        dialog = new Dialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signHereText:
                finish();
                break;

            case R.id.regButton:
                submit();
                break;
        }
    }

    private void submit() {

        String email = regEmail.getText().toString().trim();
        String password = regPass.getText().toString().trim();
        String confirmPassword = regConfirmPass.getText().toString().trim();

        if (email.isEmpty()) {
            regEmail.setError("Email is required!");
            regEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regEmail.setError("Please provide valid Email!");
            regEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            regPass.setError("Password is required!");
            regPass.requestFocus();
            return;
        }
        if (password.length() < 6) {
            regPass.setError("Min password length should be 6 characters!");
            regPass.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            regConfirmPass.setError("Confirm your password!");
            regConfirmPass.requestFocus();
            return;
        }
        if (!confirmPassword.matches(password)) {
            regConfirmPass.setError("Password doesn't match!");
            regConfirmPass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            @SuppressLint("RestrictedApi") User user = new User(email);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(registerActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(registerActivity.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            try
                            {
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d("TAG", "onComplete: exist_email");
                                regEmail.setError("Email already exists");
                                regEmail.requestFocus();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(registerActivity.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                                Log.d("TAG", "onComplete: " + e.getMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}