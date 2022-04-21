package com.MonitoringApp.ui.taskmanager.Task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.TaskItemBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


// Список тасков
public class TaskAdapter  extends ArrayAdapter<Task> {
    ArrayList<Task> items;
    Resources res;
    public TaskAdapter(Context context, int resource, int textViewResourceId, ArrayList items){
        super(context, resource, textViewResourceId, items);
        this.items = items;
        res = getContext().getResources();
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
        binding.taskName.setOnLongClickListener(taskNameDialog(item));
        binding.taskName.setOnClickListener(view -> binding.getRoot().callOnClick());

        binding.taskStartTime.setText(Task.dateFormat.format(item.getStart_time()));
        binding.taskEndTime.setText(Task.dateFormat.format(item.getEnd_time()));

        // Описание
        binding.projectCreator.setText(item.creator_login);
        binding.projectCreator.setOnClickListener(descriptionDialog(item));
        if (item.task_description != null)
            binding.taskDescriprion.setText(item.task_description);
        binding.taskDescriprion.setOnClickListener(descriptionDialog(item));

        binding.workerLogin.setText(item.worker_login != null ?
                res.getString(R.string.worker) + ": " + item.worker_login :
                res.getString(R.string.no_worker));
        binding.workerLogin.setOnClickListener(workerLoginDialog(item));

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
                item.update((response, isSuccessful) -> {
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
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.deleteTask.setOnClickListener(view -> deleteDialog(item));
        binding.taskStartTime.setOnLongClickListener(changeTime((response, isSuccessful) -> {
            item.start_time = response;
            item.update((response1, isSuccessful1) -> {
                    ((TasksActivity)getContext()).refresh();
            });
        }));
        binding.taskEndTime.setOnLongClickListener(changeTime((response, isSuccessful) -> {
            item.end_time = response;
            item.update((response1, isSuccessful1) -> {
                ((TasksActivity)getContext()).refresh();
            });
        }));
        binding.taskStartTime.setOnClickListener(view -> binding.getRoot().callOnClick());
        binding.taskEndTime.setOnClickListener(view -> binding.getRoot().callOnClick());
    }

    public View.OnLongClickListener changeTime(IResponseCallback action){
        return view -> {
            Calendar cal = Calendar.getInstance();
//                cal.setTimeZone(TimeZone.getDefault());

            TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, i, i1) -> {
                cal.set(Calendar.HOUR, i);
                cal.set(Calendar.MINUTE, i1);
                action.execute(Task.getTimeString(cal.getTime()), true);
            };

            DatePickerDialog.OnDateSetListener listener = (datePicker, i, i1, i2) -> {
                cal.set(Calendar.YEAR, i);
                cal.set(Calendar.MONTH, i1);
                cal.set(Calendar.DAY_OF_MONTH, i2);
                new TimePickerDialog(getContext(), timeSetListener, cal.get(Calendar.HOUR),
                        cal.get(Calendar.MINUTE), true).show();
            };

            new DatePickerDialog(getContext(), listener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            return false;
        };
    }

    public View.OnClickListener workerLoginDialog(Task item) {
        return view -> {
            if (!item.creator_login.equals(LoginController.getInstance().getUsername())){
                Toast.makeText(getContext(), res.getString(R.string.alert_change_worker), Toast.LENGTH_SHORT).show();
            }
            openDialogEditText(getContext(), res.getString(R.string.change_task_worker), item.worker_login, (response, isSuccessful) -> {
                item.worker_login = response;
                item.update();
            });
        };
    }

    public View.OnLongClickListener taskNameDialog(Task item){
        return view -> {
            if (!item.creator_login.equals(LoginController.getInstance().getUsername())){
                Toast.makeText(getContext(), res.getString(R.string.alert_change_name), Toast.LENGTH_SHORT).show();
                return false;
            }
            openDialogEditText(getContext(), res.getString(R.string.change_task_name), item.task_name, (response, isSuccessful) -> {
                item.task_name = response;
                item.update();
            });
            return false;
        };
    }

    // Редактирование описания
    public View.OnClickListener descriptionDialog(Task item){
        return view -> openDialogEditText(getContext(), res.getString(R.string.change_description),
                item.task_description, (response, isSuccessful) -> {
            item.task_description = response;
            item.update();
        });
    }

    public void deleteDialog(Task item){
        if (!item.creator_login.equals(LoginController.getInstance().getUsername())){
            Toast.makeText(getContext(), res.getString(R.string.alert_delete), Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder dialog = new
                AlertDialog.Builder(getContext());
        dialog.setMessage(res.getString(R.string.delete) + item.task_name + "?");
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

    public static void openDialogEditText(Context ctxt, String title, String text, IResponseCallback action){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctxt);
        alertDialog.setTitle(title);

        final EditText input = new EditText(ctxt);
        input.setText(text);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(ctxt.getResources().getString(R.string.accept),
                (dialogInterface, i) -> action.execute(input.getText().toString(), true));
        alertDialog.show();

    }
}
