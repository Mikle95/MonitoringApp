package com.MonitoringApp.ui.taskmanager.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.R;

import java.util.ArrayList;

public class TaskAdapter  extends ArrayAdapter<Task> {
    ArrayList<Task> items;
    public TaskAdapter(Context context, int resource, int textViewResourceId, ArrayList items){
        super(context, resource, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Context ctxt = getContext();
        LayoutInflater inflater =
                (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.list_item, parent, false);
        final Task item = getItem(position);


        TextView text = row.findViewById(R.id.textView2);
        text.setText(item.toString());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Task activity
            }
        });

        if (items.stream().count() == 1)
            row.callOnClick();
        return row;
    }
}
