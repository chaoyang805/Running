package com.chaoyang805.running.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.chaoyang805.running.R;
import com.chaoyang805.running.utils.LogHelper;

/**
 * Created by chaoyang805 on 2015/12/23.
 */
public class MainDetailTextView extends View {

    private static final String TAG = LogHelper.makeLogTag(MainDetailTextView.class);
    private static final int DEFAULT_PADDING = 10;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final float DEFAULT_TEXT_SIZE = 20f;
    private static final float DEFAULT_DETAIL_TEXT_SIZE = 15f;

    private float mPaddingLeft;
    private float mPaddingTop;
    private float mPaddingRight;
    private float mPaddingBottom;

    private float mTextInnerPadding;

    private CharSequence mText;
    private CharSequence mDetailText;

    private int mTextColor;
    private int mDetailTextColor;

    private float mTextSize;
    private float mDetailTextSize;

    private float mTextHeight;
    private float mDetailTextHeight;

    private TextPaint mTextPaint;
    private TextPaint mDetailTextPaint;

    private Rect mTextBounds;
    private Rect mDetailTextBounds;

    private int mGravity = Gravity.LEFT;


    public MainDetailTextView(Context context) {
        this(context, null);
    }

    public MainDetailTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainDetailTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MainDetailTextView);

        mText = t.getString(R.styleable.MainDetailTextView_mainText);
        mDetailText = t.getString(R.styleable.MainDetailTextView_detailText);

        mTextInnerPadding = t.getDimension(R.styleable.MainDetailTextView_textInnerPadding, DEFAULT_PADDING);

        mTextColor = t.getColor(R.styleable.MainDetailTextView_mainTextColor, DEFAULT_COLOR);
        mDetailTextColor = t.getColor(R.styleable.MainDetailTextView_detailTextColor, DEFAULT_COLOR);

        mTextSize = t.getDimension(R.styleable.MainDetailTextView_mainTextSize, DEFAULT_TEXT_SIZE);
        mDetailTextSize = t.getDimension(R.styleable.MainDetailTextView_detailTextSize, DEFAULT_DETAIL_TEXT_SIZE);

        mGravity = t.getInt(R.styleable.MainDetailTextView_textGravity, Gravity.LEFT);
        t.recycle();

        initPaint();
    }

    private void initPaint() {
        mTextPaint = new TextPaint();
        mDetailTextPaint = new TextPaint();
        mTextBounds = new Rect();
        mDetailTextBounds = new Rect();

        updatePaint();

    }

    private void updatePaint() {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.getTextBounds(mText.toString(), 0, mText.length(), mTextBounds);
        mTextHeight = mTextBounds.height();

        mDetailTextPaint.setAntiAlias(true);
        mDetailTextPaint.setTextSize(mDetailTextSize);
        mDetailTextPaint.setColor(mDetailTextColor);
        mDetailTextPaint.getTextBounds(mDetailText.toString(), 0, mDetailText.length(), mDetailTextBounds);
        mDetailTextHeight = mDetailTextBounds.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = measureWidth(widthMeasureSpec);
        int heightSize = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    private int measureHeight(int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int result = 0;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        }else {
            result = (int) (getPaddingTop() + getPaddingBottom() + getTextInnerPadding() +
                    mTextHeight + mDetailTextHeight);
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int result = 0;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int textLength = (int) Math.max(mTextPaint.measureText(mText.toString()),
                    mDetailTextPaint.measureText(mDetailText.toString()));
            result = getPaddingRight() + getPaddingLeft() + textLength;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x1 = getPaddingLeft();
        int y1 = (int) (getPaddingTop() + mTextHeight);
        int x2 = getPaddingLeft();
        int y2 = (int) (getPaddingTop() + mTextHeight + getTextInnerPadding() + mDetailTextHeight);
        if ((mGravity | Gravity.LEFT) == 1) {
            LogHelper.d(TAG, "gravity left");
            x1 = getPaddingLeft();
            x2 = getPaddingLeft();
        }
        if ((mGravity & Gravity.TOP) >> 4 == 1) {
            y1 = (int) (getPaddingTop() + mTextHeight);
            y2 = (int) (getPaddingTop() + mTextHeight + getTextInnerPadding() + mDetailTextHeight);

        }
        if ((mGravity & Gravity.RIGHT)>> 8 == 1) {
            x1 = getMeasuredWidth() - getPaddingRight() - mTextBounds.width();
            x2 = getMeasuredWidth() - getPaddingRight() - mDetailTextBounds.width();
        }
        if ((mGravity & Gravity.BOTTOM) >> 12 == 1) {
            y1 = (int) (getMeasuredHeight() - getPaddingBottom() - mDetailTextHeight - getTextInnerPadding());
            y2 = getMeasuredHeight() - getPaddingBottom();
        }
        if ((mGravity & Gravity.CENTER) >> 16 == 1) {
            x1 = (getMeasuredWidth() - mTextBounds.width()) / 2;
            y1 = (int) ((getMeasuredHeight() - mTextHeight - mDetailTextHeight - getTextInnerPadding()) / 2
                    + mTextHeight);
            x2 = (getMeasuredWidth() - mDetailTextBounds.width()) / 2;
            y2 = (int) (y1 + getTextInnerPadding() + mDetailTextHeight);
        }

        canvas.drawText(mText, 0, mText.length(), x1, y1, mTextPaint);
        canvas.drawText(mDetailText, 0, mDetailText.length(), x2, y2, mDetailTextPaint);
    }

    public float getTextInnerPadding() {
        return mTextInnerPadding;
    }

    public void setTextInnerPadding(float textInnerPadding) {
        mTextInnerPadding = textInnerPadding;
        postInvalidate();
    }


    public void setPaddingLeft(int paddingLeft) {
        mPaddingLeft = paddingLeft;
        postInvalidate();
    }


    public void setPaddingTop(int paddingTop) {
        mPaddingTop = paddingTop;
        postInvalidate();
    }



    public void setPaddingRight(int paddingRight) {
        mPaddingRight = paddingRight;
        postInvalidate();
    }


    public void setPaddingBottom(int paddingBottom) {
        mPaddingBottom = paddingBottom;
        postInvalidate();

    }

    public CharSequence getText() {
        return mText;
    }

    public void setText(CharSequence text) {
        mText = text;
        updatePaint();
        requestLayout();
    }

    public CharSequence getDetailText() {
        return mDetailText;
    }

    public void setDetailText(CharSequence detailText) {
        mDetailText = detailText;
        updatePaint();
        requestLayout();

    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        postInvalidate();

    }

    public int getDetailTextColor() {
        return mDetailTextColor;
    }

    public void setDetailTextColor(int detailTextColor) {
        mDetailTextColor = detailTextColor;
        mDetailTextPaint.setColor(mDetailTextColor);
        postInvalidate();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        updatePaint();
        requestLayout();
    }

    public float getDetailTextSize() {
        return mDetailTextSize;
    }

    public void setDetailTextSize(float detailTextSize) {
        mDetailTextSize = detailTextSize;
        updatePaint();
        postInvalidate();
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getDetailTextPaint() {
        return mDetailTextPaint;
    }
    static class Gravity{
        public static int CENTER = 0x10000;
        public static int LEFT = 0x0001;
        public static int TOP = 0x0010;
        public static int RIGHT = 0x0100;
        public static int BOTTOM = 0x1000;
        public static int LEFT_TOP = LEFT|TOP;
        public static int LEFT_BOTTOM = LEFT|BOTTOM;
        public static int RIGHT_TOP = RIGHT|TOP;
        public static int RIGHT_BOTTOM = RIGHT|BOTTOM;

    }
}
