package com.develop.xdk.df.nfcmachine.entity;


import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 判断是否为金钱格式
 * 小数点后只能有两位
 */
public class IsMoney {
    private IsMoney() {
    }

    public static Boolean checkMoney(String Cmoney) {
        String[] moneys = Cmoney.split("\\.");
        if (moneys[0] == null) {
            return false;
        } else {
            if (moneys[0].equals("")) {
                moneys[0] = "0";
            }
            if (Integer.parseInt(moneys[0]) == 0) {
                if (moneys.length <= 1) {
                    return false;
                } else {
                    if (moneys[1] == null) {
                        return false;
                    } else {
                        if (Integer.parseInt(moneys[1]) <= 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            } else if (Integer.parseInt(moneys[0]) < 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    //转化成0.00格式
    public static String changeMoney(String money) {
        if (money != null || !money.equals("")) {
            String[] moneys = money.split("\\.");
//            Log.e("sss", "changeMoney: " + moneys[0]);
            if (moneys[0].equals("")) {
                money = "0" + money;
                if (moneys[1].length() > 3) {
                    NumberFormat nf = new DecimalFormat("##.##");
                    money = nf.format(Double.valueOf(money));
                }
//                Log.e("aaa", "changeMoney: " + money);
                return money;
            } else if (moneys.length < 2 && !moneys[0].equals("")) {
                money = money + ".00";
                return money;
            } else {
                NumberFormat nf = new DecimalFormat("##.##");
                String myMoney = nf.format(Double.valueOf(money));
//                Log.e("sssss", "changeMoney: " + myMoney);
                return myMoney;
            }
        } else {
            return null;
        }
    }
}
