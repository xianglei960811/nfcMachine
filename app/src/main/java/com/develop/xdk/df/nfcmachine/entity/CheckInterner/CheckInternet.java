package com.develop.xdk.df.nfcmachine.entity.CheckInterner;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.develop.xdk.df.nfcmachine.utils.ToastUntil;

public class CheckInternet {
private CheckInternet(){}

    /***
     * 检查当前网络的状态
     * @param context
     * @return true 表示当前网络可用
     */
    public static Boolean checkInternet(Context context){
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager!=null){
        NetworkInfo info =connectivityManager.getActiveNetworkInfo();
        if (info!=null&&info.isConnected()){
            if (info.getState()== NetworkInfo.State.CONNECTED){
                //TODO 当前网络是连接的
//               new ToastUntil(context).ShowToastShort("网络已连接");
                return  true;
            }
        }
    }
    return false;
}
}
