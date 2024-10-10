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
/**
 * FirestoreHelper is a utility class that provides functions to interact with Firestore.
 * It helps save and retrieve user data including water intake records, user information, and history data.
 */
public class FirestoreHelper {

    private final FirebaseFirestore db;
    private final String userId;

    /**
     * Constructor for FirestoreHelper.
     * Initializes Firestore instance and fetches the current user's ID.
     */
    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Saves user data such as name, goal, weight, and gender to Firestore.
     *
     * @param name  The user's name.
     * @param goal  The user's daily water intake goal.
     * @param weight The user's weight.
     * @param gender The user's gender.
     */
    public void saveUserData(String name, int goal, int weight, String gender) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("goal", goal);
        userData.put("weight", weight);
        userData.put("gender", gender);
        db.collection("users").document(userId).update(userData)
                .addOnSuccessListener(
                        aVoid -> Log.d("Firestore", "User data successfully written!")
                ).addOnFailureListener(
                        e -> Log.w("Firestore", "Error writing document", e)
                );
    }

    /**
     * Saves a water intake record to Firestore.
     *
     * @param time  The time of water intake.
     * @param date  The date of water intake.
     * @param amount The amount of water consumed.
     */
    public void saveWaterIntakeRecord(String time, String date, int amount) {
        Map<String, Object> recordData = Map.of(
                "time", time,
                "date", date,
                "amount", amount
        );
        db.collection("users").document(userId).collection("records").add(recordData)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firestore", "Record successfully added to sub-collection!")
                )
                .addOnFailureListener(e ->
                        Log.w("Firestore", "Error adding record", e)
                );
    }

    /**
     * Fetches the total water intake for a specific date from Firestore.
     *
     * @param date     The date for which the intake should be fetched.
     * @param callback The callback to handle the retrieved intake value.
     */
    public void fetchDailyIntake( String date, intakeCallback callback) {
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

    /**
     * Fetches user data from Firestore, including name, goal, weight, gender, and daily intake.
     * Updates the DataModel with the fetched data.
     *
     * @param dataModel The DataModel instance to update with the fetched data.
     * @param callback  The callback to indicate the completion of data loading.
     */
    public void fetchUserData( DataModel dataModel, FirestoreHelperCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long goalLong = document.getLong("goal");
                    String nameStr = document.getString("name");
                    Long weightLong = document.getLong("weight");
                    String genderStr = document.getString("gender");
                    int goal = (goalLong != null) ? goalLong.intValue() : dataModel.DEFAULT_GOAL;
                    int weight = (goalLong != null) ? weightLong.intValue() : dataModel.DEFAULT_WEIGHT;
                    String name = (nameStr != null && !nameStr.isEmpty()) ? nameStr : DataModel.DEFAULT_NAME;
                    String gender = (genderStr != null && !genderStr.isEmpty()) ? genderStr : DataModel.DEFAULT_GENDER;

                    fetchDailyIntake( currentDate, totalIntake -> {
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
                    callback.onDataLoaded(dataModel.DEFAULT_GOAL, dataModel.DEFAULT_INTAKE);
                }
            } else {
                    Log.e("FirestoreHelper", "Task was not successful or result is null.");
                    callback.onDataLoaded(dataModel.DEFAULT_GOAL, dataModel.DEFAULT_INTAKE);
            }
        });
    }

    /**
     * Fetches the total water intake records for a list of dates and returns the records using a callback.
     *
     * @param allDates The list of dates for which intake records should be fetched.
     * @param callback The callback to handle the retrieved list of HistoryRecords.
     */
    public void fetchIntakeForDates( List<String> allDates, HistoryIntakeCallback callback) {
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


    /**
     * Saves the sign-up date for the current user in Firestore.
     *
     * @param signUpDate The sign-up date to be saved in Firestore.
     */
    public void saveSignUpDate(String signUpDate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("signUpDate", signUpDate);

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Sign-up date successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing sign-up date", e));
    }

    /**
     * Fetches the user's sign-up date from Firestore.
     *
     * @param callback The callback to handle the retrieved sign-up date.
     */
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

    /**
     * Callback interface to handle sign-up date retrieval.
     */
    public interface SignUpDateCallback {
        void onSignUpDateFetched(String signUpDateStr);
    }

    /**
     * Callback interface to handle retrieval of historical intake data.
     */
    public interface HistoryIntakeCallback {
        void onIntakeLoaded(List<HistoryRecord> historyIntakeList);
    }

    /**
     * Callback interface to handle the retrieval of user data.
     */
    public interface FirestoreHelperCallback {
        void onDataLoaded(int goal, int intake);
    }

    /**
     * Callback interface to handle the retrieval of daily intake data.
     */
    public interface intakeCallback {
        void onIntakeLoaded(int totalIntake);
    }
}
