package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.ViewPagerAdapter;

public class MyCoursesActivity extends AppCompatActivity {

    private TextView feedback, resc;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Ensure user is authenticated
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                // Replace '.' in email with '_' for Firebase node key
                String userEmailKey = userEmail.replace(".", "_");

                // Set up ViewPager2 and TabLayout
                ViewPager2 viewPager = findViewById(R.id.viewPager);
                TabLayout tabLayout = findViewById(R.id.tabLayout);

                // Create an adapter for ViewPager2
                ViewPagerAdapter adapter = new ViewPagerAdapter(this);
                adapter.addFragment(new EnrolledCoursesFragment(userEmailKey), "Enrolled Courses");
                adapter.addFragment(new CoursesWithResourcesFragment(userEmailKey), "Resources");
                viewPager.setAdapter(adapter);

                // Connect TabLayout with ViewPager2
                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                    tab.setText(adapter.getPageTitle(position));
                }).attach();

                // Add swipe animation to guide the user
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position == 1) {
                            // Show a toast or animation when the user swipes to the resources tab
                            Toast.makeText(MyCoursesActivity.this, "Swipe left to go back to courses", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        // Apply window insets to avoid overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        resc = findViewById(R.id.coursesresources);
//        resc.setOnClickListener(v -> {
//            Intent intent = new Intent(MyCoursesActivity.this, CoursesResourcesActivity.class);
//            startActivity(intent);
//        });

        feedback = findViewById(R.id.feedbackLink);
        feedback.setOnClickListener(view -> {
            Intent intent = new Intent(MyCoursesActivity.this, FeedbacksActivity.class);
            intent.putExtra("activityName", "MyCoursesActivity");
            startActivity(intent);
        });
    }
}