package com.kenyaredcross.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.VolunteerTasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VolunteerTasksAdapter extends RecyclerView.Adapter<VolunteerTasksAdapter.TaskViewHolder> {

    private final List<VolunteerTasks> taskList;

    public VolunteerTasksAdapter(List<VolunteerTasks> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.volunteer_task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        VolunteerTasks task = taskList.get(position);
        Log.d("VolunteerTasksAdapter", "Binding task: " + task.getDescription());
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskDescription;
        private final TextView taskDates;
        private final TextView taskStatus;
        private final TextView taskGroup;
        private final ProgressBar taskProgress;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDates = itemView.findViewById(R.id.taskDates);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            taskGroup = itemView.findViewById(R.id.taskGroup);
            taskProgress = itemView.findViewById(R.id.taskProgress);
        }

        public void bind(VolunteerTasks task) {
            taskDescription.setText(task.getDescription());
            taskDates.setText("Start: " + task.getStartDate() + " - End: " + task.getEndDate());
            taskStatus.setText("Status: " + task.getStatus());

            // Calculate and set progress
            int progress = calculateProgress(task.getStartDate(), task.getEndDate());
            taskProgress.setProgress(progress);

            // Display group ID
            taskGroup.setText("Group ID: " + task.getGroupId());
        }

        private int calculateProgress(String startDateStr, String endDateStr) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date startDate = sdf.parse(startDateStr);
                Date endDate = sdf.parse(endDateStr);
                Date currentDate = new Date();

                if (currentDate.before(startDate)) {
                    return 0; // Task hasn't started yet
                } else if (currentDate.after(endDate)) {
                    return 100; // Task is completed
                } else {
                    long totalDuration = endDate.getTime() - startDate.getTime();
                    long elapsedDuration = currentDate.getTime() - startDate.getTime();
                    return (int) ((elapsedDuration * 100) / totalDuration);
                }
            } catch (ParseException e) {
                Log.e("TaskViewHolder", "Error parsing dates: " + e.getMessage());
                return 0;
            }
        }
    }
}