package com.develop.xdk.df.nfcmachine.constant;

/**
 * 默认常量值
 */

public class C {
    public static final String SIGN_KEY = "cinzn2055";
    public static final String BASE_URL = "http://wechat.cinzn.com:9000/";
    public static final String CLIENTID = "02801";
    public static final String MACHINE = "001";
    public static final String MACHINE_NAME = "machine";
    public static final String COMPUTER = "001";
    public static final String COMPUTER_NAME = "computer";
    public static final String PRICE = "10";
    public static final String PRICE_NAME = "price";
    public static final String CLIENTID_NAME = "clientid";
    public static final String BASE_URL_NAME = "baseurl";
    public static final String READPATH_NAME = "readpath";
    public static final String READPATH = "/dev/ttyS2";
    public static final String PASS = "2055";
    public static final String PASS_NAME = "PASS";

    public static final int USER_MODE = 1;
    public static final int PAY_MONEY_MODE = 2;
    //地址
    public static final int CARD_ADDRESS = 0x00;
    //模式
    public static final byte CARD_MODE = 0x00;
    public static final byte CARD_HALT = 0x00;
    public static final String BLOCK_NAME = "block";
    public static final String BLOCK = "1";

    public static final String IS_ONLINE_NAME = "IS_ONLINE";
    public static final int IS_ONLINE = 1;//1为在线模式，0为脱机模式

    public static final String U_TABLE_NAME = "users";
    public static final String C_TABLE_NAME = "consumeInfoes";
    //表users字段名
    public static final String U_ID_NAME = "u_id";
    public static final String U_CARD_ID_NAME = "u_cardID";
    public static final String U_ACCONT_ID = "u_accontID";
    public static final String U_NAME_NAME = "u_name";
    public static final String U_CASH_MONEY_NAME = "u_cashMoney";
    public static final String U_SUBSIDY_MONEY_NAME = "u_subsidyMoney";
    public static final String U_CONSUME_DATA_NAME = "u_consumeData";
    public static final String U_IS_LOSS_NAME = "u_isloss";
    public static final int U_IS_LOSS = 1;//1表示挂失，0表示未挂失


    public static final String C_ID_NAME = "c_id";
    public static final String C_CARD_ID_NAME = "c_cardID";
    public static final String C_CONSUME_MONEY_NAME = "c_consumeMoney";
    public static final String C_DATA_NAME = "c_data";
    public static final String C_CASH_MONEY_NAME = "c_cashMoney";
    public static final String C_SUBSIDY_MONEY_NAME = "c_subsidyMoney";
    public static final String C_IS_HANDLE_NAME = "c_isHandle";
    public static final String C_IS_HANDLE = "false";
    public static final String C_NAME_NAME = "c_name";
    public static final String C_IS_ONLINE_NAME = "c_isOnline";
    public static final int C_IS_ONLINE = 1;//1表示在线，0表示脱机




    public static int USER_MESSAGE_NUMBER = 0;//用于记录信息数

    //activity名称
    public static String PAY_MONEY_ACTIVITY_NAME = "PayMoneyActivity";
    public static String PAY_MONEY_INPUT_ACTIVITY_NAME = "PayMoneyInputActivity";
    public static String DISMISS_CARD_ACTIVITY_NAME = "DismissCardActivity";
    public static String ACOUNT_ACTIVITY_NAME = "AcountActivity";
    public static String DATA_ACTIVITY_NAME = "DataActivity";
    public static String SETUP_ACTIVITY_NAME = "SetupActivity";
    public static String ACCONT_LIST_ACTIVITY_NAME = "AccontListActivity";
    public static String SCAN_ACTIVITY_NAME = "ScanActivity";
    public static final String MY_SERVICE_NAME = "MyService";

    public static final int TIME_WAITING_CONNECT = 5000;//5s后超时


    //提醒时间

    public static final String TIME_SEVEN = "7:00:00";
    public static final String TIME_TEN = "10:00:00";
    public static final String TIME_FIFTEEN = "15:00:00";
    public static final String TIME_TWENTY = "20:00:00";

    public static final String IS_LOADING_NAME = "is_loading";
    public static final Boolean IS_LOADING = true;
}
