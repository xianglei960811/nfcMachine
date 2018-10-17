package com.develop.xdk.df.nfcmachine.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.xdk.df.nfcmachine.Application.App;
import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccontIDEnsure.AccontIDEnsure;
import com.develop.xdk.df.nfcmachine.entity.Arith.Arith;
import com.develop.xdk.df.nfcmachine.entity.IsMoney;
import com.develop.xdk.df.nfcmachine.entity.LocalUser;
import com.develop.xdk.df.nfcmachine.entity.PersonDossier;
import com.develop.xdk.df.nfcmachine.entity.ReversalString.ReversalString;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.entity.UserMoney;
import com.develop.xdk.df.nfcmachine.http.controller.RechargeController;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;
import com.develop.xdk.df.nfcmachine.utils.SharedPreferencesUtils;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/2/27.
 */

/**
 * 定款收费
 */

public class PayMoneyActivity extends BaseActivity {
    @BindView(R.id.PD_Subsidy_Money)
    TextView pdSubsidyMoney;
    @BindView(R.id.PD_Cash_Money)
    TextView pdCashMoney;
    @BindView(R.id.pay_money_name)
    TextView payMoneyName;
    @BindView(R.id.pay_money_number)
    EditText payMoneyNumber;
    @BindView(R.id.pay_money_change)
    Button payMoneyChange;
    @BindView(R.id.pay_money_back)
    Button payMoneyBack;
    @BindView(R.id.pay_money_mode)
    SwitchCompat payMoneyMode;
    @BindView(R.id.pay_money_hint)
    LinearLayout payMoneyHint;
    @BindView(R.id.pay_money_tv_hint)
    TextView payMoneyTvHint;
    private SubscriberOnNextListener userInfoListener;
    private static final String TAG = "null";
    private NfcAdapter mAdapter_;
    private PendingIntent mPendingIntent_;
    private IntentFilter[] mFilters_;
    private String[][] mTechLists_;
    private Intent intents_ = null;
    private boolean isNew_ = true;
    private String NfcCardID = "";//讀取到的NFC卡片的id
    private int IS_ONLINE = 1;//1:在线模式；0：脱机模式

    @SuppressLint("HandlerLeak")
    public Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                payMoneyTvHint.setText("提醒："+msg.obj.toString());
            }
        }
    };
    @Override
    protected void initView() {
        userInfoListener = new SubscriberOnNextListener<PersonDossier>() {
            @Override
            public void onNext(PersonDossier user) {
                if (user.getPdLoss() != C.U_IS_LOSS) {
                    payMoneyTvHint.setText("提示：用户信息获取成功");
                    Log.d(TAG, "onNext: " + new Gson().toJson(user));
                    pdCashMoney.setText(Double.toString(user.getPdCashmoney()));
                    pdSubsidyMoney.setText(Double.toString(user.getPdSubsidymoney()));
                    payMoneyName.setText(user.getPdName().toString().trim());
                } else {
                    payMoneyTvHint.setText("提示：该卡片已挂失");
                }
            }
        };
    }

    /***
     * 清空视图
     */
    private void clearview() {
        payMoneyName.setText("");
        pdSubsidyMoney.setText("");
        pdCashMoney.setText("");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payMoneyNumber.setText((String) SharedPreferencesUtils.getParam(this, C.PRICE_NAME, C.PRICE));
        payMoneyNumber.setGravity(Gravity.CENTER);
        payMoneyNumber.setSelection(payMoneyNumber.getText().toString().length());//光标在字段末
        payMoneyHint.setVisibility(View.VISIBLE);
        payMoneyTvHint.setText("提示：请将放置在设备后上端读卡区");
//        payMoneyNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
//        sqlControl = new SqlControl(this, "OneCard.db", 1);
        initNFC();
        IS_ONLINE = Integer.parseInt(SharedPreferencesUtils.getParam(this, C.IS_ONLINE_NAME, C.IS_ONLINE).toString());
        if (IS_ONLINE == C.IS_ONLINE) {
            payMoneyMode.setChecked(true);
        } else {
            payMoneyMode.setChecked(false);
            checkHttpp();
        }

        setSwitched();
    }

    /***
     * switch开关
     */
    private void setSwitched() {
        payMoneyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonview, boolean ischecked) {
                if (ischecked) {
                    checkHttpp();
                } else {
                    IS_ONLINE = 0;
                    SharedPreferencesUtils.setParam(PayMoneyActivity.this, C.IS_ONLINE_NAME, IS_ONLINE);
                    checkHttpp();
                }
            }
        });
    }

    /***
     * 初始化nfc
     */
    private void initNFC() {
// 获取默认的NFC控制器
        mAdapter_ = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter_ == null) {
            Toast.makeText(PayMoneyActivity.this, "Device can not support NFC！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mAdapter_.isEnabled()) {
            Toast.makeText(PayMoneyActivity.this, "Please open NFC in system setting！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mPendingIntent_ = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addCategory("*/*");
        mFilters_ = new IntentFilter[]{ndef};// 过滤器
        mTechLists_ = new String[][]{
                new String[]{MifareClassic.class.getName()},
                new String[]{NfcA.class.getName()}};// 允许扫描的标签类型
    }

    /***
     * 得到是否检测到ACTION_TECH_DISCOVERED触发
     */
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter_.enableForegroundDispatch(PayMoneyActivity.this, mPendingIntent_, mFilters_,
                mTechLists_);
        if (isNew_) {
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent()
                    .getAction())) {
                // 处理该intent
                processIntent(getIntent());
                intents_ = getIntent();
                isNew_ = false;
            }
        }

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_pay_money);
    }

    @OnClick(R.id.pay_money_back)
    public void backClick() {
        toActivityWithFinish(MainActivity.class);
    }

    /***
     * 收款
     */
    @OnClick(R.id.pay_money_change)
    public void payMoeny() {
        String payInfo = null;
        String money = IsMoney.changeMoney(payMoneyNumber.getText().toString());
//        Log.e(TAG, "payMoeny: "+money);
        if (payMoneyName.getText().toString().trim().isEmpty()) {
            payMoneyTvHint.setText("提示：未获取付款人信息");
        } else if (payMoneyNumber.getText().toString().isEmpty()) {
            payMoneyTvHint.setText("提示：请输入付款金额");
        } else if (!IsMoney.checkMoney(money)) {
            payMoneyTvHint.setText("提示：消费金额必须大于0");
        } else{
            if (Arith.sub(Double.valueOf(pdSubsidyMoney.getText().toString())+Double.valueOf(pdCashMoney.getText().toString()),Double.valueOf(money)) >=0) {//利用Arith作精确的double运算
                payInfo = "姓名：" + payMoneyName.getText().toString().trim() + "\n" +
                        "价格：" + payMoneyNumber.getText().toString().trim() + "元";
                ShowDialog(payInfo);
            }else {
                Log.e(TAG, "payMoeny: " + Arith.sub(Double.valueOf(pdCashMoney.getText().toString()), Double.valueOf(money)));
                payMoneyTvHint.setText("提示：消费金额已超出可使用金额");
            }

        }
    }

    /***
     * 收款确认弹窗
     * @param msg
     */
    private void ShowDialog(String msg) {//显示信息确认对话框
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.alterdalog, null);
        Button ensure = (Button) view.findViewById(R.id.alter_dialog_pay_ensure);
        Button cancel = (Button) view.findViewById(R.id.alter_dialog_pay_cancel);
        final TextView tv_msg = (TextView) view.findViewById(R.id.alter_dialog_pay_message);
        TextView tv_watrm = (TextView) view.findViewById(R.id.alter_dialog_pay_warm);

        LinearLayout layout_pay = (LinearLayout) view.findViewById(R.id.alter_dialog_pay_ll);
        layout_pay.setVisibility(View.VISIBLE);

        tv_msg.setText(msg);
        tv_watrm.setVisibility(View.VISIBLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);//点击对话框外部，不消失对话框
        //设置对话框大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.95f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView();
                dialog.dismiss();
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView();
                /**
                 * 将设置的价格存入sp中
                 * */
                final String moneyNumber = payMoneyNumber.getText().toString().trim();
                if (!moneyNumber.equals(SharedPreferencesUtils.getParam(PayMoneyActivity.this, C.PRICE_NAME, C.PRICE).toString().trim())) {
                    SharedPreferencesUtils.setParam(PayMoneyActivity.this, C.PRICE_NAME, moneyNumber);
                }
                /**
                 * 当IS_ONLINE = 1 时，为在线模式时，将消费信息发送给服务器,并存入本地
                 * 当IS_ONLINE = 0 时，为脱机模式，将消费信息存入本地sql
                 * */
                if (IS_ONLINE == C.IS_ONLINE) {

                    RechargeController.getInstance().comsumeCard(new SubscriberOnNextListener<UserMoney>() {
                                                                     @Override
                                                                     public void onNext(UserMoney userMoney) {
                                                                         payMoneyTvHint.setText("提示：收款" + payMoneyNumber.getText().toString() + "元，成功");
                                                                         pdSubsidyMoney.setText(Double.toString(userMoney.getSubsidymoney()));
                                                                         pdCashMoney.setText(Double.toString(userMoney.getCashmoney()));
                                                                         sqlControl.upadeConsumeInfo(NfcCardID, payMoneyName.getText().toString(), Double.parseDouble(moneyNumber),
                                                                                 userMoney.getSubsidymoney(), userMoney.getCashmoney());
                                                                     }
                                                                 }, PayMoneyActivity.this, ReversalString.Reversal(NfcCardID),
                            (String) SharedPreferencesUtils.getParam(PayMoneyActivity.this, C.PRICE_NAME, C.PRICE));
                } else if (IS_ONLINE != C.IS_ONLINE) {
                    HashMap<String, Double> moneys = sqlControl.upadeConsumeInfo(NfcCardID, payMoneyName.getText().toString(),
                            Double.valueOf(moneyNumber), Double.valueOf(pdSubsidyMoney.getText().toString()), Double.valueOf(pdCashMoney.getText().toString()));

                    if (moneys != null) {
                        if (moneys.size() == 0) {
                            payMoneyTvHint.setText("提示：用户余额不足");
                        } else {
                            pdCashMoney.setText(moneys.get(C.U_CASH_MONEY_NAME).toString());
                            pdSubsidyMoney.setText(moneys.get(C.U_SUBSIDY_MONEY_NAME).toString());
                            payMoneyTvHint.setText("提示：收款" + payMoneyNumber.getText().toString() + "元，成功");
                        }
                    } else {
                        payMoneyTvHint.setText("提示：用户余额不足");
                    }
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
        dialog.show();
    }

    /***
     * 字符序列转换为16进制字符串
     * @param src
     * @return
     */
    private String bytesToHexString2(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        // 得到是否检测到ACTION_TECH_DISCOVERED触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            // 处理该intent
            processIntent(intent);
            intents_ = intent;
        }

    }

    /**
     * 读取NFC信息数据
     *
     * @param intent
     */
    private void processIntent(Intent intent) {
        clearview();//初始化视图
        String cardId;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareClassic mfc = MifareClassic.get(tag);
        try {
            // Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();// 获取TAG的类型
            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            cardId = bytesToHexString2(myNFCID);
//            cardId = "d10cefc8";
            NfcCardID = cardId.replaceAll(" ", "").toUpperCase();//替换空格转成大写
            if (NfcCardID.isEmpty()) {
                payMoneyTvHint.setText("提示：卡片读取失败，请重试");
            } else {
                payMoneyTvHint.setText("提示：卡片读取成功，正在获取用户信息");
                NfcCardID = NfcCardID.substring(6,8)+NfcCardID.substring(4,6)+NfcCardID.substring(2,4)+NfcCardID.substring(0,2);
                showUserInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mfc != null) {
                    mfc.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求用户信息
     */
    private void showUserInfo() {
        if (IS_ONLINE == 1) {//在线模式下，向云请求用户数据
                RechargeController.getInstance().getUserinfo(userInfoListener, PayMoneyActivity.this, NfcCardID);
            } else if (IS_ONLINE == 0) {//脱机模式下向本地数据库请求数据
            LocalUser user = sqlControl.selectuserInfo(ReversalString.Reversal(NfcCardID));
            Log.d(TAG, "processIntent: " + new Gson().toJson(user));
            if (user == null || user.toString().isEmpty()) {
                clearview();
                payMoneyTvHint.setText("提示：该用户不存在");
            } else {
                Log.e(TAG, "processIntent: " + user.getIsLoss());
                if (user.getIsLoss() == C.U_IS_LOSS) {
                    payMoneyTvHint.setText("提示：该卡已挂失");
                } else {
                    payMoneyTvHint.setText("获取用户信息成功");
                    payMoneyName.setText(user.getName().toString());
                    pdSubsidyMoney.setText(Double.toString(user.getSubsidyMoney()));
                    pdCashMoney.setText(Double.toString(user.getCashMOney()));
                }
            }
        }
    }

    /***
     * 检查服务器是否运行正常，正常则跳转回在线模式
     */
    private void checkHttpp() {
        if (IS_ONLINE != C.IS_ONLINE) {
            IS_ONLINE = 0;
            SharedPreferencesUtils.setParam(PayMoneyActivity.this, C.IS_ONLINE_NAME, IS_ONLINE);
            payMoneyMode.setChecked(false);
        }
        RechargeController.getInstance().checkOnline(new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String personDossier) {
                if (personDossier == null || new Gson().toJson(personDossier).equals("null")) {
                    return;
                } else {
                    Log.d(TAG, "onNext: true");
                    IS_ONLINE = C.IS_ONLINE;
                    SharedPreferencesUtils.setParam(PayMoneyActivity.this, C.IS_ONLINE_NAME, IS_ONLINE);
                    payMoneyMode.setChecked(true);
                    return;
                }
            }
        }, PayMoneyActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new ToastUntil(this).stopToast();
    }
}
