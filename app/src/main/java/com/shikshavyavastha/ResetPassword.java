package com.shikshavyavastha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private Button resetpwd;
    private EditText email;
    private String mEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        resetpwd = (Button)findViewById(R.id.reset_pwd);
        email = (EditText)findViewById(R.id.reset_email);

        resetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ResetPassword.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void resetPassword() {
        if (!validate()) {
            onResetFailed();
            return;
        }
        onResetSuccess();
    }

    private void onResetSuccess() {
        mEmail = email.getText().toString();
        mAuth.sendPasswordResetEmail(mEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(),"Reset Link Sent To Your Email.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ResetPassword.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error ! Reset Link is Not Sent"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onResetFailed() {
        Toast.makeText(getApplicationContext(), "Reset Password Failed", Toast.LENGTH_LONG).show();
    }

    private boolean validate() {
        boolean valid = true;
        mEmail = email.getText().toString();
        if (mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            email.setError("Enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }
        return valid;
    }
}