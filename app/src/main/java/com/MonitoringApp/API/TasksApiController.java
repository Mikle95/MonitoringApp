package com.MonitoringApp.API;

import com.MonitoringApp.API.data.Project;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class TasksApiController {
    private static volatile TasksApiController instance;
    public static TasksApiController getInstance(){
        if (instance == null)
            instance = new TasksApiController();
        return instance;
    }

    public void getProjects(IResponseCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.token, LoginController.getInstance().getToken());
        MainApiController.sendGetRequest(ApiPaths.get_project, params, callback);
    }

    public void getTasks(String pname, String creator_login, IResponseCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.token, LoginController.getInstance().getToken());
        String json_body = String.format(ApiJsonFormats.task, pname, creator_login);
        RequestBody rb = RequestBody.create(json_body, MainApiController.JSON);
        MainApiController.sendRequest(ApiPaths.get_project_tasks, params, rb, callback);
    }

    private TasksApiController(){

    }
}
