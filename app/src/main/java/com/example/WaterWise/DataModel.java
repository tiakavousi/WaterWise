package com.example.WaterWise;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataModel extends ViewModel {
    private final MutableLiveData<Integer> intakeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> goalLiveData = new MutableLiveData<>();

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
}

