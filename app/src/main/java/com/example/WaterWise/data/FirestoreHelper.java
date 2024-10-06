package com.example.WaterWise.data;

import android.util.Log;

import com.example.WaterWise.history.HistoryRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
                    int goal = (goalLong != null) ? goalLong.intValue() : dataModel.DEFAULT_GOAL;
                    int weight = (goalLong != null) ? weightLong.intValue() : dataModel.DEFAULT_WEIGHT;

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

    public void fetchIntakeForDates(String userId, List<String> allDates, HistoryIntakeCallback callback) {
        List<HistoryRecord> historyIntakeList = new ArrayList<>();

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
                        historyIntakeList.add(new HistoryRecord(date, totalIntake));
                        if (historyIntakeList.size() == allDates.size()) {
                            callback.onIntakeLoaded(historyIntakeList);
                        }
                    });
        }
    }
    public void fetchSignUpDate(FirestoreHelper.SignUpDateCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String signUpDateStr = task.getResult().getString("signUpDate");
                callback.onSignUpDateFetched(signUpDateStr); // Pass the signUpDateStr to the callback
            } else {
                callback.onSignUpDateFetched(null); // Pass null in case of error
            }
        });
    }

    public interface SignUpDateCallback {
        void onSignUpDateFetched(String signUpDateStr); // Use String type
    }

    public interface HistoryIntakeCallback {
        void onIntakeLoaded(List<HistoryRecord> historyIntakeList);
    }

    public interface FirestoreHelperCallback {
        void onDataLoaded(int goal, int intake);
    }

    public interface intakeCallback {
        void onIntakeLoaded(int totalIntake);
    }
}
