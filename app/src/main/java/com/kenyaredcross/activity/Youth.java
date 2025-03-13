package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

import java.util.Calendar;

public class Youth extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private CardView courses, youthDonationCard, myCourses, events, messaging, receipt, cert, attendanceCard;
    private Toolbar toolbar;

    private ValueEventListener enrollmentsListener;
    private ValueEventListener completedCoursesListener;
    private ValueEventListener donationsListener;
    private ValueEventListener feedbacksListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        cert = findViewById(R.id.youthCertificationCard);
        cert.setOnClickListener(v -> startActivity(new Intent(Youth.this, CertActivity.class)));

        messaging = findViewById(R.id.youthMessagingCard);
        messaging.setOnClickListener(v -> startActivity(new Intent(Youth.this, FeedbacksActivity.class)));

        receipt = findViewById(R.id.youthReceiptCards);
        receipt.setOnClickListener(v -> startActivity(new Intent(Youth.this, YTReceiptsActivity.class)));

        events = findViewById(R.id.events);
        events.setOnClickListener(v -> startActivity(new Intent(Youth.this, EventsActivity.class)));

        myCourses = findViewById(R.id.mycourses);
        myCourses.setOnClickListener(v -> startActivity(new Intent(Youth.this, MyCoursesActivity.class)));

        attendanceCard = findViewById(R.id.classAttendanceCard);
        attendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Youth.this, ClassAttendanceActivity.class);
                startActivity(intent);
            }
        });

        courses = findViewById(R.id.courses);
        courses.setOnClickListener(v -> startActivity(new Intent(Youth.this, CoursesActivity.class)));

        youthDonationCard = findViewById(R.id.youthDonationCard);
        youthDonationCard.setOnClickListener(v -> startActivity(new Intent(Youth.this, DonationActivity.class)));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Show profile popup when the user first logs in
        showProfilePopup();

        // Attach listeners to relevant nodes
        attachDatabaseListeners();
    }

    private void attachDatabaseListeners() {
        String userId = user.getUid();
        String userEmail = user.getEmail().replace(".", "_");

        // Listen for changes in Enrollments
        enrollmentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showNotification("Your course enrollment status has been updated.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("Enrollments").child(userEmail).addValueEventListener(enrollmentsListener);

        // Listen for changes in CompletedCourses
        completedCoursesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showNotification("Your course completion status has been updated.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("CompletedCourses").child(userEmail).addValueEventListener(completedCoursesListener);

        // Listen for changes in Donations
        donationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showNotification("Your donation status has been updated.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("Donations").orderByChild("email").equalTo(user.getEmail()).addValueEventListener(donationsListener);

        // Listen for changes in Feedbacks
        feedbacksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showNotification("You have new feedback.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("Feedbacks").orderByChild("senderEmail").equalTo(user.getEmail()).addValueEventListener(feedbacksListener);
    }

    private void showNotification(String message) {
        // Check if the activity is finishing or destroyed
        if (isFinishing() || isDestroyed()) {
            return; // Do not show the dialog if the activity is no longer valid
        }

        // Use runOnUiThread to ensure the dialog is shown on the main thread
        runOnUiThread(() -> {
            // Check again before showing the dialog
            if (!isFinishing() && !isDestroyed()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notification");
                builder.setMessage(message);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.setCancelable(false);

                // Show the dialog
                builder.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove all listeners to avoid memory leaks
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Check if listeners are initialized before removing them
        if (enrollmentsListener != null) {
            dbRef.child("Enrollments").removeEventListener(enrollmentsListener);
        }
        if (completedCoursesListener != null) {
            dbRef.child("CompletedCourses").removeEventListener(completedCoursesListener);
        }
        if (donationsListener != null) {
            dbRef.child("Donations").removeEventListener(donationsListener);
        }
        if (feedbacksListener != null) {
            dbRef.child("Feedbacks").removeEventListener(feedbacksListener);
        }
    }

    private void showProfilePopup() {
        String userId = user.getUid();

        databaseReference.child("Profiles").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showProfileDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error checking profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_update, null);
        builder.setView(dialogView);

        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        EditText etTown = dialogView.findViewById(R.id.etTown);
        EditText etIdNumber = dialogView.findViewById(R.id.etIdNumber);
        EditText etDob = dialogView.findViewById(R.id.etDob);
        Button btnSaveProfile = dialogView.findViewById(R.id.btnSaveProfile);

        // Set up date picker for DOB
        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year1, month1, dayOfMonth);

                if (selectedDate.after(Calendar.getInstance())) {
                    Toast.makeText(Youth.this, "Date of Birth cannot be in the future", Toast.LENGTH_SHORT).show();
                } else {
                    etDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                }
            }, year, month, day);
            datePickerDialog.show();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        btnSaveProfile.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String town = etTown.getText().toString().trim();
            String idNumber = etIdNumber.getText().toString().trim();
            String dob = etDob.getText().toString().trim();

            if (phone.isEmpty() || town.isEmpty() || idNumber.isEmpty() || dob.isEmpty()) {
                Toast.makeText(Youth.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            Profile profile = new Profile(user.getEmail(), user.getDisplayName(), phone, town, idNumber, dob);
            databaseReference.child("Profiles").child(userId).setValue(profile);

            Toast.makeText(Youth.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    public static class Profile {
        private String email, username, phone, town, idNumber, dob;

        public Profile() {
            // Required empty constructor for Firebase
        }

        public Profile(String email, String username, String phone, String town, String idNumber, String dob) {
            this.email = email;
            this.username = username;
            this.phone = phone;
            this.town = town;
            this.idNumber = idNumber;
            this.dob = dob;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPhone() {
            return phone;
        }

        public String getTown() {
            return town;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public String getDob() {
            return dob;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            Intent intent = new Intent(Youth.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            // Handle home menu item click if needed
        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(Youth.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (id ==R.id.nav_help) {
            startActivity(new Intent(Youth.this, HelpActivity.class));
        }else if (id == R.id.nav_log_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Youth.this, Login.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}