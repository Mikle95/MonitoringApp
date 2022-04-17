package com.MonitoringApp.ui.taskmanager;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.ProjectViewBinding;

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

        refresh();
        return root;
    }

    public void refresh(){
        TasksApiController.getInstance().getProjects(getProjectCallback());
    }


    public Callback getProjectCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                String json = response.body().string();
                                Project[] projs = ApiJsonFormats.parseGson(json, Project[].class);
                                adapter = new ProjectAdapter(getContext(),
                                        android.R.layout.simple_list_item_1,
                                        R.id.textView2, new ArrayList<>(Arrays.asList(projs)));
                                binding.listView.setAdapter(adapter);
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(response);
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }



}