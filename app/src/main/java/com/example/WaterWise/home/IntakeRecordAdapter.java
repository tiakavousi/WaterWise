package com.example.WaterWise.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.WaterWise.R;

import java.util.List;

/**
* A custom adapter for populating a RecyclerView with a list of water intake records.
* It manages the display of each record, including the time of intake and the amount
* of water consumed.
*/

public class IntakeRecordAdapter extends RecyclerView.Adapter<IntakeRecordAdapter.RecordViewHolder> {
    /** List of intake records to be displayed in the RecyclerView. */
    private List<IntakeRecord> recordList;

    /**
     * Constructor to initialize the adapter with a list of intake records.
     *
     * @param recordList The list of intake records to display.
     */
    public IntakeRecordAdapter(List<IntakeRecord> recordList) {
        this.recordList = recordList;
    }

    /**
     * Creates a ViewHolder for each item in the RecyclerView.
     *
     * @param parent The parent ViewGroup in which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new RecordViewHolder that holds the view for each intake record.
     */
    @NonNull
    @Override
    // Creates the ViewHolder for each item in the RecyclerView
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    /**
     * Binds data from the intake record list to the appropriate views for each item.
     *
     * @param holder The ViewHolder that should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        IntakeRecord record = recordList.get(position);
        holder.timeTextView.setText(record.getTime());
        holder.amountTextView.setText(record.getAmount() + " ml");
    }

    /**
     * Returns the total number of items in the intake record list.
     *
     * @return The total number of intake records in the list.
     */
    @Override
    public int getItemCount() {
        return recordList.size();
    }

    /**
     * Updates the list of records and notifies the adapter to refresh the view.
     *
     * @param records The updated list of intake records.
     */
    public void setRecords(List<IntakeRecord> records) {
        this.recordList = records;
        notifyDataSetChanged(); // Notify the adapter that the data has been changed to refresh the RecyclerView
    }

    /**
     * ViewHolder class to represent each record item in the RecyclerView.
     * It holds references to the TextViews for displaying the time and amount of water intake.
     */
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        /** TextView to display the time of intake. */
        TextView timeTextView;

        /** TextView to display the amount of water intake in milliliters. */
        TextView amountTextView;

        /**
         * Constructor to initialize the view holder with the item view.
         *
         * @param itemView The item view representing a single intake record.
         */
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initializing the views for displaying time and amount of water intake
            timeTextView = itemView.findViewById(R.id.timeTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }
}

