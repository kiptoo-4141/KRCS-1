package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.domain_model.Donation;
import com.kenyaredcross.R;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private List<Donation> donationList;
    public OnItemClickListener listener;

    public interface OnItemClickListener {
        void onApproveClick(int position);
        void onRejectClick(int position);
    }

    public DonationAdapter(List<Donation> donationList, OnItemClickListener listener) {
        this.donationList = donationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        holder.bind(donation);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFullName, tvAmount, tvStatus, tvDonationID, tvEmail, tvPaymentMethod, tvAccountNumber;
        private Button btnApprove, btnReject;

        public DonationViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvDonationID = itemView.findViewById(R.id.tvDonationID);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);

            btnApprove.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onApproveClick(position);
                    }
                }
            });

            btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRejectClick(position);
                    }
                }
            });
        }

        public void bind(Donation donation) {
            tvFullName.setText(donation.getFullName());
            tvAmount.setText(donation.getAmount());
            tvStatus.setText(donation.getStatus());
            tvDonationID.setText(donation.getDonationId());
            tvEmail.setText(donation.getEmail());
            tvPaymentMethod.setText(donation.getPaymentMethod());
            tvAccountNumber.setText(donation.getPhoneNumber());

            // Highlight if status is pending
            if (donation.getStatus().equals("pending")) {
                tvStatus.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.pendingColor));
            } else {
                tvStatus.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.transparent));
            }
        }
    }
}