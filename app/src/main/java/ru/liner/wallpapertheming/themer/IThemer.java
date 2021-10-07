package ru.liner.wallpapertheming.themer;

import androidx.annotation.ColorInt;

public interface IThemer {
    void applyThemeColors(@ColorInt int accentAccentColor, @ColorInt int accentAccentSecondaryColor, @ColorInt int backgroundColor);
}
