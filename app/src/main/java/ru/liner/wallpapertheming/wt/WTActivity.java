package ru.liner.wallpapertheming.wt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import ru.liner.wallpapertheming.R;

public abstract class WTActivity extends FragmentActivity implements WallpaperInterface {
    protected boolean acceptColorChanges;

    @Override
    protected void onStart() {
        super.onStart();
        WT.subscribe(this);
        this.acceptColorChanges = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            WT.requestColorUpdate(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4879);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            WT.requestColorUpdate();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4879) {
            if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        WT.requestColorUpdate(true);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WT.unsubscribe(this);
    }

    @Override
    public void onWallpaperChanged(@NonNull Drawable wallpaper) {

    }

    @Override
    public void onColorChange(@NonNull ColorData colorData) {
        Window window = getWindow();
        window.setNavigationBarColor(colorData.getPrimaryColor());
        window.setStatusBarColor(colorData.getPrimaryColorDarkVariant());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            WT.apply(findViewById(getRootLayoutID()));

    }

    @Override
    public boolean acceptChanges() {
        return acceptColorChanges;
    }

    @NonNull
    @Override
    public ColorData getDefaultColorData() {
        return new ColorData(ContextCompat.getColor(this, R.color.purple_700), ContextCompat.getColor(this, R.color.purple_500));
    }

    @IdRes
    public abstract int getRootLayoutID();
}
