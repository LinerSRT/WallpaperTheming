package ru.liner.wallpapertheming.wt;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

public class ColorData {
    @ColorInt
    private final int primaryColor;
    @ColorInt
    private final int secondaryColor;

    public ColorData(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @ColorInt
    public int getPrimaryColor() {
        return primaryColor;
    }

    @ColorInt
    public int getPrimaryColorDarkVariant() {
        return ColorUtils.blendARGB(primaryColor, Color.BLACK, .3f);
    }

    @ColorInt
    public int getSecondaryColor() {
        return secondaryColor;
    }

    @ColorInt
    public int getSecondaryColorDarkVariant() {
        return ColorUtils.blendARGB(secondaryColor, Color.BLACK, .3f);
    }

    public boolean isChanged(ColorData other){
        if(primaryColor != other.primaryColor)
            return true;
        return secondaryColor != other.secondaryColor;
    }

    @Override
    public String toString() {
        return "ColorData{" +
                "primaryColor=" + primaryColor +
                ", secondaryColor=" + secondaryColor +
                '}';
    }
}
