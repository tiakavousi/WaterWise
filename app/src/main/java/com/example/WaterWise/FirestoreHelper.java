package com.example.WaterWise;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class FirestoreHelper {

    private final FirebaseFirestore db;
    private final String userId;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void saveUserData(String name, int goal, int weight, String gender) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("goal", goal);
        userData.put("weight", weight);
        userData.put("gender", gender);

        db.collection("users").document(userId).set(userData)
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
                    int goal = document.getLong("goal").intValue();
                    String name = document.getString("name");
                    int weight = document.getLong("weight").intValue();
                    String gender = document.getString("gender");

                    fetchDailyIntake(userId, currentDate, totalIntake -> {
                        dataModel.setGoal(goal);
                        dataModel.setIntake(totalIntake);
                        Log.d("!!!!! INTAKE UPDATE", totalIntake + "");
                        dataModel.setName(name);
                        dataModel.setWeight(weight);
                        dataModel.setGender(gender);
                        callback.onDataLoaded(goal, totalIntake);
                    });
                }
            }
        });
    }


    public void fetchHistory(HistoryCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("records")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<String> uniqueDates = new HashSet<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String date = document.getString("date");
                            if (date != null) {
                                uniqueDates.add(date);
                            }
                        }

                        Log.d("FirestoreHelper", "Distinct days found: " + uniqueDates.size());

                        if (!uniqueDates.isEmpty()) {
                            fetchHistoryForDays(new ArrayList<>(uniqueDates), callback);
                        } else {
                            callback.onHistoryLoaded(new ArrayList<>());
                        }
                    } else {
                        Log.w("FirestoreHelper", "Error fetching records", task.getException());
                        callback.onHistoryLoaded(new ArrayList<>());  // Return an empty list on failure
                    }
                });
    }

    private void fetchHistoryForDays(List<String> uniqueDates, HistoryCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<HistoryRecord> historyList = new ArrayList<>();

        for (String date : uniqueDates) {
            db.collection("users").document(userId).collection("records")
                    .whereEqualTo("date", date)
                    .get()
                    .addOnCompleteListener(task -> {
                        int totalIntake = 0;
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                int intake = document.getLong("amount").intValue();
                                totalIntake += intake;
                            }
                        }

                        int finalTotalIntake = totalIntake;
                        db.collection("users").document(userId).get().addOnCompleteListener(goalTask -> {
                            if (goalTask.isSuccessful() && goalTask.getResult() != null) {
                                int goal = goalTask.getResult().getLong("goal").intValue();
                                int percentage = goal > 0 ? (finalTotalIntake * 100 / goal) : 0;

                                // Create a history record
                                HistoryRecord record = new HistoryRecord(date, percentage);
                                historyList.add(record);
                                Log.d("FirestoreHelper", "Fetched history size: " + historyList.size());

                                if (historyList.size() == uniqueDates.size()) {
                                    Log.d("FirestoreHelper", "Total history fetched: " + historyList.size());
                                    callback.onHistoryLoaded(historyList);
                                }
                            }
                        });
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
