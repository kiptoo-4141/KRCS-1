package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.MyClassesModel;

public class MyClassesAdapter extends RecyclerView.Adapter<MyClassesAdapter.ViewHolder> {
    private final Context context;
    private final List<MyClassesModel> classList;

    public MyClassesAdapter(Context context, List<MyClassesModel> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyClassesModel model = classList.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.duration.setText("Duration: " + model.getDuration());
        holder.username.setText("Enrolled by: " + model.getUsername());
        Glide.with(context).load(model.getImage()).into(holder.image);

        holder.passButton.setOnClickListener(v -> updateStatus( "passed"));
        holder.failButton.setOnClickListener(v -> updateStatus( "failed"));
    }

    private void updateStatus( String newStatus) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Enrollments");
        ref.child("status").setValue(newStatus).addOnSuccessListener(aVoid ->
                Toast.makeText(context, "Updated to " + newStatus, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() { return classList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, duration, username;
        ImageView image;
        Button passButton, failButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.class_title);
            description = itemView.findViewById(R.id.class_description);
            duration = itemView.findViewById(R.id.class_duration);
            username = itemView.findViewById(R.id.class_username);
            image = itemView.findViewById(R.id.class_image);
            passButton = itemView.findViewById(R.id.pass_button);
            failButton = itemView.findViewById(R.id.fail_button);
        }
    }
}