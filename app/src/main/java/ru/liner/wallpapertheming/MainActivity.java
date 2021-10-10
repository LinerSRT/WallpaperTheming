package ru.liner.wallpapertheming;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.button.MaterialButton;

import ru.liner.wallpapertheming.themer.ThemeConfig;
import ru.liner.wallpapertheming.themer.ThemedActivity;
import ru.liner.wallpapertheming.themer.WallpaperColors;
import ru.liner.wallpapertheming.views.RoundedSeek;
import ru.liner.wallpapertheming.views.RoundedSwitch;

public class MainActivity extends ThemedActivity {
    private ThemeConfig themeConfig;
    private RoundedSwitch useSecondColorSwitch;
    private RoundedSwitch useTextColorSwitch;
    private RoundedSeek animationDurationSeek;
    private MaterialButton refreshColors;
    private ImageView wallpaperView;
    private CardView wallpaperCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useSecondColorSwitch = findViewById(R.id.useSecondColorSwitch);
        useTextColorSwitch = findViewById(R.id.useTextColorSwitch);
        animationDurationSeek = findViewById(R.id.animationDurationSeek);
        refreshColors = findViewById(R.id.refreshColors);
        wallpaperView = findViewById(R.id.wallpaperView);
        wallpaperCard = findViewById(R.id.wallpaperCard);
        useSecondColorSwitch.setSwitchCallback((switchCompat, checked) -> {
            if (themeConfig != null) {
                themeConfig.setUseSecondAccentColor(checked);
            }
        });
        useTextColorSwitch.setSwitchCallback((switchCompat, checked) -> {
            if (themeConfig != null) {
                themeConfig.setChangeTextColor(checked);
            }
        });
        animationDurationSeek.setSwitchCallback(new RoundedSeek.SeekCallback() {
            @Override
            public void onSeek(AppCompatSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onFinish(AppCompatSeekBar seekBar, int value) {
                if (themeConfig != null) {
                    themeConfig.setAnimationDuration(value);
                }
            }
        });
        wallpaperView.setImageDrawable(deviceWallpaperManager.getWallpaperDrawable());
        refreshColors.setOnClickListener(view -> deviceWallpaperManager.requestColorsForce());
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

    @Override
    public void onWallpaperColorsChanged(WallpaperColors wallpaperColors) {
        if (wallpaperView != null)
            wallpaperView.setImageDrawable(deviceWallpaperManager.getWallpaperDrawable());
        super.onWallpaperColorsChanged(wallpaperColors);
    }

    @Override
    public void applyColorsFromActivity(int accentColor, int accentColorDark, int backgroundColor) {
        DrawableCompat.setTint(
                DrawableCompat.wrap(wallpaperCard.getBackground()),
                accentColor
        );
    }
}