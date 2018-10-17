package com.develop.xdk.df.nfcmachine.http;

import android.util.Log;

/**
 * 异常处理
 */
public class ApiException extends RuntimeException {

    public static final String NO_NET = "no internet";
    public static final String NO_USER = "not found user";
    public static final String NO_MONEY = "user have no money";
    public static final String NO_REGIST = "user not find by cardid";
    public static final String COMSUME_FAIL = "comsume insert fail";
    public static final String UPADTE_MONEY_WRONG = "user update money wrong";
    public static final String CARD_LOST = "user card lost";
    public static final String MONEY_NO = "user money < 0";
    public static final String PARM_EMPTY = "param cant be null or empty";
    public  static  final String NOT_ACCONT_DATA = "not hava any accont data";
    public ApiException(String msg) {
        super(getApiExceptionMessage(msg));
    }
    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param msg
     * @return
     */
    private static String getApiExceptionMessage(String msg){
        String message = msg;
        switch (msg) {
            case NO_NET:
                message = "本地断网，无法充值";
                break;
            case NO_USER:
                message = "没有找到此用户";
                break;
            case NO_MONEY:
                message = "余额不足";
                break;
            case NO_REGIST:
                message  = "此用户不存在";
                break;
            case COMSUME_FAIL:
                message = "消费失败，请重试。";
                break;
            case UPADTE_MONEY_WRONG:
                message = "余额更新失败，请重试。";
                break;
            case CARD_LOST:
                message = "该卡已挂失。";
                break;
            case MONEY_NO:
                message  = "余额不足";
                break;
            case PARM_EMPTY:
                message = "查询信息不足，请补足信息。";
                break;
            case NOT_ACCONT_DATA:
                message = "没有任何账单信息。";
                break;
            case "not users":
                message =  "没有用户信息";
                break;
            case "device_info invalid":
                message =  "设备未注册";
                break;
            default :
                break;
        }
        return message;
    }
}

