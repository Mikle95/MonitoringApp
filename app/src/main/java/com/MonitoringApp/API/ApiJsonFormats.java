package com.MonitoringApp.API;

import com.MonitoringApp.API.data.Project;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class ApiJsonFormats {
    public static final String login = "{\"login\":\"%s\",\"password\":\"%s\"}";
    public static final String task = "{" +   "\"project_name\": \"%s\", " +
                                              "\"project_creator_login\": \"%s\"}";

    public static <T> T parseGson(String lines, Class<T> type) throws Exception{
        Gson g = new Gson();
        T out = g.fromJson(lines, type);
        return out;
    }
}
