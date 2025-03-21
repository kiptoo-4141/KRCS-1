package com.kenyaredcross.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionGroupViewHolder> {
    private List<TransactionGroup> transactionGroups;

    public TransactionAdapter(List<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

    @NonNull
    @Override
    public TransactionGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_group, parent, false);
        return new TransactionGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionGroupViewHolder holder, int position) {
        TransactionGroup group = transactionGroups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return transactionGroups.size();
    }

    static class TransactionGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName;
        private RecyclerView rvTransactions;
        private TransactionChildAdapter childAdapter;

        public TransactionGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            rvTransactions = itemView.findViewById(R.id.rvTransactions);

            // Set up child RecyclerView
            rvTransactions.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            childAdapter = new TransactionChildAdapter();
            rvTransactions.setAdapter(childAdapter);

            // Toggle expand/collapse on group name click
            tvGroupName.setOnClickListener(v -> {
                if (rvTransactions.getVisibility() == View.GONE) {
                    rvTransactions.setVisibility(View.VISIBLE);
                } else {
                    rvTransactions.setVisibility(View.GONE);
                }
            });
        }

        public void bind(TransactionGroup group) {
            tvGroupName.setText(group.getGroupName());
            childAdapter.setTransactions(group.getTransactions());
        }
    }

    static class TransactionChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Transaction> transactions;

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case 0:
                    return new CoursePaymentViewHolder(inflater.inflate(R.layout.item_course_payment4, parent, false));
                case 1:
                    return new DonationViewHolder(inflater.inflate(R.layout.item_donation4, parent, false));
                case 2:
                    return new PaidRequestViewHolder(inflater.inflate(R.layout.item_paid_request, parent, false));
                default:
                    throw new IllegalArgumentException("Invalid view type");
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            switch (holder.getItemViewType()) {
                case 0:
                    ((CoursePaymentViewHolder) holder).bind(transaction);
                    break;
                case 1:
                    ((DonationViewHolder) holder).bind(transaction);
                    break;
                case 2:
                    ((PaidRequestViewHolder) holder).bind(transaction);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return transactions == null ? 0 : transactions.size();
        }

        @Override
        public int getItemViewType(int position) {
            Transaction transaction = transactions.get(position);
            if (transaction.getCourseId() != null) {
                return 0; // CoursePayment
            } else if (transaction.getDonationId() != null) {
                return 1; // Donation
            } else {
                return 2; // PaidRequest
            }
        }

        static class CoursePaymentViewHolder extends RecyclerView.ViewHolder {
            private TextView tvAmount, tvDate, tvPaymentMethod, tvStatus, tvDetails;

            public CoursePaymentViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvDetails = itemView.findViewById(R.id.tvDetails);
            }

            public void bind(Transaction transaction) {
                tvAmount.setText("Amount: " + transaction.getAmount());
                tvDate.setText("Date: " + transaction.getDate());
                tvPaymentMethod.setText("Method: " + transaction.getPaymentMethod());
                tvStatus.setText("Status: " + transaction.getStatus());
                tvDetails.setText("Course ID: " + transaction.getCourseId());
            }
        }

        static class DonationViewHolder extends RecyclerView.ViewHolder {
            private TextView tvAmount, tvDonationTime, tvPaymentMethod, tvStatus, tvDetails;

            public DonationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvDonationTime = itemView.findViewById(R.id.tvDonationTime);
                tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvDetails = itemView.findViewById(R.id.tvDetails);
            }

            public void bind(Transaction transaction) {
                tvAmount.setText("Amount: " + transaction.getAmount());
                tvDonationTime.setText("Donation Time: " + transaction.getDonationTime());
                tvPaymentMethod.setText("Method: " + transaction.getPaymentMethod());
                tvStatus.setText("Status: " + transaction.getStatus());
                tvDetails.setText("Donor: " + transaction.getFullName());
            }
        }

        static class PaidRequestViewHolder extends RecyclerView.ViewHolder {
            private TextView tvAmount, tvCategory, tvDateTime, tvStatus, tvDetails;

            public PaidRequestViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvDetails = itemView.findViewById(R.id.tvDetails);
            }

            public void bind(Transaction transaction) {
                tvAmount.setText("Amount: " + transaction.getAmount());
                tvCategory.setText("Category: " + transaction.getCategory());
                tvDateTime.setText("Date & Time: " + transaction.getDate());
                tvStatus.setText("Status: " + transaction.getStatus());
                tvDetails.setText("Item: " + transaction.getItemName() + ", Count: " + transaction.getRequestCount());
            }
        }
    }
}