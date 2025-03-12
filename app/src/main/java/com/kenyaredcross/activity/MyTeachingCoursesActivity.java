package com.kenyaredcross.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.ViewPagerAdapter;

public class MyTeachingCoursesActivity extends AppCompatActivity {

    private DatabaseReference coursesRef;
    private String loggedInTrainerEmail;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_teaching_courses);

        // Initialize ViewPager2 and TabLayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Initialize Firebase references
        coursesRef = FirebaseDatabase.getInstance().getReference("AssignedCourses");

        // Retrieve logged-in user's email dynamically
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loggedInTrainerEmail = user.getEmail();
            setupViewPager(); // Set up ViewPager after getting the email
        } else {
            Toast.makeText(this, "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not logged in
        }
    }

    private void setupViewPager() {
        // Create an adapter for ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new CoursesListFragment(loggedInTrainerEmail), "Courses");
        adapter.addFragment(new CoursesWithResourcesFragment(loggedInTrainerEmail), "Resources");
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
    }
}