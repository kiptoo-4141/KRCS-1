package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.VolunteerCheckModel;

import java.util.ArrayList;
import java.util.List;

public class VolunteerCheckAdapter extends FirebaseRecyclerAdapter<VolunteerCheckModel, VolunteerCheckAdapter.VolunteerViewHolder> {

    private final Context context;
    private final List<VolunteerCheckModel> selectedVolunteers = new ArrayList<>();

    public VolunteerCheckAdapter(@NonNull FirebaseRecyclerOptions<VolunteerCheckModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position, @NonNull VolunteerCheckModel model) {
        holder.volunteerUsername.setText(model.getUsername());
        holder.volunteerEmail.setText(model.getEmail());
        holder.volunteerStatus.setText(model.getStatus());
        holder.radioButton.setChecked(selectedVolunteers.contains(model));

        holder.radioButton.setOnClickListener(v -> {
            if (holder.radioButton.isChecked()) {
                selectedVolunteers.add(model);
            } else {
                selectedVolunteers.remove(model);
            }
        });
    }

    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.volunteer_check_item, parent, false);
        return new VolunteerViewHolder(view);
    }

    public List<VolunteerCheckModel> getSelectedVolunteers() {
        return selectedVolunteers;
    }

    public static class VolunteerViewHolder extends RecyclerView.ViewHolder {
        TextView volunteerUsername, volunteerEmail, volunteerStatus;
        RadioButton radioButton;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            volunteerUsername = itemView.findViewById(R.id.volunteerUsername);
            volunteerEmail = itemView.findViewById(R.id.volunteerEmail);
            volunteerStatus = itemView.findViewById(R.id.volunteerStatus);
            radioButton = itemView.findViewById(R.id.rdbtm);
        }
    }
}
