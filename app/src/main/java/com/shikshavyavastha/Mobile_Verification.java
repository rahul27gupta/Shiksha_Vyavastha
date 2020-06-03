package com.shikshavyavastha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Mobile_Verification extends AppCompatActivity {
    private EditText mobile_no, otp;
    private Button send_otp, create_acc;
    private String userID;
    private boolean verificationInProgress = false;
    private CountryCodePicker codePicker;
    private String verificationID;
    private int counter = 60;
    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile__verification);

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference();

        onDeclare();
        OTP();

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send_otp.getText().toString() == "Send OTP") {
                    if (!mobile_no.getText().toString().isEmpty() && mobile_no.getText().toString().length() == 10) {
                        String mMobile_no = "+" + codePicker.getSelectedCountryCode() + mobile_no.getText().toString();
                        requestOtp(mMobile_no);
                    } else {
                        mobile_no.setError("Mobile Number is Not Valid");
                    }
                } else {
                    String mMobile_no = "+" + codePicker.getSelectedCountryCode() + mobile_no.getText().toString();
                    resendOtp(mMobile_no, token);
                }

            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserOtp = otp.getText().toString();
                if (!UserOtp.isEmpty() && UserOtp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, UserOtp);
                    verifyAuth(credential);
                } else {
                    otp.setError("Valid OTP is Required.");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void requestOtp(String mMobile_no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mMobile_no,
                60L,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void resendOtp(String mMobile_no,
                           PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mMobile_no,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    private void OTP() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationID = s;
                token = forceResendingToken;
                send_otp.setEnabled(false);
                send_otp.setBackgroundDrawable(getResources().getDrawable(R.drawable.disable_back_gradient));
                new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        send_otp.setText(String.valueOf("00 : " + counter));
                        counter--;
                    }

                    @Override
                    public void onFinish() {
                        counter = 60;
                        send_otp.setEnabled(true);
                        send_otp.setText("Resend OTP");
                        send_otp.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gradient));
                    }
                }.start();

                otp.setVisibility(View.VISIBLE);
                create_acc.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void onDeclare() {
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        otp = (EditText) findViewById(R.id.otp);
        send_otp = (Button) findViewById(R.id.send_otp);
        create_acc = (Button) findViewById(R.id.create_account);
        codePicker = findViewById(R.id.ccp);
    }

    private void verifyAuth(final PhoneAuthCredential credential) {
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userID = mAuth.getCurrentUser().getUid();
                    reference.child("Users").child(userID).child("mobile_no").setValue(mobile_no.getText().toString());
                    Intent intent = new Intent(Mobile_Verification.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error!" + e.getMessage() ,Toast.LENGTH_LONG).show();
            }
        });
    }

}