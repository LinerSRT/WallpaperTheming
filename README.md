# Wallpaper theming
![Android](https://img.shields.io/badge/Android-7.0%2B-brightgreen) ![API](https://img.shields.io/badge/API-24-blue.svg?style=flat) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/91406a16f3f149a1aa091afaa2904af5)](https://www.codacy.com/gh/LinerSRT/WallpaperTheming/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=LinerSRT/WallpaperTheming&amp;utm_campaign=Badge_Grade)

Android 12 introduced new feature for changing system accent color using wallpaper. 
This project is start point to imlement same feature for your application in older versions
- Minimum API is 24
- This project uses AndroidX
- This project uses **androidx.palette**

<img src="https://raw.githubusercontent.com/LinerSRT/WallpaperTheming/main/media/record.gif" />

## Usage
See demo project for more detail. 

For auto applying colors from wallpaper your activity sould extends **ThemedActivity.class**
Activity will change colors for all views in folowing cases:
- First start
- Wallpaper changed

Activity will not change colors in standart lifecycle;

Pass **ThemeConfig.class** in your activity to configure theme;
**You need specify resource id of your root view in layout!**

Example: 
```java
public class MainActivity extends ThemedActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public ThemeConfig getThemeConfig() {
        return new ThemeConfig.Builder(R.id.mainLayout)
                .setAnimationDuration(500)
                .setAnimationInterpolator(new AccelerateInterpolator())
                .setUseSecondAccentColor(true)
                .build()
    }
}
```
If you want to impement theming to your custom view, you need implement **IThemer.class** interface for your view.
This interface will be called when your view needs to change it color theme

## License
Licensed under the Apache License, Version 2.0
