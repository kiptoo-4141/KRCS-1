package com.kenyaredcross.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class DonationActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    Button donation;
    EditText fname, phone, amount;
    RadioGroup paymentMethodGroup;
    RadioButton selectedPaymentMethod;

    private static final int MAX_DONATION_AMOUNT = 500000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        donation = findViewById(R.id.donateBtn);
        fname = findViewById(R.id.userDonationFullName);
        phone = findViewById(R.id.userDonationPhoneNumber);
        amount = findViewById(R.id.userDonationAmount);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);

        donation.setOnClickListener(v -> {
            String fullName = fname.getText().toString().trim();
            String phoneNumber = phone.getText().toString().trim();
            String donationAmountStr = amount.getText().toString().trim();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(donationAmountStr)) {
                Toast.makeText(DonationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int donationAmount;
            try {
                donationAmount = Integer.parseInt(donationAmountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(DonationActivity.this, "Invalid donation amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (donationAmount > MAX_DONATION_AMOUNT) {
                Toast.makeText(DonationActivity.this, "Donation amount cannot exceed 500,000", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected payment method
            int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
            if (selectedPaymentId == -1) {
                Toast.makeText(DonationActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedPaymentMethod = findViewById(selectedPaymentId);
            String paymentMethod = selectedPaymentMethod.getText().toString();

            // Create unique donation ID
            String donationId = UUID.randomUUID().toString();
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // Create Donation node
            HashMap<String, Object> donationMap = new HashMap<>();
            donationMap.put("fullName", fullName);
            donationMap.put("phoneNumber", phoneNumber);
            donationMap.put("amount", donationAmountStr);
            donationMap.put("paymentMethod", paymentMethod);  // Add payment method
            donationMap.put("donationId", donationId);

            databaseReference.child("Donations").child(donationId).setValue(donationMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Create Donation Report node
                            HashMap<String, Object> donationReportMap = new HashMap<>();
                            donationReportMap.put("fullName", fullName);
                            donationReportMap.put("phoneNumber", phoneNumber);
                            donationReportMap.put("donationId", donationId);
                            donationReportMap.put("donationTime", currentTime);
                            donationReportMap.put("status", "approved");
                            donationReportMap.put("paymentMethod", paymentMethod);  // Add payment method

                            databaseReference.child("DonationReports").child(donationId).setValue(donationReportMap)
                                    .addOnCompleteListener(reportTask -> {
                                        if (reportTask.isSuccessful()) {
                                            Toast.makeText(DonationActivity.this, "Donation Successful", Toast.LENGTH_SHORT).show();
                                            fname.setText("");
                                            phone.setText("");
                                            amount.setText("");
                                            paymentMethodGroup.clearCheck();
                                        } else {
                                            Toast.makeText(DonationActivity.this, "Failed to create donation report", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(DonationActivity.this, "Donation failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
