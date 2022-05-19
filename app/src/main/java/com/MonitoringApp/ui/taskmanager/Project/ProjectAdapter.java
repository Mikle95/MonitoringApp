package com.MonitoringApp.ui.taskmanager.Project;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.R;
import com.MonitoringApp.ui.taskmanager.Task.TaskAdapter;
import com.MonitoringApp.ui.taskmanager.Task.TasksActivity;

import java.util.ArrayList;

public class ProjectAdapter extends ArrayAdapter<Project> {
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


        TextView text = row.findViewById(R.id.task_name);
        text.setText(item.toString());

        ProgressBar pb = row.findViewById(R.id.progressBar);

        row.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            TasksApiController.getInstance().getTasks(item.project_name,
                    item.project_creator_login, getTaskCallback(pb, item.project_name, item.project_creator_login));
        });

        if (items.stream().count() == 1)
            row.callOnClick();
        return row;
    }

    public IResponseCallback getTaskCallback(ProgressBar pb, String pname, String username) {
        return (response, isSuccessful) -> {
            pb.setVisibility(View.INVISIBLE);
            if (!isSuccessful)
                System.out.println(response);
            else
                try {
                    Intent intent = new Intent(getContext(), TasksActivity.class);
                    intent.putExtra("tasks", response);
                    intent.putExtra("pname", pname);
                    intent.putExtra("plogin", username);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        };

    }

    public void closeRow(View row){
        ListView listView = row.findViewById(R.id.taskList);
        if (listView.getAdapter() != null)
            ((TaskAdapter)listView.getAdapter()).clear();
    }
}
