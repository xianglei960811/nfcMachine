package com.develop.xdk.df.nfcmachine.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.develop.xdk.df.nfcmachine.MainActivity;

/**
 * 开机启动
 */
public class SelfStartReceiver extends BroadcastReceiver {
    public SelfStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.i("dd", "onReceive: 开机了" );
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
