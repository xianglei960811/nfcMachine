package com.develop.xdk.df.nfcmachine.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.entity.PayLoading.LoadingBar;
import com.develop.xdk.df.nfcmachine.entity.ScanBroadcast.ScanBroadcastReceiver;
import com.develop.xdk.df.nfcmachine.entity.ScanBroadcast.ScanReasult;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.http.controller.RechargeController;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanActivity extends BaseActivity {
    @BindView(R.id.et_scan_money)
    EditText etScanMoney;
    @BindView(R.id.tv_sacan_hint)
    TextView tvScanHint;
    @BindView(R.id.bt_scan_back)
    Button btScanBack;
    private final String TAG = this.getClass().getSimpleName();
    private ScanBroadcastReceiver scanBroadcastReceiver = null;

    private Dialog payDialog;
    private LoadingBar loadingBar;

    @SuppressLint("HandlerLeak")
    public Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                if (loadingBar == null || payDialog == null) {
                    return;
                }
                loadingBar.loadingComplete(false);
                payDialog.setCanceledOnTouchOutside(true);
                tvScanHint.setText("提醒:" + msg.obj.toString());
            } else if (msg.what == 100) {
                tvScanHint.setText("提醒：请求超时");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payDialog();
        if (scanBroadcastReceiver == null) {
            scanBroadcastReceiver = new ScanBroadcastReceiver(new ScanReasult() {
                @Override
                public void onNext(String reasult) {
                    payDialog();
                    timeOut();
                    Log.e(TAG, "onCreate: " + reasult);
                    String Money = etScanMoney.getText().toString().trim();
                    if (Money.isEmpty() || Money == null) {
                        if (loadingBar == null || payDialog == null) {
                            return;
                        }
                        loadingBar.loadingComplete(false);
                        payDialog.setCanceledOnTouchOutside(true);
                        tvScanHint.setText("提醒：金额为空，请输入金额后重新扫码支付");
                        return;
                    }
                    RechargeController.getInstance().scanPay(new SubscriberOnNextListener<String>() {
                        @Override
                        public void onNext(String msg) {
                            loadingBar.loadingComplete(true);
                            payDialog.setCanceledOnTouchOutside(true);
                            tvScanHint.setText("提醒：支付成功");
                            etScanMoney.setText("");
                        }
                    }, ScanActivity.this, reasult, Money);
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.zkc.scancode");
            this.registerReceiver(scanBroadcastReceiver, intentFilter);
        }
        timeOut();
    }

    //请求超时
    private void timeOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 20; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 10) {
                        if (payDialog.isShowing()) {
                            loadingBar.loadingComplete(false);
                            payDialog.setCanceledOnTouchOutside(true);
                            mainHandler.sendEmptyMessage(100);
                        }
                    }else if (i==15){
                        payDialog.dismiss();
                        break;
                    }
                }
            }
        }).start();
    }

    //支付动画
    private void payDialog() {
        payDialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.payloading, null);
        loadingBar = (LoadingBar) view.findViewById(R.id.scan_dialog);
        payDialog.setContentView(view);
        payDialog.setCanceledOnTouchOutside(false);
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.3f));
        Window dialogwindow = payDialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.3f);
        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.3f);
        lp.gravity = Gravity.CENTER_VERTICAL;
        dialogwindow.setAttributes(lp);
        payDialog.show();
    }

    //初始化
    private void initviews() {
        tvScanHint.setText("提醒:请先输入金额后，在按“SCAN”键进行扫码支付");
        etScanMoney.setText("");
    }

    @Override
    protected void initView() {
        initviews();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_scan);
    }

    @OnClick(R.id.bt_scan_back)
    public void onBackClick() {
        toActivityWithFinish(MainActivity.class);
    }
//    @OnClick(R.id.bt_scan_scan)
//    public void onScanClick(){
//        Intent intentBroadcast = new Intent();
//        intentBroadcast.setAction("com.zkc.keycode");
//        intentBroadcast.putExtra("keyvalue",136);
//        sendBroadcast(intentBroadcast);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(scanBroadcastReceiver);
    }
}
