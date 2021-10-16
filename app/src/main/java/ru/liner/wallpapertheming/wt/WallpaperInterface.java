package ru.liner.wallpapertheming.wt;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public interface WallpaperInterface {
    void onWallpaperChanged(@NonNull Drawable wallpaper);

    void onColorChange(@NonNull ColorData colorData);

    boolean acceptChanges();

    @NonNull
    ColorData getDefaultColorData();
}
