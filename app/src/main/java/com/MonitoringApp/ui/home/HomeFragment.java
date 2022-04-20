package com.MonitoringApp.ui.home;

import android.content.Intent;
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

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.API.MainApiController;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.MainActivity;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.FragmentHomeBinding;
import com.MonitoringApp.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.logout.setOnClickListener(view -> LoginController.getInstance().logout(getContext()));

        return root;
    }


@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}