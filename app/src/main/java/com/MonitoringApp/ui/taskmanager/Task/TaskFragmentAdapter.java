package com.MonitoringApp.ui.taskmanager.Task;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.MonitoringApp.API.data.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TaskFragmentAdapter extends FragmentStateAdapter {
    private ArrayList<Task> items;

    public TaskFragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle,
                               ArrayList<Task> items){
        super(fragmentManager, lifecycle);
        this.items = items;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Task[] items = this.items.stream().filter(x -> x.status.equals(Task.Status.mas[position])).toArray(Task[]::new);
        return new TaskFragment()
                .setFilter(Task.Status.mas[position])
                .setTasks(new ArrayList<>(Arrays.asList(items)));
    }

    @Override
    public int getItemCount() {
        return Task.Status.mas.length;
    }
}
