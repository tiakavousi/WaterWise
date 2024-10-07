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

public class HistoryRecordAdapter extends RecyclerView.Adapter<HistoryRecordAdapter.HistoryViewHolder> {

    private List<HistoryRecord> historyList;
    private int goal;

    public HistoryRecordAdapter(List<HistoryRecord> historyList, int goal) {
        this.historyList = historyList;
        this.goal = goal;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryRecord record = historyList.get(position);
        holder.dateTextView.setText(record.getDate());
        float percentage = record.calculatePercentage(goal);
        // Check if the percentage is a whole number or requires a decimal place
        if (percentage % 1 == 0) {
            holder.percentageTextView.setText(String.format("%.0f%%", percentage)); // No decimal places
        } else {
            holder.percentageTextView.setText(String.format("%.1f%%", percentage)); // One decimal place
        }

        // Adjust the background based on percentage
        holder.circleProgressView.setPercentage(percentage);

        String rawDate = record.getDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()); // Format as "Mon, Aug 30, 2024"
        String formattedDate = "";
        try {
            Date date = inputFormat.parse(rawDate);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.dateTextView.setText(formattedDate);
        Log.d("HistoryAdapter", "Displaying history item at position: " + position + " with date: " + record.getDate());
    }

    @Override
    public int getItemCount() {
        Log.d("HistoryAdapter", "Total items to display: " + historyList.size());
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView percentageTextView;
        CircleProgressView circleProgressView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            percentageTextView = itemView.findViewById(R.id.percentageText);
            circleProgressView = itemView.findViewById(R.id.circleProgressView);
        }
    }
}
