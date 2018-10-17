package com.develop.xdk.df.nfcmachine;

import android.graphics.Color;
import android.view.KeyEvent;
import android.widget.Button;

import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.entity.BadgeView.QBadgeView;
import com.develop.xdk.df.nfcmachine.ui.AcountActivity;
import com.develop.xdk.df.nfcmachine.ui.DataActivity;
import com.develop.xdk.df.nfcmachine.ui.DismissCardActivity;
import com.develop.xdk.df.nfcmachine.ui.PayMoneyActivity;
import com.develop.xdk.df.nfcmachine.ui.PayMoneyInputActivity;
import com.develop.xdk.df.nfcmachine.ui.ScanActivity;
import com.develop.xdk.df.nfcmachine.ui.SetupActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_pay_money)
    Button mainPayMoney;
    @BindView(R.id.main_scan_pay)
    Button mainScanPay;
    @BindView(R.id.main_dismiss_card)
    Button mainDismissCard;
    @BindView(R.id.main_comsume_amount)
    Button mainComsumeAmount;
    @BindView(R.id.main_setup)
    Button mainSetup;
    @BindView(R.id.main_data)
    Button maindata;
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    private QBadgeView badgeView;
    @Override
    protected void initView() {
        badgeView = new QBadgeView(this);
    }
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("MainActivity", "onResume: "+untreated_number);
        if (untreated_number>0){
            badgeView.bindTarget(maindata).setBadgeNumber(untreated_number).setBadgeTextSize(13, true)
                    .setBadgeBackgroundColor(Color.RED).setBadgeTextColor(Color.BLACK)
                    .stroke(Color.RED, 1, true);
        }else {
//            Log.e("ssadsadas", "onResume: " );
            badgeView.hide(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
//                toastShort("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {

//                App.getInstance().exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @OnClick(R.id.main_comsume_amount)
    public void onMainComsumeAmountClick(){
        toActivity(AcountActivity.class);
    }
    @OnClick(R.id.main_pay_money)
    public void onMainPayMoneyClick(){
        toActivity(PayMoneyActivity.class);
    }
    @OnClick(R.id.main_scan_pay)
    public void onMainScanPayClick(){
        toActivity(ScanActivity.class);
    }
    @OnClick(R.id.main_dismiss_card)
    public void onMainDismissCardClick(){
        toActivity(DismissCardActivity.class);
    }
    @OnClick(R.id.main_setup)
    public void onMainSetupClick(){
        toActivity(SetupActivity.class);
    }
    @OnClick(R.id.main_data)
    public void onMainDataClick(){
        toActivity(DataActivity.class);
    }
}
