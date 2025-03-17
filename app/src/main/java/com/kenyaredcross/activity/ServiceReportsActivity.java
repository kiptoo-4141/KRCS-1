package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.adapters.ReportAdapter;
import com.kenyaredcross.domain_model.ReportItem;

import java.util.ArrayList;
import java.util.List;

public class ServiceReportsActivity extends AppCompatActivity {
    private RecyclerView rvEnrollments, rvAssignedCourses, rvCompletedCourses, rvCoursePayments;
    private ReportAdapter enrollmentAdapter, assignedCourseAdapter, completedCourseAdapter, coursePaymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reports);

        // Initialize RecyclerViews
        rvEnrollments = findViewById(R.id.rvEnrollments);
        rvAssignedCourses = findViewById(R.id.rvAssignedCourses);
        rvCompletedCourses = findViewById(R.id.rvCompletedCourses);
        rvCoursePayments = findViewById(R.id.rvCoursePayments);

        // Set Layout Managers
        rvEnrollments.setLayoutManager(new LinearLayoutManager(this));
        rvAssignedCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCompletedCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCoursePayments.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapters
        enrollmentAdapter = new ReportAdapter(getEnrollments());
        assignedCourseAdapter = new ReportAdapter(getAssignedCourses());
        completedCourseAdapter = new ReportAdapter(getCompletedCourses());
        coursePaymentAdapter = new ReportAdapter(getCoursePayments());

        // Set Adapters
        rvEnrollments.setAdapter(enrollmentAdapter);
        rvAssignedCourses.setAdapter(assignedCourseAdapter);
        rvCompletedCourses.setAdapter(completedCourseAdapter);
        rvCoursePayments.setAdapter(coursePaymentAdapter);

        // Handle Expand/Collapse
        setupExpandableSection(R.id.tvEnrollmentsHeader, R.id.rvEnrollments);
        setupExpandableSection(R.id.tvAssignedCoursesHeader, R.id.rvAssignedCourses);
        setupExpandableSection(R.id.tvCompletedCoursesHeader, R.id.rvCompletedCourses);
        setupExpandableSection(R.id.tvCoursePaymentsHeader, R.id.rvCoursePayments);
    }

    private void setupExpandableSection(int headerId, int recyclerViewId) {
        TextView header = findViewById(headerId);
        RecyclerView recyclerView = findViewById(recyclerViewId);

        header.setOnClickListener(v -> {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    // Dummy data methods
    private List<ReportItem> getEnrollments() {
        List<ReportItem> enrollments = new ArrayList<>();
        enrollments.add(new ReportItem("First Responder Training", "Advanced training for emergencies", "6 hours", "approved"));
        enrollments.add(new ReportItem("Volunteer Leadership", "Develop leadership skills", "2.5 hours", "approved"));
        return enrollments;
    }

    private List<ReportItem> getAssignedCourses() {
        List<ReportItem> assignedCourses = new ArrayList<>();
        assignedCourses.add(new ReportItem("Health and Hygiene", "Improve personal health", "1.5 hours", "Andrew", true));
        assignedCourses.add(new ReportItem("Basic First Aid", "Learn first aid basics", "4 hours", "Andrew", true));
        return assignedCourses;
    }

    private List<ReportItem> getCompletedCourses() {
        List<ReportItem> completedCourses = new ArrayList<>();
        completedCourses.add(new ReportItem("Basic First Aid", "Learn first aid basics", "4 hours", "completed"));
        return completedCourses;
    }

    private List<ReportItem> getCoursePayments() {
        List<ReportItem> coursePayments = new ArrayList<>();
        coursePayments.add(new ReportItem("Basic First Aid", "M-Pesa", "Approved", "2025-02-28", "10:20:34", 1200));
        coursePayments.add(new ReportItem("First Responder Training", "M-Pesa", "Approved", "2025-03-09", "09:11:14", 7500));
        return coursePayments;
    }
}