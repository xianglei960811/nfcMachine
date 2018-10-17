package com.develop.xdk.df.nfcmachine.entity.GetTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetSimpleDate {
    SimpleDateFormat dateFormat ;
    private  String simpledata;
    private  String[] simpDatas;
    public GetSimpleDate(){
        Date now = new Date();
        dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpledata=dateFormat.format(now);
        simpDatas = simpledata.split(" ");
    }
    public String getAllData(){
        return  simpledata;
    }
    public  String  getData(){
        return simpDatas[0];
    }
    public  String getTime(){
        return  simpDatas[1];
    }

}
