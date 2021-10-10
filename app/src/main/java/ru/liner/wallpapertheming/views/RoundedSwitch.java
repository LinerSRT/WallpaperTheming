package ru.liner.wallpapertheming.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import ru.liner.wallpapertheming.R;

public class RoundedSwitch extends BaseView {
    private FrameLayout content;
    private TextView textView;
    private SwitchCompat switchCompat;
    private String switchName;
    private SwitchCallback switchCallback;

    public RoundedSwitch(Context context) {
        this(context, null);
    }

    public RoundedSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreate() {
        content = findViewById(R.id.roundContent);
        textView = findViewById(R.id.roundedSwitchText);
        switchCompat = findViewById(R.id.roundedSwitch);
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RoundedSwitch);
            switchName = typedArray.getString(R.styleable.RoundedSwitch_switchText);
            typedArray.recycle();
        } else {
            switchName = "Switch name";
        }
        textView.setText(switchName);
        switchCompat.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(switchCallback != null)
                switchCallback.onChecked(switchCompat, checked);
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rounded_swtich_layout;
    }

    @Override
    public void applyColors(@ColorInt int accentColor, @ColorInt int accentDarkColor, @ColorInt int backgroundColor) {
        GradientDrawable backgroundGradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        accentColor, Color.TRANSPARENT
                }

        );
        backgroundGradient.setCornerRadius(dpToPx(8));
        content.setBackground(backgroundGradient);
    }

    public void setSwitchCallback(SwitchCallback switchCallback) {
        this.switchCallback = switchCallback;
    }

    public void setSwitchText(String text) {
        this.switchName = text;
        textView.setText(switchName);
    }

    public interface SwitchCallback{
        void onChecked(SwitchCompat switchCompat, boolean checked);
    }
}
