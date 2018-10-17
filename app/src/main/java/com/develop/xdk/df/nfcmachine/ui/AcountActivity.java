package com.develop.xdk.df.nfcmachine.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.entity.Arith.Arith;
import com.develop.xdk.df.nfcmachine.entity.ProgressDialog.BaseProgressDialog;
import com.develop.xdk.df.nfcmachine.entity.ProgressDialog.MyProgressDialog;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/2/27.
 */

public class AcountActivity extends BaseActivity {
    @BindView(R.id.comsume_amount_money_all)
    TextView comsumeAmountMoneyAll;
    @BindView(R.id.comsume_amount_all_number)
    TextView comsumeAmountAllNumber;
    @BindView(R.id.comsume_amount_noup)
    TextView comsumeAmountNoup;
    @BindView(R.id.comsume_amount_show)
    Button comsumeAmountShow;
    @BindView(R.id.comsume_amount_back)
    Button comsumeAmountBack;

    private Double consumeTotal;//收款合计
    private int accontTotal;//总记录数
    private int noHandleTotal;//未上传记录数
    private BaseProgressDialog myPD;
    private List<AccountUser> accountUsers = new ArrayList<>();
ToastUntil toastUntil = new ToastUntil(this);
    @Override
    protected void initView() {
        myPD = MyProgressDialog.createe(this, C.TIME_WAITING_CONNECT, new BaseProgressDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(ProgressDialog dialog) {
               toastUntil.ShowToastShort( "链接超时");
            }
        });
        myPD.show();
        consumeTotal = 0.00;
        accontTotal = 0;
        noHandleTotal = 0;

        final int mode =1;
        accountUsers = sqlControl.getAccont(mode);
        if (accountUsers != null && !accountUsers.isEmpty()) {//记录不为空时执行
            accontTotal = accountUsers.size();
            for (int i = 0; i < accountUsers.size(); i++) {
                AccountUser accountUser = accountUsers.get(i);
                consumeTotal = Arith.round(Arith.add(consumeTotal, accountUser.getCousumeMoney()), 2);//精确运算和
//                Log.e("dddsada", "initView: " + consumeTotal + "::::" + accountUser.getCousumeMoney());
                if (accountUser.getIsHandl().equals("false")) {
                    noHandleTotal++;
                }
            }
            accountUsers.clear();
        }else {
            toastUntil.ShowToastShort("没有消费信息");
        }


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comsumeAmountMoneyAll.setText(Double.toString(consumeTotal));
        comsumeAmountAllNumber.setText(Integer.toString(accontTotal));
        comsumeAmountNoup.setText(Integer.toString(noHandleTotal));
        myPD.dismiss();//去除progressDialog
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_comsume_amount);
    }

    @OnClick(R.id.comsume_amount_show)
    public void setShowClick() {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putInt("total", accontTotal);
        intent.putExtras(b);
        intent.setClass(AcountActivity.this, AccontListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.comsume_amount_back)
    public void backClick() {
        toActivityWithFinish(MainActivity.class);
    }

    @Override
    protected void onDestroy() {
        toastUntil.stopToast();
        super.onDestroy();
    }
}
