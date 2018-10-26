package com.develop.xdk.df.nfcmachine.entity.MyList;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.develop.xdk.df.nfcmachine.R;

public class MyListView extends ListView implements AbsListView.OnScrollListener {
    View foot;
    int lastVisibleItem;//最后一个可见的Item
    int totalItemCount;//总的数量
    boolean isLoading;//正在加载
    MyLoadListner myLoadListner;
    private boolean isEnding = false;

    public MyListView(Context context) {
        super(context);
//        intShow(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        intShow(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        intShow(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        intShow(context);
    }


    public void intShow(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        foot = inflater.inflate(R.layout.footer, null);
        foot.findViewById(R.id.foot_view).setVisibility(View.GONE);
        this.addFooterView(foot);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {//滚动状态且到底部
            if (!isLoading&&!isEnding) {
                isLoading = true;
                foot.findViewById(R.id.foot_view).setVisibility(View.VISIBLE);
                myLoadListner.onload();//加载更多数据
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    public void setMyLoadListner(MyLoadListner myLoadListner) {
        this.myLoadListner = myLoadListner;
    }

    /**
     * 加载完成
     */
    public void setComplete() {
        isLoading = false;
        foot.findViewById(R.id.foot_view).setVisibility(View.GONE);
    }

    public void isend(){
        isEnding = true;
        foot.findViewById(R.id.foot_view).setVisibility(View.GONE);
    }
    public interface MyLoadListner {
         void onload();
    }

}
