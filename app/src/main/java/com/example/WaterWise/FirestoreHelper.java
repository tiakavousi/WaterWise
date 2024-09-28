package com.example.WaterWise;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore db;
    private final String userId;
    private DataModel dataModel;

    public FirestoreHelper(DataModel dataModel) {
        this.dataModel = dataModel;
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void saveUserData(String name, int goal, int weight, String gender) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("goal", goal);
        userData.put("weight", weight);
        userData.put("gender", gender);

        db.collection("users").document(userId).update(userData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    public void saveWaterIntakeRecord(String time, String date, int amount) {
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("time", time);
        recordData.put("date", date);
        recordData.put("amount", amount);

        db.collection("users").document(userId).collection("records").add(recordData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Record successfully added to sub-collection!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding record", e));
    }

    public void fetchDailyIntake(String userId, String date, intakeCallback callback) {
        db.collection("users").document(userId).collection("records")
                .whereEqualTo("date", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalIntake = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            int intake = document.getLong("amount").intValue();
                            totalIntake += intake;
                        }
                        callback.onIntakeLoaded(totalIntake);  // Return total intake
                    } else {
                        callback.onIntakeLoaded(0);  // Default to 0 if query fails
                    }
                });
    }


    public void fetchUserData(String userId, DataModel dataModel, FirestoreHelperCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long goalLong = document.getLong("goal");
                    String name = document.getString("name");
                    Long weightLong = document.getLong("weight");
                    String gender = document.getString("gender");
                    int goal = (goalLong != null) ? goalLong.intValue() : 0;
                    int weight = (goalLong != null) ? weightLong.intValue() : 0;

                    fetchDailyIntake(userId, currentDate, totalIntake -> {
                        dataModel.setGoal(goal);
                        dataModel.setIntake(totalIntake);
                        Log.d("!!!!! INTAKE UPDATE", totalIntake + "");
                        dataModel.setName(name);
                        dataModel.setWeight(weight);
                        dataModel.setGender(gender);
                        callback.onDataLoaded(goal, totalIntake);
                    });
                } else {
                    // Handle the case where the document does not exist
                    Log.e("FirestoreHelper", "User document does not exist.");
                    callback.onDataLoaded(0, 0);
                }
            }else {
                    Log.e("FirestoreHelper", "Task was not successful or result is null.");
                    callback.onDataLoaded(0, 0);
            }
        });
    }


    public void fetchHistory(HistoryCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String signUpDateStr = task.getResult().getString("signUpDate");

                if (signUpDateStr != null) {
                    try {
                        // Parse the sign-up date and current date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date signUpDate = dateFormat.parse(signUpDateStr);
                        Date currentDate = new Date();

                        // Generate a list of dates from sign-up date to current date
                        List<String> allDates = getDatesBetween(signUpDate, currentDate);

                        // Fetch water intake history for all dates
                        fetchHistoryForAllDays(userId, allDates, callback);

                    } catch (ParseException e) {
                        e.printStackTrace();
                        callback.onHistoryLoaded(new ArrayList<>());
                    }
                } else {
                    callback.onHistoryLoaded(new ArrayList<>());
                }
            } else {
                Log.e("FirestoreHelper", "Failed to fetch sign-up date");
                callback.onHistoryLoaded(new ArrayList<>());
            }
        });
    }

    // Helper method to generate list of dates between two dates
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

    private void fetchHistoryForAllDays(String userId, List<String> allDates, HistoryCallback callback) {
        List<HistoryRecord> historyList = new ArrayList<>();

        for (String date : allDates) {
            db.collection("users").document(userId).collection("records")
                    .whereEqualTo("date", date)
                    .get()
                    .addOnCompleteListener(task -> {
                        int totalIntake = 0;
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Long intakeLong = document.getLong("amount");
                                int intake = intakeLong != null ? intakeLong.intValue() : 0;
                                totalIntake += intake;
                            }
                        }

                        // Fetch the user's daily goal
                        Integer goal = dataModel.getGoal().getValue();// Get goal from DataModel
                        int finalGoal = goal != null ? goal : 2000;
                        int percentage = finalGoal > 0 ? (totalIntake * 100 / finalGoal) : 0;
                        HistoryRecord record = new HistoryRecord(date, percentage);
                        historyList.add(record);

                        // If all dates have been processed, return the full history
                        if (historyList.size() == allDates.size()) {
                            callback.onHistoryLoaded(historyList);
                        }
                    });
        }
    }

    public interface HistoryCallback {
        void onHistoryLoaded(List<HistoryRecord> historyList);
    }

    public interface FirestoreHelperCallback {
        void onDataLoaded(int goal, int intake);
    }

    public interface intakeCallback {
        void onIntakeLoaded(int totalIntake);
    }
}
