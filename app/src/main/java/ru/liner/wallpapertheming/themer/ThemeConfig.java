package ru.liner.wallpapertheming.themer;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;

public class ThemeConfig {
    @IdRes
    private final int rootViewID;
    @ColorInt
    private final int defaultAccentAccentColor;
    @ColorInt
    private final int defaultBackgroundColor;
    private final long animationDuration;
    private final boolean coloredStatusBar;
    private final boolean coloredNavigationBar;
    private final boolean animateColorChanges;

    private ThemeConfig(Builder builder) {
        this.rootViewID = builder.rootViewID;
        this.defaultAccentAccentColor = builder.defaultAccentAccentColor;
        this.defaultBackgroundColor = builder.defaultBackgroundColor;
        this.animationDuration = builder.animationDuration;
        this.coloredStatusBar = builder.coloredStatusBar;
        this.coloredNavigationBar = builder.coloredNavigationBar;
        this.animateColorChanges = builder.animateColorChanges;
    }

    public int getRootViewID() {
        return rootViewID;
    }

    public int getDefaultAccentAccentColor() {
        return defaultAccentAccentColor;
    }

    public int getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public boolean isColoredStatusBar() {
        return coloredStatusBar;
    }

    public boolean isColoredNavigationBar() {
        return coloredNavigationBar;
    }

    public boolean isAnimateColorChanges() {
        return animateColorChanges;
    }

    public static class Builder{
        @IdRes
        private final int rootViewID;
        @ColorInt
        private int defaultAccentAccentColor = Color.BLACK;
        @ColorInt
        private int defaultBackgroundColor = Color.BLACK;
        private long animationDuration = 400;
        private boolean coloredStatusBar = true;
        private boolean coloredNavigationBar = true;
        private boolean animateColorChanges = false;

        public Builder(@IdRes int rootViewID) {
            this.rootViewID = rootViewID;
        }

        public Builder setDefaultAccentAccentColor(int defaultAccentAccentColor) {
            this.defaultAccentAccentColor = defaultAccentAccentColor;
            return this;
        }

        public Builder setDefaultBackgroundColor(int defaultBackgroundColor) {
            this.defaultBackgroundColor = defaultBackgroundColor;
            return this;
        }

        public Builder setAnimationDuration(long animationDuration) {
            this.animationDuration = animationDuration;
            return this;
        }

        public Builder setColoredStatusBar(boolean coloredStatusBar) {
            this.coloredStatusBar = coloredStatusBar;
            return this;
        }

        public Builder setColoredNavigationBar(boolean coloredNavigationBar) {
            this.coloredNavigationBar = coloredNavigationBar;
            return this;
        }

        public Builder setAnimateColorChanges(boolean animateColorChanges) {
            this.animateColorChanges = animateColorChanges;
            return this;
        }

        public ThemeConfig build(){
            return new ThemeConfig(this);
        }
    }
}
