package ru.liner.wallpapertheming;

import android.app.Application;

import ru.liner.wallpapertheming.wt.WT;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WT.init(this);
    }
}
