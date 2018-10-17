package com.develop.xdk.df.nfcmachine.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.develop.xdk.df.nfcmachine.Application.App;
import com.develop.xdk.df.nfcmachine.Base.BaseActivity;
import com.develop.xdk.df.nfcmachine.R;
import com.develop.xdk.df.nfcmachine.constant.C;
import com.develop.xdk.df.nfcmachine.entity.AccountUser;
import com.develop.xdk.df.nfcmachine.entity.MyList.ListAdapter;
import com.develop.xdk.df.nfcmachine.entity.MyList.MyListView;
import com.develop.xdk.df.nfcmachine.entity.ProgressDialog.BaseProgressDialog;
import com.develop.xdk.df.nfcmachine.entity.ProgressDialog.MyProgressDialog;
import com.develop.xdk.df.nfcmachine.entity.ScreenSizeUtils;
import com.develop.xdk.df.nfcmachine.utils.ToastUntil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AccontListActivity extends BaseActivity implements MyListView.MyLoadListner, AdapterView.OnItemClickListener {
    @BindView(R.id.accont_list_list)
    MyListView accontList;
    @BindView(R.id.accont_list_back)
    Button accontBack;

    private String TAG = AccontListActivity.class.getSimpleName();

    private List<AccountUser> data = new ArrayList<>();//数据
    private List<AccountUser> datas = new ArrayList<>();//全部数据的集合
    private ListAdapter adapter;
    private BaseProgressDialog myPD = null;
    private int startIndex = 0;
    private int endIndex;
    private int number = 10;//每页加载的个数
    private int totalNumber;
    private ToastUntil toastUntil = new ToastUntil(this);

    @Override
    protected void initView() {//初始化

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //显示预加载的数据


    }

    private void init() {//初始
        myPD = MyProgressDialog.createe(this, C.TIME_WAITING_CONNECT, new BaseProgressDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(ProgressDialog dialog) {//对超时后的操作
                toastUntil.ShowToastShort( "链接超时");
            }
        });
        myPD.show();

        Intent intent = this.getIntent();
        totalNumber = intent.getIntExtra("total", 0);

        accontList.intShow(this);
        endIndex = startIndex + number;
        try {
            data.addAll(sqlControl.pagingAccont(startIndex, endIndex));//list初始化加载的数据
            datas.addAll(data);
        } catch (Exception e) {
            toastUntil.ShowToastShort( "没有收账记录");
            accontList.isend();
        }
        startIndex += number;
        endIndex += number;
        showListview(datas);
    }

    /***
     * 更新list视图
     *
     * @param lists
     */
    private void showListview(List<AccountUser> lists) {
        if (lists == null) {
            accontList.isend();
            return;
        }
        if (lists.size()<number){
            accontList.isend();
            toastUntil.ShowToastShort( "已加载全部数据");
        }
        if (adapter == null) {
            accontList.setMyLoadListner(this);
            adapter = new ListAdapter(this, lists);
            accontList.setAdapter(adapter);
            if (lists.size() % number == 0&&lists.size()==totalNumber) {
                accontList.isend();
                toastUntil.ShowToastShort( "已加载全部数据");
            }
        } else {
            if (lists.size() % number == 0&&lists.size()==totalNumber) {
                accontList.isend();
                toastUntil.ShowToastShort( "已加载全部数据");
            }
            adapter.onDataChange(lists);
        }
        myPD.dismiss();

        accontList.setOnItemClickListener(this);
    }

    /**
     * 每次加载的数据
     */
    private void getLoadData() {
        data = sqlControl.pagingAccont(startIndex, endIndex);
//        Log.e(TAG, "getLoadData: "+new Gson().toJson(data));
        startIndex += number;
        endIndex += number;
        try {
            datas.addAll(data);
        } catch (Exception e) {
            accontList.isend();
            toastUntil.ShowToastShort( "已加载全部数据");
        }

//        Log.e(TAG, "getLoadData: " + datas.size());
        if (datas.size() == totalNumber) {
            toastUntil.ShowToastShort( "已加载全部数据");
            accontList.isend();
        }
    }

    @Override
    public void onload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {//延时2s执行
            @Override
            public void run() {
                getLoadData();
                showListview(datas);//更新list
                accontList.setComplete();//隐藏foot
            }
        }, 2000);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_accont_list);
    }


    @OnClick(R.id.accont_list_back)
    public void OnBackClick() {
        toActivityWithFinish(AcountActivity.class);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO 点击列表时查看更多信息
        myPD.show();
        if (id == -1) {
            //TODO 点击的是head或foot
            return;
        }
        showAccontDialog(position);
    }

    private void showAccontDialog(int position) {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.show_accont_dialog, null);
        Button accontDialogEnsure = (Button) view.findViewById(R.id.show_accont_dialog_ensure);
        TextView accontDialogNumb = (TextView) view.findViewById(R.id.show_accont_dialog_numb);
        TextView accontDialogName = (TextView) view.findViewById(R.id.show_accont_dialog_name);
        TextView accontDialogSubMoney = (TextView) view.findViewById(R.id.show_accont_dialog_subsidyMoney);
        TextView accontDialogCashMoney = (TextView) view.findViewById(R.id.show_accont_dialog_cashMoney);
        TextView accontDialogConsMoney = (TextView) view.findViewById(R.id.show_accont_dialog_consumeMoney);
        TextView accontDialogconsDate = (TextView) view.findViewById(R.id.show_accont_dialog_data);
        TextView accontDialogHandle = (TextView) view.findViewById(R.id.show_accont_dialog_handle);
        TextView accontDialogMode = (TextView) view.findViewById(R.id.show_accont_dialog_mode);
        try {

            accontDialogNumb.setText(Integer.toString(position + 1));
            accontDialogName.setText(datas.get(position).getName());
            accontDialogSubMoney.setText(Double.toString(datas.get(position).getSubsidyMoney()));
            accontDialogCashMoney.setText(Double.toString(datas.get(position).getCashMoney()));
            accontDialogConsMoney.setText(Double.toString(datas.get(position).getCousumeMoney()));
            accontDialogconsDate.setText(datas.get(position).getData());
            int mode = datas.get(position).getIsOnline();
            if (mode==C.IS_ONLINE){
                accontDialogMode.setText("在线");
            }else {
                accontDialogMode.setText("脱机");
            }

            String handler;
            if (datas.get(position).getIsHandl().equals("true")) {
                handler = "是";
                accontDialogHandle.setTextColor(Color.GREEN);
            } else {
                handler = "否";
                accontDialogHandle.setTextColor(Color.RED);
            }
            accontDialogHandle.setText(handler);

        } catch (Exception e) {
            toastUntil.ShowToastShort( "加载失败");
            Log.e(TAG, "showAccontDialog: "+e.getMessage() );
////            e.printStackTrace();
        }
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 1f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        accontDialogEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keycode, KeyEvent event) {
                if (keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                   dialog.dismiss();
                }
                return false;
            }
        });
        dialog.show();
        myPD.dismiss();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        toastUntil.stopToast();
        super.onDestroy();
    }


}
