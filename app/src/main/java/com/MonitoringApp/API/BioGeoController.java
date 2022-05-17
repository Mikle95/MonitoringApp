package com.MonitoringApp.API;

import static com.MonitoringApp.API.MainApiController.JSON;

import com.MonitoringApp.API.data.GeoLocation;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

public class BioGeoController {
    public static void sendPhoto(byte[] photo, IResponseCallback callback){
        try {
            String json = String.format(ApiJsonFormats.photo,
                    LoginController.getInstance().getUsername(),
                    Base64.getEncoder().encodeToString(photo));

            Map<String, String> map = new HashMap<>();
            map.put(ApiParams.token, LoginController.getInstance().getToken());
            MainApiController.sendRequest(ApiPaths.check_photo, map, RequestBody.create(json, JSON), callback);
        }catch (Exception e){e.printStackTrace(); callback.execute(e.getMessage(), false);}
    }

    public static void sendGeo(GeoLocation location, IResponseCallback callback){
        try {
            String json = ApiJsonFormats.writeGson(location, GeoLocation.class);
            Map<String, String> map = new HashMap<>();
            map.put(ApiParams.token, LoginController.getInstance().getToken());
            MainApiController.sendRequest(ApiPaths.send_geo, map, RequestBody.create(json, JSON), callback);
        }catch (Exception e){e.printStackTrace(); callback.execute(e.getMessage(), false);}
    }
}
