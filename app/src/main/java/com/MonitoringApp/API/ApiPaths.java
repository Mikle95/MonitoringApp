package com.MonitoringApp.API;

public class ApiPaths {
    public static final String host = "http://192.168.3.56:8080/";
    public static final String login = host + "api/v1/login";
    public static final String info = host + "api/v1/info";
    public static final String refresh = host + "api/v1/refreshToken";
    public static final String get_project = host + "api/v1/my/project";
    public static final String get_project_tasks = host + "api/v1/project/task";
    public static final String update_task = host + "api/v1/update/task";
    public static final String delete_task = host + "api/v1/delete/task";
    public static final String create_task = host + "api/v1/create/task";
}
