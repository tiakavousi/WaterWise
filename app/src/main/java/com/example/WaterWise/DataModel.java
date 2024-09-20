package com.example.WaterWise;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class DataModel extends AndroidViewModel {
    private final MutableLiveData<Integer> intakeLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> goalLiveData = new MutableLiveData<>(2000);
    private final MutableLiveData<String> nameLiveData = new MutableLiveData<>("name");
    private final MutableLiveData<String> weightLiveData = new MutableLiveData<>("100");
    private final MutableLiveData<String> genderLiveData = new MutableLiveData<>("Male");
    private final MutableLiveData<List<Record>> recordsLiveData = new MutableLiveData<>(new ArrayList<>());

    public DataModel(Application application) {
        super(application);
    }

    public LiveData<Integer> getIntake() {
        return intakeLiveData;
    }

    public void setIntake(int intake) {
        intakeLiveData.setValue(intake);
    }

    public LiveData<Integer> getGoal() {
        return goalLiveData;
    }

    public void setGoal(int goal) {
        goalLiveData.setValue(goal);
    }
    public LiveData<String> getName() {
        return nameLiveData;
    }

    public void setName(String newName) {
        nameLiveData.setValue(newName);
    }

    public LiveData<String> getWeight() {
        return weightLiveData;
    }

    public void setWeight(String newWeight) {
        weightLiveData.setValue(newWeight);
    }

    public LiveData<String> getGender(){
        return genderLiveData;
    }
    public void setGender(String newGender){
        genderLiveData.setValue(newGender);
    }

    public LiveData<List<Record>> getRecords() {
        return recordsLiveData;
    }
    public void addRecord(Record newRecord) {
        List<Record> currentRecords = recordsLiveData.getValue();
        if (currentRecords != null) {
            currentRecords.add(newRecord);
            recordsLiveData.setValue(currentRecords);
        }
    }

    public void clearRecords() {
        recordsLiveData.setValue(new ArrayList<>());
    }
}

