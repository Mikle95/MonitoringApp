package com.MonitoringApp.API;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainApiController {

    private static final OkHttpClient client = new OkHttpClient();


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private static void checkAndSendRequest(String url, Map<String, String> params, RequestBody body,
                                    IResponseCallback callback){
        LoginController lc = LoginController.getInstance();
        if (lc.endTime != null && lc.endTime.getTime() < new Date().getTime()){
            lc.check_login((response, isSuccessful) -> {
                if (isSuccessful){
                    Map<String, String> params1 = new HashMap<>();
                    params1.put(ApiParams.token, lc.getToken());
                    sendRequest(url, params1, body, getCallback(callback));
                }
                else
                    callback.execute(response, false);
            });
        }
        else
            sendRequest(url, params, body, getCallback(callback));
    }


    private static void sendRequest(String url, Map<String, String> params, RequestBody body,
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

        if (callback == null){
            callback = new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    System.out.println(response.toString());
                }
            };
        }

        final Callback onResponse = callback;
        new Thread(() -> client.newCall(request).enqueue(onResponse)).start();
    }


    public static void sendGetRequest(String url, Map<String, String> params, IResponseCallback callback){
        checkAndSendRequest(url, params, null, callback);
    }

    public static void sendPostRequest(String url, RequestBody body, IResponseCallback callback){
        checkAndSendRequest(url, null, body, callback);
    }

    public static void sendRequest(String url, Map<String, String> params, RequestBody body,
                                   IResponseCallback callback){
        checkAndSendRequest(url, params, body, callback);
    }


    public static Callback getCallback(IResponseCallback callback){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (callback != null)
                    callback.execute(e.getMessage(), false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (callback == null)
                    return;

                if (!response.isSuccessful()) {
                    callback.execute(response.message(), false);
                    return;
                }

                try (ResponseBody body = response.body()) {
                    callback.execute(body.string(), true);
                } catch (Exception e) { callback.execute(e.getMessage(), false); }
            }
        };
    }


}
