package com.brioal.brioallib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.brioal.brioallib.R;

import java.util.Random;

/**
 */
public class CircleHead extends View {
    private int MAXWIDTH = 100;
    private int mWidth; //宽度
    private int mRadicus; //半径
    private String mText = "A"; //文字
    private Paint mPaintBack; //画壁
    private Paint mPaintText; //画壁
    private int mBackColor = Color.parseColor("#ffffff"); //背景色
    private int mTextColor = Color.parseColor("#303030"); //文字颜色
    private Random random;

    public CircleHead(Context context) {
        this(context, null);
    }

    public CircleHead(Context context, AttributeSet attrs) {
        super(context, attrs);
        random = new Random();
        init();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleHead);
        mText = array.getString(R.styleable.CircleHead_text);
        array.recycle();
    }

    private void init() {
        mPaintBack = new Paint();
        mPaintBack.setAntiAlias(true);
        mPaintBack.setDither(true);
        mPaintBack.setStyle(Paint.Style.FILL);
        mPaintBack.setColor(mBackColor);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setDither(true);
        mPaintText.setColor(mTextColor);
    }

    public void setmText(String mText) {
        this.mText = mText;
        invalidate();
    }

    public int getRandomColor() {
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return Color.rgb(r, g, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.AT_MOST) { //状态不固定
            mWidth = Math.min(width, MAXWIDTH);
        } else {
            mWidth = width;
        }
        mRadicus = (mWidth - 10) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mPaintBack.setColor(getRandomColor());
        canvas.drawCircle(mWidth / 2, mWidth / 2, mRadicus, mPaintBack);
        mPaintText.setTextSize(mWidth - 40);
        Paint.FontMetrics metrics = mPaintText.getFontMetrics();
        float textWidth = mPaintText.measureText(mText + "");
        float textHeight = metrics.bottom - metrics.top;
        float offsetWidth = (textWidth) / 2;
        float offsetHeight = mWidth / 2 - (metrics.bottom - (textHeight - mWidth) / 2);

        canvas.drawText(mText + "", mWidth / 2 - offsetWidth, mWidth / 2 + offsetHeight, mPaintText);

    }
}
