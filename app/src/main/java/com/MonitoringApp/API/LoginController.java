package com.MonitoringApp.API;

import static com.MonitoringApp.API.MainApiController.JSON;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final String TOKEN_KEY = "1";
    private final String REFRESH_TOKEN_KEY = "1";


    public boolean check_login(SharedPreferences prefs){
        this.prefs = prefs;
        return check_login();
    }

    public boolean check_login(){
        if (prefs == null)
                return false;

        String token = prefs.getString(TOKEN_KEY, "");
        String rf_token = prefs.getString(REFRESH_TOKEN_KEY, "");
        if (token.equals("")) return false;

        return checkAccessibility(token, rf_token);
    }

    private boolean checkAccessibility(String token, String rf_token){
        // TODO Проверять доступ к серверу
        return true;
    }


    public void login(String login, String password, Callback callback){
        String json = String.format(ApiJsonFormats.login, login, password);
        RequestBody body = RequestBody.create(json, JSON);
        MainApiController.sendPostRequest(ApiPaths.login, body, makeCallback(callback));
    }

    private Callback makeCallback(Callback callback){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Save info in database
                System.out.println("Outer Callback");
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
