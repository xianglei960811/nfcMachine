package com.develop.xdk.df.nfcmachine.Base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Application.App;
import com.develop.xdk.df.nfcmachine.MyService.MyService;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.SQLite.SqlControl;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.entity.BadgeView.QBadgeView;
import com.develop.xdk.df.nfcmachine.entity.CheckInterner.CheckInternet;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.ui.DataActivity;
import com.develop.xdk.df.nfcmachine.ui.SetupActivity;
import com.develop.xdk.df.nfcmachine.utils.SharedPreferencesUtils;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/2/8.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public SqlControl sqlControl;
    private Intent intent;
    private MsgRecrive msgRecrive;

    protected Boolean IS_ONLINE = false;
    public int untreated_number = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.e("Base", "onCreate: "+App.getInstance().getIS_FIRST());
        if (App.getInstance().getIS_FIRST()) {
            Log.d("BaseActivity", "注册广播------------------------->");
            //注册广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.example.time.keeping");
            msgRecrive = new MsgRecrive();
            registerReceiver(msgRecrive, intentFilter);
            //启动服务，定时
            intent = new Intent(this, MyService.class);
            startService(intent);

            ShowDialog();
            App.getInstance().setIS_FIRST(false);
        }
        checkOnline();
        sqlControl = new SqlControl(this);
        overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
        setContentView();
        ButterKnife.bind(this);
        initView();
        App.getInstance().addActivity(this);
        if (SharedPreferencesUtils.getParam(this, C.IS_ONLINE_NAME, C.IS_ONLINE).toString().isEmpty()) {
            SharedPreferencesUtils.setParam(this, C.IS_ONLINE_NAME, C.IS_ONLINE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        UntreatedNumber();

    }

    protected abstract void initView();

    protected abstract void setContentView();


    public void toActivityWithFinish(Class<?> toclass) {
        startActivity(new Intent(this, toclass));
        App.getInstance().removeActivity(this);
    }

    public void toActivity(Class<?> toclass) {
        startActivity(new Intent(this, toclass));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            App.getInstance().removeActivity(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /***
     * 动态广播接收器
     */
    private class MsgRecrive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("ddd", "onReceive: guangbo");
            ShowDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlControl = null;
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
        super.finish();
        if (msgRecrive != null && intent != null) {
            unregisterReceiver(msgRecrive);
            stopService(intent);
        }
    }

    /***
     * 联网状态
     */
    private void checkOnline() {
//        Log.d("1111", "checkOnline: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (CheckInternet.checkInternet(BaseActivity.this)) {
                            IS_ONLINE = true;
                        } else {
                            IS_ONLINE = false;
                        }
//                        Log.d("dsad", "run: " + IS_ONLINE);
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    new ToastUntil(BaseActivity.this).ShowToastShort(e.getMessage());
                    Log.e("CheckOnline", "run: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 弹窗提示更新用户信息
     */
    private void ShowDialog() {
        final AlertDialog.Builder alter = new AlertDialog.Builder(this);
        alter.setTitle("温馨提示：").setMessage("请及时前往“数据处理”界面更新档案");
        alter.setCancelable(false);

        alter.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toActivity(DataActivity.class);
            }
        }).setNegativeButton("取消", null);
        AlertDialog alertDialog = alter.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();


//        final Dialog dialog = new Dialog(this,R.style.NormalDialogStyle);
//        View view = View.inflate(this,R.layout.alterdalog,null);
//        TextView tv_title = (TextView) view.findViewById(R.id.alter_dialog_pay_title);
//        TextView tv_message = (TextView) view.findViewById(R.id.alter_dialog_pay_message);
//        Button bt_cancle = (Button) view.findViewById(R.id.alter_dialog_pay_cancel);
//        Button bt_ensure = (Button) view.findViewById(R.id.alter_dialog_pay_ensure);
//
//        dialog.setContentView(view);
//        dialog.setCanceledOnTouchOutside(false);//点击对话框外部，不消失对话框
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        //设置对话框大小
//        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.3f));
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//        dialogWindow.setAttributes(lp);
//
//        tv_title.setText("温馨提示");
//        tv_message.setText("请及时前往“数据处理”界面更新档案");
//        bt_cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        bt_ensure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toActivity(DataActivity.class);
//                dialog.dismiss();
//            }
//        });
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode ==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
//                    toActivity(DataActivity.class);
//                    dialog.dismiss();
//                }
//                return false;
//            }
//        });
//        dialog.show();
    }

    /***
     * 获取未处理记录信息数，为控件上显示标记坐准备
     */
    private void UntreatedNumber() {
        List<AccountUser> lists = sqlControl.getAccont(1);
        List<AccountUser> handles = new ArrayList<>();
        if (lists != null) {
            for (int i = 0; i < lists.size(); i++) {
                AccountUser accountUser = lists.get(i);
                if (accountUser.getIsHandl().equals("false")) {
                    handles.add(accountUser);
                }
            }
            untreated_number = handles.size();
            lists.clear();
        } else {
            untreated_number = 0;
        }
        handles.clear();
    }
}
