package com.android.cc.mecily.envtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.tianditu.android.maps.GeoPoint;
import com.tianditu.android.maps.MapController;
import com.tianditu.android.maps.MapView;
import com.tianditu.android.maps.MyLocationOverlay;
import com.tianditu.android.maps.TErrorCode;
import com.tianditu.android.maps.TGeoAddress;
import com.tianditu.android.maps.TGeoDecode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mecily on 2020/8/25.
 */

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private double lat = 116.0;
    private double lon = 39.0;

    private MapView mapView;
    private Button btn;
    private Button btn2;
    private Button btn3;
    private String path;


    //在API23+以上，不仅要在AndroidManifest.xml里面添加权限 还要在JAVA代码中请求权限：
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在加载布局之前获取所需权限
        addpermission();
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat",116.0);
        lon = intent.getDoubleExtra("lon", 39.0);
        Toast.makeText(this, "lat:"+lat+"lon:"+lon, Toast.LENGTH_SHORT).show();
        btn = (Button)findViewById(R.id.shutScreen);
        btn2 = (Button)findViewById(R.id.loc);
        btn3 = (Button)findViewById(R.id.turnTo);

        init();

        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);


    }
    public void addpermission(){
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    public void init(){

        new Thread(){
            @Override
            public void run() {

                mapView=(MapView)findViewById(R.id.mapview);
                //启用内置的地图缩放按钮
                mapView.setBuiltInZoomControls(true);
                //得到mapview的控制权，可以用它控制和驱动平移和缩放


                MapController mapController=mapView.getController();
                //用给定的经纬度构造一个GeoPoint，单位是微度（度*1E6）
                GeoPoint point=new GeoPoint((int)(lon*1E6),(int)(lat*1E6));
                //设置地图中心点
                mapController.setCenter(point);
                //设置地图等级
                mapController.setZoom(16);
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shutScreen:
                sreenShot();
                //testViewSnapshoot(mapView);
                double density = calculate();
                Toast.makeText(SecondActivity.this, "建筑密度为："+density, Toast.LENGTH_SHORT).show();
                break;
            case R.id.loc:
                Toast.makeText(SecondActivity.this, "turn to", Toast.LENGTH_SHORT).show();
                //创建MyLocationOverlay
                MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
//启用指南针位置更新
                myLocationOverlay.enableCompass();
//启用我的位置
                myLocationOverlay.enableMyLocation();
                mapView.addOverlay(myLocationOverlay);
//获得当前位置
                GeoPoint mPoint = myLocationOverlay.getMyLocation();
//动画移动到当前位置
                MapController mapController=mapView.getController();
                mapController.animateTo(mPoint);
                Log.d("MainActivity", "lat:"+mPoint.getLatitudeE6());
                Log.d("MainActivity", "lon:"+mPoint.getLongitudeE6());
                TGeoDecode tGeoDecode = new TGeoDecode(new OnGeoResultListener());
                tGeoDecode.search(mPoint);
                break;
            case R.id.turnTo:
                Toast.makeText(this, "click the button turnto", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
    }


    public double calculate(){
        double density = 0;
        double buildingV = -1581360;
        Bitmap oldMap = BitmapFactory.decodeFile(path);
        int width = oldMap.getWidth();
        int height = oldMap.getHeight();

        Log.d("MainActivity","width:"+width);
        Log.d("MainActivity", "height:"+height);

        int pixel;
        int tmp = -16777216;
        Map<Integer, Integer> res = new HashMap();
        for (int i = (int)(width*0.2); i < (int)(width*0.8); i++){
            for (int j = 0; j < height; j++){
                pixel = oldMap.getPixel(i, j);

                if(res.containsKey(pixel)){
                    res. put(pixel, res.get(pixel) + 1);
                }else{
                    //Log.d("MainActivity", "the pixel is :"+pixel);
                    res.put(pixel,1);
                }
            }
        }
        Log.d("MainActivity", "the size of the map:"+res.size());
        for(Map.Entry<Integer, Integer> entry:res.entrySet()){
            int mapKey = entry.getKey();
            int mapValue = entry.getValue();
            double dens = (double)mapValue/(height*width*0.6);
            density = dens;
            if (mapKey == buildingV) {
                Log.d("MainActivity:", "value:" + mapValue + "建筑密度为：" + dens);
            }
        }
        return density;
    }
    public void sreenShot() {
        path = "/mnt/sdcard/" + new Date().getTime() + ".png";
        String cmd = "screencap -p " + path;
        try {
            Process process = Runtime.getRuntime().exec("su");//不同的设备权限不一样
            PrintWriter pw = new PrintWriter(process.getOutputStream());
            pw.println(cmd);

            pw.flush();
            pw.println("exit");
            pw.flush();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            pw.close();
            process.destroy();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void testViewSnapshoot(MapView v){
        //Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        Bitmap drawingCache = getViewBitmap(v);
        if (drawingCache != null) {
            //ivShow.setImageBitmap(drawingCache);
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
        }
        saveFile(SecondActivity.this, drawingCache);
    }
    private void saveFile(Context context, Bitmap bmp){

        File appDir = new File(Environment.getExternalStorageDirectory(), "oasystem");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //图片文件名称
        String fileName = "oa_"+System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private Bitmap getViewBitmap(MapView v){
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while (cacheBitmap == null) {
            cacheBitmap = v.getDrawingCache();
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
}
