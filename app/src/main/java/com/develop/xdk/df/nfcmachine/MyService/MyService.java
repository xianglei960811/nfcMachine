package com.develop.xdk.df.nfcmachine.MyService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.SQLite.SqlControl;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.CheckInterner.CheckInternet;
import com.develop.xdk.df.nfcmachine.entity.GetTime.GetNETtime;
import com.develop.xdk.df.nfcmachine.entity.LocalUser;
import com.develop.xdk.df.nfcmachine.entity.NfcUser;
import com.develop.xdk.df.nfcmachine.http.controller.RechargeController;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

public class MyService extends Service {
    private int max_progress;//进度的最大值
    private String progress;//进度值
    private Intent intent;
    private Intent intent1;
    SqlControl sqlControl;

    private IBinder binder = new MyBinder();//实力话binger接口对象

    @Override
    public void onCreate() {
        super.onCreate();
        sqlControl = new SqlControl(this);
        intent = new Intent("com.example.communication.RECEIVER");
        intent1 = new Intent();
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setAction("com.example.time.keeping");
        Log.d("service", "onCreate:*---------------------------> ");
    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        startDown();
        Log.d("MyService", "onStartCommand:-----------------------------> ");
        timekeeping();
        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * 开始下载
     */
    public void startDown() {
        C.USER_MESSAGE_NUMBER = 0;//初始化
        RechargeController.getInstance().receiptAll(new SubscriberOnNextListener<List<NfcUser>>() {
            @Override
            public void onNext(final List<NfcUser> pd) {
//                Log.d("service", "onNext: " + new Gson().toJson(pd));
                 final LocalUser localUser = new LocalUser();
                max_progress = pd.size();
                 final Runnable runn = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < max_progress; i++) {
                            progress = getPercent(i + 1, max_progress);
                            NfcUser user = pd.get(i);
                            localUser.setName(user.getPdName());
                            localUser.setCardID(user.getPdCardid());
                            localUser.setCashMOney(user.getPdCashmoney());
                            localUser.setSubsidyMoney(user.getPdSubsidymoney());
                            localUser.setIsLoss(user.getPdLoss());
                            localUser.setAccontid(user.getPdAccountid());
                            sqlControl.insertUserSql(localUser);

                            Log.e("server", "run: "+progress);
                            intent.putExtra("progress", progress);
                            intent.putExtra("position", i + 1);
                            intent.putExtra("max", max_progress);
                            sendBroadcast(intent);
                            if (progress.equals("100.00%")){
                                new ToastUntil(getApplicationContext()).ShowToastShort("用户档案更新完成");
                            }
                        }
                    }
                };
                new Thread(){
                    @Override
                    public void run() {
                        Looper.prepare();
                        new Handler().post(runn);
                        Looper.loop();
                    }
                }.start();
            }
        }, this);
    }

    /***
     * 计算百分比
     * @param i 当前值
     * @param max 总值
     * @return 返回百分比
     */
    private String getPercent(int i, int max) {
        String result = "";
        double x = i * 1.0;
        double tempresult = x / max;
        //百分比格式，后面不足两位的用0补齐 ##.00%
        DecimalFormat df = new DecimalFormat("0.00%");
        result = df.format(tempresult);
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        intent1 = null;
        intent = null;
        sqlControl = null;
        binder =null;
        Log.d("MyService", "onDestroy:-------------------------> ");
    }

    /**
     *  执行时间查询操作
     */
    private void timekeeping() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
//                        Log.e("aaa", "run: " );
                        if (CheckInternet.checkInternet(MyService.this)) {//联网状态

                            String NetTime = GetNETtime.getInsance().geTime();
                            if (NetTime != null) {
//                                Log.d("timeKeeping", "run: " + NetTime);
                                if (NetTime.equals(C.TIME_SEVEN)||NetTime.equals(C.TIME_FIFTEEN)||NetTime.equals(C.TIME_TEN)||NetTime.equals(C.TIME_TWENTY)) {
//                                   if (NetTime.equals("17:46:30")){
                                    sendBroadcast(intent1);
                                }
                            }
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                new Handler().post(runnable);
                Looper.loop();
            }
        }.start();

    }
}
