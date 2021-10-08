package ru.liner.wallpapertheming.themer;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    private int accentColor;
    @ColorInt
    private int accentSecondaryColor;
    @ColorInt
    private int backgroundColor;

    @Override
    protected void onStart() {
        super.onStart();
        themeConfig = getThemeConfig();
        accentColor = themeConfig.getDefaultAccentAccentColor();
        accentSecondaryColor = ColorUtils.darkerColor(accentColor, .3f);
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


    private void getWallpaperColorsPreO(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if(Utils.isCurrentWallpaperLive(this, wallpaperManager.getWallpaperInfo())) {
                //TODO Live wallpaper is not supported!
                accentColor = themeConfig.getDefaultAccentAccentColor();
                accentSecondaryColor = ColorUtils.darkerColor(accentColor, .3f);
                backgroundColor = themeConfig.getDefaultBackgroundColor();
            } else {
                palette = Palette.from(ImageUtils.drawableToBitmap(wallpaperManager.getFastDrawable())).generate();
                accentColor = palette.getVibrantColor(themeConfig.getDefaultAccentAccentColor());
                accentSecondaryColor = ColorUtils.darkerColor(accentColor, .3f);
                backgroundColor = ColorUtils.darkerColor(palette.getDarkMutedColor(themeConfig.getDefaultBackgroundColor()), .8f);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4879);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    private void getWallpaperColors(){
        WallpaperColors wallpaperColors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
        if(wallpaperColors == null){
            //TODO Live wallpaper is not supported!
            accentColor = themeConfig.getDefaultAccentAccentColor();
            accentSecondaryColor = ColorUtils.darkerColor(accentColor, .3f);
            backgroundColor = themeConfig.getDefaultBackgroundColor();
        } else {
            accentColor = wallpaperColors.getSecondaryColor().toArgb();
            accentSecondaryColor = ColorUtils.darkerColor(accentColor, .3f);
            backgroundColor = ColorUtils.darkerColor(wallpaperColors.getTertiaryColor().toArgb(), .8f);
        }
    }

    private void applyTheme() {
        if (!wallpaperChanged)
            return;
        if (window == null)
            window = getWindow();
        if (wallpaperManager == null)
            wallpaperManager = WallpaperManager.getInstance(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            getWallpaperColors();
        } else {
            getWallpaperColorsPreO();
        }
        if (themeConfig.isAnimateColorChanges()) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), themeConfig.getDefaultAccentAccentColor(), accentColor);
            colorAnimation.setDuration(themeConfig.getAnimationDuration());
            colorAnimation.setStartDelay(250);
            colorAnimation.setInterpolator(new AccelerateInterpolator());
            colorAnimation.addUpdateListener(animator -> setThemeColors((Integer) animator.getAnimatedValue(), ColorUtils.darkerColor((Integer) animator.getAnimatedValue(), 0.3f), backgroundColor));
            colorAnimation.start();
        } else {
            setThemeColors(accentColor, accentSecondaryColor, backgroundColor);
        }
    }

    private void setThemeColors(@ColorInt int accentAccentColor, @ColorInt int accentAccentSecondaryColor, @ColorInt int backgroundColor) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setBackgroundDrawable(new ColorDrawable(accentAccentColor));
        window.setStatusBarColor(themeConfig.isColoredStatusBar() ? accentAccentSecondaryColor : Color.BLACK);
        window.setNavigationBarColor(themeConfig.isColoredNavigationBar() ? accentAccentColor : Color.BLACK);
        ViewGroup viewGroup = findViewById(themeConfig.getRootViewID());
        if (themeConfig.isChangeBackgroundColors())
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
                ((IThemer) child).applyThemeColors(accentColor, accentSecondaryColor, backgroundColor);
            } else if (themeConfig.isChangeStandardViewColors()) {
                ColorStateList colorStateList = ColorStateList.valueOf(accentColor);
                if (child instanceof MaterialButton) {
                    ((MaterialButton) child).setRippleColor(ColorStateList.valueOf(accentSecondaryColor));
                    child.setBackgroundTintList(colorStateList);
                    if (themeConfig.isChangeTextColors())
                        ((MaterialButton) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof Switch) {
                    int[][] states = new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked},
                    };
                    int[] thumbColors = new int[]{
                            Color.WHITE,
                            accentColor,
                    };
                    int[] trackColors = new int[]{
                            ColorUtils.lightenColor(backgroundColor, .3f),
                            accentSecondaryColor,
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
                    if (themeConfig.isChangeTextColors())
                        ((CheckBox) child).setTextColor(ColorUtils.isColorDark(backgroundColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof ProgressBar) {
                    ((ProgressBar) child).setProgressTintList(colorStateList);
                    ((ProgressBar) child).setSecondaryProgressTintList(colorStateList);
                    if (((ProgressBar) child).isIndeterminate()) {
                        ((ProgressBar) child).setIndeterminateTintList(colorStateList);
                    }
                } else if (child instanceof RadioButton) {
                    ((RadioButton) child).setButtonTintList(colorStateList);
                    if (themeConfig.isChangeTextColors())
                        ((RadioButton) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof AppCompatButton) {
                    child.setBackgroundTintList(colorStateList);
                    if (themeConfig.isChangeTextColors())
                        ((AppCompatButton) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof Button) {
                    child.setBackgroundTintList(colorStateList);
                    if (themeConfig.isChangeTextColors())
                        ((Button) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
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
