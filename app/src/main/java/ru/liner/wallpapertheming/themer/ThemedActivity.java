package ru.liner.wallpapertheming.themer;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import ru.liner.wallpapertheming.utils.Broadcast;
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
        accentAccentSecondaryColor = ColorUtils.blendARGB(themeConfig.getDefaultAccentAccentColor(), Color.BLACK, 0.2f);
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
            accentAccentSecondaryColor = ColorUtils.blendARGB(accentAccentColor, Color.BLACK, 0.2f);
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
            } else if (child instanceof Button) {
                //TODO Add Button default implementation
            } else {
                //TODO Add other components
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
