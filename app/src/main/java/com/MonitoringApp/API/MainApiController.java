package com.MonitoringApp.API;

import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainApiController {
    private static MainApiController instance;

    public MainApiController(){
        instance = this;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void


    public static void sendPostRequest(String url, RequestBody body, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ApiPaths.login)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static MainApiController getInstance() {
        if (instance == null)
            instance = new MainApiController();
        return instance;
    }
}
