package com.develop.xdk.df.nfcmachine.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.entity.LocalUser;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;


import java.util.ArrayList;
import java.util.List;

/***
 * sql方法的封装
 */
public class BaseSQL {
    private MySqliteHelp sqLiteOpenHelper;
    private SQLiteDatabase db;
    private volatile static BaseSQL instance;
    private Context context;
    private ToastUntil toastUntil;

    private BaseSQL(Context context) {
        this.context = context;
        sqLiteOpenHelper = new MySqliteHelp(context.getApplicationContext());
        toastUntil = new ToastUntil(context);
    }

    public static BaseSQL getInstance(Context context) {//获取
//        singelerHolder.context = context;
//        return singelerHolder.INSTSNCE;
        BaseSQL inst = null;
        if (inst == null) {
            synchronized (BaseSQL.class) {
                inst = instance;
                if (inst == null) {
                    inst = new BaseSQL(context);
                    instance = inst;
                }
            }
        }
        return inst;
    }


    /***
     * 向表user中插入数据
     *
     * @param cv
     */
    public void InsertUser(ContentValues cv) {
        if (db == null || !db.isOpen()) {
            OpenSql();
        }
        try {
            db.insert(C.U_TABLE_NAME, null, cv);
        } catch (Exception e) {
            toastUntil.ShowToastShort( "插入用户表失败，请重试");
            return;
        } finally {
            if (db != null || db.isOpen()) {
                db.close();
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
    public List<LocalUser> selectUser(String selectionID, String[] valus, String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = null;
        List<LocalUser> lists = new ArrayList<>();
        String[] users = new String[]{C.U_CARD_ID_NAME, C.U_NAME_NAME, C.U_CASH_MONEY_NAME, C.U_SUBSIDY_MONEY_NAME,
                C.U_CONSUME_DATA_NAME, C.U_IS_LOSS_NAME, C.U_ACCONT_ID};
        if (db == null || !db.isOpen()) {
            OpenSql();
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
                db.close();
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
    public void updataUser(ContentValues cv, String whereId, String[] valus) {
        if (db == null || !db.isOpen()) {
            OpenSql();
        }
        try {
            db.update(C.U_TABLE_NAME, cv, whereId + "=?", valus);
        } catch (Exception e) {
            toastUntil.ShowToastShort("更新用户表信息失败，请重试");
            return;
        } finally {
            if (db != null || !db.isOpen()) {
                db.close();
            }
            if (!cv.toString().isEmpty()) {
                cv.clear();
            }
        }
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
        Log.e("all", "selectconsume: " );
        Cursor cursor = null;
        List<AccountUser> lists = new ArrayList<>();
        String[] account = new String[]{C.C_NAME_NAME, C.C_ID_NAME, C.C_CARD_ID_NAME, C.C_SUBSIDY_MONEY_NAME, C.C_CASH_MONEY_NAME,
                C.C_CONSUME_MONEY_NAME, C.C_IS_HANDLE_NAME, C.C_DATA_NAME, C.C_IS_ONLINE_NAME};
        if (db == null || !db.isOpen()) {
            OpenSql();
        }
        try {
//            Log.e("try", "selectconsume: " );
            cursor = db.query(C.C_TABLE_NAME, account, selectionID, valus, groupBy, having, orderBy, limit);
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
            Log.e("catch", "selectconsume: " +e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null || !db.isOpen()) {
                db.close();
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
            OpenSql();
        }
        try {
            db.update(C.C_TABLE_NAME, cv, whereId + "=?", valus);
        } catch (Exception e) {
            toastUntil.ShowToastShort("更新用户表信息失败，请重试");
            return;
        } finally {
            if (db != null || !db.isOpen()) {
                db.close();
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
            OpenSql();
        }
        try {
            db.insert(C.C_TABLE_NAME, null, cv);
        } catch (Exception e) {
            toastUntil.ShowToastShort( "插入消费表失败，请重试");
            return;
        } finally {
            if (db != null || db.isOpen()) {
                db.close();
            }
            cv.clear();
        }
    }

    /***
     * 清除表consume的数据
     *
     * @param whereArgs  删除条件
     * @param valus    条件中用了占位符的参数
     */
    public void clearConsume(String whereArgs, String[] valus) {
        if (db == null || !db.isOpen()) {
            OpenSql();
        }
        try {
            db.delete(C.C_TABLE_NAME, whereArgs, valus);
        } catch (Exception e) {
            toastUntil.ShowToastShort( "清除失败");
            return;
        } finally {
            if (db != null || !db.isOpen()) {
                db.close();
            }
        }
    }

    /***
     * 清除表user的数据
     *
     * @param whereArgs  删除条件
     * @param valus    条件中用了占位符的参数
     */
    public void clearUser(String whereArgs, String[] valus) {
        if (db == null || !db.isOpen()) {
            OpenSql();
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
                db.close();
            }
        }
    }

    /***
     * 打开数据库，或创建
     */
    private void OpenSql() {
//        db = sqLiteOpenHelper.getWritableDatabase(C.PASS);
        db = sqLiteOpenHelper.getWritableDatabase();
    }
}
