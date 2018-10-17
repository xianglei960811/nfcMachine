package com.develop.xdk.df.nfcmachine.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.develop.xdk.df.nfcmachine.Application.App;
import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.utils.SharedPreferencesUtils;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/2/27.
 */

public class SetupActivity extends BaseActivity {


    @BindView(R.id.set_up_machine)
    EditText setUpMachine;
    @BindView(R.id.set_up_computer)
    EditText setUpComputer;
    @BindView(R.id.set_up_address)
    EditText setUpAddress;
    @BindView(R.id.set_up_clientid)
    EditText setUpClientid;
    @BindView(R.id.set_up_password)
    EditText setUpPassword;
    @BindView(R.id.set_up_save)
    Button setUpSave;
    @BindView(R.id.set_up_back)
    Button setUpBack;
    @BindView(R.id.setup_cont)
    LinearLayout setuoCont;


    private Dialog dialog;
    private ToastUntil toastUntil = new ToastUntil(this);

    @Override
    protected void initView() {
        setUpAddress.setText((String) SharedPreferencesUtils.getParam(this, C.BASE_URL_NAME, C.BASE_URL));
        setUpClientid.setText((String) SharedPreferencesUtils.getParam(this, C.CLIENTID_NAME, C.CLIENTID));
        setUpComputer.setText((String) SharedPreferencesUtils.getParam(this, C.COMPUTER_NAME, C.COMPUTER));
        setUpMachine.setText((String) SharedPreferencesUtils.getParam(this, C.MACHINE_NAME, C.MACHINE));
        setUpPassword.setText((String) SharedPreferencesUtils.getParam(this, C.PASS_NAME, C.PASS));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (setuoCont.getVisibility() == View.VISIBLE) {//如果linear layout为显示状态，则隐藏
            setuoCont.setVisibility(View.GONE);
        }
        ShowDialog();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_setup);
    }

    @OnClick(R.id.set_up_back)
    public void backClick() {
        toActivityWithFinish(MainActivity.class);
    }

    @OnClick(R.id.set_up_save)
    public void saveDate() {
        if (TextUtils.isEmpty(setUpComputer.getText().toString())) {
            toastUntil.ShowToastShort("电脑号不能为空");
            return;
        }
        if (TextUtils.isEmpty(setUpClientid.getText().toString())) {
            toastUntil.ShowToastShort("单位代码不能为空");
            return;
        }
        if (TextUtils.isEmpty(setUpAddress.getText().toString())) {
            toastUntil.ShowToastShort("请求地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(setUpMachine.getText().toString())) {
            toastUntil.ShowToastShort("机器号不能为空");
            return;
        }
        if (TextUtils.isEmpty(setUpPassword.getText().toString())) {
            toastUntil.ShowToastShort("密码不能为空");
            return;
        }
        SharedPreferencesUtils.setParam(this, C.COMPUTER_NAME, setUpComputer.getText().toString());
        SharedPreferencesUtils.setParam(this, C.CLIENTID_NAME, setUpClientid.getText().toString());
        SharedPreferencesUtils.setParam(this, C.BASE_URL_NAME, setUpAddress.getText().toString());
        SharedPreferencesUtils.setParam(this, C.MACHINE_NAME, setUpMachine.getText().toString());
        SharedPreferencesUtils.setParam(this, C.PASS_NAME, setUpPassword.getText().toString().trim());
        toastUntil.stopToast();
        App.getInstance().exitApp();
    }

    /**
     * 操作验证Dialog显示
     **/
    private void ShowDialog() {
        dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_seting, null);
        Button cancel = (Button) view.findViewById(R.id.setup_bt_cancel);
        Button ensure = (Button) view.findViewById(R.id.setup_bt_ensure);
        final EditText password = (EditText) view.findViewById(R.id.setup_et_pass);
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
//                toActivity(MainActivity.class);
                dialog.dismiss();
                App.getInstance().removeActivity(SetupActivity.this);
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassWord(password.getText().toString().trim());
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keycode, KeyEvent event) {
                if (keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                    toActivity(MainActivity.class);
//                    dialog.dismiss();
                    App.getInstance().removeActivity(SetupActivity.this);
                }
                return false;
            }
        });
        dialog.show();
    }

    /**
     * 验证密码
     **/
    private void checkPassWord(String pass) {
        if (TextUtils.isEmpty(pass)) {
            toastUntil.ShowToastShort("密码不能为空");
            return;
        }
        if (pass.equals(SharedPreferencesUtils.getParam(this, C.PASS_NAME, C.PASS).toString())) {
            showCont();
            dialog.dismiss();
        } else {
            toastUntil.ShowToastShort("密码错误");
            return;
        }
    }

    /**
     * 验证通过，显示设置界面
     **/
    private void showCont() {
        if (setuoCont.getVisibility() == View.GONE) {//如果linear layout为隐藏状态，则显示
            setuoCont.setVisibility(View.VISIBLE);
        }
    }
}
