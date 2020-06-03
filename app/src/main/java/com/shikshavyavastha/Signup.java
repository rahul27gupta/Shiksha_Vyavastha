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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    private EditText editText1, editText2, editText3, editText4, editText5;
    private Button button;
    private TextView textView2, textview1;
    private String first_name, last_name, email, password, confirmpwd, age, gender, country, ts;
    private String userID;
    private Animation registerAnim;

    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference();

        Initialize();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goButton();
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpPD();
            }
        });
    }

    private void signUpPD() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goButton() {
        if (!validate()) {
            signupFailed();
            return;
        } else {
            signupProcess();
        }
    }

    private void signupProcess() {
        Long tsLong = System.currentTimeMillis()/1000;
        ts = tsLong.toString();
        first_name = editText1.getText().toString();
        last_name = editText2.getText().toString();
        email = editText3.getText().toString();
        age = "0";
        gender = "null";
        country = "Country";
        password = editText4.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userID = mAuth.getCurrentUser().getUid();
                    UserDetailsFileHelper helperclass = new UserDetailsFileHelper(first_name, last_name, email, age, gender, country, ts);
                    reference.child("Users").child(userID).setValue(helperclass);
                    Intent intent = new Intent(Signup.this, Mobile_Verification.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signupFailed() {
        Toast.makeText(getApplicationContext(), "Signup Failed", Toast.LENGTH_LONG).show();
        button.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;
        getValueFromUser();
        if (first_name.isEmpty() || first_name.length() < 3) {
            editText1.setError("Enter your first name");
            valid = false;
        } else {
            editText1.setError(null);
        }
        if (last_name.isEmpty() || last_name.length() < 3) {
            editText2.setError("Enter your last name");
            valid = false;
        } else {
            editText2.setError(null);
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText3.setError("Enter a valid email address");
            valid = false;
        } else {
            editText3.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editText4.setError("Between 4 and 10 Alphanumeric Characters");
            valid = false;
        } else {
            editText4.setError(null);
        }
        if (confirmpwd.isEmpty() || !confirmpwd.equals(password)) {
            editText5.setError("Password Mismatch");
            valid = false;
        } else {
            editText5.setError(null);
        }
        return valid;
    }

    private void getValueFromUser() {
        first_name = editText1.getText().toString();
        last_name = editText2.getText().toString();
        email = editText3.getText().toString();
        password = editText4.getText().toString();
        confirmpwd = editText5.getText().toString();
    }

    private void Initialize() {
        editText1 = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        button = (Button) findViewById(R.id.button);
        textView2 = (TextView) findViewById(R.id.textView2);
        textview1 = (TextView) findViewById(R.id.textView1);

        //Animation_Set
        textview1.setAnimation(registerAnim);
    }
}