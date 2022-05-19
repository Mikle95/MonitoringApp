package com.MonitoringApp.ui.login;

import androidx.annotation.NonNull;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.ApiParams;
import com.MonitoringApp.API.ApiPaths;
import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.API.MainApiController;
import com.MonitoringApp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private boolean loginOrGetToken(){
        binding.loading.setVisibility(View.VISIBLE);
        LoginController lc = LoginController.getInstance();
        return lc.check_login(getSharedPreferences("User_Credentials", MODE_PRIVATE),
                getCallback());
    }

    @Override
    public void finish() {
        sendFirebaseToken();
        super.finish();
    }

    private void sendFirebaseToken(){
        String TAG = "MyFirebaseMsgService";
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
//                        String msg = token;
//                        Log.d(TAG, msg);
//                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        System.out.println(msg);

                        Map<String, String> params = new HashMap<>();
                        params.put(ApiParams.token, LoginController.getInstance().getToken());
                        String json = String.format(ApiJsonFormats.firebase_token, token);
                        RequestBody body = RequestBody.create(json, MainApiController.JSON);
                        MainApiController.sendRequest(ApiPaths.send_firebase_token, params, body, new IResponseCallback() {
                            @Override
                            public void execute(String response, boolean isSuccessful) {
                                System.out.println(response);
                            }
                        });
                        //binding.textHome.setText(msg);
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LoginController.getInstance().getToken().equals("")){
            finishAffinity();
            System.out.println("Unauthorized cancel login activity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());


        if (loginOrGetToken()){
            binding.refresh.setVisibility(View.VISIBLE);
            binding.refresh.setOnClickListener(view -> loginOrGetToken());
        }
        else
            binding.loading.setVisibility(View.INVISIBLE);

        binding.username.setText(LoginController.getInstance().getUsername());

        setContentView(binding.getRoot());

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginButton.setEnabled(true);


        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            LoginController.getInstance().login(usernameEditText.getText().toString(), passwordEditText.getText().toString(), getCallback());
        });
    }

    private IResponseCallback getCallback() {
        return new IResponseCallback() {
            @Override
            public void execute(String response, boolean isSuccessful) {
                if (!isSuccessful) {
                    System.out.println(response);
                    LoginActivity.this.runOnUiThread(() -> {
                        binding.loading.setVisibility(View.INVISIBLE);
                        binding.password.setError(response);
                    });
                }
                else {
                    finish();
                }
            }
        };
    }
}