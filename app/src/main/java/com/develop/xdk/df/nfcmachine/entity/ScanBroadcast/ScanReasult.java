package com.develop.xdk.df.nfcmachine.entity.ScanBroadcast;

/**
 * Author: XL
 * Date: 2018-10-16 14:55
 * Describe:接收到广播后的回调接口
 */
public interface ScanReasult {
   void onNext(String reasult);
}
