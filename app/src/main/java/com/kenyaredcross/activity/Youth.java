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
import java.util.Calendar;

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

public class Youth extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;

    CardView courses, youthDonationCard, myCourses, events, messaging, receipt, cert;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");

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
    }

    private void showProfilePopup() {
        String userId = user.getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Profile already exists, do nothing
                } else {
                    // Profile does not exist, show the popup
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
            databaseReference.child(userId).setValue(profile);

            Toast.makeText(Youth.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }




    // Corrected: Created Profile Class
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

        // Handle navigation item selections
        if (id == R.id.nav_about_us2) {
            Intent intent = new Intent(Youth.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            // Handle home menu item click if needed
        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(Youth.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            // Handle log out
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Youth.this, Login.class));
            finish(); // Optional: finish this activity to prevent returning to it
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
