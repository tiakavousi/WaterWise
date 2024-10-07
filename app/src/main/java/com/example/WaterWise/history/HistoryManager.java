package com.example.WaterWise.history;

import android.util.Log;

import com.example.WaterWise.data.DataModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryManager {
    private final DataModel dataModel;


    public HistoryManager(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    // Method to fetch history from Firestore
    public void fetchHistoryRecords() {
        String signUpDateStr = dataModel.getSignUpDate().getValue();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date signUpDate = dateFormat.parse(signUpDateStr);
            Date currentDate = new Date();

            // Generate a list of dates from sign-up date to current date
            List<String> allDates = getDatesBetween(signUpDate, currentDate);

            // Fetch intake for all dates
            // Fetch intake records from DataModel
            dataModel.loadHistoryRecords();
        } catch (ParseException e) {
            Log.d("SignUp date", "ParseException");
            // Handle the error in DataModel by setting empty history records
            dataModel.getHistoryRecords().setValue(new ArrayList<>());
        }
    }

    // Helper method to generate a list of dates between two dates
    private List<String> getDatesBetween(Date startDate, Date endDate) {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        while (!calendar.getTime().after(endDate)) {
            dates.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1); // Move to the next day
        }

        return dates;
    }
}
