package ru.liner.wallpapertheming.wt;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.palette.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

import ru.liner.wallpapertheming.themer.Utils;
import ru.liner.wallpapertheming.utils.ImageUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/***
 * Wallpaper Themer manager
 */
@SuppressWarnings("deprecation | unused")
@SuppressLint("StaticFieldLeak")
public class WT {
    private static WT WT;
    private Context context;
    private WallpaperManager wallpaperManager;
    private List<WallpaperInterface> wallpaperInterfaceList;
    private ColorData currentColorData;
    private BroadcastReceiver wallpaperChangedReceiver;

    /***
     * Use this method to init WT once!
     * @param context Application context
     */
    public static void init(Context context) {
        WT = new WT();
        if (WT.wallpaperManager == null) {
            WT.context = context;
            WT.wallpaperManager = WallpaperManager.getInstance(WT.context);
            WT.wallpaperInterfaceList = new ArrayList<>();
            WT.currentColorData = null;
            WT.wallpaperChangedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (ActivityCompat.checkSelfPermission(WT.context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        throw new RuntimeException("WT needs to be granted READ_EXTERNAL_STORAGE permission to work");
                    requestColorUpdate();
                }
            };
            WT.context.registerReceiver(WT.wallpaperChangedReceiver, new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
        }
    }

    /***
     * Return current wallpaper color data
     * @return primary and secondary color from wallpaper
     */
    @Nullable
    public static ColorData getCurrentColorData() {
        checkInitialization();
        return WT.currentColorData;
    }

    /***
     * Iterate and apply wallpaper colors for all views implementing @WallpaperInterface
     * @param viewGroup root view for start
     */
    @RequiresPermission(anyOf = {READ_EXTERNAL_STORAGE})
    public static void apply(ViewGroup viewGroup) {
        checkInitialization();
        if (WT.currentColorData == null)
            requestColorUpdate(true);
        if (viewGroup == null)
            return;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof WallpaperInterface) {
                if (child instanceof ViewGroup) {
                    apply((ViewGroup) child);
                }
                ((WallpaperInterface) child).onColorChange(WT.currentColorData);
            } else if (child instanceof ViewGroup) {
                apply((ViewGroup) child);
            }
        }
    }

    /***
     * Subscribe to wallpaper changes and color updates
     * @param wallpaperInterface interface for listening
     */
    public static void subscribe(WallpaperInterface wallpaperInterface) {
        checkInitialization();
        WT.wallpaperInterfaceList.add(wallpaperInterface);
    }

    /***
     * Unsubscribe from wallpaper changes and color updates
     * @param wallpaperInterface interface for listening
     */
    public static void unsubscribe(WallpaperInterface wallpaperInterface) {
        checkInitialization();
        WT.wallpaperInterfaceList.remove(wallpaperInterface);
    }

    /***
     * Request lazy color updates for @WallpaperInterface
     * May not trigger interface if current color data don't changed
     */
    @RequiresPermission(anyOf = {READ_EXTERNAL_STORAGE})
    public static void requestColorUpdate() {
        checkInitialization();
        requestColorUpdate(false);
    }

    /***
     * Request lazy color updates for @WallpaperInterface
     * May not trigger interface if current color data don't changed and force update not specified
     * @param force specify to update immediately
     */
    @RequiresPermission(anyOf = {READ_EXTERNAL_STORAGE})
    public static void requestColorUpdate(boolean force) {
        checkInitialization();
        for (WallpaperInterface wallpaperInterface : WT.wallpaperInterfaceList) {
            if (wallpaperInterface.acceptChanges()) {
                wallpaperInterface.onWallpaperChanged(getWallpaperDrawable());
            }
        }
        if (force)
            WT.currentColorData = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            android.app.WallpaperColors androidWallpaperColors = WT.wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
            if (androidWallpaperColors == null) {
                extractColor(getWallpaperDrawable());
            } else {
                Color primary = androidWallpaperColors.getPrimaryColor();
                Color secondary = androidWallpaperColors.getSecondaryColor();
                if (primary != null) {
                    ColorData colorData = new ColorData(primary.toArgb(), secondary == null ? primary.toArgb() : secondary.toArgb());
                    if (WT.currentColorData == null || WT.currentColorData.isChanged(colorData)) {
                        WT.currentColorData = colorData;
                        for (WallpaperInterface wallpaperInterface : WT.wallpaperInterfaceList) {
                            if (wallpaperInterface.acceptChanges()) {
                                wallpaperInterface.onColorChange(colorData);
                            }
                        }
                    }
                }
            }
        } else {
            extractColor(getWallpaperDrawable());
        }
    }

    /***
     * Get drawable from current wallpaper
     * Return thumbnail if wallpaper is live
     * @return drawable
     */
    @RequiresPermission(anyOf = {READ_EXTERNAL_STORAGE})
    public static Drawable getWallpaperDrawable() {
        checkInitialization();
        return Utils.isCurrentWallpaperLive(WT.context, WT.wallpaperManager.getWallpaperInfo()) ?
                WT.wallpaperManager.getWallpaperInfo().loadThumbnail(WT.context.getPackageManager()) :
                WT.wallpaperManager.getDrawable();
    }

    /***
     * Extract color from drawable using AndroidX pallete
     * @param drawable to process
     */
    private static void extractColor(Drawable drawable) {
        checkInitialization();
        new Thread(() -> {
            Palette palette = Palette.from(ImageUtils.drawableToBitmap(drawable)).generate();
            Palette.Swatch accentSwatch = palette.getDarkVibrantSwatch();
            if (accentSwatch == null)
                accentSwatch = palette.getVibrantSwatch();
            Palette.Swatch accentSecondSwatch = palette.getDominantSwatch();
            if (accentSecondSwatch == null)
                accentSecondSwatch = palette.getLightVibrantSwatch();
            if (accentSwatch == null)
                accentSwatch = palette.getDominantSwatch();
            if (accentSecondSwatch == null)
                accentSecondSwatch = palette.getDominantSwatch();
            ColorData colorData = new ColorData(accentSwatch.getRgb(), accentSecondSwatch.getRgb());
            if (WT.currentColorData == null || WT.currentColorData.isChanged(colorData)) {
                WT.currentColorData = colorData;
                for (WallpaperInterface wallpaperInterface : WT.wallpaperInterfaceList) {
                    if (wallpaperInterface.acceptChanges()) {
                        wallpaperInterface.onColorChange(colorData);
                    }
                }
            }
        }).start();
    }

    /***
     * Check if WT is initialized
     */
    private static void checkInitialization() {
        if (WT == null)
            throw new NullPointerException("WT is null! Add WT.init(this); to Application class of your package!");
    }
}
