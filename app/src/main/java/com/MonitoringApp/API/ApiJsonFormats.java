package com.MonitoringApp.API;

import com.MonitoringApp.API.data.Project;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ApiJsonFormats {
    public static final String login = "{\"login\":\"%s\",\"password\":\"%s\"}";
    public static final String task = "{" +   "\"project_name\": \"%s\", " +
                                              "\"project_creator_login\": \"%s\"}";

    public static final String photo = "{\"login\":\"%s\",\"photo\":\"%s\"}";
    public static final String firebase_token = "{\"token\":\"%s\"}";


    public static final Gson g = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .serializeNulls()
            .create();

    public static <T> T parseGson(String lines, Class<T> type) throws Exception{
        T out = g.fromJson(lines, type);
        return out;
    }

    public static <T> String writeGson(T object, Class<T> type) throws Exception{
        String out = g.toJson(object, type);
        return out;
    }
}
