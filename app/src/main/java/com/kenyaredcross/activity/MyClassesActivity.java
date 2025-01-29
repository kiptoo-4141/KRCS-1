package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.EnrollmentAdapter;
import com.kenyaredcross.domain_model.EnrollmentModel;

import java.util.List;

public class MyClassesActivity extends AppCompatActivity {

    private RecyclerView enrollmentRecyclerView;
    private EnrollmentAdapter enrollmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        enrollmentRecyclerView = findViewById(R.id.enrollmentRecyclerView);
        enrollmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchEnrollments();
    }

    private void fetchEnrollments() {
        DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference().child("Enrollments");

        FirebaseRecyclerOptions<EnrollmentModel> options = new FirebaseRecyclerOptions.Builder<EnrollmentModel>()
                .setQuery(enrollmentsRef, EnrollmentModel.class)
                .build();

        FirebaseRecyclerAdapter<EnrollmentModel, EnrollmentAdapter.EnrollmentViewHolder> adapter =
                new FirebaseRecyclerAdapter<EnrollmentModel, EnrollmentAdapter.EnrollmentViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull EnrollmentAdapter.EnrollmentViewHolder holder, int position, @NonNull EnrollmentModel model) {
                        holder.bind(model);
                    }

                    @NonNull
                    @Override
                    public EnrollmentAdapter.EnrollmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrollment, parent, false);
                        return new EnrollmentAdapter.EnrollmentViewHolder(view);
                    }
                };

        enrollmentRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
