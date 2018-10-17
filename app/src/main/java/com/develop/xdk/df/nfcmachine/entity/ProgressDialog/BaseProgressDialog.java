package com.develop.xdk.df.nfcmachine.entity.ProgressDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;


import java.util.Timer;
import java.util.TimerTask;

public class BaseProgressDialog extends ProgressDialog {
    private static final String TAG = "ProgressDialog";
    private long mTimeOut = 0;//为0时，无限大
    private OnTimeOutListener mTimeOutListener = null;
    private Timer mTimer = null;
    /**
     * 超时后，调用该handler
     */
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mTimeOutListener != null) {
                mTimeOutListener.onTimeOut(BaseProgressDialog.this);
                dismiss();
            }
        }
    };


    public BaseProgressDialog(Context context) {
        super(context);
    }


    /**
     * 设置timeOut长度 和 处理器
     *
     * @param t               timeout时间长度
     * @param timeOutListener 超时后的处理器
     */
    public void setmTimeOut(long t, OnTimeOutListener timeOutListener) {
        mTimeOut = t;
        if (timeOutListener != null) {
            this.mTimeOutListener = timeOutListener;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mTimeOut != 0) {
            mTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = mHandler.obtainMessage();
                    mHandler.sendMessage(msg);
                }
            };
            mTimer.schedule(timerTask, mTimeOut);//设定timaout时间后，调用TimeTask任务
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    if (mTimer != null){
        mTimer.cancel();
        mTimer = null;
    }
    }

    /**
     * 通过静态creat的方式创建一个实力对象
     *
     * @param context
     * @param time     timeout的时间长度
     * @param listener timeotlistner 超时后的处理器
     *  return   MyProgrssDialog 对象
     */
    public static BaseProgressDialog creatProgressDialog(Context context, long time, OnTimeOutListener listener) {
        BaseProgressDialog baseProgressDialog = new BaseProgressDialog(context);
        if (time != 0) {
            baseProgressDialog.setmTimeOut(time, listener);
        }
        return baseProgressDialog;
    }

    /**
     *处理超时的接口
     *
     */
    public interface OnTimeOutListener {
        /**
         * 当progress超时后调用该方法
         */
        abstract public void onTimeOut(ProgressDialog dialog);
    }
}
