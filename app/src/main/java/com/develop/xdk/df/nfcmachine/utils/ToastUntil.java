package com.develop.xdk.df.nfcmachine.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * toast显示工具类
 */
public class ToastUntil {
    private Context context;  //在此窗口提示信息

    private Toast toast = null;  //用于判断是否已有Toast执行

    public ToastUntil(Context context) {
        this.context = context;
    }

    /**
     * 短时间显示
     *
     * @param text
     */
    public void ShowToastShort( String text) {
        if(toast == null)
        {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);  //正常执行
        }
        else {
            toast.setText(text);  //用于覆盖前面未消失的提示信息
        }
        toast.show();

    }

    /**
     * 停止toast显示
     */
    public void stopToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
