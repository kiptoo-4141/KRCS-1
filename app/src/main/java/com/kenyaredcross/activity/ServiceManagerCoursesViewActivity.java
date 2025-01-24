package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.ServiceMangerCoursesAdapter;
import com.kenyaredcross.domain_model.ServiceManagerCoursesModel;

public class ServiceManagerCoursesViewActivity extends AppCompatActivity {

    RecyclerView rvCoursesManagement;
    ServiceMangerCoursesAdapter ServiceMangerCoursesAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_manager_courses_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchView =findViewById(R.id.coursesSearch);

        rvCoursesManagement = findViewById(R.id.rvCourseManagement);
        rvCoursesManagement.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ServiceManagerCoursesModel> options =
                new FirebaseRecyclerOptions.Builder<ServiceManagerCoursesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Courses"), ServiceManagerCoursesModel.class)
                        .build();
        ServiceMangerCoursesAdapter = new ServiceMangerCoursesAdapter(options);
        rvCoursesManagement.setAdapter(ServiceMangerCoursesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ServiceMangerCoursesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ServiceMangerCoursesAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
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

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<ServiceManagerCoursesModel> options =
                new FirebaseRecyclerOptions.Builder<ServiceManagerCoursesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Courses")
                                .orderByChild("courseTitle")
                                .startAt(str).endAt(str + "~"), ServiceManagerCoursesModel.class)
                        .build();

        ServiceMangerCoursesAdapter.updateOptions(options);
    }
}
