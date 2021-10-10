package ru.liner.wallpapertheming.themer;

import androidx.annotation.ColorInt;

import java.util.Objects;

import ru.liner.wallpapertheming.utils.ColorUtils;

public class WallpaperColors {
    @ColorInt
    private final int accentColor;
    @ColorInt
    private final int accentColorDark;
    @ColorInt
    private final int accentSecondColor;
    @ColorInt
    private final int accentSecondColorDark;
    @ColorInt
    private final int backgroundColor;

    public WallpaperColors(int accentColor, int accentColorDark, int accentSecondColor, int accentSecondColorDark, int backgroundColor) {
        this.accentColor = accentColor;
        this.accentColorDark = accentColorDark;
        this.accentSecondColor = accentSecondColor;
        this.accentSecondColorDark = accentSecondColorDark;
        this.backgroundColor = backgroundColor;
    }
    public WallpaperColors() {
        this.accentColor = 0;
        this.accentColorDark = 0;
        this.accentSecondColor = 0;
        this.accentSecondColorDark = 0;
        this.backgroundColor = 0;
    }

    public int getAccentColor() {
        return accentColor;
    }
    public int getAccentSecondColor() {
        return accentSecondColor;
    }

    public int getAccentColorDark() {
        return ColorUtils.darkerColor(accentColor, .3f);
    }

    public int getAccentSecondColorDark() {
        return ColorUtils.darkerColor(accentSecondColor, .3f);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isChanged(WallpaperColors other){
        if(accentColor != other.accentColor)
            return true;
        if(accentColorDark != other.accentColorDark)
            return true;
        if(accentSecondColor != other.accentSecondColor)
            return true;
        if(accentSecondColorDark != other.accentSecondColorDark)
            return true;
        return backgroundColor != other.backgroundColor;
    }

    @Override
    public String toString() {
        return "WallpaperColors{" +
                "accentColor=" + accentColor +
                ", accentColorDark=" + accentColorDark +
                ", accentSecondColorDark=" + accentSecondColorDark +
                ", backgroundColor=" + backgroundColor +
                '}';
    }
}
