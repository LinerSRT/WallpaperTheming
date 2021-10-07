package ru.liner.wallpapertheming.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils {
    public static Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(Math.max(1, width), Math.max(1, height), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setFilterBitmap(true);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
