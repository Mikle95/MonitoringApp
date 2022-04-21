package com.MonitoringApp.ui.taskmanager.Project;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.ProjectViewBinding;
import com.MonitoringApp.ui.taskmanager.Task.TasksActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ProjectViewFragment extends Fragment {

    private ProjectViewBinding binding;
    ProjectAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ProjectViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.refreshProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        binding.allTasks.setOnClickListener(view -> {
            TasksApiController.getInstance().getAllTasks((response, isSuccessful) ->
                    new Handler(Looper.getMainLooper()).post(() -> {
                if (isSuccessful){
                    Intent intent = new Intent(getContext(), TasksActivity.class);
                    intent.putExtra("tasks", response);
                    intent.putExtra("pname", getResources().getString(R.string.my_tasks));
                    startActivity(intent);
                }
                else
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
            }));
        });

        refresh();
        return root;
    }

    public void refresh(){
        TasksApiController.getInstance().getProjects(getProjectCallback());
    }


    public IResponseCallback getProjectCallback() {
        return (response, isSuccessful) -> new Handler(Looper.getMainLooper()).post(() -> {
            if (isSuccessful) {
                try {
                    Project[] projs = ApiJsonFormats.parseGson(response, Project[].class);
                    adapter = new ProjectAdapter(getContext(),
                            android.R.layout.simple_list_item_1,
                            R.id.task_name, new ArrayList<>(Arrays.asList(projs)));
                    binding.listView.setAdapter(adapter);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(response);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }



}