package com.example.WaterWise;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

    private static final String KEY_GOAL = "key_goal";
    private static final String KEY_INTAKE = "key_intake";
    private static final String KEY_RECORDS = "key_records";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_WEIGHT = "key_weight";
    private static final String KEY_GENDER = "key_gender";

    private static final int DEFAULT_GOAL = 2000;
    private static final int DEFAULT_INTAKE = 0;
    private static final int DEFAULT_WEIGHT = 40;
    private static final String DEFAULT_NAME = "User";
    private static final String DEFAULT_GENDER = "Female";
    private static final String KEY_LAST_RESET_DATE = "key_last_reset_date";


    private SharedPreferences sharedPreferences;
    private Gson gson;

    // LiveData fields
    private MutableLiveData<Integer> goal = new MutableLiveData<>();
    private MutableLiveData<Integer> intake = new MutableLiveData<>();
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<Integer> weight = new MutableLiveData<>();
    private MutableLiveData<String> gender = new MutableLiveData<>();
    private MutableLiveData<List<Record>> records = new MutableLiveData<>(new ArrayList<>());

    private String getSharedPrefsName() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return "waterwise_prefs_" + userId;
    }

    public DataModel(Application application) {
        super(application);
        String sharedPrefsName = getSharedPrefsName();
        sharedPreferences = application.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE);
        gson = new Gson();
        checkAndResetDataIfNeeded();
        loadAllData();
    }
    // Check if today's date is different from the saved date, and reset if needed
    private void checkAndResetDataIfNeeded() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastResetDate = sharedPreferences.getString(KEY_LAST_RESET_DATE, null);

        if (lastResetDate == null || !lastResetDate.equals(currentDate)) {
            // new day clear intake
            setValueAndSave(intake, 0, KEY_INTAKE);
            saveToPreferences(KEY_INTAKE, 0);
            Log.d("!!!!!! !!!!!!! Intake After RESET", getIntake().getValue() + "");

            // new day clear records
            List<Record> emptyRecords = new ArrayList<>();
            setValueAndSave(records, emptyRecords, KEY_RECORDS);
            saveToPreferences(KEY_RECORDS, gson.toJson(emptyRecords));

            // update the last reset date
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LAST_RESET_DATE, currentDate);
            editor.apply();
        } else{
            Log.d("DataModel", "No reset needed.");
        }
    }

    // Getters and setters
    public MutableLiveData<Integer> getGoal() { return goal; }
    public void setGoal(int goalValue) { setValueAndSave(goal, goalValue, KEY_GOAL); }

    public MutableLiveData<Integer> getIntake() { return intake; }
    public void setIntake(int intakeValue) { setValueAndSave(intake, intakeValue, KEY_INTAKE); }

    public MutableLiveData<String> getName() { return name; }
    public void setName(String nameValue) { setValueAndSave(name, nameValue, KEY_NAME); }

    public MutableLiveData<Integer> getWeight() { return weight; }
    public void setWeight(int weightValue) { setValueAndSave(weight, weightValue, KEY_WEIGHT); }

    public MutableLiveData<String> getGender() { return gender; }
    public void setGender(String genderValue) { setValueAndSave(gender, genderValue, KEY_GENDER); }

    public MutableLiveData<List<Record>> getRecords() { return records; }
    public void addRecord(Record record) {
        List<Record> currentRecords = records.getValue();
        if (currentRecords != null) {
            currentRecords.add(record);
            records.setValue(currentRecords);
            saveToPreferences(KEY_RECORDS, gson.toJson(currentRecords));
        }
        Log.d("DataModel!!!!! ", "Record Date: " + record.getDate());

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

    // Generic save method
    private <T> void setValueAndSave(MutableLiveData<T> liveData, T value, String key) {
        liveData.setValue(value);
        saveToPreferences(key, value);
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
        Type type = new TypeToken<List<Record>>() {}.getType();
        List<Record> savedRecords = json != null ? gson.fromJson(json, type) : new ArrayList<>();
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