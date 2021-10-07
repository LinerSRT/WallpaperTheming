package ru.liner.wallpapertheming;

import android.os.Bundle;
import android.widget.TextView;

import ru.liner.wallpapertheming.themer.ThemeConfig;
import ru.liner.wallpapertheming.themer.ThemedActivity;

public class MainActivity extends ThemedActivity {
    private TextView accentColor;
    private TextView accentSecondColor;
    private TextView backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accentColor = findViewById(R.id.accentColor);
        accentSecondColor = findViewById(R.id.accentSecondColor);
        backgroundColor = findViewById(R.id.backgroundColor);
    }

    @Override
    public ThemeConfig getThemeConfig() {
        return new ThemeConfig.Builder(R.id.mainLayout)
                .setAnimateColorChanges(false)
                .setColoredNavigationBar(true)
                .setColoredNavigationBar(true)
                .build();
    }
}