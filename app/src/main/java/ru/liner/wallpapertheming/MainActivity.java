package ru.liner.wallpapertheming;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import ru.liner.wallpapertheming.themer.ThemeConfig;
import ru.liner.wallpapertheming.themer.ThemedActivity;

public class MainActivity extends ThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public ThemeConfig getThemeConfig() {
        return new ThemeConfig.Builder(R.id.mainLayout)
                .setAnimateColorChanges(false)
                .setColoredNavigationBar(true)
                .setColoredNavigationBar(true)
                .setChangeBackgroundColors(false)
                .setChangeTextColors(false)
                .setAnimateColorChanges(true)
                .setAnimationDuration(1000)
                .setDefaultAccentColor(ContextCompat.getColor(this, R.color.design_default_color_primary))
                .build();
    }
}