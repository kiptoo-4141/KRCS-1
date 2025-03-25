package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Supplier extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    private CardView inventoryCard, requests, supplyPayment, receipt, feeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if user profile exists
        checkUserProfile();

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        receipt = findViewById(R.id.supplyReceipt);
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        inventoryCard = findViewById(R.id.supplyInventoryCrd);
        requests = findViewById(R.id.FMRequestsCard);
        supplyPayment = findViewById(R.id.SupplyPayment);
//        reports = findViewById(R.id.SupplyReports);
        feeds = findViewById(R.id.feedss);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        navigationView.setNavigationItemSelectedListener(this);

        // Set click listeners for card views
        inventoryCard.setOnClickListener(v -> startActivity(new Intent(Supplier.this, SupplierInventoryActivity.class)));
        requests.setOnClickListener(v -> {
            if (user != null) {
                String userEmail = user.getEmail();
                Intent intent = new Intent(Supplier.this, SupplyRequestActivity.class);
                intent.putExtra("supplierEmail", userEmail);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
        supplyPayment.setOnClickListener(v -> {
            if (user != null) {
                String userEmail = user.getEmail();
                Intent intent = new Intent(Supplier.this, SupplyPaymentActivity.class);
                intent.putExtra("supplierEmail", userEmail);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
//        reports.setOnClickListener(v -> startActivity(new Intent(Supplier.this, SupplyReportsActivity.class)));
        feeds.setOnClickListener(v -> startActivity(new Intent(Supplier.this, FeedbacksActivity.class)));
        receipt.setOnClickListener(v -> startActivity(new Intent(Supplier.this, SupplyReceiptsActivity.class)));

        // Handle toolbar navigation
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void checkUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        databaseReference.child("Profiles").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showProfilePopup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Supplier.this, "Error checking profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfilePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_update, null);
        builder.setView(dialogView);

        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        TextInputEditText etLocation = dialogView.findViewById(R.id.etLocation);
        TextInputEditText etIdNumber = dialogView.findViewById(R.id.etIdNumber);
        TextInputEditText etDob = dialogView.findViewById(R.id.etDob);
        TextInputEditText etGender = dialogView.findViewById(R.id.etGender);
        TextInputEditText etSkills = dialogView.findViewById(R.id.etSkills);
        TextInputEditText etExperiences = dialogView.findViewById(R.id.etExperiences);
        Button btnSaveProfile = dialogView.findViewById(R.id.btnSaveProfile);

        // Auto-populate email and retrieve full name from Users node
        etEmail.setText(auth.getCurrentUser().getEmail());
        etEmail.setEnabled(false);

        databaseReference.child("Users").child(auth.getCurrentUser().getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    etUsername.setText(username);
                    etUsername.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Supplier.this, "Error retrieving username: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up date picker for DOB
        etDob.setOnClickListener(v -> showDatePicker(etDob));

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        btnSaveProfile.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String idNumber = etIdNumber.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String skills = etSkills.getText().toString().trim();
            String experiences = etExperiences.getText().toString().trim();
            String username = etUsername.getText().toString().trim(); // Get the username value

            if (validateInputs(phone, location, idNumber, dob, gender, skills, experiences)) {
                saveProfile(username, phone, location, idNumber, dob, gender, skills, experiences, alertDialog); // Pass username to saveProfile
            }
        });

        alertDialog.show();
    }

    private void showDatePicker(TextInputEditText etDob) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    // Create a Calendar instance for the selected date
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);

                    // Check if the selected date is in the future
                    if (selectedDate.after(Calendar.getInstance())) {
                        Toast.makeText(Supplier.this, "Date of Birth cannot be in the future", Toast.LENGTH_SHORT).show();
                    } else {
                        // Format the selected date and set it to the etDob field
                        String formattedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etDob.setText(formattedDate);
                    }
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private boolean validateInputs(String phone, String location, String idNumber, String dob, String gender, String skills, String experiences) {
        if (phone.isEmpty() || location.isEmpty() || idNumber.isEmpty() || dob.isEmpty() || gender.isEmpty() || skills.isEmpty() || experiences.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveProfile(String username, String phone, String location, String idNumber, String dob, String gender, String skills, String experiences, AlertDialog dialog) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> profile = new HashMap<>();
        profile.put("email", auth.getCurrentUser().getEmail());
        profile.put("username", username); // Use the passed username value
        profile.put("phone", phone);
        profile.put("location", location);
        profile.put("idNumber", idNumber);
        profile.put("dob", dob);
        profile.put("gender", gender);
        profile.put("skills", skills);
        profile.put("experiences", experiences);

        databaseReference.child("Profiles").child(userId).setValue(profile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Supplier.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(Supplier.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            startActivity(new Intent(Supplier.this, AboutUsActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(Supplier.this, ContactUsActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(Supplier.this, UserProfileActivity.class));
        }else if (id == R.id.nav_help) {
            startActivity(new Intent(Supplier.this, HelpActivity.class));
        } else if (id == R.id.nav_log_out) {
            auth.signOut();
            startActivity(new Intent(Supplier.this, Login.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}