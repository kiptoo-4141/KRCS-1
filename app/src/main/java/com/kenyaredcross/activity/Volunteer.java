package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;

import java.util.HashMap;
import java.util.Map;

public class Volunteer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    CardView group, courses, profile, myCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");

        // Check if user profile exists
        checkUserProfile();

        // Set up window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myCourses = findViewById(R.id.volunteermycourses);
        myCourses.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, MyCoursesActivity.class)));

        profile = findViewById(R.id.profileCard);
        profile.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, MyProfileActivity.class)));

        group = findViewById(R.id.groupCard);
        group.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, MyGroupsActivity.class)));

        courses = findViewById(R.id.volunteercourses);
        courses.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, CoursesActivity.class)));

        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkUserProfile() {
        String userId = auth.getCurrentUser().getUid();

        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // User profile exists, do nothing
            } else {
                // Show popup form for user details
                showUserProfilePopup();
            }
        });
    }

    private void showUserProfilePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Complete Your Profile");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_user_profile, null);
        builder.setView(dialogView);

        EditText fullName = dialogView.findViewById(R.id.fullName);
        EditText email = dialogView.findViewById(R.id.email);
        EditText location = dialogView.findViewById(R.id.location);
        EditText specialization = dialogView.findViewById(R.id.specialization);
        EditText phone = dialogView.findViewById(R.id.phone);

        email.setText(auth.getCurrentUser().getEmail());
        email.setEnabled(false);

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                String name = fullName.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userLocation = location.getText().toString().trim();
                String userSpecialization = specialization.getText().toString().trim();
                String userPhone = phone.getText().toString().trim();

                if (name.isEmpty() || userLocation.isEmpty() || userSpecialization.isEmpty() || userPhone.isEmpty()) {
                    Toast.makeText(Volunteer.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserProfile(name, userEmail, userLocation, userSpecialization, userPhone);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void saveUserProfile(String name, String email, String location, String specialization, String phone) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("location", location);
        userData.put("specialization", specialization);
        userData.put("phone", phone);

        databaseReference.child(userId).setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Volunteer.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Volunteer.this, "Failed to save profile. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            startActivity(new Intent(Volunteer.this, AboutUsActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(Volunteer.this, ContactUsActivity.class));
        } else if (id == R.id.nav_log_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Volunteer.this, Login.class));
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
