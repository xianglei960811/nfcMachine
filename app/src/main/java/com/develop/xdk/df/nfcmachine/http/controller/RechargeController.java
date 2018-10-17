package com.develop.xdk.df.nfcmachine.http.controller;

import android.content.Context;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AcconetParam;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.entity.BaseParam;
import com.develop.xdk.df.nfcmachine.entity.ComsumeParam;
import com.develop.xdk.df.nfcmachine.entity.GetTime.GetNETtime;
import com.develop.xdk.df.nfcmachine.entity.HttpResult;
import com.develop.xdk.df.nfcmachine.entity.NfcUser;
import com.develop.xdk.df.nfcmachine.entity.OffLineParam;
import com.develop.xdk.df.nfcmachine.entity.PersonDossier;
import com.develop.xdk.df.nfcmachine.entity.UserMoney;
import com.develop.xdk.df.nfcmachine.http.ApiException;
import com.develop.xdk.df.nfcmachine.http.HttpMethods;
import com.develop.xdk.df.nfcmachine.http.service.NfcService;
import com.develop.xdk.df.nfcmachine.http.subscribers.ProgressSubscriber;
import com.develop.xdk.df.nfcmachine.http.subscribers.SubscriberOnNextListener;
import com.develop.xdk.df.nfcmachine.utils.BeanUtil;
import com.develop.xdk.df.nfcmachine.utils.SharedPreferencesUtils;
import com.develop.xdk.df.nfcmachine.utils.SignUtil;
import com.google.gson.Gson;

import java.util.List;
import java.util.SortedMap;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2018/6/11.
 */

public class RechargeController {
    private Retrofit retrofit;
    private NfcService NfcService;

    private RechargeController() {
        retrofit = HttpMethods.getInstance().getRetrofit();
        NfcService = retrofit.create(NfcService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final RechargeController INSTANCE = new RechargeController();
    }

    //获取单例
    public static RechargeController getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 获取用户信息
     *
     * @param onNextListener
     * @param context
     * @param cardid
     */
    public void getUserinfo(SubscriberOnNextListener onNextListener, Context context, String cardid) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        BaseParam param = new BaseParam();
        param.setCardid(cardid);
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.getUserInfo(param)
                .map(new HttpResultFunc<PersonDossier>());
        toSubscribe(observable, subscriber);
        Log.d("getUserinfo", new Gson().toJson(param));
    }

    /**
     * 消费
     *
     * @param onNextListener
     * @param context
     * @param cardid
     */
    public void comsumeCard(SubscriberOnNextListener onNextListener, Context context, String cardid, String money) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        ComsumeParam param = new ComsumeParam();
        param.setCardid(cardid);
        param.setSpendMoney(Double.valueOf(money.toString()));
        param.setComputer((String) SharedPreferencesUtils.getParam(context, C.COMPUTER_NAME, C.COMPUTER));
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setWindowNumber((String) SharedPreferencesUtils.getParam(context, C.MACHINE_NAME, C.MACHINE));
        param.setSpendDate(GetNETtime.getInsance().getdata());
        param.setSpendTime(GetNETtime.getInsance().geTime());
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.comsumeCard(param)
                .map(new HttpResultFunc<UserMoney>());
        toSubscribe(observable, subscriber);
        Log.d("comsumeCard", new Gson().toJson(param));
    }


    /**
     * 接受所有用户信息
     *
     * @param subscriberOnNextListener
     * @param context
     */
    public void receiptAll(SubscriberOnNextListener subscriberOnNextListener, Context context) {
        ProgressSubscriber subscriber = new ProgressSubscriber(subscriberOnNextListener, context);
        OffLineParam param = new OffLineParam();
        param.setComputer((String) SharedPreferencesUtils.getParam(context, C.COMPUTER_NAME, C.COMPUTER));
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setWindowNumber((String) SharedPreferencesUtils.getParam(context, C.MACHINE_NAME, C.MACHINE));
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.receiptAll(param).map(new HttpResultFunc<List<NfcUser>>());
        toSubscribe(observable, subscriber);
        Log.d("receiptAll", new Gson().toJson(param));
    }

    /**
     * 向服务器发送流水账单
     *
     * @param onNextListener
     * @param accont
     * @param context
     */
    public void sendAccont(SubscriberOnNextListener onNextListener, AccountUser accont, Context context) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        OffLineParam param = new OffLineParam();
        param.setCardid(accont.getCardID());
        param.setComputer((String) SharedPreferencesUtils.getParam(context, C.COMPUTER_NAME, C.COMPUTER));
        String[] dates = accont.getData().split("\\ ");
        param.setSpendDate(dates[0]);
        param.setSpendMoney(accont.getCousumeMoney());
        param.setPdCashmoney(accont.getCashMoney());
        param.setPdSubsidymoney(accont.getSubsidyMoney());
        param.setSpendTime(dates[1]);
        param.setIsHandle(accont.getIsHandl());
        param.setWindowNumber((String) SharedPreferencesUtils.getParam(context, C.MACHINE_NAME, C.MACHINE));
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.sendAccount(param).map(new HttpResultFunc<UserMoney>());
        toSubscribe(observable, subscriber);
        Log.d("sendAll: ", new Gson().toJson(param));
    }

    /**
     * 挂失：根据用户账号，查询用户信息
     *
     * @param onNextListener
     * @param context
     * @param accontId
     */
    public void getUser_accontId(SubscriberOnNextListener onNextListener, Context context, String accontId) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        AcconetParam param = new AcconetParam();
        param.setAccountid(accontId);
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.getUser_AccontId(param).map(new HttpResultFunc<PersonDossier>());
        toSubscribe(observable, subscriber);
        Log.d("AccontID", new Gson().toJson(param));
    }

    /**
     * 挂失卡
     *
     * @param onNextListener
     * @param context
     * @param accontid
     */
    public void lostCard(SubscriberOnNextListener onNextListener, Context context, String accontid) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        AcconetParam param = new AcconetParam();
        param.setAccountid(accontid);
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.lostCard(param).map(new HttpResultFunc<PersonDossier>());
        toSubscribe(observable, subscriber);
        Log.d("lostcard", new Gson().toJson(param));
    }

    /**
     * 检查服务器是否正常运行
     *
     * @param onNextListener
     * @param context
     */
    public void checkOnline(SubscriberOnNextListener onNextListener, Context context) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        BaseParam param = new BaseParam();
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map,C.SIGN_KEY));
        Observable observable = NfcService.checkOnline(param).map(new HttpResultFunc<String>());
        toSubscribe(observable,subscriber);
        Log.d("checkOnline", new Gson().toJson(param));
    }
    /**
     * 消费
     *
     * @param onNextListener
     * @param context
     * @param authCode
     */
    public void scanPay(SubscriberOnNextListener onNextListener, Context context, String authCode, String money) {
        ProgressSubscriber subscriber = new ProgressSubscriber(onNextListener, context);
        ComsumeParam param = new ComsumeParam();
        param.setAuthCode(authCode);
        param.setSpendMoney(Double.valueOf(money.toString()));
        param.setComputer((String) SharedPreferencesUtils.getParam(context, C.COMPUTER_NAME, C.COMPUTER));
        param.setClientid((String) SharedPreferencesUtils.getParam(context, C.CLIENTID_NAME, C.CLIENTID));
        param.setWindowNumber((String) SharedPreferencesUtils.getParam(context, C.MACHINE_NAME, C.MACHINE));
        param.setSpendDate(GetNETtime.getInsance().getdata());
        param.setSpendTime(GetNETtime.getInsance().geTime());
        param.setTimestamp(String.valueOf(System.currentTimeMillis()));
        SortedMap map = BeanUtil.ClassToMap(param);
        param.setSign(SignUtil.createSign(map, C.SIGN_KEY));
        Observable observable = NfcService.scanPay(param)
                .map(new HttpResultFunc<String>());
        toSubscribe(observable, subscriber);
        Log.d("scanpay", new Gson().toJson(param));
    }
    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            Log.e("result", new Gson().toJson(httpResult));
            if (httpResult.getCode() == 0) {
                throw new ApiException(httpResult.getMsg());
            }
            return httpResult.getData();
        }
    }

}
