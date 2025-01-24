package com.kenyaredcross.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.FMReportsModel;
import java.util.ArrayList;
import java.util.List;

public class FMReportsAdapter extends RecyclerView.Adapter<FMReportsAdapter.ReportViewHolder> {
    private final List<FMReportsModel> reports = new ArrayList<>();
    private final DatabaseReference financeReportsRef;
    private final DatabaseReference donationReportsRef;

    public FMReportsAdapter() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        financeReportsRef = database.getReference("FinanceReports");
        donationReportsRef = database.getReference("DonationReports");

        loadReportsFromFirebase();
    }

    private void loadReportsFromFirebase() {
        // Retrieve FinanceReports data
        financeReportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reports.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FMReportsModel report = data.getValue(FMReportsModel.class);
                    if (report != null) {
                        report.setDonationId(null); // Set to distinguish finance report
                        reports.add(report);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FMReportsAdapter", "Failed to retrieve FinanceReports", error.toException());
            }
        });

        // Retrieve DonationReports data
        donationReportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    FMReportsModel report = data.getValue(FMReportsModel.class);
                    if (report != null) {
                        reports.add(report); // DonationId not null for donation reports
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FMReportsAdapter", "Failed to retrieve DonationReports", error.toException());
            }
        });
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 0) ? R.layout.fm_reports_item : R.layout.donation_reports_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ReportViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        FMReportsModel report = reports.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemViewType(int position) {
        return (reports.get(position).getDonationId() != null) ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameRp, itemAmountRp, dateTimeRp, itemStatusRp, itemInvManagerRp, requestId, itemCategoryRp;
        private final TextView donationId, fullname, donationTime, paymentMethod, phoneNumber, status;
        private final int viewType;

        public ReportViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == 0) { // Finance Report Views
                itemNameRp = itemView.findViewById(R.id.itemNameRp);
                itemAmountRp = itemView.findViewById(R.id.itemAmountRp);
                dateTimeRp = itemView.findViewById(R.id.dateTimeRp);
                itemStatusRp = itemView.findViewById(R.id.itemStatusRp);
                itemInvManagerRp = itemView.findViewById(R.id.itemInvManagerRp);
                requestId = itemView.findViewById(R.id.requestId);
                itemCategoryRp = itemView.findViewById(R.id.itemCategoryRp);

                donationId = null;
                fullname = null;
                donationTime = null;
                paymentMethod = null;
                phoneNumber = null;
                status = null;
            } else { // Donation Report Views
                donationId = itemView.findViewById(R.id.donationId);
                fullname = itemView.findViewById(R.id.fullname);
                donationTime = itemView.findViewById(R.id.donationTime);
                dateTimeRp = itemView.findViewById(R.id.dateTimeRp);
                paymentMethod = itemView.findViewById(R.id.paymentMethod);
                phoneNumber = itemView.findViewById(R.id.phoneNumber);
                status = itemView.findViewById(R.id.status);

                itemNameRp = null;
                itemAmountRp = null;
                itemStatusRp = null;
                itemInvManagerRp = null;
                requestId = null;
                itemCategoryRp = null;
            }
        }

        public void bind(FMReportsModel report) {
            if (viewType == 0) { // Bind Finance Report
                itemNameRp.setText(report.getItemName());
                itemAmountRp.setText(report.getAmount());
                dateTimeRp.setText(report.getDateTime());
                itemStatusRp.setText(report.getStatus());
                itemInvManagerRp.setText(report.getInventoryManager());
                requestId.setText(report.getRequestId());
                itemCategoryRp.setText(report.getCategory());
            } else { // Bind Donation Report
                donationId.setText(report.getDonationId());
                fullname.setText(report.getFullName());
                donationTime.setText(report.getDonationTime());
                dateTimeRp.setText(report.getDateTime());
                paymentMethod.setText(report.getPaymentMethod());
                phoneNumber.setText(report.getPhoneNumber());
                status.setText(report.getStatus());
            }
        }
    }
}
