package ru.liner.wallpapertheming.themer;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
    private final ColorExtractor colorExtractor;

    public DeviceWallpaperManager(@NonNull Context context, @NonNull ICallback callback) {
        this.context = context;
        this.wallpaperManager = WallpaperManager.getInstance(context);
        this.callback = callback;
        this.colorExtractor = new ColorExtractor(wallpaperColors -> {
            if (currentColors == null || currentColors.isChanged(wallpaperColors)) {
                currentColors = wallpaperColors;
                callback.onWallpaperColorsChanged(currentColors);
            }
        });
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

    public void requestColors() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            android.app.WallpaperColors androidWallpaperColors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
            if (androidWallpaperColors == null) {
                colorExtractor.doInBackground(
                        Utils.isCurrentWallpaperLive(context, wallpaperManager.getWallpaperInfo()) ?
                                wallpaperManager.getWallpaperInfo().loadThumbnail(context.getPackageManager()) :
                                wallpaperManager.getDrawable());
            } else {
                boolean darkTheme = (getColorHints(androidWallpaperColors) & 2) != 0;
                WallpaperColors wallpaperColors = new WallpaperColors(
                        androidWallpaperColors.getPrimaryColor().toArgb(),
                        ColorUtils.darkerColor(androidWallpaperColors.getPrimaryColor().toArgb(), .3f),
                        androidWallpaperColors.getSecondaryColor().toArgb(),
                        ColorUtils.darkerColor(androidWallpaperColors.getSecondaryColor().toArgb(), .3f),
                        darkTheme ?
                                ColorUtils.darkerColor(androidWallpaperColors.getPrimaryColor().toArgb(), 0.9f) :
                                ColorUtils.lightenColor(androidWallpaperColors.getPrimaryColor().toArgb(), 0.9f)
                );
                if (currentColors == null || currentColors.isChanged(wallpaperColors)) {
                    currentColors = wallpaperColors;
                    callback.onWallpaperColorsChanged(currentColors);
                }
            }
        } else {
            colorExtractor.doInBackground(
                    Utils.isCurrentWallpaperLive(context, wallpaperManager.getWallpaperInfo()) ?
                            wallpaperManager.getWallpaperInfo().loadThumbnail(context.getPackageManager()) :
                            wallpaperManager.getDrawable());
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

    private static class ColorExtractor extends AsyncTask<Drawable, WallpaperColors, WallpaperColors> {
        private final IExtractorCallback callback;

        public ColorExtractor(IExtractorCallback callback) {
            this.callback = callback;
        }

        @Override
        protected WallpaperColors doInBackground(Drawable... drawables) {
            if (drawables.length == 0)
                return null;
            Palette palette = Palette.from(ImageUtils.drawableToBitmap(drawables[0])).generate();
            Palette.Swatch accentSwatch = palette.getDarkVibrantSwatch();
            if (accentSwatch == null)
                accentSwatch = palette.getVibrantSwatch();
            Palette.Swatch accentSecondSwatch = palette.getDominantSwatch();
            if (accentSecondSwatch == null)
                accentSecondSwatch = palette.getLightVibrantSwatch();
            return new WallpaperColors(
                    accentSwatch.getRgb(),
                    ColorUtils.darkerColor(accentSwatch.getRgb(), .3f),
                    accentSecondSwatch.getRgb(),
                    ColorUtils.darkerColor(accentSecondSwatch.getRgb(), .3f),
                    palette.getDarkMutedColor(Color.DKGRAY)
            );
        }

        @Override
        protected void onPostExecute(WallpaperColors wallpaperColors) {
            if (wallpaperColors != null)
                callback.onExtracted(wallpaperColors);
        }

        public interface IExtractorCallback {
            void onExtracted(WallpaperColors wallpaperColors);
        }
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
