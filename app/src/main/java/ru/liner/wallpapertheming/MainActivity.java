package ru.liner.wallpapertheming;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import ru.liner.wallpapertheming.recycler.MyData;
import ru.liner.wallpapertheming.recycler.RecyclerAdapter;
import ru.liner.wallpapertheming.wt.ColorData;
import ru.liner.wallpapertheming.themer.ThemeConfig;
import ru.liner.wallpapertheming.themer.ThemedActivity;
import ru.liner.wallpapertheming.wt.WT;
import ru.liner.wallpapertheming.themer.WallpaperColors;
import ru.liner.wallpapertheming.wt.WallpaperInterface;
import ru.liner.wallpapertheming.views.RoundedSeek;
import ru.liner.wallpapertheming.views.RoundedSwitch;

public class MainActivity extends ThemedActivity {
    private ThemeConfig themeConfig;
    private RoundedSwitch useSecondColorSwitch;
    private RoundedSwitch useTextColorSwitch;
    private RoundedSeek animationDurationSeek;
    private ImageView wallpaperView;
    private CardView wallpaperCard;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useSecondColorSwitch = findViewById(R.id.useSecondColorSwitch);
        useTextColorSwitch = findViewById(R.id.useTextColorSwitch);
        animationDurationSeek = findViewById(R.id.animationDurationSeek);
        wallpaperView = findViewById(R.id.wallpaperView);
        wallpaperCard = findViewById(R.id.wallpaperCard);
        recyclerView = findViewById(R.id.recyclerView);
        List<MyData> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(new MyData(UUID.randomUUID().toString().substring(0, 5), new Random().nextBoolean()));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recyclerView.setAdapter(new RecyclerAdapter(data));
        useSecondColorSwitch.setSwitchCallback((switchCompat, checked) -> {
            if (themeConfig != null) {
                themeConfig.setUseSecondAccentColor(checked);
                deviceWallpaperManager.requestColorsForce();
            }
        });
        useTextColorSwitch.setSwitchCallback((switchCompat, checked) -> {
            if (themeConfig != null) {
                themeConfig.setChangeTextColor(checked);
                deviceWallpaperManager.requestColorsForce();
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
                    deviceWallpaperManager.requestColorsForce();
                }
            }
        });
        WT.subscribe(new WallpaperInterface() {
            @Override
            public void onWallpaperChanged(@NonNull Drawable wallpaper) {

            }

            @Override
            public void onColorChange(@NonNull ColorData colorData) {
                Log.d("TAGTAG", "onColorChange: "+colorData);
            }

            @Override
            public boolean acceptChanges() {
                return true;
            }

            @NonNull
            @Override
            public ColorData getDefaultColorData() {
                return new ColorData(0,0);
            }
        });
        WT.requestColorUpdate(true);
        wallpaperView.setImageDrawable(deviceWallpaperManager.getWallpaperDrawable());
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