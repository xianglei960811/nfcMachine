package com.develop.xdk.df.nfcmachine.entity.ScanBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Author: XL
 * Date: 2018-10-15 10:45
 * Describe:广播接收者，用于接收外部扫码器的参数
 */
public class ScanBroadcastReceiver extends BroadcastReceiver {
    private volatile String ReasultMsg = null;
    private ScanReasult reasult;

    public ScanBroadcastReceiver(ScanReasult reasult) {
        this.reasult = reasult;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String decodeReasult = intent.getExtras().getString("code");
        String keyStr = "";
        if (decodeReasult.contains("{") && decodeReasult.contains("}")) {
            int starStr = decodeReasult.lastIndexOf("{");
            int endStr = decodeReasult.lastIndexOf("}");
            //check keycode
            if (starStr > -1 && endStr > -1 && endStr - starStr < 5) {
                keyStr = decodeReasult.substring(starStr + 1, endStr);
                ReasultMsg = decodeReasult.substring(0, starStr);
            }
        }
        Log.e("ScanBroadcastReceiver", "onReceive: " + ReasultMsg);
        reasult.onNext(ReasultMsg);
    }

    //获取得到的信息
    public String getReasultMsg() {
        Log.e("ScanBroadcastReceiver", "getReasultMsg: " + ReasultMsg);
        return ReasultMsg;
    }
}
