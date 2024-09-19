package com.example.WaterWise;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private final FirebaseFirestore db;
    private final String userId;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("User ID: " , userId);
    }

    public void saveUserData(String name, int goal, int intake, String weight, String gender) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("goal", goal);
        userData.put("intake", intake);
        userData.put("weight", weight);
        userData.put("gender", gender);

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }

    public void fetchUserData(String userId, DataModel dataModel, FirestoreHelperCallback callback) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int goal = document.getLong("goal").intValue();
                    int intake = document.getLong("intake").intValue();
                    String name = document.getString("name");
                    String weight = document.getString("weight");
                    String gender = document.getString("gender");

                    dataModel.setGoal(goal);
                    dataModel.setIntake(intake);
                    dataModel.setName(name != null ? name : "name");
                    dataModel.setWeight(weight != null ? weight : "100");
                    dataModel.setGender(gender != null ? gender : "Male");

                    callback.onDataLoaded(goal, intake);
                }
            }
        });
    }

    // Create an interface for the callback
    public interface FirestoreHelperCallback {
        void onDataLoaded(int goal, int intake);
    }


}
