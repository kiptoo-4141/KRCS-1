package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.EventModel2;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private final List<EventModel2> eventList;
    private final OnEventClickListener listener;

    public EventAdapter(List<EventModel2> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventModel2 event = eventList.get(position);
        holder.tvEventTitle.setText(event.getEvent_title());
        holder.tvEventDescription.setText(event.getEvent_description());
        holder.tvEventLocation.setText("Location: " + event.getEvent_location());
        holder.tvEventFees.setText("Fees: " + event.getEvent_fees());
        holder.tvEventDate.setText("Date: " + event.getEvent_date());

        holder.btnEditEvent.setOnClickListener(v -> listener.onEditEvent(event));
        holder.btnDeleteEvent.setOnClickListener(v -> listener.onDeleteEvent(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvEventDescription, tvEventLocation, tvEventFees, tvEventDate;
        Button btnEditEvent, btnDeleteEvent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
            tvEventFees = itemView.findViewById(R.id.tvEventFees);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }

    public interface OnEventClickListener {
        void onEditEvent(EventModel2 event);
        void onDeleteEvent(EventModel2 event);
    }
}