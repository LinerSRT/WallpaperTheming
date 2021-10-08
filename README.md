# Wallpaper theming
![Android](https://img.shields.io/badge/Android-7.0%2B-brightgreen) ![API](https://img.shields.io/badge/API-24-blue.svg?style=flat)

Android 12 introduced new feature for changing system accent color using wallpaper. 
This project is start point to imlement same feature for your application in older versions
- Minimum API is 24
- This project uses AndroidX
- This project uses **androidx.palette**

<img src="https://raw.githubusercontent.com/LinerSRT/WallpaperTheming/main/media/preview1.jpg" width="413" height="359" />
<img src="https://raw.githubusercontent.com/LinerSRT/WallpaperTheming/main/media/preview2.jpg" width="413" height="359" />
<img src="https://raw.githubusercontent.com/LinerSRT/WallpaperTheming/main/media/preview3.jpg" width="413" height="359" />

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
                .setAnimateColorChanges(true) // animate color changes
                .setAnimationDuration(1000) // animation duration
                .setColoredNavigationBar(true) // apply color to navigation bar
                .setChangeBackgroundColors(false) // apply background color for activity
                .setChangeTextColors(false) // apply text color for views
                .setDefaultAccentColor(ContextCompat.getColor(this, R.color.design_default_color_primary)) // set default accent color
                .setChangeStandardViewColors(true) // change android.widget.* views
                .setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.white)) // set default background color
                .build();
    }
}
```
If you want to impement theming to your custom view, you need implement **IThemer.class** interface for your view.
This interface will be called when your view needs to change it color theme

## License
Licensed under the Apache License, Version 2.0