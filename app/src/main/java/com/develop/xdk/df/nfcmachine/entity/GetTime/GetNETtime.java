package com.develop.xdk.df.nfcmachine.entity.GetTime;


import android.util.Log;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/***
 * 获取时间(不受设备时间影响，断网状态下也可获取)
 */
public class GetNETtime {
    //创建单例
    private static class SingletonHolder {
        private static final GetNETtime INSTANCE = new GetNETtime();
    }

    //获取单例
    public static GetNETtime getInsance() {
        return SingletonHolder.INSTANCE;
    }

    private GetNETtime() {

    }

    //获取日期+时间
    public String getAllData() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String data = dff.format(new Date());
        return data;
    }

    //获取日期
    public String getdata() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String data = dff.format(new Date());
        String[] datas = data.split("\\ ");
        return datas[0];
    }

    //获取时间
    public String geTime() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String data = dff.format(new Date());
        String[] datas = data.split("\\ ");
        return datas[1];
    }
}
