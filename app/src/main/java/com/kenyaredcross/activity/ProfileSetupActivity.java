package com.kenyaredcross.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.kenyaredcross.R;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText inputName, inputEmail, inputSkills, inputExperience, inputCertifications;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_PROFILE_COMPLETE = "isProfileComplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputSkills = findViewById(R.id.input_skills);
        inputExperience = findViewById(R.id.input_experience);
        inputCertifications = findViewById(R.id.input_certifications);
        Button saveProfileButton = findViewById(R.id.save_profile_button);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        saveProfileButton.setOnClickListener(v -> {
            // Save data in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", inputName.getText().toString());
            editor.putString("email", inputEmail.getText().toString());
            editor.putString("skills", inputSkills.getText().toString());
            editor.putString("experience", inputExperience.getText().toString());
            editor.putString("certifications", inputCertifications.getText().toString());
            editor.putBoolean(KEY_PROFILE_COMPLETE, true);
            editor.apply();

            // Redirect to MyProfileActivity
            startActivity(new Intent(ProfileSetupActivity.this, MyProfileActivity.class));
            finish();
        });
    }
}
