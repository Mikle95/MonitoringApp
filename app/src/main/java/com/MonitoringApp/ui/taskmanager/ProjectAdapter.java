package com.MonitoringApp.ui.taskmanager;

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

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.R;
import com.MonitoringApp.ui.taskmanager.Task.TaskAdapter;
import com.MonitoringApp.ui.taskmanager.Task.TasksActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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


        TextView text = row.findViewById(R.id.textView2);
        text.setText(item.toString());

        ListView listView = row.findViewById(R.id.taskList);
        Button btn1 = row.findViewById(R.id.button2);
        Button btn2 = row.findViewById(R.id.button3);
        ProgressBar pb = row.findViewById(R.id.progressBar);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (btn1.getVisibility() != View.GONE) {
//                    btn1.setVisibility(View.GONE);
//                    btn2.setVisibility(View.GONE);
//                    closeRow(row);
//                    return;
//                }
//                btn1.setVisibility(View.VISIBLE);
//                btn2.setVisibility(View.VISIBLE);

                pb.setVisibility(View.VISIBLE);
                TasksApiController.getInstance().getTasks(item.project_name,
                        item.project_creator_login, getTaskCallback(pb));
            }
        });

        if (items.stream().count() == 1)
            row.callOnClick();
        return row;
    }

    public Callback getTaskCallback(ProgressBar pb) {
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.INVISIBLE);
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.INVISIBLE);
                        if (response.isSuccessful())
                            try {
                                Intent intent = new Intent(getContext(), TasksActivity.class);
                                intent.putExtra("tasks", response.body().string());
                                getContext().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                });
            }
        };
    }

    public void closeRow(View row){
        ListView listView = row.findViewById(R.id.taskList);
        if (listView.getAdapter() != null)
            ((TaskAdapter)listView.getAdapter()).clear();
    }
}