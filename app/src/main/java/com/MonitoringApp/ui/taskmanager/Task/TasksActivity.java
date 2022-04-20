package com.MonitoringApp.ui.taskmanager.Task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.databinding.TaskViewBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TasksActivity extends AppCompatActivity {
    private TaskViewBinding binding;
    private String proj_login;
    private String proj_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TaskViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        proj_name = intent.getStringExtra("pname");
        binding.projectNameText.setText(proj_name);
        proj_login = intent.getStringExtra("plogin");
        fillLists(intent.getStringExtra("tasks"));
        binding.refreshTasks.setOnClickListener(view -> refresh());

        binding.addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task.create(proj_name, (response, isSuccessful) -> runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccessful)
                            refresh();
                        else
                            Toast.makeText(TasksActivity.this, response, Toast.LENGTH_SHORT)
                                    .show();
                    }
                }));

//                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
            }
        });
    }

    public void refresh(){
        binding.progressBar3.setVisibility(View.VISIBLE);
        TasksApiController.getInstance().getTasks(proj_name, proj_login, (response, isSuccessful) -> {
            if (!isSuccessful)
                System.out.println(response);
            else
                runOnUiThread(() -> fillLists(response));
        });


    }

    public void fillLists(String json){
        try {
            Task[] tasks = ApiJsonFormats.parseGson(json, Task[].class);
            buildTabs(new ArrayList<>(Arrays.asList(tasks)));
            return;
        }catch (Exception e){ e.printStackTrace(); }
        finish();
    }

    public void buildTabs(ArrayList<Task> mas){
        TaskFragmentAdapter adapter =
                new TaskFragmentAdapter(getSupportFragmentManager(), getLifecycle(), mas);

        binding.viewPage.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPage,
                (tab, position) -> tab.setText(Task.Status.mas[position])).attach();
        binding.progressBar3.setVisibility(View.INVISIBLE);
    }
}
