package com.MonitoringApp.ui.taskmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.FragmentDashboardBinding;
import com.MonitoringApp.databinding.TaskManagerBinding;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TaskmanagerFragment extends Fragment {

    private TaskManagerBinding binding;
    ProjectAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = TaskManagerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new ProjectAdapter(getContext(), android.R.layout.simple_list_item_1, R.id.textView2, new ArrayList<Project>());
        Project a = new Project();
        a.project_creator_login = "Mikle";
        a.project_name = "Some project";
        adapter.add(a);
        Project b = new Project();
        b.project_creator_login = "Mikle";
        b.project_name = "Some project";
        adapter.add(b);


        binding.listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private static class ProjectAdapter extends ArrayAdapter<Project> {
        ArrayList<Project> items;
        public ProjectAdapter(Context context, int resource, int textViewResourceId, ArrayList items){
            super(context, resource, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            Context ctxt = getContext();
            LayoutInflater inflater =
                    (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.list_item, parent, false);
            final Project item = getItem(position);


            TextView text = row.findViewById(R.id.textView2);
            text.setText(item.project_name + "\n" +
                    item.project_creator_login);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.
//                    row.findViewById(R.id.taskList);
                }
            });


            return row;
        }
    }
}