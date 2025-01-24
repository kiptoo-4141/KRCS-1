package com.kenyaredcross.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.SearchUserRecyclerAdapter;
import com.kenyaredcross.domain_model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class MessagingSearchUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserRecyclerAdapter adapter;
    private List<UserModel> userList;
    private EditText searchEditText;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_search_users);

        recyclerView = findViewById(R.id.searchedUsersView);
        searchEditText = findViewById(R.id.search_input);
        userList = new ArrayList<>();

        // Initialize Firebase Realtime Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Fetch users from Firebase and populate the RecyclerView
        fetchUsersFromFirebase();

        // Initialize the adapter and pass the context
        adapter = new SearchUserRecyclerAdapter(this, userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void fetchUsersFromFirebase() {
        // Retrieve all users from Firebase under the 'Users' node
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();  // Clear the current list before adding new data

                // Loop through each user node in the 'Users' node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }

                // Update the RecyclerView with the fetched user data
                adapter.updateData(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MessagingSearchUsersActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        List<UserModel> filteredList = new ArrayList<>();

        for (UserModel user : userList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    user.getRole().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        // Update the RecyclerView with the filtered list
        adapter.updateData(filteredList);
    }

    public void updateUserList(List<UserModel> newUserList) {
        adapter.updateData(newUserList);
    }
}
