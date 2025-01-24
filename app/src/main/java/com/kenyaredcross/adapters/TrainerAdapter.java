package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.TrainerModel;

import java.util.List;

public class TrainerAdapter extends ArrayAdapter<TrainerModel> {
    private int selectedPosition = -1; // Track selected position

    public TrainerAdapter(Context context, List<TrainerModel> trainers) {
        super(context, 0, trainers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trainer_list_item, parent, false);
        }

        TrainerModel trainer = getItem(position);

        TextView trainerName = convertView.findViewById(R.id.trainerName);
        TextView trainerEmail = convertView.findViewById(R.id.trainerEmail);
        RadioButton trainerRadioButton = convertView.findViewById(R.id.selectedTrainerRadioButton);

        trainerName.setText(trainer.getUsername());
        trainerEmail.setText(trainer.getEmail());

        // Update RadioButton state based on selected position
        trainerRadioButton.setChecked(position == selectedPosition);

        // Handle RadioButton clicks to enforce single selection
        trainerRadioButton.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        return convertView;
    }

    public TrainerModel getSelectedTrainer() {
        return selectedPosition != -1 ? getItem(selectedPosition) : null;
    }
}
