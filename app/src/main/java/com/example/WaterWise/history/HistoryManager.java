package com.example.WaterWise.history;

import android.util.Log;

import com.example.WaterWise.data.DataModel;
import com.example.WaterWise.data.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryManager {

    private final FirestoreHelper firestoreHelper;

    public HistoryManager(FirestoreHelper firestoreHelper) {
        this.firestoreHelper = firestoreHelper;
    }

    // Method to fetch history from Firestore
    public void fetchHistoryRecords(DataModel dataModel) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestoreHelper.fetchSignUpDate(signUpDateStr -> {
            if (signUpDateStr != null) {
                try {
                    // Parse the sign-up date and current date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date signUpDate = dateFormat.parse(signUpDateStr);
                    Date currentDate = new Date();

                    // Generate a list of dates from sign-up date to current date
                    List<String> allDates = getDatesBetween(signUpDate, currentDate);

                    // Fetch intake for all dates
                    firestoreHelper.fetchIntakeForDates(userId, allDates, historyIntakeList -> {
                        List<HistoryRecord> historyRecords = new ArrayList<>();

                        for (HistoryRecord intakeData : historyIntakeList) {
                            // Create a history record
                            HistoryRecord record = new HistoryRecord(intakeData.getDate(), intakeData.getIntake());
                            historyRecords.add(record);
                        }

                        // Update the history records LiveData in the DataModel
                        dataModel.getHistoryRecords().setValue(historyRecords);

                    });
                } catch (ParseException e) {
                    Log.d("SignUp date", "ParseException");
                    dataModel.getHistoryRecords().setValue(new ArrayList<>());
                }
            } else {
                Log.d("SignUp date", "null");
                dataModel.getHistoryRecords().setValue(new ArrayList<>());
            }
        });
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
