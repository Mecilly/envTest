package com.android.cc.mecily.envtest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mecily on 2020/8/27.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;

    }
}
