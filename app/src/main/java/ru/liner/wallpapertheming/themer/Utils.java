package ru.liner.wallpapertheming.themer;

import android.app.WallpaperInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.wallpaper.WallpaperService;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<ResolveInfo> getDeviceLiveWallpapers(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager != null ? packageManager.queryIntentServices(new Intent(WallpaperService.SERVICE_INTERFACE), PackageManager.GET_META_DATA) : new ArrayList<>();
    }

    public static boolean isCurrentWallpaperLive(Context context, WallpaperInfo wallpaperInfo) {
        if (wallpaperInfo == null)
            return false;
        List<ResolveInfo> wallpapersList = getDeviceLiveWallpapers(context);
        for (ResolveInfo resolveInfo : wallpapersList) {
            if (wallpaperInfo.getServiceName().equals(resolveInfo.serviceInfo.name)) {
                return true;
            }
        }
        return false;
    }
}
