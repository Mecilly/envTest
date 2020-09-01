package com.android.cc.mecily.envtest;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class Dianbo extends AppCompatActivity {

    private int mobiletype = 0, lac = 0, rsp = 0, cid = 0, flagcid = 0;
    private String mobileid;
    private String nowtime;
    private TelephonyManager tm;
    private String textString = "";
    private TextView cellText, cellText2;
    private String errString = "";
    private boolean flag = true;
    private Handler handler = new Handler();
    private MyDatabaseHelper dbHelper;
    private Button get_gaocheng;
    private TextView tv_gaocheng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dianbo);
        final double lat = 73.55;
        final double lon = 39.36;
        dbHelper = new MyDatabaseHelper(this, "BS.db", null, 1);
        tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mobileid = tm.getDeviceId();
        cellText = (TextView)findViewById(R.id.tv_dianbo_1);
        cellText2 = (TextView)findViewById(R.id.tv_dianbo_2);
//        cellText2.setMovementMethod(new ScrollingMovementMethod());
        get_gaocheng = (Button)findViewById(R.id.get_gaocheng);
        tv_gaocheng = (TextView)findViewById(R.id.tv_gaocheng);
        get_gaocheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        double gaocheng = DBUtils.getUserInfoByName(lat, lon);
                        Log.d("查看高程：","lat:"+lat+"lon:"+lon+"gaocheng:"+gaocheng);

                    }
                }).start();
            }
        });
        handler.post(task);

        Button subway2 = (Button)findViewById(R.id.subway_judge);
        subway2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subwayJudge() == true){
                    Toast.makeText(Dianbo.this, "在地铁上", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Dianbo.this, "不在地铁上", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button createData = (Button)findViewById(R.id.createData);
        createData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.getWritableDatabase();
            }
        });
        Button addData = (Button)findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("LAC",20941);
                values.put("CID",8012);
                db.insert("BS", null, values);
                values.clear();
                for(int i = 0; i<10; i++){
                    for(int j = 0; j<10; j++){
                        values.put("LAC", 20941+i);
                        values.put("CID", 8012+j+i);
                        db.insert("BS", null, values);
                        values.clear();
                    }
                }
            }
        });
    }
    private boolean subwayJudge(){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "select * from   BS  where   LAC=? and CID=?", new String[] {Integer.toString(cid), Integer.toString(lac)});
        while (cursor.moveToNext()) {
            int id1 =cursor.getInt(cursor.getColumnIndex("id"));
            Log.d("SecondActivity","the id is "+id1);

            db.close();

            return true;//
        }
        db.close();
        return false;
    }
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
            dowork();
            cellText.setText(String.format("时间:%s \n\n设备ID:%s | 网络:%d \n\n Lac:%d | Cid:%d | 场强:%d\n\n %s",
                    nowtime, mobileid, mobiletype, lac, cid, rsp,
                    errString));
            if (flagcid != cid){
                flagcid = cid;
                textString = "\n\n【网络:" + mobiletype + " | LAC:" + lac
                        + " | Cid:" + cid + " | " + nowtime + "】" + textString;
                cellText2.setText(textString);
            }

        }
    };
    private PhoneStateListener pStateListener = new PhoneStateListener(){
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);
            if (mobiletype == 1 || mobiletype == 2){
                rsp = signalStrength.getGsmSignalStrength();

            }else if(mobiletype == 13){
                String str = signalStrength.toString();
                String[] s = str.split("\\ ");
                rsp = Integer.parseInt(s[11]);

            }else{
                String str = signalStrength.toString();
                String[] s = str.split("\\ ");
                rsp = Integer.parseInt(s[3]);
            }
        }
    };


    @SuppressLint("NewApi")
    public void dowork(){
        mobiletype = tm.getNetworkType();
        Date now = new Date();
        java.text.DateFormat df = java.text.DateFormat.getTimeInstance();
        nowtime = df.format(now);
        try{
            errString = "use getAllCellInfo() interface";
            CellInfo stationinfo = tm.getAllCellInfo().get(0);
            if (stationinfo instanceof CellInfoGsm){
                CellInfoGsm cellInfoGsm = (CellInfoGsm)stationinfo;
                CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
                CellSignalStrengthGsm cellrsp = cellInfoGsm.getCellSignalStrength();
                rsp = cellrsp.getDbm();
                lac = cellIdentity.getLac();
                cid = cellIdentity.getCid();
            }else if (stationinfo instanceof CellInfoWcdma){
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) stationinfo;
                CellIdentityWcdma cellIdentity = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma cellrsp = cellInfoWcdma.getCellSignalStrength();
                rsp = cellrsp.getDbm();
                lac = cellIdentity.getLac();
                cid = cellIdentity.getCid();
                String sss = Integer.toHexString(cid);
                sss = sss.substring(sss.length() - 4);
                cid = Integer.parseInt(sss, 16);
            }else if (stationinfo instanceof CellInfoLte){
                CellInfoLte cellInfoLte = (CellInfoLte) stationinfo;
                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte cellrsp = cellInfoLte.getCellSignalStrength();
                rsp = cellrsp.getDbm();
                lac = cellIdentity.getTac();
                cid = cellIdentity.getCi();
                String sss = Integer.toHexString(cid);
                sss = sss.substring(sss.length() - 4);
                cid = Integer.parseInt(sss, 16);
            }else{

            }
        }catch (Exception e){
            try{
                errString = "err:cannot use getAllCellInfo() interface";
                if(flag){
                    tm.listen(pStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    flag=false;
                }
                GsmCellLocation lc = (GsmCellLocation) tm.getCellLocation();
                lac = lc.getLac();
                cid = lc.getCid();
                String sss = Integer.toHexString(cid);
                sss = sss.substring(sss.length() - 4);
                cid = Integer.parseInt(sss, 16);
            }catch (Exception e2){
                errString = "err:网络切换，无信号";
                lac = 0;
                cid = 0;
            }
        }
    }

}
