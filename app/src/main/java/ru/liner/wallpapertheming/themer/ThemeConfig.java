package ru.liner.wallpapertheming.themer;

import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;

public class ThemeConfig {
    @IdRes
    private final int rootViewID;
    private  boolean useSecondAccentColor;
    private  boolean animateApplyingColors;
    private  boolean changeSystemBars;
    private  boolean changeTextColor;
    private  boolean changeAndroidViews;
    private  boolean changeActivityBackgroundColor;
    private  Interpolator animationInterpolator;
    private  long animationDuration;

    private ThemeConfig(Builder builder) {
        this.rootViewID = builder.rootViewID;
        this.useSecondAccentColor = builder.useSecondAccentColor;
        this.animateApplyingColors = builder.animateApplyingColors;
        this.changeSystemBars = builder.changeSystemBars;
        this.changeTextColor = builder.changeTextColor;
        this.changeAndroidViews = builder.changeAndroidViews;
        this.changeActivityBackgroundColor = builder.changeActivityBackgroundColor;
        this.animationInterpolator = builder.animationInterpolator;
        this.animationDuration = builder.animationDuration;
    }

    public int getRootViewID() {
        return rootViewID;
    }

    public boolean isUseSecondAccentColor() {
        return useSecondAccentColor;
    }

    public boolean isAnimateApplyingColors() {
        return animateApplyingColors;
    }

    public boolean isChangeSystemBars() {
        return changeSystemBars;
    }

    public boolean isChangeTextColor() {
        return changeTextColor;
    }

    public boolean isChangeAndroidViews() {
        return changeAndroidViews;
    }

    public boolean isChangeActivityBackgroundColor() {
        return changeActivityBackgroundColor;
    }

    public Interpolator getAnimationInterpolator() {
        return animationInterpolator;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public void setUseSecondAccentColor(boolean useSecondAccentColor) {
        this.useSecondAccentColor = useSecondAccentColor;
    }

    public void setAnimateApplyingColors(boolean animateApplyingColors) {
        this.animateApplyingColors = animateApplyingColors;
    }

    public void setChangeSystemBars(boolean changeSystemBars) {
        this.changeSystemBars = changeSystemBars;
    }

    public void setChangeTextColor(boolean changeTextColor) {
        this.changeTextColor = changeTextColor;
    }

    public void setChangeAndroidViews(boolean changeAndroidViews) {
        this.changeAndroidViews = changeAndroidViews;
    }

    public void setChangeActivityBackgroundColor(boolean changeActivityBackgroundColor) {
        this.changeActivityBackgroundColor = changeActivityBackgroundColor;
    }

    public void setAnimationInterpolator(Interpolator animationInterpolator) {
        this.animationInterpolator = animationInterpolator;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public static class Builder{
        @IdRes
        private final int rootViewID;
        private boolean useSecondAccentColor;
        private boolean animateApplyingColors;
        private boolean changeSystemBars;
        private boolean changeTextColor;
        private boolean changeAndroidViews;
        private boolean changeActivityBackgroundColor;
        private Interpolator animationInterpolator;
        private long animationDuration;


        public Builder(@IdRes int rootViewID) {
            this.rootViewID = rootViewID;
            this.useSecondAccentColor = true;
            this.animateApplyingColors = true;
            this.changeSystemBars = true;
            this.changeTextColor = false;
            this.changeAndroidViews = true;
            this.changeActivityBackgroundColor = false;
            this.animationInterpolator = new DecelerateInterpolator();
            this.animationDuration = 400;
        }

        public Builder setUseSecondAccentColor(boolean useSecondAccentColor) {
            this.useSecondAccentColor = useSecondAccentColor;
            return this;
        }

        public Builder setAnimateApplyingColors(boolean animateApplyingColors) {
            this.animateApplyingColors = animateApplyingColors;
            return this;
        }

        public Builder setChangeSystemBars(boolean changeSystemBars) {
            this.changeSystemBars = changeSystemBars;
            return this;
        }

        public Builder setChangeTextColor(boolean changeTextColor) {
            this.changeTextColor = changeTextColor;
            return this;
        }

        public Builder setChangeAndroidViews(boolean changeAndroidViews) {
            this.changeAndroidViews = changeAndroidViews;
            return this;
        }

        public Builder setChangeActivityBackgroundColor(boolean changeActivityBackgroundColor) {
            this.changeActivityBackgroundColor = changeActivityBackgroundColor;
            return this;
        }

        public Builder setAnimationInterpolator(Interpolator animationInterpolator) {
            this.animationInterpolator = animationInterpolator;
            return this;
        }

        public Builder setAnimationDuration(long animationDuration) {
            this.animationDuration = animationDuration;
            return this;
        }

        public ThemeConfig build(){
            return new ThemeConfig(this);
        }
    }
}
