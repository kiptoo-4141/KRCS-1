package com.kenyaredcross.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ServiceManagerCoursesModel;
import com.kenyaredcross.domain_model.TrainerModel;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceMangerCoursesAdapter extends FirebaseRecyclerAdapter<ServiceManagerCoursesModel, ServiceMangerCoursesAdapter.MyViewHolder> {

    public ServiceMangerCoursesAdapter(@NonNull FirebaseRecyclerOptions<ServiceManagerCoursesModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ServiceManagerCoursesModel model) {
        // Set course data
        holder.courseId.setText(model.getId());
        holder.courseTitle.setText(model.getTitle());
        holder.courseDescription.setText(model.getDescription());
        holder.courseDuration.setText(model.getDuration());
        holder.certificationStatus.setText(model.getCertification_status());

        // Load image using Glide
        Glide.with(holder.img.getContext())
                .load(model.getImage_link())
                .placeholder(R.drawable.baseline_question_mark_24)  // Placeholder image
                .circleCrop()
                .error(R.drawable.baseline_question_mark_24)  // Error image
                .into(holder.img);

        // Delete course logic
        holder.deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(holder.courseId.getContext())
                    .setTitle("Are You Sure?")
                    .setMessage("Deleted data cannot be undone!")
                    .setPositiveButton("YES", (dialogInterface, which) -> {
                        FirebaseDatabase.getInstance().getReference().child("Courses")
                                .child(getRef(position).getKey()).removeValue()
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(holder.courseId.getContext(), "Course Deleted", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(holder.courseId.getContext(), "Failed to Delete Course", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("NO", (dialogInterface, which) ->
                            Toast.makeText(holder.courseId.getContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                    )
                    .create();

            dialog.show();
        });


        // Edit course logic
        holder.editButton.setOnClickListener(v -> {
            final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                    .setContentHolder(new ViewHolder(R.layout.edit_course_popup))
                    .setGravity(Gravity.CENTER)  // Center the dialog
                    .setExpanded(true, 1200)
                    .create();

            View view = dialogPlus.getHolderView();

            EditText courseId = view.findViewById(R.id.updateCourseId);
            EditText courseTitle = view.findViewById(R.id.updateCourseTitle);
            EditText courseDescription = view.findViewById(R.id.updateCourseDescription);
            EditText courseDuration = view.findViewById(R.id.updateCourseDuration);
            EditText certificationStatus = view.findViewById(R.id.updateCertificatioStatus);
            EditText imageLink = view.findViewById(R.id.updateImageLink);
            Button updateBtn = view.findViewById(R.id.updateCourseButton);

            courseId.setText(model.getId());
            courseTitle.setText(model.getTitle());
            courseDescription.setText(model.getDescription());
            courseDuration.setText(model.getDuration());
            certificationStatus.setText(model.getCertification_status());
            imageLink.setText(model.getImage_link());

            dialogPlus.show();

            updateBtn.setOnClickListener(v1 -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", courseId.getText().toString());
                map.put("title", courseTitle.getText().toString());
                map.put("description", courseDescription.getText().toString());
                map.put("duration", courseDuration.getText().toString());
                map.put("certification_status", certificationStatus.getText().toString());
                map.put("image_link", imageLink.getText().toString());

                FirebaseDatabase.getInstance().getReference().child("Courses")
                        .child(getRef(position).getKey()).updateChildren(map)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(holder.courseId.getContext(), "Course Updated", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.courseId.getContext(), "Failed to Update Course", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        });
            });
        });

        // Assign course logic
        holder.assignButton.setOnClickListener(v -> {
            final DialogPlus dialogPlus = DialogPlus.newDialog(holder.img.getContext())
                    .setContentHolder(new ViewHolder(R.layout.assign_to_popup))
                    .setGravity(Gravity.CENTER)
                    .setExpanded(true, 1200)
                    .create();

            View view = dialogPlus.getHolderView();
            ListView trainerListView = view.findViewById(R.id.trainerListView);
            Button assignBtn = view.findViewById(R.id.assignBtn2);

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            usersRef.orderByChild("role").equalTo("Trainer").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<TrainerModel> trainerList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.child("username").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            trainerList.add(new TrainerModel(name, email));
                        }

                        TrainerAdapter adapter = new TrainerAdapter(view.getContext(), trainerList);
                        trainerListView.setAdapter(adapter);

                        // Assign selected trainer logic
                        assignBtn.setOnClickListener(v1 -> {
                            TrainerModel selectedTrainer = adapter.getSelectedTrainer();
                            if (selectedTrainer != null) {
                                DatabaseReference assignedCoursesRef = FirebaseDatabase.getInstance().getReference().child("AssignedCourses");
                                String assignedCourseId = assignedCoursesRef.push().getKey();

                                Map<String, Object> assignedCourseData = new HashMap<>();
                                assignedCourseData.put("courseId", model.getId());
                                assignedCourseData.put("courseTitle", model.getTitle());
                                assignedCourseData.put("courseDescription", model.getDescription());
                                assignedCourseData.put("courseDuration", model.getDuration());
                                assignedCourseData.put("certificationStatus", model.getCertification_status());
                                assignedCourseData.put("trainerName", selectedTrainer.getUsername());
                                assignedCourseData.put("trainerEmail", selectedTrainer.getEmail());

                                assignedCoursesRef.child(assignedCourseId).setValue(assignedCourseData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(holder.itemView.getContext(), "Course Assigned to Trainer", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(holder.itemView.getContext(), "Failed to Assign Course", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Please select a Trainer", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "No Trainers Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(holder.itemView.getContext(), "Error Fetching Trainers", Toast.LENGTH_SHORT).show();
                }
            });

            dialogPlus.show();
        });

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate service manager courses view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_manager_courses_view, parent, false);
        return new MyViewHolder(view);
    }

    // ViewHolder class
    static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView courseId, courseTitle, courseDescription, courseDuration, certificationStatus;
        Button deleteButton, editButton, assignButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.courseimg);
            courseId = itemView.findViewById(R.id.courseId);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            courseDuration = itemView.findViewById(R.id.courseDuration);
            certificationStatus = itemView.findViewById(R.id.certificationStatus);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            assignButton = itemView.findViewById(R.id.assignButton);
        }
    }
}
