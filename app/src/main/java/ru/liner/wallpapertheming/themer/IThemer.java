package ru.liner.wallpapertheming.themer;

import androidx.annotation.ColorInt;

public interface IThemer {
    void applyColors(@ColorInt int accentColor, @ColorInt int accentSecondaryColor, @ColorInt int backgroundColor);
}
