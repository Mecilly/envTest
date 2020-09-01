package com.android.cc.mecily.envtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.base.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class Hefeng_sdk extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hefeng_sdk);
        HeConfig.init("HE2008261509531342", "56becb10320f42c782d02e786f6035dd");
        HeConfig.switchToDevService();
        //getWeatherNow获取实时天气，其中第一个参数就是这个活动自身，第二个是定位，可以采用城市id，也可以使用经纬度
        HeWeather.getWeatherNow(Hefeng_sdk.this, "116.35,39.96", Lang.ZH_HANS, Unit.METRIC, new HeWeather.OnResultWeatherNowListener() {
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
        HeWeather.getWeather3D(Hefeng_sdk.this, "116.35,39.96", Lang.ZH_HANS, Unit.METRIC, new HeWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i("getWeather onError: ", throwable.toString());
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                if (Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode())) {

                    List<WeatherDailyBean.DailyBean> daily = weatherDailyBean.getDaily();
                    Log.d("预报日期：",daily.get(0).getFxDate());
                    Log.d("最高温度：",daily.get(0).getTempMax());
                    Log.d("最低温度：",daily.get(0).getTempMin());
                    Log.d("白天天气描述：",daily.get(0).getTextDay());
                    Log.d("晚间天气描述：",daily.get(0).getTextNight());
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
