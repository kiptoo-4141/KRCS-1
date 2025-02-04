package com.kenyaredcross.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.kenyaredcross.R;

public class MyProfileActivity extends AppCompatActivity {

    private TextView username, email, skills, experience, certifications;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_PROFILE_COMPLETE = "isProfileComplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);

        // Initialize UI elements
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        skills = findViewById(R.id.skills_list);
        experience = findViewById(R.id.experience_list);
        certifications = findViewById(R.id.certifications_list);
        Button logoutButton = findViewById(R.id.logout_button);

        // Load profile data
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isProfileComplete = sharedPreferences.getBoolean(KEY_PROFILE_COMPLETE, false);

        if (!isProfileComplete) {
            // Redirect to Profile Setup Activity
            Intent intent = new Intent(MyProfileActivity.this, ProfileSetupActivity.class);
            startActivity(intent);
            finish(); // Close this activity to prevent navigation back without completing the form
        } else {
            // Load data from shared preferences (or Firebase if applicable)
            username.setText(sharedPreferences.getString("username", "John Doe"));
            email.setText(sharedPreferences.getString("email", "john.doe@example.com"));
            skills.setText(sharedPreferences.getString("skills", "- Leadership\n- Communication\n- First Aid Training"));
            experience.setText(sharedPreferences.getString("experience", "- Volunteered at XYZ Organization"));
            certifications.setText(sharedPreferences.getString("certifications", "- CPR Certified"));
        }

        // Logout button logic
        logoutButton.setOnClickListener(v -> {
            // Clear shared preferences on logout (optional)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            finish();
        });
    }
}
