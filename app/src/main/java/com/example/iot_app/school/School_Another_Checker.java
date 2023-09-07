package com.example.iot_app.school;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Application;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.iot_app.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class School_Another_Checker extends AppCompatActivity {
    private EditText editTextStudentName, editTextTakerName, editTextParentMobile;
    public String textStudentName;
    public String textTakerName;
    public String textMobile;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "Another_Checker";
    Button btn_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_another_checker);
        FirebaseApp.initializeApp(this);
        getSupportActionBar().setTitle("Verification");

        progressBar = findViewById(R.id.progressBar);
        editTextStudentName = findViewById(R.id.editText_parent_student_name);
        editTextTakerName = findViewById(R.id.editText_parent_taker_name);
        editTextParentMobile = findViewById(R.id.editText_parent_mobile_number);

        btn_scan = findViewById(R.id.button_taker_submit);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textStudentName = editTextStudentName.getText().toString();
                textTakerName = editTextTakerName.getText().toString();
                textMobile = editTextParentMobile.getText().toString();
                FirebaseFirestore.getInstance().collection("Parent").document("Student Details").collection(textStudentName)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                boolean studentFound = false;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    // Fetch the data here and do something with it
                                    Map<String, Object> data = document.getData();
                                    // Access specific fields by key
                                    String field1 = document.getString("Mobile");
                                    // Add these logs for debugging
                                    Log.d(TAG, "field1: " + field1);
                                    Log.d(TAG, "textMobile: " + textMobile);
                                    if (field1.equalsIgnoreCase(textMobile.trim())) {
                                        studentFound = true;
                                        Toast.makeText(School_Another_Checker.this, "Student Found", Toast.LENGTH_SHORT).show();
                                        // Check for SMS permissions
                                        if (ContextCompat.checkSelfPermission(School_Another_Checker.this, Manifest.permission.SEND_SMS)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            // Request SMS permissions if not granted
                                            ActivityCompat.requestPermissions(School_Another_Checker.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                                        } else {
                                            // Send SMS message
                                            sendSMS(textMobile, textTakerName + " has arrived at school to escort your child " + textStudentName + ". If you agree, please write back 'OK'."); // Replace "Your SMS message" with your actual SMS message
                                        }
                                    }
                                    else {
                                        Toast.makeText(School_Another_Checker.this, "Entity not matched", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (!studentFound) {
                                    Toast.makeText(School_Another_Checker.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Exception e = task.getException();
                                if (e != null) {
                                    Log.e(TAG, "Error querying Firestore: " + e.getMessage());
                                    Toast.makeText(School_Another_Checker.this, "Failed to Fetch  Data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error sending SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}