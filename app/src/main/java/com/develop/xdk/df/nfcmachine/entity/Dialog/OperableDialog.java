package com.develop.xdk.df.nfcmachine.entity.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.entity.Dialog.Loading.MyLoading;
import com.develop.xdk.df.nfcmachine.entity.Dialog.Loading.timeOutListner;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;

/**
 * Author: XL
 * Date: 2018-10-22 11:38
 * Describe:可操作的确认弹窗
 */
public class OperableDialog extends MyLoading {
    private Context context;
    private String message;
    private String title;
    private String ensureButtonText;
    private String cancelButtonText;

    private clickListenerInterface clickListenerInterface;

    public OperableDialog(@NonNull Context context, int timeout,
                          String title, String message, String ensureButtonText, String cancelButtonText, com.develop.xdk.df.nfcmachine.entity.Dialog.Loading.timeOutListner timeOutListner) {
        super(context, timeout, timeOutListner);
        this.context = context;
        this.title = title;
        this.message = message;
        this.ensureButtonText = ensureButtonText;
        this.cancelButtonText = cancelButtonText;
    }

    public OperableDialog(@NonNull Context context, int themeResId, int timeout
            , String title, String message, String ensureButtonText, String cancelButtonText, timeOutListner timeOutListner) {
        super(context, themeResId, timeout, timeOutListner);
        this.context = context;
        this.title = title;
        this.message = message;
        this.ensureButtonText = ensureButtonText;
        this.cancelButtonText = cancelButtonText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.operable_dialog, null);
        Button bt_ensure = (Button) view.findViewById(R.id.operable_dialog_ensure);
        Button bt_cancel = (Button) view.findViewById(R.id.operale_dialog_cancel);
        TextView tv_msg = (TextView) view.findViewById(R.id.operable_dialog_message);
        TextView tv_title = (TextView) view.findViewById(R.id.operable_dialog_title);
        this.setContentView(view);
        this.setCanceledOnTouchOutside(false);
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.96f));
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.96f);
        lp.height = (int) (ScreenSizeUtils.getInstance(context).getScreenWidth() * 0.96f);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        tv_msg.setText(message);
        tv_title.setText(title);
        bt_ensure.setOnClickListener(new clickListener());
        bt_cancel.setOnClickListener(new clickListener());
    }

    public void setClickListenerInterface(OperableDialog.clickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.operable_dialog_ensure:
                    clickListenerInterface.onEnsure();
                    break;
                case R.id.operale_dialog_cancel:
                    clickListenerInterface.onCancel();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface clickListenerInterface {
        public void onEnsure();

        public void onCancel();
    }
}
