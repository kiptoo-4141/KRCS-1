package com.kenyaredcross.activity;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.EquipmentAdapter;
import com.kenyaredcross.domain_model.EquipmentModel;

public class CoordinatorEquipmentsActivity extends AppCompatActivity {

    private RecyclerView equipmentRecyclerView;
    private EquipmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coordinator_equipments);

        equipmentRecyclerView = findViewById(R.id.equipmentRecyclerView);
        equipmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<EquipmentModel> options =
                new FirebaseRecyclerOptions.Builder<EquipmentModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("OrganisationInventory"), EquipmentModel.class)
                        .build();

        adapter = new EquipmentAdapter(options, this);
        equipmentRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}