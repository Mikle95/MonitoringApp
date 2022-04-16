package com.MonitoringApp.API;

import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainApiController {

    private static final OkHttpClient client = new OkHttpClient();


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static void sendRequest(String url, Map<String, String> params, RequestBody body,
                                   Callback callback){
        HttpUrl.Builder httpBuilder;
        try {
            httpBuilder = HttpUrl.parse(url).newBuilder();
        } catch (Exception e) {e.printStackTrace(); return;}

        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(),param.getValue());
            }
        }

        Request request;
        if (body != null)
            request = new Request.Builder()
                .url(httpBuilder.build())
                .post(body)
                .build();
        else
            request = new Request.Builder()
                    .url(httpBuilder.build())
                    .build();

        client.newCall(request).enqueue(callback);
    }

    public static void sendGetRequest(String url, Map<String, String> params, Callback callback){
        sendRequest(url, params, null, callback);
    }

    public static void sendPostRequest(String url, RequestBody body, Callback callback){
        sendRequest(url, null, body, callback);
    }

}
