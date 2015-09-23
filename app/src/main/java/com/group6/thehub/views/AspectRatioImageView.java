package com.group6.thehub.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.group6.thehub.R;

/**
 * Created by Sathwik on 23-Sep-15.
 */
public class AspectRatioImageView extends ImageView {

    private int ratioX;

    private int ratioY;

    public int getRatioX() {
        return ratioX;
    }

    public void setRatioX(int ratioX) {
        this.ratioX = ratioX;
    }

    public int getRatioY() {
        return ratioY;
    }

    public void setRatioY(int ratioY) {
        this.ratioY = ratioY;
    }

    public AspectRatioImageView(Context context) {
        this(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        this(context,attrs,0);

    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView, 0, 0);
        try {
            setRatioX(typedArray.getInt(R.styleable.AspectRatioImageView_ratioX, 0));
            setRatioY(typedArray.getInt(R.styleable.AspectRatioImageView_ratioY, 0));
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int ratioX = getRatioX();
        int ratioY = getRatioY();
        int height = (width*ratioY)/ratioX;

        setMeasuredDimension(width, height);
    }
}
