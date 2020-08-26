package com.android.cc.mecily.envtest;

import android.util.Log;

import com.tianditu.android.maps.TErrorCode;
import com.tianditu.android.maps.TGeoAddress;
import com.tianditu.android.maps.TGeoDecode;

/**
 * Created by Mecily on 2020/8/14.
 */

class OnGeoResultListener implements TGeoDecode.OnGeoResultListener {

    @Override
    public void onGeoDecodeResult(TGeoAddress tGeoAddress, int errorCode) {
        String str = "";
        if (TErrorCode.OK == errorCode) {
            // 查询点相关信息
            str = "最近的 poi 名称:" + tGeoAddress.getPoiName() + "\n";
            str += "查询点 Poi 点的方位:" + tGeoAddress.getPoiDirection() + "\n";
            str += "查询点 Poi 点的距离:" + tGeoAddress.getPoiDistance() + "\n";
            str += "查询点行政区名称:" + tGeoAddress.getCity() + "\n";
            str += "查询点地理描述全称:" + tGeoAddress.getFullName() + "\n";
            str += "查询点的地址:" + tGeoAddress.getAddress() + "\n";
            str += "查询点的方位:" + tGeoAddress.getAddrDirection() + "\n";
            str += "查询点的距离:" + tGeoAddress.getAddrDistance() + "\n";
            str += "查询点道路名称:" + tGeoAddress.getRoadName() + "\n";
            str += "查询点与最近道路的距离:" + tGeoAddress.getRoadDistance();
            //tvAddress.setText(tGeoAddress.getFullName());
            //System.out.println(str);

        } else {

            //System.out.println("查询出错：" + errorCode);
            str = "查询出错："+errorCode;

        }
        Log.d("MainActivity", str);
    }
}