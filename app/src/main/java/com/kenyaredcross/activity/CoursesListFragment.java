package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Course;
import com.kenyaredcross.viewholder.CourseViewHolder;

public class CoursesListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference coursesRef;
    private FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
    private final String loggedInTrainerEmail;

    public CoursesListFragment(String loggedInTrainerEmail) {
        this.loggedInTrainerEmail = loggedInTrainerEmail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        coursesRef = FirebaseDatabase.getInstance().getReference("AssignedCourses");
        loadCourses();

        return view;
    }

    private void loadCourses() {
        FirebaseRecyclerOptions<Course> options = new FirebaseRecyclerOptions.Builder<Course>()
                .setQuery(coursesRef.orderByChild("trainerEmail").equalTo(loggedInTrainerEmail), Course.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull Course model) {
                holder.courseTitle.setText(model.getCourseTitle());
                holder.courseDesc.setText(model.getCourseDescription());

                holder.itemView.setOnClickListener(v -> {
                    // Open course details or resources
                    Toast.makeText(getContext(), "Clicked: " + model.getCourseTitle(), Toast.LENGTH_SHORT).show();
                });
            }

            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
                return new CourseViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}