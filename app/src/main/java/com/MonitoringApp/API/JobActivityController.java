package com.MonitoringApp.API;

import java.util.HashMap;
import java.util.Map;

public class JobActivityController {

    public static void getActivity(IResponseCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.token, LoginController.getInstance().getToken());
        MainApiController.sendGetRequest(ApiPaths.get_activity, params, callback);
    }

    public static void startActivity(IResponseCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.token, LoginController.getInstance().getToken());
        MainApiController.sendGetRequest(ApiPaths.start_activity, params, callback);
    }

    public static void endActivity(IResponseCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.token, LoginController.getInstance().getToken());
        MainApiController.sendGetRequest(ApiPaths.end_activity, params, callback);
    }
}
