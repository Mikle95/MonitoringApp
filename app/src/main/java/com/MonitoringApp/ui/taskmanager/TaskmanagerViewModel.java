package com.MonitoringApp.ui.taskmanager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TaskmanagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TaskmanagerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is TaskManager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}