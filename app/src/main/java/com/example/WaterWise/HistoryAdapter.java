package com.example.WaterWise;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryRecord> historyList;

    public HistoryAdapter(List<HistoryRecord> historyList) {
        this.historyList = historyList;
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
        holder.percentageTextView.setText(record.getPercentage() + "%");
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

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            percentageTextView = itemView.findViewById(R.id.percentageTextView);
        }
    }
}
