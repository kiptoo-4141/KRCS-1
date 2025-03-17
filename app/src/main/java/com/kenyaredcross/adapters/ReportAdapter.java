package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ReportItem;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private List<ReportItem> reportItems;

    public ReportAdapter(List<ReportItem> reportItems) {
        this.reportItems = reportItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportItem item = reportItems.get(position);

        // Set common fields
        holder.tvTitle.setText(item.getTitle());

        // Set optional fields
        if (item.getDescription() != null) {
            holder.tvDescription.setText(item.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        if (item.getDuration() != null) {
            holder.tvDuration.setText(item.getDuration());
            holder.tvDuration.setVisibility(View.VISIBLE);
        } else {
            holder.tvDuration.setVisibility(View.GONE);
        }

        if (item.getStatus() != null) {
            holder.tvStatus.setText(item.getStatus());
            holder.tvStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        if (item.getTrainerName() != null) {
            holder.tvTrainerName.setText(item.getTrainerName());
            holder.tvTrainerName.setVisibility(View.VISIBLE);
        } else {
            holder.tvTrainerName.setVisibility(View.GONE);
        }

        if (item.getPaymentMethod() != null) {
            holder.tvPaymentMethod.setText(item.getPaymentMethod());
            holder.tvPaymentMethod.setVisibility(View.VISIBLE);
        } else {
            holder.tvPaymentMethod.setVisibility(View.GONE);
        }

        if (item.getDate() != null) {
            holder.tvDate.setText(item.getDate());
            holder.tvDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }

        if (item.getTime() != null) {
            holder.tvTime.setText(item.getTime());
            holder.tvTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvTime.setVisibility(View.GONE);
        }

        if (item.getAmount() != 0) {
            holder.tvAmount.setText(String.format("Ksh %.2f", item.getAmount()));
            holder.tvAmount.setVisibility(View.VISIBLE);
        } else {
            holder.tvAmount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDuration, tvStatus, tvTrainerName, tvPaymentMethod, tvDate, tvTime, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTrainerName = itemView.findViewById(R.id.tvTrainerName);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
