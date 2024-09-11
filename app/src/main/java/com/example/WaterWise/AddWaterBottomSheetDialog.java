package com.example.WaterWise;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddWaterBottomSheetDialog extends BottomSheetDialogFragment {

    public interface OnWaterAmountSelectedListener {
        void onAmountSelected(int amount);
    }

    private OnWaterAmountSelectedListener listener;

    public void setOnWaterAmountSelectedListener(OnWaterAmountSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the bottom sheet dialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Inflate the layout for the bottom sheet dialog
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_add_water, null);

        // Set the content view for the dialog
        dialog.setContentView(view);

        // Find buttons in the layout
        Button btn200ml = view.findViewById(R.id.btn200ml);
        Button btn500ml = view.findViewById(R.id.btn500ml);
        Button btn1000ml = view.findViewById(R.id.btn1000ml);
        Button btn2000ml = view.findViewById(R.id.btn2000ml);

        // Set click listeners for each button
        btn200ml.setOnClickListener(v -> selectAmount(200));
        btn500ml.setOnClickListener(v -> selectAmount(500));
        btn1000ml.setOnClickListener(v -> selectAmount(1000));
        btn2000ml.setOnClickListener(v -> selectAmount(2000));

        return dialog;
    }

    private void selectAmount(int amount) {
        if (listener != null) {
            listener.onAmountSelected(amount);
        }
        dismiss();
    }
}

