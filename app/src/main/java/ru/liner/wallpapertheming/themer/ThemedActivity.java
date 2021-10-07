package ru.liner.wallpapertheming.themer;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.google.android.material.button.MaterialButton;

import ru.liner.wallpapertheming.utils.Broadcast;
import ru.liner.wallpapertheming.utils.ColorUtils;
import ru.liner.wallpapertheming.utils.ImageUtils;

@SuppressWarnings("deprecation")
public abstract class ThemedActivity extends AppCompatActivity {
    protected WallpaperManager wallpaperManager;
    protected ThemeConfig themeConfig;
    protected Broadcast wallpaperListener;
    protected Window window;
    protected Palette palette;
    protected boolean wallpaperChanged;
    @ColorInt
    private int accentAccentColor;
    @ColorInt
    private int accentAccentSecondaryColor;
    @ColorInt
    private int backgroundColor;

    @Override
    protected void onStart() {
        super.onStart();
        themeConfig = getThemeConfig();
        accentAccentColor = themeConfig.getDefaultAccentAccentColor();
        accentAccentSecondaryColor = ColorUtils.darkerColor(accentAccentColor, .3f);
        backgroundColor = themeConfig.getDefaultBackgroundColor();
        wallpaperChanged = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        wallpaperListener = new Broadcast(this, Intent.ACTION_WALLPAPER_CHANGED) {
            @Override
            public void handleChanged(Intent intent) {
                wallpaperChanged = true;
            }
        };
        wallpaperListener.setListening(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wallpaperListener.setListening(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyTheme();
    }

    private void applyTheme() {
        if (!wallpaperChanged)
            return;
        if (window == null)
            window = getWindow();
        if (wallpaperManager == null)
            wallpaperManager = WallpaperManager.getInstance(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            palette = Palette.from(ImageUtils.drawableToBitmap(wallpaperManager.getDrawable())).generate();
            accentAccentColor = palette.getVibrantColor(themeConfig.getDefaultAccentAccentColor());
            accentAccentSecondaryColor = ColorUtils.darkerColor(accentAccentColor, .3f);
            backgroundColor = palette.getDarkMutedColor(themeConfig.getDefaultBackgroundColor());
            if (themeConfig.isAnimateColorChanges()) {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, accentAccentColor);
                colorAnimation.setDuration(themeConfig.getAnimationDuration());
                colorAnimation.setStartDelay(250);
                colorAnimation.setInterpolator(new AnticipateOvershootInterpolator());
                colorAnimation.addUpdateListener(animator -> setThemeColors(accentAccentColor, accentAccentSecondaryColor, backgroundColor));
                colorAnimation.start();
            } else {
                setThemeColors(accentAccentColor, accentAccentSecondaryColor, backgroundColor);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4879);
        }
    }

    private void setThemeColors(@ColorInt int accentAccentColor, @ColorInt int accentAccentSecondaryColor, @ColorInt int backgroundColor) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setBackgroundDrawable(new ColorDrawable(accentAccentColor));
        window.setStatusBarColor(themeConfig.isColoredStatusBar() ? accentAccentSecondaryColor : Color.BLACK);
        window.setNavigationBarColor(themeConfig.isColoredNavigationBar() ? accentAccentColor : Color.BLACK);
        ViewGroup viewGroup = findViewById(themeConfig.getRootViewID());
        viewGroup.setBackgroundColor(backgroundColor);
        iterateViews(viewGroup);
    }

    private void iterateViews(ViewGroup view) {
        if (view == null)
            return;
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof ViewGroup) {
                iterateViews((ViewGroup) child);
            } else if (child instanceof IThemer) {
                ((IThemer) child).applyThemeColors(accentAccentColor, accentAccentSecondaryColor, backgroundColor);
            } else {
                ColorStateList colorStateList = ColorStateList.valueOf(accentAccentColor);
                if (child instanceof MaterialButton) {
                    ((MaterialButton) child).setRippleColor(ColorStateList.valueOf(accentAccentSecondaryColor));
                    child.setBackgroundTintList(colorStateList);
                    ((MaterialButton) child).setTextColor(ColorUtils.isColorDark(accentAccentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof Switch) {
                    int[][] states = new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked},
                    };
                    int[] thumbColors = new int[]{
                            Color.WHITE,
                            accentAccentColor,
                    };
                    int[] trackColors = new int[]{
                            ColorUtils.lightenColor(backgroundColor, .3f),
                            accentAccentSecondaryColor,
                    };
                    ((Switch) child).setTrackTintList(new ColorStateList(states, trackColors));
                    ((Switch) child).setThumbTintList(new ColorStateList(states, thumbColors));
                } else if (child instanceof AppCompatSeekBar) {
                    ((AppCompatSeekBar) child).setThumbTintList(colorStateList);
                    ((AppCompatSeekBar) child).setProgressTintList(colorStateList);
                } else if (child instanceof SeekBar) {
                    ((SeekBar) child).setThumbTintList(colorStateList);
                    ((SeekBar) child).setProgressTintList(colorStateList);
                } else if (child instanceof CheckBox) {
                    ((CheckBox) child).setButtonTintList(colorStateList);
                    ((CheckBox) child).setTextColor(ColorUtils.isColorDark(backgroundColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof ProgressBar) {
                    ((ProgressBar) child).setProgressTintList(colorStateList);
                    ((ProgressBar) child).setSecondaryProgressTintList(colorStateList);
                    if (((ProgressBar) child).isIndeterminate()) {
                        ((ProgressBar) child).setIndeterminateTintList(colorStateList);
                    }
                } else if (child instanceof RadioButton) {
                    ((RadioButton) child).setButtonTintList(colorStateList);
                    ((RadioButton) child).setTextColor(ColorUtils.isColorDark(accentAccentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof AppCompatButton) {
                    child.setBackgroundTintList(colorStateList);
                    ((AppCompatButton) child).setTextColor(ColorUtils.isColorDark(accentAccentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof Button) {
                    child.setBackgroundTintList(colorStateList);
                    ((Button) child).setTextColor(ColorUtils.isColorDark(accentAccentColor) ? Color.WHITE : Color.BLACK);
                }
            }
        }
    }

    public abstract ThemeConfig getThemeConfig();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4879) {
            if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    applyTheme();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
