package com.MonitoringApp.ui.login;

import androidx.annotation.NonNull;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.ActivityLoginBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private boolean loginOrGetToken(){
        LoginController lc = LoginController.getInstance();
        binding.loading.setVisibility(View.VISIBLE);
        return lc.check_login(getSharedPreferences("User_Credentials", MODE_PRIVATE),
                getCallback());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        if (loginOrGetToken()){
            binding.refresh.setVisibility(View.VISIBLE);
            binding.refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginOrGetToken();
                }
            });
        }

        setContentView(binding.getRoot());

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginButton.setEnabled(true);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
//                loginViewModel.login
                        LoginController.getInstance().login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), getCallback());
            }
        });
    }

    private Callback getCallback(){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.loading.setVisibility(View.INVISIBLE);
                        binding.password.setError(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful())
                            finish();
                        else {
                            binding.loading.setVisibility(View.INVISIBLE);
                            binding.password.setError("Wrong data");
                        }
                    }
                });

            }
        };
    }
}