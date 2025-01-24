package com.kenyaredcross.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtils {

    // Firebase Realtime Database reference
    private static final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

    // Firebase Authentication instance
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Retrieves data of the currently logged-in user.
     *
     * @param callback A callback to handle the retrieved user data.
     */
    public static void getLoggedInUserData(FirebaseCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();

            if (email != null) {
                // Format the email to match the database node key
                String formattedEmail = email.replace("@", "_").replace(".", "_");

                // Query the user's data from Firebase
                usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("User data not found");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
            } else {
                callback.onFailure("User email is null");
            }
        } else {
            callback.onFailure("No logged-in user");
        }
    }

    /**
     * Callback interface to handle user data retrieval.
     */
    public interface FirebaseCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    /**
     * User data model class for Firebase.
     */
    public static class User {
        public String email;
        public String password;
        public String role;
        public long signupTime;
        public String status;
        public String username;

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        public User() {
        }

        // Getters for fields (optional but useful for accessing values)
        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public long getSignupTime() {
            return signupTime;
        }

        public String getStatus() {
            return status;
        }

        public String getUsername() {
            return username;
        }
    }
}
