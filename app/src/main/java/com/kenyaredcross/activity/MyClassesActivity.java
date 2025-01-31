package com.kenyaredcross.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.MyClassesAdapter;
import com.kenyaredcross.domain_model.MyClassesModel;

public class MyClassesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyClassesAdapter adapter;
    private List<MyClassesModel> classList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        classList = new ArrayList<>();
        adapter = new MyClassesAdapter(this, classList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Enrollments");
        fetchClasses();
    }

    private void fetchClasses() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot courseSnapshot : userSnapshot.getChildren()) {
                        MyClassesModel model = courseSnapshot.getValue(MyClassesModel.class);
                        if (model != null && "approved".equals(model.getStatus())) {
                            classList.add(model);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
}
