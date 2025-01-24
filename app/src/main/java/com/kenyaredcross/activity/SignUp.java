package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    // Define UI elements
    EditText signupusername, signupemail, signuppassword, signupconfirmpassword;
    Button signupbutton;
    TextView loginredirect;
    AutoCompleteTextView roleSelection;

    // Role selection options
    String[] roles = {"Youth", "Volunteer", "Service Manager", "Trainer", "Coordinator", "Finance Manager", "Inventory Manager", "Supplier"};
    ArrayAdapter<String> adapterItems;
    String selectedRole = "Youth";  // Default role

    // Firebase references
    private FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        signupusername = findViewById(R.id.SignUpUserName);
        signupemail = findViewById(R.id.SignUpEmail);
        signuppassword = findViewById(R.id.SignUpPassword);
        signupconfirmpassword = findViewById(R.id.SignUpConfirmPassword);
        signupbutton = findViewById(R.id.SignUpButton);
        loginredirect = findViewById(R.id.LoginRedirect);
        roleSelection = findViewById(R.id.roleSelection2);

        // Initialize role dropdown
        adapterItems = new ArrayAdapter<>(this, R.layout.dropdown_role_item, roles);
        roleSelection.setAdapter(adapterItems);

        // Handle role selection
        roleSelection.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedRole = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(SignUp.this, "Selected Role: " + selectedRole, Toast.LENGTH_SHORT).show();
        });

        // Redirect to login activity
        loginredirect.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        });

        // Handle sign-up button click
        signupbutton.setOnClickListener(v -> {
            String email = signupemail.getText().toString().trim();
            String username = signupusername.getText().toString().trim();
            String password = signuppassword.getText().toString().trim();
            String confirmPassword = signupconfirmpassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String sanitizedEmail = email.replace(".", "_");  // Use sanitized email to store user
                                long currentTime = System.currentTimeMillis();   // Get current timestamp for signupTime

                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("username", username);
                                userInfo.put("email", email);
                                userInfo.put("password", password); // Consider hashing the password
                                userInfo.put("role", selectedRole);
                                userInfo.put("signupTime", currentTime); // Use current timestamp
                                userInfo.put("status", "pending");     // Automatically set status to 'pending'

                                reference = FirebaseDatabase.getInstance().getReference("Users");

                                // Save the user info using the sanitized email
                                reference.child(sanitizedEmail).setValue(userInfo)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUp.this, Login.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignUp.this, "Database Error: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUp.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Method to log user logout time
    public void logUserLogout(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.child("logoutTime").setValue(System.currentTimeMillis())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Logout", "Logout time updated for: " + userId);
                    } else {
                        Log.e("LogoutError", "Failed to update logout time: ", task.getException());
                    }
                });
    }
}
