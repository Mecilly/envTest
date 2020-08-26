package com.android.cc.mecily.envtest.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Mecily on 2020/8/26.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
