package com.MonitoringApp.API.data;

import static com.MonitoringApp.API.MainApiController.JSON;

import android.annotation.SuppressLint;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.ApiParams;
import com.MonitoringApp.API.ApiPaths;
import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.API.MainApiController;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class Task {
    public long task_id;
    public String creator_login;
    public String project_name;
    public String task_name;
    public String task_description;
    public String start_time;
    public String end_time;
    public String status;
    public String progress;
    public String worker_login;

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    @SuppressLint("SimpleDateFormat")
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\nHH:mm");

    public Timestamp getEnd_time() {
        return getTimestamp(end_time);
    }

    public Timestamp getStart_time(){
        return getTimestamp(start_time);
    }

    private static Timestamp getTimestamp(String date){
        try {
            return new Timestamp(df.parse(date).getTime());
        } catch (Exception e) { return null; }
    }

    @Override
    public String toString() {
        return task_name;
    }

    public static class Status{
        public static final String[] mas = new String[] {"NEW", "PROGRESS", "FINISHED", "APPROVING"};
        public static final String new_task = mas[0];
        public static final String in_progress = mas[1];
        public static final String ended = mas[2];
        public static final String approving = mas[3];

    }

    public void update(Callback callback){
        try {
            String json = ApiJsonFormats.writeGson(this, Task.class);
            Map<String, String> map = new HashMap<>();
            map.put(ApiParams.token, LoginController.getInstance().getToken());
            MainApiController.sendRequest(ApiPaths.update_task, map, RequestBody.create(json, JSON), callback);
        }catch (Exception e){e.printStackTrace();}
    }

    public void update(){
        update(null);
    }
}
