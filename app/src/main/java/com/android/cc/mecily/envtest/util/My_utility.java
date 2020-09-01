package com.android.cc.mecily.envtest.util;

import android.util.Log;

import com.android.cc.mecily.envtest.gson.Forecast_7d;
import com.android.cc.mecily.envtest.gson.He_Now;
import com.android.cc.mecily.envtest.gson.He_Weather;
import com.android.cc.mecily.envtest.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Mecily on 2020/8/28.
 */

public class My_utility {
    public static He_Now handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);

            String weatherContent = jsonObject.getJSONObject("now").toString();
            Log.d("My_utility:",weatherContent);
            return new Gson().fromJson(weatherContent, He_Now.class);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Forecast_7d handleForecastResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String forecastContent  =jsonObject.getJSONObject("").toString();
            Log.d("My_utility:", forecastContent);
            return new Gson().fromJson(forecastContent, Forecast_7d.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
