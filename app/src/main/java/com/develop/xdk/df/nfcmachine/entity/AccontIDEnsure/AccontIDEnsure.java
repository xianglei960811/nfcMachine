package com.develop.xdk.df.nfcmachine.entity.AccontIDEnsure;

import android.util.Log;

/**
 *用于判断输入的账号是否为10位数
 * 不足，则在前面补0
 */
public class AccontIDEnsure {
    private AccontIDEnsure() {
    }

    public static String ensureAccontID(String id){
        Log.e("asada", "ensureAccontID: "+id.length() );
        if (id.length()==10){
            return id;
        }else {
            String zero = "";
            for (int i = 0;i<(10 - id.length());i++){
                zero = zero+"0";
            }
            return zero+id;
        }
    }
}
