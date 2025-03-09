package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.kenyaredcross.R;

public class IntroActivity extends AppCompatActivity {

    Button btnGetStarted;
    FirebaseAuth mAuth;
    ImageView logoImageView;
    TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);

        // Set padding for system insets (e.g., status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        btnGetStarted = findViewById(R.id.btnGetStarted);
        logoImageView = findViewById(R.id.imageView10);
        welcomeTextView = findViewById(R.id.textView27);

        // Load animations
        Animation heartbeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heartbeat);
        Animation glowAnimation = AnimationUtils.loadAnimation(this, R.anim.glow);

        // Apply heartbeat animation to the "Get Started" button
        btnGetStarted.startAnimation(heartbeatAnimation);

        // Apply glow animation to the logo
        logoImageView.startAnimation(glowAnimation);

        // Set onClickListener for the "Get Started" button
        btnGetStarted.setOnClickListener(v -> {
            // Log out the current user
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();  // Sign out the current user
            }

            // Redirect to Login activity
            Intent intent = new Intent(IntroActivity.this, Login.class);
            startActivity(intent);
            finish();  // Finish the IntroActivity so the user cannot return to it
        });
    }
}