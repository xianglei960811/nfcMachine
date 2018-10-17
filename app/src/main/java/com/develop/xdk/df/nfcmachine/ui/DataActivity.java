package com.develop.xdk.df.nfcmachine.ui;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.MyService.MyService;
//import com.develop.xdk.df.nfcmachine.SQLite.BaseSQL;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.SQLite.BaseUserDB;
import com.develop.xdk.df.nfcmachine.entity.BadgeView.QBadgeView;
import com.develop.xdk.df.nfcmachine.entity.ProgressBar.MyProgressBar;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;
import com.readystatesoftware.viewbadger.BadgeView;

import butterknife.BindView;
import butterknife.OnClick;

public class DataActivity extends BaseActivity {
    @BindView(R.id.data_up_updb)
    Button dataUpUpdb;
    @BindView(R.id.data_up_back_loaddb)
    Button dataUpBataLoaddb;
    @BindView(R.id.data_up_text_show)
    TextView dataUpTextShow;
    @BindView(R.id.data_up_back)
    Button dataUpBack;
    @BindView(R.id.data_up_clear)
    Button dataUpClear;
    @BindView(R.id.data_up_pb)
    MyProgressBar dataUpPB;
    @BindView(R.id.data_up_position)
    TextView dataUpPosition;
    @BindView(R.id.data_up_know)
    Button dataUpKnow;

    private MsgRecrive msgRecrive;
    private Dialog dialog;
    private Intent intent;
    private MyService myService;
    private Boolean IsShowDialog;//避免progressDialog重复加载

    ToastUntil toastUntil = new ToastUntil(this);

    private Dialog progressDialog;
    public QBadgeView badgeView;

    TextView progress_position = null;
    MyProgressBar progress_bar = null;
    Button progress_bar_hide = null;

    @Override
    protected void initView() {
        dialog = new Dialog(this, R.style.NormalDialogStyle);
        progressDialog = new Dialog(this, R.style.NormalDialogStyle);
        dataUpKnow.setVisibility(View.GONE);
        dataUpPosition.setVisibility(View.GONE);
        IsShowDialog = true;
//        registerReceiver()
        intent = new Intent(DataActivity.this, MyService.class);//绑定服务
        bindService(intent, connection, Service.BIND_AUTO_CREATE);

        badgeView = new QBadgeView(this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();//注册广播
        intentFilter.addAction("com.example.communication.RECEIVER");
        msgRecrive = new MsgRecrive();
        registerReceiver(msgRecrive, intentFilter);

        intProgressBar();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO 设置右上角浮标
        if (untreated_number > 0) {
            badgeView.bindTarget(dataUpUpdb).setBadgeNumber(untreated_number).setBadgeTextSize(13, true)
                    .setBadgeBackgroundColor(Color.RED).setBadgeTextColor(Color.BLACK)
                    .stroke(Color.RED, 1, true);
        } else {
            badgeView.hide(false);
        }
    }

    @OnClick(R.id.data_up_know)
    public void SetKnow() {
        dataUpPosition.setVisibility(View.GONE);
        dataUpKnow.setVisibility(View.GONE);
    }

    @OnClick(R.id.data_up_back_loaddb)
    public void SetLoaddbClick() {//下载用户信息
        if (!IS_ONLINE) {
            toastUntil.ShowToastShort("网络未链接");
        } else {
            ShowDialog("loaddb");
            if (IsShowDialog) {
                dialog.show();
                dataUpKnow.setVisibility(View.GONE);
                dataUpPosition.setVisibility(View.GONE);
            } else {
                toastUntil.ShowToastShort("正在下载档案");
            }
        }
    }

    @OnClick(R.id.data_up_updb)
    public void SetUpdbClick() {
        dataUpPosition.setVisibility(View.GONE);
        dataUpKnow.setVisibility(View.GONE);
        final int mode = 0;//表示
        if (IS_ONLINE) {
            sqlControl.getAccont(mode);//上传未处理信息
        } else {
            toastUntil.ShowToastShort("网络未链接");
        }
    }

    @OnClick(R.id.data_up_clear)
    public void setClearClick() {
        dataUpPosition.setVisibility(View.GONE);
        dataUpKnow.setVisibility(View.GONE);
        ShowDialog("cleardata");
        dialog.show();
    }

    @OnClick(R.id.data_up_back)
    public void SetBackClick() {
        toActivityWithFinish(MainActivity.class);
    }

    /**
     * 弹窗提醒
     *
     * @param mode
     */
    private void ShowDialog(final String mode) {//显示信息确认对话框
        View view = View.inflate(this, R.layout.alterdalog, null);
        Button ensure = (Button) view.findViewById(R.id.alter_dialog_pay_ensure);
        Button cancel = (Button) view.findViewById(R.id.alter_dialog_pay_cancel);
        TextView tv_msg = (TextView) view.findViewById(R.id.alter_dialog_pay_message);
        TextView tv_warm = (TextView) view.findViewById(R.id.alter_dialog_pay_warm);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.alter_dialog_pay_ll);
        layout.setVisibility(View.VISIBLE);

        if (mode.equals("cleardata")) {
            tv_msg.setText("确定清除非当天的在线消费数据和一月前已处理的脱机数据？");
        } else if (mode.equals("loaddb")) {
            tv_msg.setText("更新将清除本地用户档案" + "\n" + "是否继续？");
        }
        tv_warm.setVisibility(View.GONE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);//点击对话框外部，不消失对话框
        //设置对话框大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("cleardata")) {
                    sqlControl.clearConsume();//清空数据库
                } else if (mode.equals("loaddb")) {//更新数据库
                    BaseUserDB.getInstance(DataActivity.this).clearUser(null, null);
                    myService.startDown();
                    IsShowDialog = true;
                }
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keycode, KeyEvent event) {
                if (keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                }
                return false;
            }
        });
    }

    /**
     * 初始化progressbarDialog进度条
     */
    private void intProgressBar() {
        View view = View.inflate(this, R.layout.progress_line_bar, null);
        progress_bar_hide = (Button) view.findViewById(R.id.progress_line_bar_bt_hide);
        progress_position = (TextView) view.findViewById(R.id.progress_line_bar_tv_position);
        progress_bar = (MyProgressBar) view.findViewById(R.id.progress_line_bar);
        progressDialog.setContentView(view);

        progressDialog.setCanceledOnTouchOutside(false);//点击对话框外部，不消失对话框
        //设置对话框大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = progressDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        progress_bar_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsShowDialog = false;
                dataUpPosition.setVisibility(View.VISIBLE);
                toastUntil.ShowToastShort("后台进行下载");
                progressDialog.dismiss();
            }
        });
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    progressDialog.dismiss();
                    toastUntil.ShowToastShort("后台进行下载");
                    dataUpPosition.setVisibility(View.VISIBLE);
                    IsShowDialog = false;
                }
                return false;
            }
        });
    }

    /***
     * 动态广播接收器
     */
    private class MsgRecrive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            dataUpPosition.setVisibility(View.VISIBLE);
            String progress = intent.getStringExtra("progress");
            int position = intent.getIntExtra("position", 0);
            int max = intent.getIntExtra("max", 0);
            Log.d("progress", "onReceive: " + progress);

            dataUpPosition.setText("当前下载：" + progress);

            if (progress_bar != null && progress_position != null) {
                progress_position.setText(position + "条/" + max + "条");
                progress_bar.setCurrentPosition(progress);
            }
            if (IsShowDialog) {
                progressDialog.show();
            }
            if (progress.equals("100.00%")) {
                dataUpPosition.setVisibility(View.VISIBLE);
                dataUpPosition.setText("用户档案更新完成");
                dataUpKnow.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                IsShowDialog = true;
//                toastUntil.ShowToastShort("数据更新完成");
            }
        }
    }

    /***
     *  定义ServiceConnection接口对象并实现接口
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (myService == null) {
                myService = ((MyService.MyBinder) service).getService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgRecrive);
        if (connection != null) {
            unbindService(connection);
        }
        toastUntil.stopToast();
    }
}
