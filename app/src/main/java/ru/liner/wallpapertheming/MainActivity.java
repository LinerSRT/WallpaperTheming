package ru.liner.wallpapertheming;

import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import ru.liner.wallpapertheming.themer.ThemeConfig;
import ru.liner.wallpapertheming.themer.ThemedActivity;
import ru.liner.wallpapertheming.views.RoundedSeek;
import ru.liner.wallpapertheming.views.RoundedSwitch;

public class MainActivity extends ThemedActivity {
    private ThemeConfig themeConfig;
    private RoundedSwitch useSecondColorSwitch;
    private RoundedSeek animationDurationSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useSecondColorSwitch = findViewById(R.id.useSecondColorSwitch);
        animationDurationSeek = findViewById(R.id.animationDurationSeek);
        useSecondColorSwitch.setSwitchCallback((switchCompat, checked) -> {
            if(themeConfig != null) {
                themeConfig.setUseSecondAccentColor(checked);
                deviceWallpaperManager.requestColorsForce();
            }
        });
        animationDurationSeek.setSwitchCallback(new RoundedSeek.SeekCallback() {
            @Override
            public void onSeek(AppCompatSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onFinish(AppCompatSeekBar seekBar, int value) {
                if(themeConfig != null) {
                    themeConfig.setAnimationDuration(value);
                    deviceWallpaperManager.requestColorsForce();
                }
            }
        });
    }

    @Override
    public ThemeConfig getThemeConfig() {
        return themeConfig == null ? themeConfig = new ThemeConfig.Builder(R.id.mainLayout)
                .setAnimationDuration(500)
                .setAnimationInterpolator(new AccelerateInterpolator())
                .setUseSecondAccentColor(true)
                .build() : themeConfig;
    }

    @Override
    public int getAccentColor() {
        return 0;
    }

    @Override
    public int getAccentDarkColor() {
        return 0;
    }
}