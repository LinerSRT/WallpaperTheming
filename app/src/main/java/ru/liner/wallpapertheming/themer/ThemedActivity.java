package ru.liner.wallpapertheming.themer;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import ru.liner.wallpapertheming.utils.ColorUtils;

@SuppressWarnings("deprecation")
public abstract class ThemedActivity extends AppCompatActivity implements DeviceWallpaperManager.ICallback {
    protected ThemeConfig themeConfig;
    protected Window window;
    protected DeviceWallpaperManager deviceWallpaperManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        window = getWindow();
        themeConfig = getThemeConfig();
        deviceWallpaperManager = new DeviceWallpaperManager(this, this);
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            deviceWallpaperManager.requestColors();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4879);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceWallpaperManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceWallpaperManager.requestColors();
    }

    public void applyColorsFromActivity(@ColorInt int accentColor, @ColorInt int accentColorDark, @ColorInt int backgroundColor) {

    }


    private void applyColors(WallpaperColors wallpaperColors) {
        @ColorInt int accentColor = themeConfig.isUseSecondAccentColor() ? wallpaperColors.getAccentSecondColor() : wallpaperColors.getAccentColor();
        @ColorInt int accentColorDark = themeConfig.isUseSecondAccentColor() ? wallpaperColors.getAccentSecondColorDark() : wallpaperColors.getAccentColorDark();
        @ColorInt int backgroundColor = wallpaperColors.getBackgroundColor();
        applyColors(accentColor, accentColorDark, backgroundColor);
    }

    private void applyColors(@ColorInt int accentColor, @ColorInt int accentColorDark, @ColorInt int backgroundColor) {
        applyColorsFromActivity(accentColor, accentColorDark, backgroundColor);
        if (themeConfig.isChangeSystemBars()) {
            window.setNavigationBarColor(accentColorDark);
            window.setStatusBarColor(accentColorDark);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setBackgroundDrawable(new ColorDrawable(accentColor));
        ViewGroup viewGroup = findViewById(themeConfig.getRootViewID());
        if (themeConfig.isChangeActivityBackgroundColor())
            viewGroup.setBackgroundColor(backgroundColor);
        iterateViews(viewGroup, accentColor, accentColorDark, backgroundColor);
    }

    @Override
    public void onWallpaperColorsChanged(WallpaperColors wallpaperColors) {
        if (window == null)
            window = getWindow();
        if (themeConfig.isAnimateApplyingColors()) {
            @ColorInt int accentColor = themeConfig.isUseSecondAccentColor() ? wallpaperColors.getAccentSecondColor() : wallpaperColors.getAccentColor();
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), deviceWallpaperManager.getCurrentColors() == null ? getAccentColor() : !themeConfig.isUseSecondAccentColor() ? deviceWallpaperManager.getCurrentColors().getAccentSecondColor() : deviceWallpaperManager.getCurrentColors().getAccentColor(), accentColor);
            colorAnimation.setDuration(themeConfig.getAnimationDuration());
            colorAnimation.setStartDelay(250);
            colorAnimation.setInterpolator(themeConfig.getAnimationInterpolator());
            colorAnimation.addUpdateListener(valueAnimator -> {
                int color = (int) valueAnimator.getAnimatedValue();
                applyColors(color, ColorUtils.darkerColor(color, .3f), wallpaperColors.getBackgroundColor());
            });
            colorAnimation.start();
        } else {
            applyColors(wallpaperColors);
        }
    }

    private void iterateViews(ViewGroup view, @ColorInt int accentColor, @ColorInt int accentDarkColor, @ColorInt int backgroundColor) {
        if (view == null)
            return;
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);
            if (child instanceof IThemer) {
                if (child instanceof ViewGroup) {
                    iterateViews((ViewGroup) child, accentColor, accentDarkColor, backgroundColor);
                }
                ((IThemer) child).applyColors(accentColor, accentDarkColor, backgroundColor);
            } else if (child instanceof RecyclerView) {
                for (int v = 0; v < ((RecyclerView) child).getChildCount(); v++) {
                    RecyclerView.ViewHolder holder = ((RecyclerView) child).getChildViewHolder(((RecyclerView) child).getChildAt(v));
                    iterateViews((ViewGroup) holder.itemView, accentColor, accentDarkColor, backgroundColor);
                }
            } else if (child instanceof ViewGroup) {
                iterateViews((ViewGroup) child, accentColor, accentDarkColor, backgroundColor);
            } else if (themeConfig.isChangeAndroidViews()) {
                ColorStateList colorStateList = ColorStateList.valueOf(accentColor);
                if (child instanceof MaterialButton) {
                    ((MaterialButton) child).setRippleColor(ColorStateList.valueOf(accentDarkColor));
                    child.setBackgroundTintList(colorStateList);
                    if (themeConfig.isChangeTextColor())
                        ((MaterialButton) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof SwitchCompat) {
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
                            accentDarkColor,
                    };
                    ((SwitchCompat) child).setTrackTintList(new ColorStateList(states, trackColors));
                    ((SwitchCompat) child).setThumbTintList(new ColorStateList(states, thumbColors));
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
                            accentDarkColor,
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
                    if (themeConfig.isChangeTextColor())
                        ((CheckBox) child).setTextColor(ColorUtils.isColorDark(backgroundColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof ProgressBar) {
                    ((ProgressBar) child).setProgressTintList(colorStateList);
                    ((ProgressBar) child).setSecondaryProgressTintList(colorStateList);
                    if (((ProgressBar) child).isIndeterminate()) {
                        ((ProgressBar) child).setIndeterminateTintList(colorStateList);
                    }
                } else if (child instanceof RadioButton) {
                    ((RadioButton) child).setButtonTintList(colorStateList);
                    if (themeConfig.isChangeTextColor())
                        ((RadioButton) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof AppCompatButton) {
                    child.setBackgroundTintList(colorStateList);
                    if (themeConfig.isChangeTextColor())
                        ((AppCompatButton) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof Button) {
                    child.setBackgroundTintList(colorStateList);
                    if (themeConfig.isChangeTextColor())
                        ((Button) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                } else if (child instanceof TextView) {
                    if (!themeConfig.isChangeTextColor())
                        continue;
                    ((TextView) child).setTextColor(ColorUtils.isColorDark(accentColor) ? Color.WHITE : Color.BLACK);
                    ((TextView) child).setHighlightColor(accentColor);
                    ((TextView) child).setLinkTextColor(accentColor);
                }
            }
        }
    }

    public abstract ThemeConfig getThemeConfig();

    @ColorInt
    public abstract int getAccentColor();

    @ColorInt
    public abstract int getAccentDarkColor();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4879) {
            if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceWallpaperManager.requestColors();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
