package ru.liner.wallpapertheming;

import android.os.Bundle;

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
                .setChangeActivityBackgroundColor(false)
                .setAnimationDuration(2000)
                .build();
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