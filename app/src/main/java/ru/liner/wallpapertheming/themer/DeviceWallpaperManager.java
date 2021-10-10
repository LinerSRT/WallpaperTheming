package ru.liner.wallpapertheming.themer;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import ru.liner.wallpapertheming.utils.ColorUtils;
import ru.liner.wallpapertheming.utils.ImageUtils;

@SuppressWarnings("deprecation")
public class DeviceWallpaperManager {
    @NonNull
    private final Context context;
    private final WallpaperManager wallpaperManager;
    @NonNull
    private final ICallback callback;
    @Nullable
    private final BroadcastReceiver wallpaperChangedReceiver;
    @Nullable
    private WallpaperColors currentColors;

    public DeviceWallpaperManager(@NonNull Context context, @NonNull ICallback callback) {
        this.context = context;
        this.wallpaperManager = WallpaperManager.getInstance(context);
        this.callback = callback;
        this.currentColors = null;
        wallpaperChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                requestColors();
            }
        };
        context.registerReceiver(wallpaperChangedReceiver, new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
    }

    public void requestColorsForce(){
        currentColors = null;
        requestColors();
    }

    public Drawable getWallpaperDrawable(){
        return  Utils.isCurrentWallpaperLive(context, wallpaperManager.getWallpaperInfo()) ?
                wallpaperManager.getWallpaperInfo().loadThumbnail(context.getPackageManager()) :
                wallpaperManager.getDrawable();
    }

    public void requestColors() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            android.app.WallpaperColors androidWallpaperColors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
            if (androidWallpaperColors == null) {
                extractColor(getWallpaperDrawable());
            } else {
                boolean darkTheme = (getColorHints(androidWallpaperColors) & 2) != 0;
                Color primary = androidWallpaperColors.getPrimaryColor();
                Color secondary = androidWallpaperColors.getSecondaryColor();
                Color tertiary = androidWallpaperColors.getTertiaryColor();
                WallpaperColors wallpaperColors = new WallpaperColors(
                        primary == null ? Color.BLACK : primary.toArgb(),
                        ColorUtils.darkerColor(primary == null ? Color.BLACK : primary.toArgb(), .3f),
                        secondary == null ? Color.BLACK : secondary.toArgb(),
                        ColorUtils.darkerColor(secondary == null ? Color.BLACK : secondary.toArgb(), .3f),
                        darkTheme ?
                                ColorUtils.darkerColor(primary == null ? Color.BLACK : primary.toArgb(), 0.9f) :
                                ColorUtils.lightenColor(primary == null ? Color.BLACK : primary.toArgb(), 0.9f)
                );
                if (currentColors == null || currentColors.isChanged(wallpaperColors)) {
                    currentColors = wallpaperColors;
                    callback.onWallpaperColorsChanged(currentColors);
                }
            }
        } else {
            extractColor(getWallpaperDrawable());
        }
    }

    public void destroy() {
        if (wallpaperChangedReceiver != null) {
            context.unregisterReceiver(wallpaperChangedReceiver);
        }
    }

    @Nullable
    public WallpaperColors getCurrentColors() {
        return currentColors;
    }

    public interface ICallback {
        void onWallpaperColorsChanged(WallpaperColors wallpaperColors);
    }

    private void extractColor(Drawable drawable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Palette palette = Palette.from(ImageUtils.drawableToBitmap(drawable)).generate();
                Palette.Swatch accentSwatch = palette.getDarkVibrantSwatch();
                if (accentSwatch == null)
                    accentSwatch = palette.getVibrantSwatch();
                Palette.Swatch accentSecondSwatch = palette.getDominantSwatch();
                if (accentSecondSwatch == null)
                    accentSecondSwatch = palette.getLightVibrantSwatch();
                if(accentSwatch == null)
                    accentSwatch = palette.getDominantSwatch();
                if(accentSecondSwatch == null)
                    accentSecondSwatch = palette.getDominantSwatch();
                WallpaperColors wallpaperColors = new WallpaperColors(
                        accentSwatch.getRgb(),
                        ColorUtils.darkerColor(accentSwatch.getRgb(), .3f),
                        accentSecondSwatch.getRgb(),
                        ColorUtils.darkerColor(accentSecondSwatch.getRgb(), .3f),
                        palette.getDarkMutedColor(Color.DKGRAY)
                );
                if (currentColors == null || currentColors.isChanged(wallpaperColors)) {
                    currentColors = wallpaperColors;
                    new Handler(Looper.getMainLooper()).post(() -> callback.onWallpaperColorsChanged(currentColors));
                }
            }
        }).start();
    }

    private int getColorHints(android.app.WallpaperColors colors) {
        String str = colors.toString();
        int index = str.lastIndexOf("h: ");
        String val = str.substring(index + 3, str.length()-1);
        if (TextUtils.isDigitsOnly(val))
            return Integer.parseInt(val);
        return 0;
    }
}
