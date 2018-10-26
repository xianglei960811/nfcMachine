package com.develop.xdk.df.nfcmachine.entity.Dialog.Loading;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

/**
 * Author: XL
 * Date: 2018-10-19 16:07
 * Describe:自定义等待dialog 并实现超时消失
 */
public class MyLoading extends Dialog {
    //    private static MyLoading INSTANCE = null;
    private Context context;

    private int timeout = 0;
    private timeOutListner timeOutListner;

    public MyLoading(@NonNull Context context, int timeout, timeOutListner timeOutListner) {
        super(context);
        this.context = context;
        this.timeout = timeout;
        this.timeOutListner = timeOutListner;
    }

    public MyLoading(@NonNull Context context, int themeResId, int timeout, timeOutListner timeOutListner) {
        super(context, themeResId);
        this.context = context;
        this.timeout = timeout;
        this.timeOutListner = timeOutListner;
    }
    //    public static synchronized MyLoading getInstance(Context context){
//        if (INSTANCE==null){
//            INSTANCE = new MyLoading(context);
//        }
//        return INSTANCE;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void show() {
        super.show();
        timeOut();
    }

    //请求超时
    private void timeOut() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = 1;
                while (true) {
                    i++;
//                for (int i = 1; i <= 60; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == timeout) {
                        timeOutListner.onTimeOut("请求超时");
                        dismiss();
                        break;
                    }
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                new Handler().post(runnable);
                Looper.loop();
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        //TODO 对物理按键-返回，进行拦截
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dismiss();
        }
        return false;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        context = null;
        timeout = 0;
    }

}
