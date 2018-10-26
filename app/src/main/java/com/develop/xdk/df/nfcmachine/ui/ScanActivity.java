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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.Dialog.Loading.LoadingBar;
import com.develop.xdk.df.nfcmachine.entity.IsMoney;
import com.develop.xdk.df.nfcmachine.entity.PersonDossier;
import com.develop.xdk.df.nfcmachine.entity.ScanBroadcast.ScanBroadcastReceiver;
import com.develop.xdk.df.nfcmachine.entity.ScanBroadcast.ScanReasult;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.entity.UserMoney;
import com.develop.xdk.df.nfcmachine.http.controller.RechargeController;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;
import com.develop.xdk.df.nfcmachine.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanActivity extends BaseActivity {
    @BindView(R.id.et_scan_money)
    EditText etScanMoney;
    @BindView(R.id.tv_sacan_hint)
    TextView tvScanHint;
    @BindView(R.id.bt_scan_back)
    Button btScanBack;
    @BindView(R.id.scan_ll_money)
    LinearLayout llScanMoney;
    @BindView(R.id.scan_subsidy_money)
    TextView tvScanSubsidyMoney;
    @BindView(R.id.scan_cash_money)
    TextView tvScanCashMoney;
    private final String TAG = this.getClass().getSimpleName();
    private ScanBroadcastReceiver scanBroadcastReceiver = null;

    private Dialog payDialog;
    private LoadingBar loadingBar;
    private volatile boolean is_TimeOut = true;

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
                etScanMoney.setText("");
                if (msg.obj==null){
                    tvScanHint.setText("提醒：链接服务器失败");
                    return;
                }
                tvScanHint.setText("提醒:" + msg.obj.toString());
                is_TimeOut = false;
            } else if (msg.what == 100) {
                tvScanHint.setText("提醒：请求超时");
                etScanMoney.setText("");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        payDialog();
        if (scanBroadcastReceiver == null) {
            scanBroadcastReceiver = new ScanBroadcastReceiver(new ScanReasult() {
                @Override
                public void onNext(final String reasult) {
                    //扫码成功的回调接口，获取扫码的信息
                    llScanMoney.setVisibility(View.GONE);
                    payDialog();
//                    timeOut();
                    Log.e(TAG, "onCreate: " + reasult);
                    String Money = etScanMoney.getText().toString().trim();
                    if (Money.isEmpty() || Money == null) {
                        if (loadingBar == null || payDialog == null) {
                            payDialog.dismiss();
                            return;
                        }
                        loadingBar.loadingComplete(false);
                        payDialog.setCanceledOnTouchOutside(true);
                        tvScanHint.setText("提醒：请输入消费金额后重新扫码支付");
                        etScanMoney.setText("");
                        payDialog.dismiss();
                        return;
                    }
                    Money = IsMoney.changeMoney(Money);
                    if (!IsMoney.checkMoney(Money)) {
                        tvScanHint.setText("提醒：请输入正确的消费金额后重新扫码支付");
                        etScanMoney.setText("");
                        payDialog.dismiss();
                        return;
                    }
                    if ((int) SharedPreferencesUtils.getParam(ScanActivity.this, C.IS_ONLINE_NAME, C.IS_ONLINE) != C.IS_ONLINE) {
                        tvScanHint.setText("提醒：请切换至在线模式进行消费");
                        return;
                    }
                    etScanMoney.setText(Money.toString());
                    final String finalMoney = Money;
                    RechargeController.getInstance().scanPay(new SubscriberOnNextListener<PersonDossier>() {
                        @Override
                        public void onNext(PersonDossier money) {
                            llScanMoney.setVisibility(View.GONE);
                            String head = reasult.substring(0,2);
                            Log.e(TAG, "onNext: "+head );
                            String type = null;
                            if (head.equals("28")){
                                type = "支付宝";
//                                sqlControl.upadeConsumeInfo("",type, Double.parseDouble(finalMoney),0,0);
                                sqlControl.inserScanConsume(type, Double.valueOf(finalMoney));
                            }else if (head.equals("10")||head.equals("11")||head.equals("12")||head.equals("13")||head.equals("14")||head.equals("15")){
                                type="微信";
                                sqlControl.inserScanConsume(type, Double.valueOf(finalMoney));
                            }else {
                                type = "一卡通";
                                llScanMoney.setVisibility(View.VISIBLE);
                                tvScanCashMoney.setText(money.getPdCashmoney() + "");
                                tvScanSubsidyMoney.setText(money.getPdSubsidymoney() + "");
                                sqlControl.upadeConsumeInfo(money.getPdCardid(),money.getPdName(), Double.parseDouble(finalMoney), money.getPdSubsidymoney(), money.getPdCashmoney());
                            }
//                            if (money != null) {
//                                llScanMoney.setVisibility(View.VISIBLE);
//                                tvScanCashMoney.setText(money.getCashmoney() + "");
//                                tvScanSubsidyMoney.setText(money.getSubsidymoney() + "");
////                                sqlControl.upadeConsumeInfo(cardID, name, finalMoney, money.getSubsidymoney(), money.getCashmoney());
//                            }else {
//                                sqlControl.upadeConsumeInfo("","微信", Double.parseDouble(finalMoney),money.getSubsidymoney(),money.getCashmoney());
//                            }
                            loadingBar.loadingComplete(true);
                            payDialog.setCanceledOnTouchOutside(true);
                            tvScanHint.setText("提醒："+type+"支付（" + finalMoney.toString() + "元）成功");
                            etScanMoney.setText("");
                            is_TimeOut = false;
                        }
                    }, ScanActivity.this, reasult, Money);
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.zkc.scancode");
            this.registerReceiver(scanBroadcastReceiver, intentFilter);
        }
//        timeOut();
    }

    //请求超时
    private void timeOut() {
        is_TimeOut = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                while (is_TimeOut) {
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
                    } else if (i == 15) {
                        payDialog.dismiss();
                        break;
                    }
                    i++;
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
        timeOut();
        payDialog.show();
    }

    //初始化
    private void initviews() {
        tvScanHint.setText("提醒:请先输入金额后，在按“SCAN”键进行扫码支付");
        etScanMoney.setText("");
    }

    @Override
    protected void initView() {
        llScanMoney.setVisibility(View.GONE);
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
