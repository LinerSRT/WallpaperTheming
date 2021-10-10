package ru.liner.wallpapertheming.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import ru.liner.wallpapertheming.themer.IThemer;

public abstract class BaseView extends FrameLayout implements IThemer {
    protected Context context;
    protected LayoutInflater inflater;
    @Nullable
    protected AttributeSet attributeSet;

    public BaseView(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        onStart();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.attributeSet = attrs;
        onStart();
    }

    protected void onStart() {
        inflater.inflate(getLayoutRes(), this);
        onCreate();
    }

    protected void onCreate() {

    }

    @LayoutRes
    public abstract int getLayoutRes();


    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public int pxToSp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public int pxToSp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public int dpToSp(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public int dpToSp(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().scaledDensity);
    }

    public void setMargins(int left, int top, int right, int bottom) {
        if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) getLayoutParams();
            p.setMargins(left, top, right, bottom);
            requestLayout();
        }
    }

}