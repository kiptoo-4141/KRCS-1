package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.activity.ClassAttendanceActivity;
import com.kenyaredcross.domain_model.AttendanceRecord;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private List<AttendanceRecord> attendanceRecords;
    private Context context;
    private boolean isTrainer;

    public AttendanceAdapter(List<AttendanceRecord> attendanceRecords, Context context, boolean isTrainer) {
        this.attendanceRecords = attendanceRecords;
        this.context = context;
        this.isTrainer = isTrainer;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_record, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceRecord record = attendanceRecords.get(position);
        holder.tvCourseTitle.setText(record.getCourseTitle());
        holder.tvStatus.setText(record.isActive() ? "Active" : "Expired");
        holder.tvDateTime.setText(record.getActivationTime());
        holder.tvStudentCount.setText(String.valueOf(record.getStudents().size()));

        if (isTrainer) {
            holder.btnAction.setText("View Details");
            holder.btnAction.setOnClickListener(v -> {
                if (context instanceof ClassAttendanceActivity) {
                    ((ClassAttendanceActivity) context).viewAttendanceDetails(record);
                }
            });
        } else {
            if (record.isSignedByCurrentUser()) {
                holder.btnAction.setText("Signed");
                holder.btnAction.setEnabled(false);
            } else {
                holder.btnAction.setText("Sign");
                holder.btnAction.setEnabled(record.isActive());
                holder.btnAction.setOnClickListener(v -> {
                    if (context instanceof ClassAttendanceActivity) {
                        ((ClassAttendanceActivity) context).signAttendance(record);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return attendanceRecords.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvStatus, tvDateTime, tvStudentCount;
        Button btnAction;

        AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}