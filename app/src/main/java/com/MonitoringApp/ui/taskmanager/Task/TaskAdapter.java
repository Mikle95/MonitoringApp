package com.MonitoringApp.ui.taskmanager.Task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiContext;

import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.LoginController;
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
                final String status = item.status;
                item.status = Task.Status.mas[i];
                item.update(new IResponseCallback() {
                    @Override
                    public void execute(String response, boolean isSuccessful) {
                        if (isSuccessful)
                            return;
                        item.status = status;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
//                                binding.taskStatus.setSelection(
//                                        Arrays.asList(Task.Status.mas).indexOf(status));
                            }
                        });
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.deleteTask.setOnClickListener(view -> deleteDialog(binding, item));
    }

    // Редактирование описания
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

    public void deleteDialog(TaskItemBinding binding, Task item){
//        if (!item.creator_login.equals(LoginController.getInstance().getUsername())){
//            Toast.makeText(getContext(), "Удалять может только создатель", Toast.LENGTH_SHORT).show();
//            return;
//        }

        AlertDialog.Builder dialog = new
                AlertDialog.Builder(getContext());
        dialog.setMessage("Delete " + item.task_name + "?");
        dialog.setPositiveButton("Yes",
                (dialog12, which) -> item.delete(
                        (response, isSuccessful) -> new Handler(Looper.getMainLooper()).post(() -> {
            if (isSuccessful) {
                remove(item);
                notifyDataSetChanged();
            }
            else
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
        })));
        dialog.setNegativeButton("No", (dialog1, which) -> dialog1.cancel());
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
