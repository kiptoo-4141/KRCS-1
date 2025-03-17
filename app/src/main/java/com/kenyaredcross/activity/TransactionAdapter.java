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

    static class TransactionChildAdapter extends RecyclerView.Adapter<TransactionChildAdapter.TransactionViewHolder> {
        private List<Transaction> transactions;

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.bind(transaction);
        }

        @Override
        public int getItemCount() {
            return transactions == null ? 0 : transactions.size();
        }

        static class TransactionViewHolder extends RecyclerView.ViewHolder {
            private TextView tvAmount, tvDate, tvPaymentMethod, tvStatus, tvDetails;

            public TransactionViewHolder(@NonNull View itemView) {
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
                tvDetails.setText("Details: " + (transaction.getCourseId() != null ? "Course ID: " + transaction.getCourseId() :
                        transaction.getItemName() != null ? "Item: " + transaction.getItemName() :
                                "Donation by: " + transaction.getFullName()));
            }
        }
    }
}