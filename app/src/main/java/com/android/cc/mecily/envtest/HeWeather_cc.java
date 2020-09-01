package com.android.cc.mecily.envtest;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cc.mecily.envtest.gson.He_Now;
import com.android.cc.mecily.envtest.gson.He_Weather;
import com.android.cc.mecily.envtest.gson.Weather;
import com.android.cc.mecily.envtest.util.HttpUtil;
import com.android.cc.mecily.envtest.util.My_utility;
import com.android.cc.mecily.envtest.util.Utility;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.base.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HeWeather_cc extends AppCompatActivity implements View.OnClickListener{

    private TextView he_weather_tv;
    private Button jump;
    private Button dianbo;
    private String locString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_he_weather);
        Intent intent = getIntent();
        locString = intent.getStringExtra("loc");
        Log.d("location HeWeather:", locString);
        he_weather_tv = (TextView)findViewById(R.id.he_weather);
        dianbo = (Button)findViewById(R.id.dianbo);
        dianbo.setOnClickListener(this);
        jump = (Button)findViewById(R.id.get_fromsdk);
        jump.setOnClickListener(this);
    }
    public void onClick(View v){
        //这里url1
        switch (v.getId()){

            case R.id.get_fromsdk:
                //Intent intent = new Intent(HeWeather_cc.this, Hefeng_sdk.class);
                //startActivity(intent);
                getHeData(locString);
                break;
            case R.id.dianbo:
                Intent intent = new Intent(HeWeather_cc.this, Dianbo.class);
                startActivity(intent);
            default:
                break;
        }

    }
    public void getHeData(String locString){
        HeConfig.init("HE2008261509531342", "56becb10320f42c782d02e786f6035dd");
        HeConfig.switchToDevService();
        HeWeather.getWeatherNow(HeWeather_cc.this, locString, Lang.ZH_HANS, Unit.METRIC, new HeWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {
                Log.i("getWeather onError: ", e.toString());
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                Log.i("getWeather onSuccess: ",  new Gson().toJson(weatherBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(weatherBean.getCode())) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    Log.d("实况观测时间：",now.getObsTime());
                    Log.d("体感温度：",now.getFeelsLike());
                    Log.d("温度：",now.getTemp());
                    Log.d("实况天气状况：",now.getText());
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i("failed code: ", code.toString());
                }
            }
        });
        HeWeather.getWeather3D(HeWeather_cc.this, locString, Lang.ZH_HANS, Unit.METRIC, new HeWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i("getWeather onError: ", throwable.toString());
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                if (Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode())) {

                    List<WeatherDailyBean.DailyBean> daily = weatherDailyBean.getDaily();
                    Log.d("预报日期：",daily.get(1).getFxDate());
                    Log.d("最高温度：",daily.get(1).getTempMax());
                    Log.d("最低温度：",daily.get(1).getTempMin());
                    Log.d("白天天气描述：",daily.get(1).getTextDay());
                    Log.d("晚间天气描述：",daily.get(1).getTextNight());
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i("failed code: ", code.toString());
                }
            }
        });
    }
}
