package com.example.WaterWise.data;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.WaterWise.history.HistoryRecord;
import com.example.WaterWise.home.IntakeRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataModel extends AndroidViewModel {
    private FirestoreHelper firestoreHelper;
    private static final String KEY_GOAL = "key_goal";
    private static final String KEY_INTAKE = "key_intake";
    private static final String KEY_RECORDS = "key_records";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_WEIGHT = "key_weight";
    private static final String KEY_GENDER = "key_gender";

    public static final int DEFAULT_GOAL = 2000;
    public static final int DEFAULT_INTAKE = 0;
    public static final int DEFAULT_WEIGHT = 40;
    public static final String DEFAULT_NAME = "User";
    public static final String DEFAULT_GENDER = "Female";
    private static final String KEY_LAST_RESET_DATE = "key_last_reset_date";


    private SharedPreferences sharedPreferences;
    private Gson gson;

    // LiveData fields
    private MutableLiveData<Integer> goal = new MutableLiveData<>();
    private MutableLiveData<Integer> intake = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<Integer> weight = new MutableLiveData<>();
    private MutableLiveData<String> gender = new MutableLiveData<>();
    private MutableLiveData<List<IntakeRecord>> records = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<HistoryRecord>> historyRecords = new MutableLiveData<>(new ArrayList<>());


    public DataModel(Application application) {
        super(application);
        String sharedPrefsName = getSharedPrefsName();
        sharedPreferences = application.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
        gson = new Gson();
        checkAndResetDataIfNeeded();
        loadAllData();
        firestoreHelper = new FirestoreHelper();
    }

    private String getSharedPrefsName() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return "waterwise_prefs_" + userId;
    }

    private void checkAndResetDataIfNeeded() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastResetDate = sharedPreferences.getString(KEY_LAST_RESET_DATE, null);

        if (lastResetDate == null || !lastResetDate.equals(currentDate)) {
            // new day clear intake
            setIntake(0);
            setRecords(new ArrayList<>());
            sharedPreferences.edit().putString(KEY_LAST_RESET_DATE, currentDate).apply();
            Log.d("Intake After RESET", getIntake().getValue() + "");
        } else{
            Log.d("DataModel", "No reset needed.");
        }
    }

    // Getters and setters
    public MutableLiveData<Integer> getGoal() { return goal; }
    public MutableLiveData<Integer> getIntake() { return intake; }
    public MutableLiveData<String> getName() { return name; }
    public MutableLiveData<Integer> getWeight() { return weight; }
    public MutableLiveData<String> getGender() { return gender; }
    public MutableLiveData<List<IntakeRecord>> getRecords() { return records; }
    public MutableLiveData<List<HistoryRecord>> getHistoryRecords() { return historyRecords;}

    public void setName(String nameValue) {
        name.setValue(nameValue);
        saveToPreferences(KEY_NAME, nameValue);
    }

    public void setWeight(int weightValue) {
        weight.setValue(weightValue);
        saveToPreferences(KEY_WEIGHT, weightValue);
    }

    public void setGender(String genderValue) {
        gender.setValue(genderValue);
        saveToPreferences(KEY_GENDER, genderValue);
    }

    public void setRecords(List<IntakeRecord> newRecords) {
        records.setValue(newRecords);
        saveToPreferences(KEY_RECORDS, gson.toJson(newRecords));
    }

    public void setGoal(int goalValue) {
        goal.setValue(goalValue);
        saveToPreferences(KEY_GOAL, goalValue);
    }

    public void setIntake(int intakeValue) {
        intake.setValue(intakeValue);
        saveToPreferences(KEY_INTAKE, intakeValue);
    }

    public void addRecord(IntakeRecord record) {
        List<IntakeRecord> currentRecords = records.getValue();
        if (currentRecords != null) {
            currentRecords.add(record);
            setRecords(currentRecords);
        }
        firestoreHelper.saveWaterIntakeRecord(record.getTime(),record.getDate(), record.getAmount());
    }

    // Load all data at once
    private void loadAllData() {
        loadFromPreferences(goal, KEY_GOAL, DEFAULT_GOAL);
        loadFromPreferences(intake, KEY_INTAKE, DEFAULT_INTAKE);
        loadRecords();
        loadFromPreferences(name, KEY_NAME, DEFAULT_NAME);
        loadFromPreferences(weight, KEY_WEIGHT, DEFAULT_WEIGHT);
        loadFromPreferences(gender, KEY_GENDER, DEFAULT_GENDER);
    }

    // Generic load method
    private <T> void loadFromPreferences(MutableLiveData<T> liveData, String key, T defaultValue) {
        if (defaultValue instanceof Integer) {
            liveData.setValue((T) Integer.valueOf(sharedPreferences.getInt(key, (Integer) defaultValue)));
        } else if (defaultValue instanceof String) {
            String value = sharedPreferences.getString(key, (String) defaultValue);
            if (value != null && !value.isEmpty()) {
                liveData.setValue((T) value);
            } else {
                liveData.setValue(defaultValue);
            }
        }
    }

    // records load method
    private void loadRecords() {
        String json = sharedPreferences.getString(KEY_RECORDS, null);
        Type type = new TypeToken<List<IntakeRecord>>() {}.getType();
        List<IntakeRecord> savedRecords = json != null ? gson.fromJson(json, type) : new ArrayList<>();
        records.setValue(savedRecords);
    }

    // Save to SharedPreferences
    private void saveToPreferences(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.apply();
    }
}