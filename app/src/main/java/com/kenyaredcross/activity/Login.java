package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

public class Login extends AppCompatActivity {

    // UI elements
    EditText loginEmail, loginPassword;
    Button buttonLogin;
    TextView textViewSignUp, forgotPassword;
    TextInputLayout passwordInputLayout;

    // Role selection dropdown
    String[] role = {"Youth", "Volunteer", "Service Manager", "Trainer", "Coordinator", "Finance Manager", "Inventory Manager", "Supplier"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    // Firebase references
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    String selectedRole = "Youth";  // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        loginEmail = findViewById(R.id.LoginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        buttonLogin = findViewById(R.id.loginButton);
        textViewSignUp = findViewById(R.id.signupoption);
        forgotPassword = findViewById(R.id.forgotPassword);
//        passwordInputLayout = findViewById(R.id.loginPassword);

        // Initialize Role Dropdown
        autoCompleteTextView = findViewById(R.id.roleSelection);
        adapterItems = new ArrayAdapter<>(this, R.layout.dropdown_role_item, role);
        autoCompleteTextView.setAdapter(adapterItems);

        // Handle role selection
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedRole = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(Login.this, "Role: " + selectedRole, Toast.LENGTH_SHORT).show();

            if (selectedRole.equals("Youth")) {
                textViewSignUp.setVisibility(View.VISIBLE);
            } else {
                textViewSignUp.setVisibility(View.VISIBLE);
            }
        });

        // Handle login button click
        buttonLogin.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                checkUserInDatabase(email);
                            }
                        } else {
                            Toast.makeText(Login.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Redirect to Sign-Up activity if clicked
        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // Redirect to Forgot Password activity if clicked
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserInDatabase(String userEmail) {
        String sanitizedEmail = userEmail.replace(".", "_");
        reference = FirebaseDatabase.getInstance().getReference("Users");  // Assuming all users are stored under "Users" node
        reference.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Debugging: Log all data fetched from the database for this user
                    Toast.makeText(Login.this, "User found in database", Toast.LENGTH_SHORT).show();

                    // Check if the 'role' and 'status' fields exist
                    if (dataSnapshot.hasChild("role") && dataSnapshot.hasChild("status")) {
                        String storedRole = dataSnapshot.child("role").getValue(String.class);
                        String status = dataSnapshot.child("status").getValue(String.class);

                        // Debugging: Show what the role and status values are
                        Toast.makeText(Login.this, "Role: " + storedRole + ", Status: " + status, Toast.LENGTH_SHORT).show();

                        // Check if the stored role matches the selected role
                        if (storedRole != null && storedRole.equals(selectedRole)) {
                            // Check if the user's status is approved
                            if (status != null && status.equals("approved")) {
                                Toast.makeText(Login.this, "Login Success. Role: " + storedRole, Toast.LENGTH_SHORT).show();
                                redirectToRoleActivity();  // Redirect user based on their role
                            } else {
                                Toast.makeText(Login.this, "Your account is not approved by the admin.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login.this, "Role mismatch. You are not a " + selectedRole, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Debugging: Let the user know the required fields are missing
                        Toast.makeText(Login.this, "Error: Role or Status field is missing in the database for this user.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User doesn't exist in the database
                    loginEmail.setError("User does not exist");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Redirect user to different activities based on role
    private void redirectToRoleActivity() {
        Intent intent;
        switch (selectedRole) {
            case "Youth":
                intent = new Intent(Login.this, Youth.class);
                break;
            case "Volunteer":
                intent = new Intent(Login.this, Volunteer.class);
                break;
            case "Service Manager":
                intent = new Intent(Login.this, ServiceManager.class);
                break;
            case "Trainer":
                intent = new Intent(Login.this, Trainer.class);
                break;
            case "Coordinator":
                intent = new Intent(Login.this, Coordinator.class);
                break;
            case "Finance Manager":
                intent = new Intent(Login.this, FinanceManager.class);
                break;
            case "Inventory Manager":
                intent = new Intent(Login.this, InventoryManager.class);
                break;
            case "Supplier":
                intent = new Intent(Login.this, Supplier.class);
                break;
            default:
                Toast.makeText(Login.this, "Role not recognized", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
}