package com.example.WaterWise.history;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.WaterWise.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * Adapter class for displaying history records in a RecyclerView. Each item represents
 * a single history record, displaying the date and the percentage of the goal achieved.
 */
public class HistoryRecordAdapter extends RecyclerView.Adapter<HistoryRecordAdapter.HistoryViewHolder> {
    // List of history records to be displayed in the RecyclerView
    private List<HistoryRecord> historyList;
    private int goal;
    /**
     * Constructor for the HistoryRecordAdapter.
     *
     * @param historyList List of history records to be displayed.
     * @param goal        The goal for calculating the percentage of completion.
     */
    public HistoryRecordAdapter(List<HistoryRecord> historyList, int goal) {
        this.historyList = historyList;
        this.goal = goal;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new HistoryViewHolder that holds a View representing a history item.
     */
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for a history item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        // Get the history record at the current position
        HistoryRecord record = historyList.get(position);
        // Set the date text
        holder.dateTextView.setText(record.getDate());
        // Calculate the percentage of goal achieved
        float percentage = record.calculatePercentage(goal);
        // Check if the percentage is a whole number or requires a decimal place
        if (percentage % 1 == 0) {
            holder.percentageTextView.setText(String.format("%.0f%%", percentage)); // No decimal places
        } else {
            holder.percentageTextView.setText(String.format("%.1f%%", percentage)); // One decimal place
        }

        // Adjust the background based on percentage
        holder.circleProgressView.setPercentage(percentage);
        // Format the date for better readability
        String rawDate = record.getDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Format as "Mon, Aug 30, 2024"
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        String formattedDate = "";
        try {
            // Parse the date from the original format and reformat it
            Date date = inputFormat.parse(rawDate);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            // Handle potential parsing errors
            e.printStackTrace();
        }
        // Set the formatted date in the TextView
        holder.dateTextView.setText(formattedDate);
    }

    /**
     * Returns the total number of items in the history list.
     *
     * @return The size of the history list.
     */
    @Override
    public int getItemCount() {
        Log.d("HistoryAdapter", "Total items to display: " + historyList.size());
        return historyList.size();
    }

    /**
     * ViewHolder class for RecyclerView that holds the views for each history item.
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        // TextView for displaying the date of the history record
        TextView dateTextView;
        // TextView for displaying the percentage of the goal achieved
        TextView percentageTextView;
        // Custom view for displaying the circular progress bar
        CircleProgressView circleProgressView;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view representing a single item in the RecyclerView.
         */
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views for the history item
            dateTextView = itemView.findViewById(R.id.dateTextView);
            percentageTextView = itemView.findViewById(R.id.percentageText);
            circleProgressView = itemView.findViewById(R.id.circleProgressView);
        }
    }
}
