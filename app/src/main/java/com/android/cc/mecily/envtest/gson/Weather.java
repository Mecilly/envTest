package com.android.cc.mecily.envtest.gson;

import com.google.gson.annotations.SerializedName;

import org.w3c.dom.ls.LSException;

import java.util.List;

/**
 * Created by Mecily on 2020/8/27.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
