package com.MonitoringApp.ui.taskmanager.Task;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.databinding.TaskViewBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;

public class TasksActivity extends AppCompatActivity {
    private TaskViewBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TaskViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fillLists();

    }

    public void fillLists(){
        try {
            String json = getIntent().getStringExtra("tasks");
            Task[] tasks = ApiJsonFormats.parseGson(json, Task[].class);
            buildTabs(new ArrayList<>(Arrays.asList(tasks)));
        }catch (Exception e){
            e.printStackTrace();
            finish();
        }
    }

    public void buildTabs(ArrayList<Task> mas){
        TaskFragmentAdapter adapter =
                new TaskFragmentAdapter(getSupportFragmentManager(), getLifecycle(), mas);

        binding.viewPage.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPage, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(Task.Status.mas[position]);
            }
        }).attach();
    }
}
