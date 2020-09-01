package com.android.cc.mecily.envtest;

import android.util.Log;

//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by Mecily on 2020/8/31.
 */

public class DBUtils {
    private static final String TAG = "DBUtils";
    private static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
            String ip = "47.100.5.78";
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":3306/" + dbName,
                    "root", "root");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;
    }
    public static double getUserInfoByName(double lat, double lon) {

        Connection conn = getConnection("gaocheng");
        try {
            Statement st = conn.createStatement();
            String sql = "select * from gaocheng2 where longitude = "+lat+" and latitude = "+lon;
            Log.d("sql language:", sql);
            ResultSet res = st.executeQuery(sql);
            //Log.d("response:", ""+res.getDouble("gaocheng"));
            if (res == null) {
                return 100.0;
            } else {
                //String tmp = res.getString(0);
                double gao = 10.0;
                while (res.next()){
                    gao = res.getDouble(3);
                    Log.d("response:", ""+gao);
                }

                conn.close();
                st.close();
                res.close();
                return gao;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return 0;
        }
    }


}
