package com.android.cc.mecily.envtest.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mecily on 2020/8/27.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}


