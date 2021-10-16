package ru.liner.wallpapertheming.recycler;

public class MyData {
    private final String text;
    private final boolean value;

    public MyData(String text, boolean value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public String toString() {
        return "MyData{" +
                "text='" + text + '\'' +
                ", value=" + value +
                '}';
    }
}
