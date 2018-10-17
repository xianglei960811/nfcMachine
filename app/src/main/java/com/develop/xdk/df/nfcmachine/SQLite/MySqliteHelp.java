package com.develop.xdk.df.nfcmachine.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import static android.content.ContentValues.TAG;

public class MySqliteHelp extends SQLiteOpenHelper implements TableColumns{
    private static  final String DB_NAME = "OneCard.db";
    private static final  int DB_VERSION = 1;

    public static final String CREAT_TABLE_USERS = "CREATE TABLE IF NOT EXISTS "+ USER_COLUMNS.TABLE_USERS
            +"("
            +USER_COLUMNS.FIELD_ID+" integer not NULL primary key autoincrement,"
            +USER_COLUMNS.FIELD_NAME+" vachar(200),"
            +USER_COLUMNS.FIELD_CARD_ID+" vachar(200) not NULl,"
            +USER_COLUMNS.FIELD_ACCONT_ID+" vachar(200),"
            +USER_COLUMNS.FIELD_CASH_MONEY+" double,"
            +USER_COLUMNS.FIELD_SUBSIDY_MONEY+" double,"
            +USER_COLUMNS.FIELD_IS_LOSS+" int,"
            +USER_COLUMNS.FIELD_CONSUME_DATE+" vachar(200)"
            +")";
    public static final String CREAT_TABLE_CONSUME = "CREATE TABLE IF NOT EXISTS "+CONSUME_COUMNS.TABLE_USERS
            +"("
            +CONSUME_COUMNS.FIELD_ID+" integer not null primary key autoincrement,"
            +CONSUME_COUMNS.FIELD_CARD_ID+" vachar(200) not null,"
            +CONSUME_COUMNS.FIELD_NAME+" vachar(200),"
            +CONSUME_COUMNS.FIELD_CASH_MONEY+" double,"
            +CONSUME_COUMNS.FIELD_SUBSIDY_MONEY+" double,"
            +CONSUME_COUMNS.FIELD_CONSUME_MONEY+" double,"
            +CONSUME_COUMNS.FIELD_IDATE+" vachar(200),"
            +CONSUME_COUMNS.FIFLE_IS_HANDLE+" vachar(200),"
            +CONSUME_COUMNS.FIFLE_IS_ONLINE+" int"
            +")";
    public MySqliteHelp(Context context) {
        this(context,DB_NAME,null,DB_VERSION);
    }

    public MySqliteHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
//        //TODO 必须预先加载，不然会报错
//        SQLiteDatabase.loadLibs(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String creatUserSql = "create table users (u_id integer not NULL primary key autoincrement,u_cardID vachar(200) not NULl ," +
//                "u_accontID vachar(200),u_name varchar(200),u_cashMoney double,u_subsidyMoney double,u_isloss int,u_consumeData vachar(200))";
//
//        String creatConsumeSql = "create table consumeInfoes(c_id integer not null primary key autoincrement,c_cardID vachar(200) not null," +
//                "c_name vachar(200),c_cashMoney double,c_subsidyMoney double,c_consumeMoney double,c_data vachar(200),c_isHandle vachar(200),c_isOnline int)";
        Log.d(TAG, "Create database------- ");
        db.execSQL(CREAT_TABLE_USERS);
        db.execSQL(CREAT_TABLE_CONSUME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "newVersion:"+newVersion);
    }


}
