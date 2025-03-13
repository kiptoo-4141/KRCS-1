package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.AttendanceAdapter;
import com.kenyaredcross.adapters.ClassForAttendanceAdapter;
import com.kenyaredcross.adapters.EnrolledStudentsAdapter;
import com.kenyaredcross.domain_model.AttendanceRecord;
import com.kenyaredcross.domain_model.CourseModel;
import com.kenyaredcross.domain_model.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClassAttendanceActivity extends AppCompatActivity {

    private RecyclerView classAttendanceView;
    private FloatingActionButton fabActivateAttendance;
    private LinearLayout emptyStateLayout;
    private TextView emptyStateText;
    private String currentUserRole;
    private String currentUserEmail;
    private DatabaseReference usersRef, assignedCoursesRef, enrollmentsRef, attendanceRef;
    private FirebaseAuth mAuth;
    private AttendanceAdapter attendanceAdapter;
    private ClassForAttendanceAdapter classForAttendanceAdapter;
    private final List<CourseModel> courseList = new ArrayList<>();
    private final List<AttendanceRecord> attendanceRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        assignedCoursesRef = database.getReference("AssignedCourses");
        enrollmentsRef = database.getReference("Enrollments");
        attendanceRef = database.getReference("ClassAttendance");

        // Initialize views
        classAttendanceView = findViewById(R.id.classAttendanceView);
        fabActivateAttendance = findViewById(R.id.fabActivateAttendance);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Set up RecyclerView
        classAttendanceView.setLayoutManager(new LinearLayoutManager(this));

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
            String emailKey = currentUserEmail.replace(".", "_");

            // Check user role
            usersRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentUserRole = snapshot.child("role").getValue(String.class);
                        setupUIBasedOnRole();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ClassAttendanceActivity.this, "Failed to get user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set up floating action button
        fabActivateAttendance.setOnClickListener(v -> {
            if ("Trainer".equals(currentUserRole)) {
                showTrainerCoursesDialog();
            }
        });
    }

    private void setupUIBasedOnRole() {
        if ("Trainer".equals(currentUserRole)) {
            fabActivateAttendance.setVisibility(View.VISIBLE);
            loadTrainerAttendanceRecords();
        } else if ("Youth".equals(currentUserRole) || "Volunteer".equals(currentUserRole)) {
            fabActivateAttendance.setVisibility(View.GONE);
            loadActiveAttendanceSessions();
        } else {
            // Other roles
            emptyStateLayout.setVisibility(View.VISIBLE);
            classAttendanceView.setVisibility(View.GONE);
            emptyStateText.setText("Class attendance is only available for Trainers, Youth, and Volunteers");
        }
    }

    private void loadTrainerAttendanceRecords() {
        attendanceRef.orderByChild("trainerEmail").equalTo(currentUserEmail)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        attendanceRecords.clear();

                        for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                            AttendanceRecord record = recordSnapshot.getValue(AttendanceRecord.class);
                            if (record != null) {
                                record.setId(recordSnapshot.getKey());
                                attendanceRecords.add(record);
                            }
                        }

                        if (attendanceRecords.isEmpty()) {
                            emptyStateLayout.setVisibility(View.VISIBLE);
                            classAttendanceView.setVisibility(View.GONE);
                            emptyStateText.setText("No attendance records found. Activate attendance for your courses to start tracking.");
                        } else {
                            emptyStateLayout.setVisibility(View.GONE);
                            classAttendanceView.setVisibility(View.VISIBLE);
                            attendanceAdapter = new AttendanceAdapter(attendanceRecords, ClassAttendanceActivity.this, true);
                            classAttendanceView.setAdapter(attendanceAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClassAttendanceActivity.this, "Failed to load attendance records: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadActiveAttendanceSessions() {
        String emailKey = currentUserEmail.replace(".", "_");

        enrollmentsRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> enrolledCourseIds = new ArrayList<>();

                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String status = courseSnapshot.child("status").getValue(String.class);
                    if ("approved".equals(status)) {
                        enrolledCourseIds.add(courseSnapshot.getKey());
                    }
                }

                if (enrolledCourseIds.isEmpty()) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    classAttendanceView.setVisibility(View.GONE);
                    emptyStateText.setText("You are not enrolled in any courses yet. Enroll in courses to view attendance options.");
                } else {
                    loadAttendanceForEnrolledCourses(enrolledCourseIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassAttendanceActivity.this, "Failed to load enrollments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAttendanceForEnrolledCourses(List<String> courseIds) {
        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendanceRecords.clear();

                for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                    AttendanceRecord record = recordSnapshot.getValue(AttendanceRecord.class);
                    if (record != null && courseIds.contains(record.getCourseId())) {
                        record.setId(recordSnapshot.getKey());
                        attendanceRecords.add(record);
                    }
                }

                if (attendanceRecords.isEmpty()) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    classAttendanceView.setVisibility(View.GONE);
                    emptyStateText.setText("No active attendance sessions found for your enrolled courses.");
                } else {
                    emptyStateLayout.setVisibility(View.GONE);
                    classAttendanceView.setVisibility(View.VISIBLE);
                    attendanceAdapter = new AttendanceAdapter(attendanceRecords, ClassAttendanceActivity.this, false);
                    classAttendanceView.setAdapter(attendanceAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassAttendanceActivity.this, "Failed to load attendance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTrainerCoursesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Course to Activate Attendance");

        View view = getLayoutInflater().inflate(R.layout.dialog_course_list, null);
        RecyclerView courseRecyclerView = view.findViewById(R.id.courseRecyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList.clear();
        assignedCoursesRef.orderByChild("trainerEmail").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                            String courseId = courseSnapshot.child("courseId").getValue(String.class);
                            String courseTitle = courseSnapshot.child("courseTitle").getValue(String.class);
                            CourseModel course = new CourseModel(courseId, courseTitle);
                            courseList.add(course);
                        }

                        if (courseList.isEmpty()) {
                            Toast.makeText(ClassAttendanceActivity.this, "No courses assigned to you", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        classForAttendanceAdapter = new ClassForAttendanceAdapter(courseList, course -> {
                            showActivateAttendanceDialog(course);
                        });
                        courseRecyclerView.setAdapter(classForAttendanceAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClassAttendanceActivity.this, "Failed to load courses: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showActivateAttendanceDialog(CourseModel course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activate Attendance for " + course.getTitle());

        View view = getLayoutInflater().inflate(R.layout.dialog_activate_attendance, null);
        EditText durationEditText = view.findViewById(R.id.editTextDuration);
        Button btnActivate = view.findViewById(R.id.btnActivate);

        AlertDialog dialog = builder.setView(view).create();

        btnActivate.setOnClickListener(v -> {
            String durationStr = durationEditText.getText().toString().trim();
            if (durationStr.isEmpty()) {
                durationEditText.setError("Please enter duration");
                return;
            }

            int durationMinutes;
            try {
                durationMinutes = Integer.parseInt(durationStr);
                if (durationMinutes <= 0) {
                    durationEditText.setError("Duration must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                durationEditText.setError("Please enter a valid number");
                return;
            }

            activateAttendance(course, durationMinutes);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void activateAttendance(CourseModel course, int durationMinutes) {
        String emailKey = currentUserEmail.replace(".", "_");
        usersRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String trainerName = snapshot.child("username").getValue(String.class);

                    // Create a new attendance record
                    AttendanceRecord record = new AttendanceRecord();
                    record.setCourseId(course.getId());
                    record.setCourseTitle(course.getTitle());
                    record.setTrainerName(trainerName);
                    record.setTrainerEmail(currentUserEmail);
                    record.setActive(true);
                    record.setDurationMinutes(durationMinutes);

                    // Set activation time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    record.setActivationTime(sdf.format(new Date()));

                    // Calculate expiration time
                    long expirationTime = System.currentTimeMillis() + ((long) durationMinutes * 60 * 1000);
                    record.setExpirationTime(sdf.format(new Date(expirationTime)));

                    // Save to database
                    String recordId = attendanceRef.push().getKey();
                    if (recordId != null) {
                        attendanceRef.child(recordId).setValue(record)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ClassAttendanceActivity.this,
                                            "Attendance activated for " + course.getTitle() + " for " + durationMinutes + " minutes",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(ClassAttendanceActivity.this,
                                                "Failed to activate attendance: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassAttendanceActivity.this, "Failed to get trainer data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signAttendance(AttendanceRecord record) {
        String emailKey = currentUserEmail.replace(".", "_");
        usersRef.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String studentName = snapshot.child("username").getValue(String.class);

                    // Create a new student record
                    Student student = new Student();
                    student.setName(studentName);
                    student.setEmail(currentUserEmail);

                    // Set signing time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    student.setSignTime(sdf.format(new Date()));

                    // Update attendance record
                    DatabaseReference recordRef = attendanceRef.child(record.getId());
                    Map<String, Student> students = record.getStudents();
                    if (students == null) {
                        students = new HashMap<>();
                    }
                    students.put(emailKey, student);
                    recordRef.child("students").setValue(students)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ClassAttendanceActivity.this,
                                        "Attendance signed successfully",
                                        Toast.LENGTH_SHORT).show();

                                // Update UI
                                record.setSignedByCurrentUser(true);
                                if (attendanceAdapter != null) {
                                    attendanceAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(ClassAttendanceActivity.this,
                                            "Failed to sign attendance: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassAttendanceActivity.this, "Failed to get student data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void viewAttendanceDetails(AttendanceRecord record) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attendance Details for " + record.getCourseTitle());

        View view = getLayoutInflater().inflate(R.layout.dialog_attendance_details, null);
        TextView tvCourseTitle = view.findViewById(R.id.tvCourseTitle);
        TextView tvTrainerName = view.findViewById(R.id.tvTrainerName);
        TextView tvActivationTime = view.findViewById(R.id.tvActivationTime);
        TextView tvExpirationTime = view.findViewById(R.id.tvExpirationTime);
        TextView tvStatus = view.findViewById(R.id.tvStatus);
        RecyclerView studentsRecyclerView = view.findViewById(R.id.studentsRecyclerView);
        Button btnClose = view.findViewById(R.id.btnClose);

        tvCourseTitle.setText("Course: " + record.getCourseTitle());
        tvTrainerName.setText("Trainer: " + record.getTrainerName());
        tvActivationTime.setText("Activated: " + record.getActivationTime());
        tvExpirationTime.setText("Expires: " + record.getExpirationTime());
        tvStatus.setText("Status: " + (record.isActive() ? "Active" : "Expired"));

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (record.getStudents() != null && !record.getStudents().isEmpty()) {
            EnrolledStudentsAdapter adapter = new EnrolledStudentsAdapter(new ArrayList<>(record.getStudents().values()));
            studentsRecyclerView.setAdapter(adapter);
        } else {
            TextView tvNoStudents = new TextView(this);
            tvNoStudents.setText("No students have signed this attendance yet");
            tvNoStudents.setPadding(16, 16, 16, 16);
            studentsRecyclerView.addView(tvNoStudents);
        }

        AlertDialog dialog = builder.setView(view).create();
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}