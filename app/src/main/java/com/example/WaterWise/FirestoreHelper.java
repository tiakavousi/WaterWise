package com.example.WaterWise;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore db;
    private final String userId;


    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void saveUserData(String name, int goal, int intake, int weight, String gender) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("goal", goal);
//        userData.put("intake", intake);
        userData.put("weight", weight);
        userData.put("gender", gender);

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    // New method to save a water intake record in the sub-collection
    public void saveWaterIntakeRecord(String time, String date, int amount) {
        Map<String, Object> recordData = new HashMap<>();
        recordData.put("time", time);
        recordData.put("date", date);
        recordData.put("amount", amount);

        // Adding the record to the 'records' sub-collection for the current user
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
//                  int intake = document.getLong("intake").intValue();
                    String name = document.getString("name");
                    int weight = document.getLong("weight").intValue();
                    String gender = document.getString("gender");

                    // Fetch daily intake asynchronously
                    fetchDailyIntake(userId, currentDate, totalIntake -> {
                        // Set the values only after the intake is fetched
                        dataModel.setGoal(goal);
                        dataModel.setIntake(totalIntake);
                        Log.d("!!!!! INTAKE UPDATE", totalIntake + "");
                        dataModel.setName(name);
                        dataModel.setWeight(weight);
                        dataModel.setGender(gender);

                        // Call the callback after setting all the data
                        callback.onDataLoaded(goal, totalIntake);
                    });
                }
            }
        });
    }

    // Create an interface for the callback
    public interface FirestoreHelperCallback {
        void onDataLoaded(int goal, int intake);
    }

    // Callback interface
    public interface intakeCallback {
        void onIntakeLoaded(int totalIntake);
    }


}
