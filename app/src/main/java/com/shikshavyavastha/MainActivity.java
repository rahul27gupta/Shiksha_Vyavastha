package com.shikshavyavastha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText email, password;
    private TextView fgtpwd, createusr;
    private Button login;
    private String uname, pwd;
    private ImageView logo;
    private FirebaseAuth mAuth;

    private static int TIME_DELAY = 2000;
    private static final int RequestPermissionCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        onDeclare();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        createusr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
                finish();
            }
        });

        fgtpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        onLoginSuccess();
    }

    private void onLoginSuccess() {
        uname = email.getText().toString();
        pwd = password.getText().toString();
        mAuth.signInWithEmailAndPassword(uname, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (mAuth.getCurrentUser().getEmail() != null & mAuth.getCurrentUser().getPhoneNumber() != null) {
                        Intent intent = new Intent(MainActivity.this, Profile.class);
                        startActivity(intent);
                        finish();
                    }
                    if (mAuth.getCurrentUser().getEmail() != null & mAuth.getCurrentUser().getPhoneNumber() == null) {
                        Intent intent = new Intent(MainActivity.this, Mobile_Verification.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onLoginFailed() {
        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
        login.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;
        uname = email.getText().toString();
        pwd = password.getText().toString();
        if (uname.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(uname).matches()) {
            email.setError("Enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }
        if (pwd.isEmpty() || pwd.length() < 4 || pwd.length() > 10) {
            password.setError("Enter a valid password");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }

    private void onDeclare() {
        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.inut_password);
        fgtpwd = (TextView) findViewById(R.id.link_pwd);
        createusr = (TextView) findViewById(R.id.link_signup);
        login = (Button) findViewById(R.id.login_btn);
        logo = (ImageView) findViewById(R.id.logo);
    }
}