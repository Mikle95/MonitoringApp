package com.MonitoringApp.API;

import static com.MonitoringApp.API.MainApiController.JSON;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.ui.login.LoginActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
    private static final String Login_KEY = "login_token";
    private static final String REFRESH_TOKEN_KEY = "user_refresh";

    private static volatile LoginController instance;
    private SharedPreferences prefs;

    private volatile String token = "";
    private volatile String login = "";
    public volatile Timestamp endTime = null;

    public String getUsername() {return login;}
    public String getToken() {return token;}

    public void setPrefs(SharedPreferences prefs){this.prefs = prefs;}

    public boolean check_login(SharedPreferences prefs, IResponseCallback callback){
        this.prefs = prefs;
        return check_login(callback);
    }

    public boolean check_login(IResponseCallback callback){
        if (prefs == null)
                return false;

        String rf_token = prefs.getString(REFRESH_TOKEN_KEY, "");
        login = prefs.getString(Login_KEY, "");
        if (rf_token.equals("")) return false;

        refreshToken(rf_token, callback);
        return true;
    }

    private void refreshToken(String rf_token, IResponseCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put(ApiParams.refresh_token, rf_token);
        MainApiController.sendGetRequest(ApiPaths.refresh, params, makeCallback(callback));
    }


    public void login(String login, String password, IResponseCallback callback){
        this.login = login;
        String json = String.format(ApiJsonFormats.login, login, password);
        RequestBody body = RequestBody.create(json, JSON);
        MainApiController.sendPostRequest(ApiPaths.login, body, makeCallback(callback));
    }

    public void logout(Context context){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(REFRESH_TOKEN_KEY);
        editor.apply();
        token = "";

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private IResponseCallback makeCallback(IResponseCallback callback) {
        return (response, isSuccessful) -> {
            try {
                JSONObject jObject = new JSONObject(response);
                String token = (String) jObject.get("token");
                String endTime = (String) jObject.get("token_endtime");
                String rf_token = (String) jObject.get("refresh_token");

                SharedPreferences.Editor editor = prefs.edit();
                LoginController.this.token = token;
                editor.putString(Login_KEY, LoginController.this.login);
                editor.putString(REFRESH_TOKEN_KEY, rf_token);
                editor.apply();

                this.endTime = new Timestamp(Task.df.parse(endTime).getTime());
            } catch (Exception e) {
                e.printStackTrace();
                response = e.getMessage();
                isSuccessful = false;
            }

            if (callback != null)
                callback.execute(response, isSuccessful);
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
