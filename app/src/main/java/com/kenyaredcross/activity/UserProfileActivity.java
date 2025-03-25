package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.UserProfile;

public class UserProfileActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, emailEditText, dobEditText, genderEditText,
            idNumberEditText, phoneEditText, locationEditText, skillsEditText, experiencesEditText;
    private Button saveButton;
    private DatabaseReference profilesRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize views
        initializeViews();

        // Initialize Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        profilesRef = FirebaseDatabase.getInstance().getReference("Profiles");

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user profile
        loadUserProfile();

        // Set up save button
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        dobEditText = findViewById(R.id.dobEditText);
        genderEditText = findViewById(R.id.genderEditText);
        idNumberEditText = findViewById(R.id.idNumberEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        locationEditText = findViewById(R.id.locationEditText);
        skillsEditText = findViewById(R.id.skillsEditText);
        experiencesEditText = findViewById(R.id.experiencesEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void loadUserProfile() {
        profilesRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile profile = snapshot.getValue(UserProfile.class);
                    if (profile != null) {
                        profile.setUserId(currentUser.getUid());
                        populateProfileFields(profile);
                    }
                } else {
                    // Create new profile if doesn't exist
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(currentUser.getUid());
                    newProfile.setEmail(currentUser.getEmail());
                    populateProfileFields(newProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateProfileFields(UserProfile profile) {
        usernameEditText.setText(profile.getUsername() != null ? profile.getUsername() : "");
        emailEditText.setText(profile.getEmail() != null ? profile.getEmail() : currentUser.getEmail());
        dobEditText.setText(profile.getDob() != null ? profile.getDob() : "");
        genderEditText.setText(profile.getGender() != null ? profile.getGender() : "");
        idNumberEditText.setText(profile.getIdNumber() != null ? profile.getIdNumber() : "");
        phoneEditText.setText(profile.getPhone() != null ? profile.getPhone() : "");
        locationEditText.setText(profile.getLocation() != null ? profile.getLocation() : "");
        skillsEditText.setText(profile.getSkills() != null ? profile.getSkills() : "");
        experiencesEditText.setText(profile.getExperiences() != null ? profile.getExperiences() : "");
    }

    private void saveProfile() {
        UserProfile profile = new UserProfile();
        profile.setUserId(currentUser.getUid());
        profile.setUsername(usernameEditText.getText().toString().trim());
        profile.setEmail(emailEditText.getText().toString().trim());
        profile.setDob(dobEditText.getText().toString().trim());
        profile.setGender(genderEditText.getText().toString().trim());
        profile.setIdNumber(idNumberEditText.getText().toString().trim());
        profile.setPhone(phoneEditText.getText().toString().trim());
        profile.setLocation(locationEditText.getText().toString().trim());
        profile.setSkills(skillsEditText.getText().toString().trim());
        profile.setExperiences(experiencesEditText.getText().toString().trim());

        // Validate required fields
        if (profile.getUsername().isEmpty()) {
            usernameEditText.setError("Username is required");
            return;
        }

        profilesRef.child(currentUser.getUid()).setValue(profile)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(UserProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(UserProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }
}