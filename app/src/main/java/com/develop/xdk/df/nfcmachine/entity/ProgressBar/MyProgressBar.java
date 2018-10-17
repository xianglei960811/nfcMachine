package com.develop.xdk.df.nfcmachine.entity.ProgressBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.develop.xdk.df.nfcmachine.R;

public class MyProgressBar extends View {
    private Paint paint;//绘制灰色线条的画笔
    private Paint painttext;//绘制下载进度文字的画笔
    private float offset;//偏移量
    private double maxposition;//下载的总数
    private double currentposition;//下载了多少
    private Rect rect;//获取百分比数字的宽度
    private String percentValue;//要显示的百分比
    private float offsetRight;//灰色线条距右边的距离
    private int textsize;//百分比 文字的大小
    private float offsettop;//距顶部的偏移量

    public MyProgressBar(Context context) {
        super(context, null);
        initview();
    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        initview();
        //TODO 获取自定义属性，给textsize赋值
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.MyProgressBar);
        textsize = (int) t.getDimension(R.styleable.MyProgressBar_myPB, 36);
        getTextwidth();
    }


    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //TODO 绘制底色
        paint.setColor(getResources().getColor(R.color.PD_buttom));
        paint.setStrokeWidth(1);
        canvas.drawLine(0, offsettop, getWidth(), offsettop, paint);
        //TODO 绘制进度条颜色
        paint.setColor(getResources().getColor(R.color.PD_position));
        paint.setStrokeWidth(2);
        canvas.drawLine(0, offsettop, offset, offsettop, paint);
        //TODO 绘制白色区域及百分比
        paint.setColor(getResources().getColor(R.color.PD_WHRITE));
        paint.setStrokeWidth(1);
        painttext.setColor(getResources().getColor(R.color.PD_position));
        painttext.setTextSize(textsize);
        painttext.setAntiAlias(true);
        painttext.getTextBounds(percentValue, 0, percentValue.length(), rect);
        canvas.drawLine(offset, offsettop, offset + rect.width() + 4, offsettop, paint);
        canvas.drawText(percentValue, offset, offsettop + rect.height() / 2 - 2, painttext);
    }

    /***
     * 设置当前进度
     * @param position
     */
    public void setCurrentPosition(String position) {
        this.percentValue = position;
        currentposition = Double.valueOf(position.replaceAll("%", ""));
        initCurrentProgressBar();
        invalidate();
    }

    /***
     * 获取当前进度条长度
     */
    private void initCurrentProgressBar() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (currentposition < maxposition) {
                    offset = (float) ((getWidth() - offsetRight) * currentposition / maxposition);
                } else {
                    offset = getWidth() - offsetRight;
                }
            }
        });
    }


    /***
     * 初始化变量
     */
    private void initview() {
        paint = new Paint();
        painttext = new Paint();
        offset = 0f;
        maxposition = 100f;
        currentposition = 0f;
        rect = new Rect();
        percentValue = "0%";
        offsetRight = 0f;
        textsize = 25;
        offsettop = 18f;
    }

    /***
     * 获取100%的宽度
     */
    private void getTextwidth() {
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(textsize);
        paint.setAntiAlias(true);
        paint.getTextBounds("100%", 0, "100%".length(), rect);
        offsetRight = rect.width() + 5;
    }
}
