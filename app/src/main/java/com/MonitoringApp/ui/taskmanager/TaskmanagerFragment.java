package com.MonitoringApp.ui.taskmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.MonitoringApp.databinding.FragmentDashboardBinding;
import com.MonitoringApp.databinding.TaskManagerBinding;

public class TaskmanagerFragment extends Fragment {

    private TaskmanagerViewModel taskmanagerViewModel;
    private TaskManagerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        taskmanagerViewModel = new ViewModelProvider(this).get(TaskmanagerViewModel.class);

        binding = TaskManagerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
//        taskmanagerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}