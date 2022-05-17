package com.MonitoringApp.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.biometrics.BiometricPrompt;
import android.location.Location;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.MonitoringApp.API.ApiJsonFormats;
import com.MonitoringApp.API.BioGeoController;
import com.MonitoringApp.API.IResponseCallback;
import com.MonitoringApp.API.JobActivityController;
import com.MonitoringApp.API.LoginController;
import com.MonitoringApp.API.MainApiController;
import com.MonitoringApp.API.TasksApiController;
import com.MonitoringApp.API.data.GeoLocation;
import com.MonitoringApp.API.data.JobActivity;
import com.MonitoringApp.API.data.Project;
import com.MonitoringApp.API.data.Task;
import com.MonitoringApp.MainActivity;
import com.MonitoringApp.R;
import com.MonitoringApp.databinding.FragmentHomeBinding;
import com.MonitoringApp.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private final int CAMERA_CAPTURE = 2;
    private FragmentHomeBinding binding;
    private JobActivity jobActivity;
    private Handler timeHandler;
    private IResponseCallback callback;
    private MyLocationManager locationManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        locationManager = new MyLocationManager(getContext());
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.logout.setOnClickListener(view -> LoginController.getInstance().logout(getContext()));
        binding.confirmGeo.setOnClickListener(view -> askForGeo());

        binding.startStop.setOnClickListener(view -> {
                confirmIdentity(((response, isSuccessful) -> {
                    if (jobActivity == null || jobActivity.end_time != null)
                        JobActivityController.startActivity(((response1, isSuccessful1) -> refresh()));
                    else
                        JobActivityController.endActivity(((response1, isSuccessful1) -> refresh()));
                }));
        });

        timeHandler = new Handler();
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                if (binding != null) {
                    if (jobActivity != null && jobActivity.end_time == null
                            && jobActivity.getStartTime() > 0) {
                        long diff = (new Date().getTime() - jobActivity.getStartTime());
                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000);
                        binding.timerText.setText(
                                String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
                    }
                    timeHandler.postDelayed(this, 1000);
                }
            }
        });

        refresh();

        return root;
    }

    public void refresh(){
        if (LoginController.getInstance().getToken().equals("")){
            binding.textLogin.setText("Вход не выполнен");
            return;
        }

        binding.textLogin.setText(LoginController.getInstance().getUsername());
        JobActivityController.getActivity(this::getJobActivity);
    }

    private void getJobActivity(String response, boolean isSuccessful){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (!isSuccessful){
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    prepareJobActivity(true);
                    return;
                }
                try{
                    JobActivity[] mas = ApiJsonFormats.parseGson(response, JobActivity[].class);
                    if (mas.length > 0)
                        jobActivity = mas[mas.length - 1];
                    prepareJobActivity(jobActivity == null || jobActivity.end_time != null);
                }catch (Exception e) {
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void prepareJobActivity(boolean flag){
        binding.startStop.setText(flag ? getResources().getString(R.string.start_activity) :
                getResources().getString(R.string.stop_activity));

        if (flag)
            binding.timerText.setText("00:00:00");

        binding.confirmGeo.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
    }


    private void askForGeo(){
        confirmIdentity(new IResponseCallback() {
            @Override
            public void execute(String response, boolean isSuccessful) {
                //TODO confirmation response

                Location location = locationManager.getLocation(getActivity());
                if (location == null){
                    Toast.makeText(getContext(), "Не могу получить" +
                            " геолокацию", Toast.LENGTH_SHORT);
                    return;
                }
                GeoLocation geo = new GeoLocation();
                geo.latitude = location.getLatitude();
                geo.longitude = location.getLongitude();
                geo.altitude = location.getAltitude();
                geo.time_update = new Timestamp(location.getTime());

                BioGeoController.sendGeo(geo, (response1, isSuccessful1) -> {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(getContext(), response1, Toast.LENGTH_SHORT).show());
                });
            }
        });
    }


    //TODO еще раз проверить
    private void confirmIdentity(IResponseCallback callback){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(getResources().getString(R.string.choose_identify_way));

        int hasBiometry = BiometricManager.from(getContext()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        if (hasBiometry != BiometricManager.BIOMETRIC_SUCCESS)
            sendPhoto(callback);

        alertDialog.setPositiveButton(getResources().getString(R.string.photo),
                (dialogInterface, i) -> sendPhoto(callback));
        alertDialog.setNegativeButton(getResources().getString(R.string.biometry),
                (dialogInterface, i) -> localBiometry(callback));

        alertDialog.show();
    }

    private void sendPhoto(IResponseCallback callback){
        this.callback = callback;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_CAPTURE && data != null){
            Bitmap bm = data.getParcelableExtra("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            BioGeoController.sendPhoto(byteArray, callback);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void localBiometry(IResponseCallback callback){
        BiometricPrompt bp = new BiometricPrompt.Builder(getContext())
                .setTitle(getResources().getString(R.string.confirm_identity))
                .setConfirmationRequired(true)
                .setNegativeButton(getResources().getString(R.string.cancel),
                        ContextCompat.getMainExecutor(getContext()), (dialogInterface, i) -> {})
                .build();

        bp.authenticate(new CancellationSignal(), ContextCompat.getMainExecutor(getContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getContext(), errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.execute("", true);
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        timeHandler = null;
    }

}