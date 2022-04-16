package com.MonitoringApp.ui.taskmanager;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.TaskManagerBinding;

import java.util.ArrayList;


public class TaskmanagerFragment extends Fragment {

    private TaskManagerBinding binding;
    ProjectAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = TaskManagerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new ProjectAdapter(getContext(), android.R.layout.simple_list_item_1, R.id.textView2, new ArrayList<Project>());

        // TODO: -- REMOVE
        test(adapter, 10);
        binding.listView.setAdapter(adapter);

        return root;
    }

    // TODO: -- REMOVE
    public static void test(ProjectAdapter adapter, int count){
        for (int i = 0; i < count; ++i){
            Project a = new Project();
            a.project_creator_login = "Mikle";
            a.project_name = "Some project";
            adapter.add(a);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }


    private static class ProjectAdapter extends ArrayAdapter<Project> {
        ArrayList<Project> items;
        View activeView;
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

            ListView listView = row.findViewById(R.id.taskList);
            Button btn1 = row.findViewById(R.id.button2);
            Button btn2 = row.findViewById(R.id.button3);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listView.getAdapter() != null && ((ProjectAdapter)listView.getAdapter()).getCount() > 0) {
                        btn1.setVisibility(View.GONE);
                        btn2.setVisibility(View.GONE);
                        closeRow(row);
                        return;
                    }
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);

                    ProjectAdapter adapter = new ProjectAdapter(getContext(), android.R.layout.simple_list_item_1, R.id.textView2, new ArrayList<Project>());
                    test(adapter, 20);

                    listView.setAdapter(adapter);
                }
            });

            if (items.stream().count() == 1)
                row.callOnClick();
            return row;
        }

        public void closeRow(View row){
            ListView listView = row.findViewById(R.id.taskList);
            ((ProjectAdapter)listView.getAdapter()).clear();
        }
    }
}