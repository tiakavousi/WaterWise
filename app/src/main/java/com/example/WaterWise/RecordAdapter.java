package com.example.WaterWise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> recordList;

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.timeTextView.setText(record.getTime());
        holder.amountTextView.setText(record.getAmount() + " ml");
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void setRecords(List<Record> records) {
        this.recordList = records;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, amountTextView;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }
}

