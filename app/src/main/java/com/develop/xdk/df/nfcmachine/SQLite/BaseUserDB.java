package com.develop.xdk.df.nfcmachine.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.LocalUser;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;

import java.util.ArrayList;
import java.util.List;

/**
 * user表操作类
 */
public class BaseUserDB extends BaseDB implements TableColumns.USER_COLUMNS {
    private static BaseUserDB INSTANCE;
    private Context mContext;
    private SQLiteDatabase db;

    private ToastUntil toastUntil;

    protected BaseUserDB(Context context) {
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
    public static BaseUserDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BaseUserDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BaseUserDB(context);
                }
            }
        }
        return INSTANCE;
    }

    /***
     * 向表user中插入数据
     *
     * @param cv
     */
    public synchronized void InsertUser(ContentValues cv) {
        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        try {
            db.insert(C.U_TABLE_NAME, null, cv);
        } catch (Exception e) {
            toastUntil.ShowToastShort("插入用户表失败，请重试");
            return;
        } finally {
            if (db != null || db.isOpen()) {
                closeDatabase();
            }
            cv.clear();
        }
    }

    /***
     * 查询表user中的信息
     *
     * @param selectionID  查询 条件
     * @param valus 条件中用了占位符的参数
     * @param groupBy 数据分组
     * @param having 分组后的条件
     * @param orderBy 排序方式
     * @param limit 分页查询 格式："" + startIdex + "," + endIndex + ""
     * @return 返回cursor
     */
    public synchronized List<LocalUser> selectUser(String selectionID, String[] valus, String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = null;
        List<LocalUser> lists = new ArrayList<>();
        String[] users = new String[]{C.U_CARD_ID_NAME, C.U_NAME_NAME, C.U_CASH_MONEY_NAME, C.U_SUBSIDY_MONEY_NAME,
                C.U_CONSUME_DATA_NAME, C.U_IS_LOSS_NAME, C.U_ACCONT_ID};
        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        try {
            cursor = db.query(C.U_TABLE_NAME, users, selectionID + "=?", valus, groupBy, having, orderBy, limit);
            if (cursor==null||cursor.getCount()==0){
//                ToastUntil.ShowToastShort(context,"此用户不存在");
                return null;
            }else {
                LocalUser user = new LocalUser();
                while (cursor.moveToNext()){
                    user.setSubsidyMoney(cursor.getDouble(cursor.getColumnIndex(C.U_SUBSIDY_MONEY_NAME)));
                    user.setCashMOney(cursor.getDouble(cursor.getColumnIndex(C.U_CASH_MONEY_NAME)));
                    user.setName(cursor.getString(cursor.getColumnIndex(C.U_NAME_NAME)));
                    user.setAccontid(cursor.getString(cursor.getColumnIndex(C.U_ACCONT_ID)));
                    user.setIsLoss(cursor.getInt(cursor.getColumnIndex(C.U_IS_LOSS_NAME)));
                    user.setCardID(cursor.getString(cursor.getColumnIndex(C.U_CARD_ID_NAME)));
                    user.setData(cursor.getString(cursor.getColumnIndex(C.U_CONSUME_DATA_NAME)));
                    lists.add(user);
                }
                return lists;
            }
        } catch (Exception e) {
            toastUntil.ShowToastShort( e.getMessage());
            return null;
        }finally {
            if (db.isOpen() || db != null) {
                closeDatabase();
                Log.d("dddd", "selectUser:db ============================ ");
            }
            if (cursor != null || cursor.getCount() != 0) {
                cursor.close();
                Log.d("dddd", "selectUser:cursor ============================ ");
            }
        }
    }

    /***
     * 更新表user中的信息
     *
     * @param cv  contentValues
     * @param whereId 查询条件
     * @param valus 条件中用了占位符的参数
     */
    public synchronized void updataUser(ContentValues cv, String whereId, String[] valus) {
        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        try {
            db.update(C.U_TABLE_NAME, cv, whereId + "=?", valus);
        } catch (Exception e) {
            toastUntil.ShowToastShort("更新用户表信息失败，请重试");
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
     * 清除表user的数据
     *
     * @param whereArgs  删除条件
     * @param valus    条件中用了占位符的参数
     */
    public synchronized void clearUser(String whereArgs, String[] valus) {
        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        try {
            String clearSqlite_sequence = "delete from sqlite_sequence";//将自增归0
            db.delete(C.U_TABLE_NAME, whereArgs, valus);
            db.execSQL(clearSqlite_sequence);
            toastUntil.ShowToastShort("用户表已清空");
        } catch (Exception e) {
            toastUntil.ShowToastShort( e.getMessage());
        } finally {
            if (db != null || !db.isOpen()) {
                closeDatabase();
            }
        }
    }
}
