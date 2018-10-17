package com.develop.xdk.df.nfcmachine.SQLite;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.entity.Arith.Arith;
import com.develop.xdk.df.nfcmachine.entity.GetTime.GetNETtime;
import com.develop.xdk.df.nfcmachine.entity.LocalUser;
import com.develop.xdk.df.nfcmachine.entity.ProgressDialog.BaseProgressDialog;
import com.develop.xdk.df.nfcmachine.entity.ProgressDialog.MyProgressDialog;
import com.develop.xdk.df.nfcmachine.entity.ReversalString.ReversalString;
import com.develop.xdk.df.nfcmachine.entity.UserMoney;
import com.develop.xdk.df.nfcmachine.http.controller.RechargeController;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;
import com.develop.xdk.df.nfcmachine.ui.DataActivity;
import com.develop.xdk.df.nfcmachine.utils.SharedPreferencesUtils;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/***
 * 对db的具体操作
 */
public class SqlControl {
    private static String TAG = "";
    private Context context;
    private int number = 0;
    private ToastUntil toastUntil;
    private BaseProgressDialog myPD;

    public SqlControl(Context context) {
        this.context = context;
        TAG = context.getClass().getSimpleName();
        toastUntil = new ToastUntil(context);
        myPD = MyProgressDialog.createe(context, C.TIME_WAITING_CONNECT, new BaseProgressDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(ProgressDialog dialog) {
                toastUntil.ShowToastShort("连接超时");
            }
        });
    }


    /**
     * 将从云端获取的数据写入本地sqlite
     *
     * @param user
     */
    public void insertUserSql(LocalUser user) {
//        myPD.show();
        ContentValues cv = new ContentValues();
        cv.put(C.U_CARD_ID_NAME, user.getCardID());
        cv.put(C.U_ACCONT_ID, user.getAccontid());
        cv.put(C.U_NAME_NAME, user.getName());
        cv.put(C.U_CASH_MONEY_NAME, user.getCashMOney());
        cv.put(C.U_SUBSIDY_MONEY_NAME, user.getSubsidyMoney());
        cv.put(C.U_CONSUME_DATA_NAME, GetNETtime.getInsance().getAllData());
        cv.put(C.U_IS_LOSS_NAME, user.getIsLoss());
//        BaseSQL.getInstance(context).InsertUser(cv);
        BaseUserDB.getInstance(context).InsertUser(cv);
        C.USER_MESSAGE_NUMBER++;
        cv.clear();
        myPD.dismiss();
    }

    /**
     * 将消费记录存入sql中
     *
     * @param cardid
     * @param consumeMoney
     */
    public HashMap<String, Double> upadeConsumeInfo(String cardid, String name, double consumeMoney, double subsidyMoney, double cashMoney) {
        myPD.show();
        int Mode = (int) SharedPreferencesUtils.getParam(context, C.IS_ONLINE_NAME, C.IS_ONLINE);
        HashMap<String, Double> map = new HashMap<>();
        if (Mode != C.IS_ONLINE) {//脱机模式，需对金额作处理
            if (Arith.sub(subsidyMoney, consumeMoney) >= 0) {//利用Arith作精确的double运算
                subsidyMoney = Arith.round(Arith.sub(subsidyMoney, consumeMoney), 2);
            } else if (Arith.sub(cashMoney, consumeMoney) >= 0) {
                cashMoney = Arith.round(Arith.sub(cashMoney, consumeMoney), 2);
            } else {
                myPD.dismiss();
                return null;
            }
            map.put(C.U_SUBSIDY_MONEY_NAME, subsidyMoney);
            map.put(C.U_CASH_MONEY_NAME, cashMoney);
        }

        ContentValues cv = new ContentValues();
        cv.put(C.U_SUBSIDY_MONEY_NAME, subsidyMoney);
        cv.put(C.U_CASH_MONEY_NAME, cashMoney);
        cv.put(C.U_CONSUME_DATA_NAME, GetNETtime.getInsance().getAllData());
//        BaseSQL.getInstance(context).updataUser(cv, C.U_CARD_ID_NAME, new String[]{ReversalString.Reversal(cardid).toUpperCase()});
        BaseUserDB.getInstance(context).updataUser(cv, C.U_CARD_ID_NAME, new String[]{ReversalString.Reversal(cardid).toUpperCase()});
        Log.d(TAG, "users updata is sussess---------------> ");

        ContentValues cv1 = new ContentValues();
        cv1.put(C.C_CARD_ID_NAME, ReversalString.Reversal(cardid).toUpperCase());
        cv1.put(C.C_CONSUME_MONEY_NAME, consumeMoney);
        cv1.put(C.C_DATA_NAME, GetNETtime.getInsance().getAllData());
        cv1.put(C.C_CASH_MONEY_NAME, cashMoney);
        cv1.put(C.C_SUBSIDY_MONEY_NAME, subsidyMoney);
        if (Mode == C.IS_ONLINE) {
            cv1.put(C.C_IS_HANDLE_NAME, "true");
        } else {
            cv1.put(C.C_IS_HANDLE_NAME, C.C_IS_HANDLE);
        }
        cv1.put(C.C_NAME_NAME, name);
        cv1.put(C.C_IS_ONLINE_NAME, Mode);
//        BaseSQL.getInstance(context).InsertConsume(cv1);
        BaseConsumeDB.getInstance(context).InsertConsume(cv1);
        cv1.clear();
        Log.d(TAG, "consume info insert is sussess----------> ");
        myPD.dismiss();
        return map;

    }

    /**
     * 查询本地所有消费数据并上传云作处理,
     * 差询本地所有消费记录并存入list后返回
     */
    public List<AccountUser> getAccont(final int mode) {
        myPD.show();
        List<AccountUser> list;
        try {
            if (mode == 0) {
                //TODO 处理脱机数据
                list = dataToHttp();
                return list;
            } else if (mode == 1) {
                //TODO 本地查询账单数据
                list = localAcconts();
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "getAccont: " + e.getMessage());
            return null;
        } finally {
            myPD.dismiss();
        }

    }

    /**
     * 根据卡号，向sql查询用户信息
     *
     * @param cardID
     */
    public LocalUser selectuserInfo(String cardID) {
        myPD.show();
        LocalUser user1 = new LocalUser();
//        if (TAG.equals(C.PAY_MONEY_ACTIVITY_NAME) || TAG.equals(C.PAY_MONEY_INPUT_ACTIVITY_NAME)) {//定额支付，自定义支付反转卡号
//            cardID = ReversalString.Reversal(cardID).toUpperCase();
//        }
//        Log.e(TAG, "selectuserInfo: " + cardID);
//        List<LocalUser> users = BaseSQL.getInstance(context).selectUser(C.U_CARD_ID_NAME, new String[]{cardID}, null, null, null, null);
        List<LocalUser> users = BaseUserDB.getInstance(context).selectUser(C.U_CARD_ID_NAME, new String[]{cardID},
                null, null, null, null);
        if (users == null || users.isEmpty()) {
            myPD.dismiss();
            return null;
        } else {
            for (LocalUser user : users) {
                user1 = user;
            }
            myPD.dismiss();
            return user1;
        }
    }

    /**
     * 清空consume的信息
     * 超过一个月的处理过的脱机消费数据
     * 超过一天的在线消费的数据
     */
    public void clearConsume() {
        myPD.show();
        String clearsql_1 = "DELETE FROM " + C.C_TABLE_NAME + " WHERE " +
                "DATE('" + GetNETtime.getInsance().getdata() + "','-30 day') >=DATE(" + C.C_DATA_NAME + ") AND "
                + C.C_IS_HANDLE_NAME + " = 'true' AND "
                + C.C_IS_ONLINE_NAME + " = '0'";
        //超过一天的在线消费的数据
        String clearsql_2 = "DELETE FROM " + C.C_TABLE_NAME + " WHERE " +
                "DATE('" + GetNETtime.getInsance().getdata() + "','-1 day') >= DATE(" + C.C_DATA_NAME + ") AND "
                + C.C_IS_ONLINE_NAME + " = '1'";

        BaseConsumeDB.getInstance(context).clearConsume(clearsql_1);
        Log.d(TAG, "clearConsume: 1 month ago is success---------------->");

//        BaseSQL.getInstance(context).clearConsume(whereArg1, new String[]{getDayAgo(), String.valueOf(C.C_IS_ONLINE)});
        BaseConsumeDB.getInstance(context).clearConsume(clearsql_2);
        Log.d(TAG, "clearConsume: 1day ago is success--------------> ");
//        BaseSQL.getInstance(context).clearConsume(null, null);
        toastUntil.ShowToastShort("清除成功");
        myPD.dismiss();
    }

    /**
     * 查询所有用户信息，并存入list
     */

    public Boolean selectUsers() {
        myPD.show();
//        List<LocalUser> users = BaseSQL.getInstance(context).selectUser(null, null, null, null, null, null);
        List<LocalUser> users = BaseUserDB.getInstance(context).selectUser(null,
                null, null, null, null, null);
        if (users == null || users.isEmpty()) {
            myPD.dismiss();
            return false;
        } else {
            myPD.dismiss();
            return true;
        }

    }

    /**
     * 挂失后，更新本地的数据
     */

    public Boolean lossCard(String accontid) {
        myPD.show();
        if (accontid.isEmpty() || accontid == null) {
            toastUntil.ShowToastShort("账号为空");
            myPD.dismiss();
            return false;
        }
//        List<LocalUser> users = BaseSQL.getInstance(context).selectUser(C.U_ACCONT_ID, new String[]{accontid}, null, null, null, null);
        List<LocalUser> users = BaseUserDB.getInstance(context).selectUser(C.U_ACCONT_ID,
                new String[]{accontid}, null, null, null, null);
        if (users == null || users.isEmpty()) {
            toastUntil.ShowToastShort("该用户已挂失，但本地没有该用户");
            myPD.dismiss();
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(C.U_IS_LOSS_NAME, "1");
//        BaseSQL.getInstance(context).updataUser(cv, C.U_ACCONT_ID, new String[]{accontid});
        BaseUserDB.getInstance(context).updataUser(cv, C.U_ACCONT_ID, new String[]{accontid});
        Log.e(TAG, "lossCard:  updata is success----------------->");
        cv.clear();
        myPD.dismiss();
        return true;
    }

    /**
     * 分页查询accont数据
     */
    public List<AccountUser> pagingAccont(int startIdex, int endIndex) {
        List<AccountUser> lists;
//        lists = BaseSQL.getInstance(context).selectconsume(null, null, null, null, null, "" + startIdex + "," + endIndex + "");
        lists = BaseConsumeDB.getInstance(context).selectconsume(null, null,
                null, null, null, "" + startIdex + "," + endIndex + "");
        if (lists == null || lists.isEmpty()) {
            return null;
        } else {
            return lists;
        }
    }


    /***
     * 本地查询账单
     *
     * @return
     */
    private List<AccountUser> localAcconts() {
        myPD.show();
        List<AccountUser> lists;
//        C.C_DATA_NAME + "LIKE ?"     new String[]{mydata.getData() + "%"}
//        Log.e(TAG, "localAcconts: ");
//        lists = BaseSQL.getInstance(context).selectconsume(null, null, null, null, null, null);
        lists = BaseConsumeDB.getInstance(context).selectconsume(null,
                null, null, null, null, null);
        if (lists == null || lists.isEmpty()) {
            myPD.dismiss();
            return null;
        } else {
            myPD.dismiss();
            return lists;
        }
    }


    /***
     * 处理脱机数据
     *
     */
    private List<AccountUser> dataToHttp() {
        myPD.show();
        final DataActivity activity = (DataActivity) context;
//        List<AccountUser> lists = BaseSQL.getInstance(context).selectconsume(C.C_IS_HANDLE_NAME + "=?", new String[]{C.C_IS_HANDLE}, null, null, null, null);
        List<AccountUser> lists = BaseConsumeDB.getInstance(context).selectconsume(C.C_IS_HANDLE_NAME + "=?", new String[]{C.C_IS_HANDLE}, null, null, null, null);
        Log.d(TAG, "dataToHttp: ++" + new Gson().toJson(lists));
        if (lists == null || lists.isEmpty()) {
            toastUntil.ShowToastShort("没有未处理的消费信息");
            myPD.dismiss();
            return null;
        } else {
            for (final AccountUser user : lists) {
                if ((user.getIsOnline()) != C.IS_ONLINE) {//仅上传脱机模式
                    RechargeController.getInstance().sendAccont(new SubscriberOnNextListener<UserMoney>() {
                        @Override
                        public void onNext(UserMoney money) {
                            if (money == null) {
                                toastUntil.ShowToastShort("上传失败，请重试");
                                myPD.dismiss();
                                return;
                            } else {
                                ContentValues cv = new ContentValues();
                                cv.put(C.C_IS_HANDLE_NAME, "true");

//                                BaseSQL.getInstance(context).updataConsume(cv, C.C_ID_NAME, new String[]{String.valueOf(user.getId())});
                                BaseConsumeDB.getInstance(context).updataConsume(cv, C.C_ID_NAME, new String[]{String.valueOf(user.getId())});
                                Log.d(TAG, "upade acoont is success ----------------------> ");

                                ContentValues cv1 = new ContentValues();
                                cv1.put(C.U_CASH_MONEY_NAME, money.getCashmoney());
                                cv1.put(C.U_SUBSIDY_MONEY_NAME, money.getSubsidymoney());
//                                BaseSQL.getInstance(context).updataUser(cv1, C.U_CARD_ID_NAME, new String[]{user.getCardID()});
                                BaseUserDB.getInstance(context).updataUser(cv1, C.U_CARD_ID_NAME, new String[]{user.getCardID()});
                                Log.d(TAG, "Upadara Users is Success --------------->");

                                cv.clear();
                                cv1.clear();
                                number++;
                                toastUntil.ShowToastShort("共处理" + number + "条消费数据");
                                if (activity.untreated_number == number) {
                                    activity.badgeView.hide(false);
                                }
                            }
                        }
                    }, user, context);
                }
            }
        }
        myPD.dismiss();
        return null;
    }

}
