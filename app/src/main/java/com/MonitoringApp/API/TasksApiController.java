package com.MonitoringApp.API;

import com.MonitoringApp.API.data.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;

public class TasksApiController {
    private static volatile TasksApiController instance;
    public static TasksApiController getInstance(){
        if (instance == null)
            instance = new TasksApiController();
        return instance;
    }

    public void getProjects(Callback callback){
        Map<String, String> params = new HashMap<>();
        params.put("token", LoginController.getInstance().getToken());
        MainApiController.sendGetRequest(ApiPaths.get_project, params, callback);
    }

    private TasksApiController(){

    }
}
