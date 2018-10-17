package com.develop.xdk.df.nfcmachine.http.service;


import com.develop.xdk.df.nfcmachine.entity.BaseParam;
import com.develop.xdk.df.nfcmachine.entity.HttpResult;
import com.develop.xdk.df.nfcmachine.entity.NfcUser;
import com.develop.xdk.df.nfcmachine.entity.PersonDossier;
import com.develop.xdk.df.nfcmachine.entity.UserMoney;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * nfc接口
 */
public interface NfcService {
    //获取用户信息
    @POST("user/info")
    Observable<HttpResult<PersonDossier>> getUserInfo(@Body BaseParam param);

    //手持机消费
    @POST("user/comsume/nfc")
    Observable<HttpResult<UserMoney>> comsumeCard(@Body BaseParam param);

    //下载档案
    @POST("user/info/all")
    Observable<HttpResult<List<NfcUser>>> receiptAll(@Body BaseParam param);

    //上传消费记录
    @POST("user/comsume/nfc/outline")
    Observable<HttpResult<UserMoney>> sendAccount(@Body BaseParam param);

    //根据账号获取信息
    @POST("user/info/acccountid")
    Observable<HttpResult<PersonDossier>> getUser_AccontId(@Body BaseParam param);

    //挂失卡
    @POST("user/lostcard")
    Observable<HttpResult<PersonDossier>> lostCard(@Body BaseParam param);

    //检查服务器是否正常
    @POST("user/online")
    Observable<HttpResult<String>> checkOnline(@Body BaseParam param);

    //扫码支付
    @POST("user/info")
    Observable<HttpResult<String>> scanPay(@Body BaseParam param);
}
