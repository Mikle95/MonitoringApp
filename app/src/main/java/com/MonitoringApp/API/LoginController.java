package com.MonitoringApp.API;

import static com.MonitoringApp.API.MainApiController.JSON;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginController {
    private static volatile LoginController instance;
    private SharedPreferences prefs;
    // TODO Поменять ключи
    private static final String TOKEN_KEY = "user_token";
    private static final String REFRESH_TOKEN_KEY = "user_refresh";

    private volatile String token = "";
    public String getToken() {return token;}

    public void setPrefs(SharedPreferences prefs){this.prefs = prefs;}

    public boolean check_login(SharedPreferences prefs, Callback callback){
        this.prefs = prefs;
        return check_login(callback);
    }

    public boolean check_login(Callback callback){
        if (prefs == null)
                return false;

//        String token = prefs.getString(TOKEN_KEY, "");
        String rf_token = prefs.getString(REFRESH_TOKEN_KEY, "");
        if (rf_token.equals("")) return false;

        refreshToken(rf_token, callback);
        return true;
    }

    private void refreshToken(String rf_token, Callback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.refresh_token, rf_token);
        MainApiController.sendGetRequest(ApiPaths.refresh, params, makeCallback(callback));
        // TODO Проверять доступ к серверу
    }


    public void login(String login, String password, Callback callback){
        String json = String.format(ApiJsonFormats.login, login, password);
        RequestBody body = RequestBody.create(json, JSON);
        MainApiController.sendPostRequest(ApiPaths.login, body, makeCallback(callback));
    }

    public void logout(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(REFRESH_TOKEN_KEY);
        editor.apply();
    }

    private Callback makeCallback(Callback callback){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null)
                    callback.onFailure(call, e);
                else
                    e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Save info in database
                try {
                    if (response.isSuccessful()) {
                        String jsonString = response.body().string();
                        JSONObject jObject = new JSONObject(jsonString);
                        String token = (String) jObject.get("token");
                        String rf_token = (String) jObject.get("refresh_token");

                        SharedPreferences.Editor editor = prefs.edit();
                        LoginController.this.token = token;
//                        editor.putString(TOKEN_KEY, token);
                        editor.putString(REFRESH_TOKEN_KEY, rf_token);
                        editor.apply();
                    }
                } catch (Exception e) {e.printStackTrace();}

                if (callback != null)
                    callback.onResponse(call, response);
            }
        };
    }

    public static LoginController getInstance() {
        if (instance == null)
            instance = new LoginController();
        return instance;
    }

    private LoginController(){

    }
}
