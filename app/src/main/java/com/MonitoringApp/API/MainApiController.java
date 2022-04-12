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

    public void testLogin(Callback callback){
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.3.56:8080/api/v1/login";//"http://127.0.0.1:8080/api/v1/login";

        String json = "{\"login\":\"tipikambr@yandex.ru\",\"password\":\"qwerty\"}";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
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
