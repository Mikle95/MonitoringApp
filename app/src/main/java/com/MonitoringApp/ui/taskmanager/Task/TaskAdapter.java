package com.MonitoringApp.ui.taskmanager.Task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.TaskItemBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


// Список тасков
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
        final View row = inflater.inflate(R.layout.task_item, parent, false);
        final TaskItemBinding binding = TaskItemBinding.bind(row);
        final Task item = getItem(position);

        prepareRow(binding, item);

        // Раскрыть/Закрыть информацию о таске
        row.setOnClickListener(view -> binding.editingLayout.setVisibility(
                binding.editingLayout.getVisibility() ==
                        View.VISIBLE ? View.GONE : View.VISIBLE));

        if (items.stream().count() == 1)
            row.callOnClick();
        return row;
    }

    // Заполнение полей строки
    public void prepareRow(TaskItemBinding binding, Task item){
        binding.editingLayout.setVisibility(View.GONE);
        binding.taskName.setText("  " + item.toString());
        binding.taskStartTime.setText(Task.dateFormat.format(item.getStart_time()));
        binding.taskEndTime.setText(Task.dateFormat.format(item.getEnd_time()));

        // Описание
        binding.projectCreator.setText(item.creator_login);
        if (item.task_description != null)
            binding.taskDescriprion.setText(item.task_description);
        binding.taskDescriprion.setOnClickListener(descriptionDialog(binding, item));

        // Выпадающий список
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Task.Status.mas);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.taskStatus.setAdapter(spinnerAdapter);
        binding.taskStatus.setSelection(Arrays.asList(Task.Status.mas).indexOf(item.status));
        binding.taskStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (item.status.equals(Task.Status.mas[i]))
                    return;
                item.status = Task.Status.mas[i];
                item.update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public View.OnClickListener descriptionDialog(TaskItemBinding binding, Task item){
        return view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Описание");

            final EditText input = new EditText(getContext());
            input.setText(item.task_description);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("Подтвердить", (dialogInterface, i) -> {
                item.task_description = input.getText().toString();
                binding.taskDescriprion.setText(item.task_description);
                item.update();
            });
            alertDialog.show();
        };
    }
}
