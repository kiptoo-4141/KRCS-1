package com.kenyaredcross.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.EventModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventsAdapter extends FirebaseRecyclerAdapter<EventModel, EventsAdapter.myViewHolder> {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;


    public EventsAdapter(@NonNull FirebaseRecyclerOptions<EventModel> options) {
        super(options);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull EventModel model) {
        holder.title.setText(model.getEvent_title());
        holder.description.setText(model.getEvent_description());
//        holder.fees.setText(model.getEvent_fees());
        holder.location.setText(model.getEvent_location());

        // Load image with Glide
        Glide.with(holder.img.getContext())
                .load(model.getImage_link())
                .placeholder(R.drawable.contact_us) // Replace with your actual placeholder
                .circleCrop()
                .error(R.drawable.heartbroken) // Replace with your actual error image
                .into(holder.img);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView title, description, fees, location;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.EventImg);
            title = itemView.findViewById(R.id.EventTitle);
            description = itemView.findViewById(R.id.EventDescription);
//            fees = itemView.findViewById(R.id.EventFee);
            location = itemView.findViewById(R.id.EventLocation);

        }
    }
}
