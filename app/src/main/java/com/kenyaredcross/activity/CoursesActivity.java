package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.CoursesAdapter;
import com.kenyaredcross.domain_model.CoursesModel;

public class CoursesActivity extends AppCompatActivity {

    TextView feedback;

    RecyclerView rvCourses;
    CoursesAdapter  CoursesAdapter;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        feedback = findViewById(R.id.feedbackLink);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoursesActivity.this, FeedbacksActivity.class);
                intent.putExtra("activityName", "CoursesActivity"); // Add this line to pass the activity name
                startActivity(intent);
            }
        });




        rvCourses = findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CoursesModel> options =
                new FirebaseRecyclerOptions.Builder<CoursesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Courses"),CoursesModel.class)
                        .build();

        CoursesAdapter= new CoursesAdapter(options);
        rvCourses.setAdapter(CoursesAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        CoursesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CoursesAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                txtSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void txtSearch (String str){

        FirebaseRecyclerOptions<CoursesModel> options =
                new FirebaseRecyclerOptions.Builder<CoursesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Courses").orderByChild("courseTitle")
                                .startAt(str).endAt(str+"~"),CoursesModel.class)
                        .build();
        CoursesAdapter = new CoursesAdapter(options);
        CoursesAdapter.startListening();
        rvCourses.setAdapter(CoursesAdapter);
    }

}