package com.MonitoringApp.ui.taskmanager.Task;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.FragmentTaskBinding;

import java.util.ArrayList;

public class TaskFragment extends Fragment {

    private String filter = "";
    private ArrayList<Task> items;
    private FragmentTaskBinding binding;
    private TaskAdapter adapter;

    public TaskFragment() {
        // Required empty public constructor
    }

    public TaskFragment setFilter(String filter){
        this.filter = filter;
        return this;
    }

    public TaskFragment setTasks(ArrayList<Task> items){
        this.items = items;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        adapter = new TaskAdapter(getContext(), android.R.layout.simple_list_item_1,
                R.id.textView2, items);
        binding.taskList2.setAdapter(adapter);
        // Inflate the layout for this fragment
        return root;
    }
}