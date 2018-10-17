package com.develop.xdk.df.nfcmachine.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;


import java.util.ArrayList;
import java.util.List;

public class BaseConsumeDB extends BaseDB implements TableColumns.CONSUME_COUMNS{
    private static BaseConsumeDB INSTANCE;
    private Context mContext;
    private SQLiteDatabase db;

    private ToastUntil toastUntil;

    protected BaseConsumeDB(Context context) {
        super(context);
        mContext = context;
        toastUntil = new ToastUntil(mContext);
    }

    /**
     * 获取单例
     *
     * @param context
     * @return
     */
    public static BaseConsumeDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BaseUserDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BaseConsumeDB(context);
                }
            }
        }
        return INSTANCE;
    }

    /***
     * 查询表consumeInfo中的信息
     *
     * @param selectionID  查询 条件
     * @param valus 条件中用了占位符的参数
     * @param groupBy 数据分组
     * @param having 分组后的条件
     * @param orderBy 排序方式
     * @param limit 分页查询 格式："" + startIdex + "," + endIndex + ""
     * @return 返回cursor
     */
    public List<AccountUser> selectconsume(String selectionID, String[] valus, String groupBy, String having, String orderBy, String limit) {
//        Log.e("all", "selectconsume: " );
        Cursor cursor = null;
        List<AccountUser> lists = new ArrayList<>();
        String[] account = new String[]{C.C_NAME_NAME, C.C_ID_NAME, C.C_CARD_ID_NAME, C.C_SUBSIDY_MONEY_NAME, C.C_CASH_MONEY_NAME,
                C.C_CONSUME_MONEY_NAME, C.C_IS_HANDLE_NAME, C.C_DATA_NAME, C.C_IS_ONLINE_NAME};
        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        try {
//            Log.e("try", "selectconsume: " );
            cursor = db.query(C.C_TABLE_NAME, account, selectionID, valus, groupBy, having, C.C_DATA_NAME+" desc", limit);
            if (cursor == null || cursor.getCount() == 0) {
//                Log.e("cursor", "selectconsume: null" );
                return null;
            } else {
                while (cursor.moveToNext()) {
                    AccountUser user = new AccountUser();
                    user.setId(cursor.getInt(cursor.getColumnIndex(C.C_ID_NAME)));
                    user.setCardID(cursor.getString(cursor.getColumnIndex(C.C_CARD_ID_NAME)));
                    user.setIsHandl(cursor.getString(cursor.getColumnIndex(C.C_IS_HANDLE_NAME)));
                    user.setCousumeMoney(cursor.getDouble(cursor.getColumnIndex(C.C_CONSUME_MONEY_NAME)));
                    user.setCashMoney(cursor.getDouble(cursor.getColumnIndex(C.C_CASH_MONEY_NAME)));
                    user.setData(cursor.getString(cursor.getColumnIndex(C.C_DATA_NAME)));
                    user.setSubsidyMoney(cursor.getDouble(cursor.getColumnIndex(C.C_SUBSIDY_MONEY_NAME)));
                    user.setName(cursor.getString(cursor.getColumnIndex(C.C_NAME_NAME)));
                    user.setIsOnline(cursor.getInt(cursor.getColumnIndex(C.C_IS_ONLINE_NAME)));
                    lists.add(user);
                    user = null;
                }
//                Log.e("list", "selectconsume: "+lists.size() );
                return lists;
            }
        } catch (Exception e) {
            toastUntil.ShowToastShort( e.getMessage());
            Log.e("BaseConsumeDB", "selectconsume: " +e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null || !db.isOpen()) {
                closeDatabase();
            }
        }
    }

    /***
     * 更新表consume中的信息
     *
     * @param cv  contentValues
     * @param whereId 查询条件
     * @param valus 条件中用了占位符的参数
     */
    public void updataConsume(ContentValues cv, String whereId, String[] valus) {
        if (db == null || !db.isOpen()) {
            db=getWritableDatabase();
        }
        try {
            db.update(C.C_TABLE_NAME, cv, whereId + "=?", valus);
        } catch (Exception e) {
            toastUntil.ShowToastShort("更新用户表信息失败，请重试");
            Log.e("BaseConsumeDB", "updataConsume:"+e.getMessage() );
            return;
        } finally {
            if (db != null || !db.isOpen()) {
                closeDatabase();
            }
            if (!cv.toString().isEmpty()) {
                cv.clear();
            }
        }
    }

    /***
     * 向表consumeInfoes中插入数据
     *
     * @param cv
     */
    public void InsertConsume(ContentValues cv) {
        if (db == null || !db.isOpen()) {
            db=getWritableDatabase();
        }
        try {
            db.insert(C.C_TABLE_NAME, null, cv);
        } catch (Exception e) {
            toastUntil.ShowToastShort( "插入消费表失败，请重试");
            Log.e("BaseConsumeDB", "InsertConsume: "+e.getMessage() );
            return;
        } finally {
            if (db != null || db.isOpen()) {
                closeDatabase();
            }
            cv.clear();
        }
    }

    /**
     *   清除表consume的数据
     * @param sql  执行的sql语句
     */
    public void clearConsume(String sql) {
        if (db == null || !db.isOpen()) {
            db=getWritableDatabase();
        }
        try {
            db.execSQL(sql);
        } catch (Exception e) {
//            toastUntil.ShowToastShort( "清除失败");
            e.printStackTrace();
//            return;
        } finally {
            if (db != null || !db.isOpen()) {
                closeDatabase();
            }
        }
    }
}
