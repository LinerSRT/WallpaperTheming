package ru.liner.wallpapertheming.themer;

import androidx.annotation.ColorInt;

public interface IThemer {
    void applyColors(@ColorInt int accentColor, @ColorInt int accentDarkColor, @ColorInt int backgroundColor);
}
