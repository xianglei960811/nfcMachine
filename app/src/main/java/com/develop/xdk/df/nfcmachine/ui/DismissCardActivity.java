package com.develop.xdk.df.nfcmachine.ui;

import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.MainActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccontIDEnsure.AccontIDEnsure;
import com.develop.xdk.df.nfcmachine.entity.PersonDossier;
import com.develop.xdk.df.nfcmachine.http.controller.RechargeController;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/2/27.
 */

public class DismissCardActivity extends BaseActivity {
    @BindView(R.id.dismiss_card_accontID)
    EditText dismissCardAccontID;
    @BindView(R.id.dismiss_card_class)
    TextView dismissCardClass;
    @BindView(R.id.dismiss_card_name)
    TextView dismissCardName;
    @BindView(R.id.dismiss_card_subsidy_Money)
    TextView dismissCardSubMoney;
    @BindView(R.id.dismiss_card_cash_money)
    TextView dismissCardCashMoney;
    @BindView(R.id.dismiss_card_do)
    Button dismissCardDo;
    @BindView(R.id.dismiss_card_back)
    Button dismissCardBack;
    @BindView(R.id.dismiss_card_get_user)
    Button dismissCadrdGet;

    private String accontId;

    ToastUntil toastUntil = new ToastUntil(this);


    @Override
    protected void initView() {
        dismissCardAccontID.setInputType(InputType.TYPE_CLASS_NUMBER);//只能输入数字
        clearView();

    }

    private void clearView() {
        dismissCardName.setText("");
        dismissCardClass.setText("");
        dismissCardCashMoney.setText("");
        dismissCardSubMoney.setText("");
        accontId = "";
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_dismiss_card);
    }

    @OnClick(R.id.dismiss_card_back)
    public void backClick() {
        toActivityWithFinish(MainActivity.class);
    }

    @OnClick(R.id.dismiss_card_get_user)//查询按钮
    public void GetClick() {
        clearView();
        if (dismissCardAccontID.getText().toString().trim().isEmpty()) {
            toastUntil.ShowToastShort("账号不能为空");
        } else {
            if (!IS_ONLINE) {
                toastUntil.ShowToastShort("设备断网，请连接网络");
            } else {
                String accontID = "";
                accontID = AccontIDEnsure.ensureAccontID(dismissCardAccontID.getText().toString().trim());
                accontId = accontID;
                RechargeController.getInstance().getUser_accontId(new SubscriberOnNextListener<PersonDossier>() {
                    @Override
                    public void onNext(PersonDossier user) {
                        if (user == null || new Gson().toJson(user).equals("null")) {
                            toastUntil.ShowToastShort("信息获取失败。");
                        } else {
                            if (user.getPdLoss() != C.U_IS_LOSS) {
                                dismissCardName.setText(user.getPdName());
                                dismissCardClass.setText(user.getPdDepartment());
                                dismissCardCashMoney.setText(Double.toString(user.getPdCashmoney()));
                                dismissCardSubMoney.setText(Double.toString(user.getPdSubsidymoney()));
                            } else {
                                dismissCardAccontID.setText("");
                                toastUntil.ShowToastShort("该用户已挂失！");
                            }
                        }
                    }
                }, this, accontID);
            }
        }
    }

    @OnClick(R.id.dismiss_card_do)
    public void DoClick() {
        if (dismissCardName.getText().toString().trim().isEmpty() || dismissCardClass.getText().toString().trim().isEmpty() ||
                dismissCardCashMoney.getText().toString().trim().isEmpty() || dismissCardSubMoney.getText().toString().trim().isEmpty() || accontId.isEmpty()) {
            toastUntil.ShowToastShort("未获取用户信息！");
        } else {
            RechargeController.getInstance().lostCard(new SubscriberOnNextListener<PersonDossier>() {
                @Override
                public void onNext(PersonDossier pd) {
                    if (sqlControl.lossCard(accontId)) {
                        toastUntil.ShowToastShort("挂失成功");
                    } else {
//                        toastUntil.ShowToastShort("本地信息更新失败");
                    }
                    clearView();
                }
            }, this, accontId);

        }
    }


    @Override
    protected void onDestroy() {
        toastUntil.stopToast();
        super.onDestroy();
    }
}
