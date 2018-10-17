package com.develop.xdk.df.nfcmachine.entity.ProgressDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
/**
 * 对BaseProgressDialog 的再封装
 */
public class MyProgressDialog extends BaseProgressDialog {
    private  static MyProgressDialog INSTANCE = null;

    private MyProgressDialog(Context context) {
        super(context);
    }


    public static BaseProgressDialog createe(Context context, long time, OnTimeOutListener timeOutListener){
        final BaseProgressDialog baseProgressDialog = BaseProgressDialog.creatProgressDialog(context,time,timeOutListener);
        baseProgressDialog.setMessage("加载中，请稍后");
        baseProgressDialog.setCancelable(true);
        baseProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {//当dialog被强制取消时，清除任务
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                baseProgressDialog.cancel();
            }
        });
        baseProgressDialog.setCanceledOnTouchOutside(false);
        baseProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return baseProgressDialog;
    }
}
