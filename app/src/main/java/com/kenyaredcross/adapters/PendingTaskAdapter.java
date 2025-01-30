package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.PendingTask;

import java.util.List;

public class PendingTaskAdapter extends RecyclerView.Adapter<PendingTaskAdapter.PendingTaskViewHolder> {
    private final Context context;
    private final List<PendingTask> pendingTaskList;
    private final DatabaseReference tasksRef;

    public PendingTaskAdapter(Context context, List<PendingTask> pendingTaskList, String groupId) {
        this.context = context;
        this.pendingTaskList = pendingTaskList;
        this.tasksRef = FirebaseDatabase.getInstance().getReference("pendingtasks").child(groupId);
    }

    @NonNull
    @Override
    public PendingTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_task_item, parent, false);
        return new PendingTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingTaskViewHolder holder, int position) {
        PendingTask task = pendingTaskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.assignedTo.setText("Assigned to: " + task.getAssignedTo());
        holder.status.setText("Status: " + task.getStatus());

        if ("Completed".equals(task.getStatus())) {
            holder.markCompleteBtn.setVisibility(View.GONE);
        } else {
            holder.markCompleteBtn.setOnClickListener(v -> {
                tasksRef.child(task.getTaskId()).child("status").setValue("Completed");
                task.setStatus("Completed");
                notifyItemChanged(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return pendingTaskList.size();
    }

    public static class PendingTaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, assignedTo, status;
        Button markCompleteBtn;

        public PendingTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            assignedTo = itemView.findViewById(R.id.assigned_to);
            status = itemView.findViewById(R.id.task_status);
            markCompleteBtn = itemView.findViewById(R.id.mark_complete_btn);
        }
    }
}
