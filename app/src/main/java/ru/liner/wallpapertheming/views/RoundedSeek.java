package ru.liner.wallpapertheming.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;

import ru.liner.wallpapertheming.R;

public class RoundedSeek extends BaseView {
    private FrameLayout content;
    private TextView roundedSeekName;
    private AppCompatSeekBar roundedSeek;
    private TextView roundedSeekValue;
    private String seekText;
    private String seekValue;
    private SeekCallback seekCallback;

    public RoundedSeek(Context context) {
        this(context, null);
    }

    public RoundedSeek(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreate() {
        content = findViewById(R.id.roundContent);
        roundedSeekName = findViewById(R.id.roundedSeekName);
        roundedSeek = findViewById(R.id.roundedSeek);
        roundedSeekValue = findViewById(R.id.roundedSeekValue);
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RoundedSeek);
            seekText = typedArray.getString(R.styleable.RoundedSeek_seekText);
            seekValue = typedArray.getString(R.styleable.RoundedSeek_seekValueFormat);
            typedArray.recycle();
        } else {
            seekText = "Seek";
            seekValue = "%s";
        }
        roundedSeekName.setText(seekText);
        roundedSeekValue.setText(String.format(seekValue, roundedSeek.getProgress()));
        roundedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekCallback != null){
                    seekCallback.onSeek(roundedSeek, i,b);
                }
                roundedSeekValue.setText(String.format(seekValue, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekCallback != null){
                    seekCallback.onFinish(roundedSeek, roundedSeek.getProgress());
                }
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rounded_seek_layout;
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
        roundedSeekValue.setTextColor(accentColor);
    }

    public void setSwitchCallback(SeekCallback seekCallback) {
        this.seekCallback = seekCallback;
    }


    public interface SeekCallback{
        void onSeek(AppCompatSeekBar seekBar, int value, boolean fromUser);
        void onFinish(AppCompatSeekBar seekBar, int value);
    }
}
