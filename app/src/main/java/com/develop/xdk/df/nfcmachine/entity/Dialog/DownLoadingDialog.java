package com.develop.xdk.df.nfcmachine.entity.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.entity.Dialog.DownLoading.DownLoadBar;
import com.develop.xdk.df.nfcmachine.entity.Dialog.Loading.MyLoading;
import com.develop.xdk.df.nfcmachine.entity.Dialog.Loading.timeOutListner;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;

/**
 * Author: XL
 * Date: 2018-10-22 15:15
 * Describe:下载弹窗动画
 */
public class DownLoadingDialog extends MyLoading {
    private Context context;
    private volatile DownLoadBar loadBar;

    public DownLoadingDialog(@NonNull Context context, int s, timeOutListner timeOutListner) {
        super(context, s, timeOutListner);
        this.context = context;
    }

    public DownLoadingDialog(@NonNull Context context, int themeResId, int s, timeOutListner timeOutListner) {
        super(context, themeResId, s, timeOutListner);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.down_loadingdialog, null);
        loadBar = (DownLoadBar) view.findViewById(R.id.down_loading_bar);
        this.setContentView(view);
        this.setCanceledOnTouchOutside(true);
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.3f));
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.3f);
        lp.height = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.3f);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

    }


    @Override
    public void show() {
        super.show();
    }

    @Override
    public void setOnCancelListener(@Nullable DialogInterface.OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setCurrentPosition(int position) {
        loadBar.setmCurrentProgress(position);
    }

}
