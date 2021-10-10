package ru.liner.wallpapertheming.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import ru.liner.wallpapertheming.themer.IThemer;

public class WPTextView extends AppCompatTextView implements IThemer {
    public WPTextView(@NonNull Context context) {
        super(context);
    }

    public WPTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void applyColors(int accentColor, int accentSecondaryColor, int backgroundColor) {
        setBackgroundColor(backgroundColor);
        setLinkTextColor(accentColor);
    }
}
