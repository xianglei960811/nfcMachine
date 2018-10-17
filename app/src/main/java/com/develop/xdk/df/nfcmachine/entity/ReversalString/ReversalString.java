package com.develop.xdk.df.nfcmachine.entity.ReversalString;

import java.util.ArrayList;
import java.util.List;

/**
 * 该工具用于反转字符串，每两位进行一次反转
 * 例如：进：as df, Return ：df as
 */
public class ReversalString {
    private ReversalString() {
    }

    public static String Reversal(String befor) {
        List<String> aftrs = new ArrayList<>();
        String s = "";
        int j = 0;
        int n = 0;
        if (befor.isEmpty()){
            return null;
        }
        for (int i = 0; i < befor.length() / 2; i++) {
            n = j + 2;
            if (n > befor.length()) {
                break;
            }
            aftrs.add(befor.substring(j, n));
            j = j + 2;
        }
        for (int i = aftrs.size(); i > 0; i--) {
            s += aftrs.get(i - 1);
        }
        return s;
    }
}
