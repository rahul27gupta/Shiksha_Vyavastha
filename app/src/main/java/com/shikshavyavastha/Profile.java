package com.shikshavyavastha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Calendar;
import java.util.Locale;

public class Profile extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private String userID, Gender;
    private EditText mName, mEmail, mPhone, mAge;
    private TextView mAccount;
    private RadioGroup mGender;
    private RadioButton mSex, mMale, mFemale;
    private Button mUpdate, mLogout;
    private CountryCodePicker codePicker;
    private Long ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Declare();
        ConfigureProfileData();

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectGender();
                UpdateProfile();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Profile.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void UpdateProfile() {
        if (Validate()) {
            ConfigureFirebase();
        } else {
        }
    }

    private void ConfigureFirebase() {
        String mCountry = codePicker.getSelectedCountryNameCode().toString();
        reference.child("gender").setValue(Gender);
        reference.child("age").setValue(mAge.getText().toString());
        reference.child("country").setValue(mCountry);
        Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG).show();
    }

    private boolean Validate() {
        boolean valid = true;
        if (mAge.getText().toString().equals("0")) {
            mAge.setError("Enter Age");
            valid = false;
        } else {
            mAge.setError(null);
        }
        if (Gender == null) {
            Toast.makeText(getApplicationContext(), "Select Gender", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void SelectGender() {
        int selectedId = mGender.getCheckedRadioButtonId();
        mSex = (RadioButton) findViewById(selectedId);
        if (selectedId == -1) {
            Gender = null;
        } else {
            Gender = mSex.getText().toString();
        }
    }

    private void Declare() {
        mName = findViewById(R.id.profile_name);
        mEmail = findViewById(R.id.profile_email);
        mPhone = findViewById(R.id.profile_phone);
        mAge = findViewById(R.id.age);
        mGender = findViewById(R.id.gender);
        mUpdate = findViewById(R.id.update_profile);
        mLogout = findViewById(R.id.logout);
        mMale = findViewById(R.id.male);
        mFemale = findViewById(R.id.female);
        codePicker = findViewById(R.id.ccp_second);
        mAccount = findViewById(R.id.account);
    }

    private void ConfigureProfileData() {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String f_name = dataSnapshot.child("first_Name").getValue().toString();
                    String l_name = dataSnapshot.child("last_Name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String phone = dataSnapshot.child("mobile_no").getValue().toString();
                    String age = dataSnapshot.child("age").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String timestamp = dataSnapshot.child("ts").getValue().toString();
                    ts = Long.parseLong(timestamp);
                    getDate(ts);
                    if (gender.equals("Male")) {
                        mMale.setChecked(true);
                    }
                    if (gender.equals("Female")) {
                        mFemale.setChecked(true);
                    }
                    if (gender.equals("null")) {
                        mMale.setChecked(false);
                        mFemale.setChecked(false);
                    }
                    mName.setText(f_name + " " + l_name);
                    mEmail.setText(email);
                    mPhone.setText(phone);
                    mAge.setText(age);
                    codePicker.setCountryForNameCode(country);
                } else {
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDate(Long ts) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(ts * 1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm a", cal).toString();
        mAccount.setText("Account Created On " + date);
        return date;
    }
}