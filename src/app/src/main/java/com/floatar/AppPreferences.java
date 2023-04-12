package com.floatar;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREFS_NAME = "my_app_prefs";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_DARK_THEME_ENABLED = "dark_theme_enabled";

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getVolume() {
        return prefs.getInt(KEY_VOLUME, 50);
    }

    public void setVolume(int volume) {
        prefs.edit().putInt(KEY_VOLUME, volume).apply();
    }

    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public boolean areDarkThemeEnabled() {
        return prefs.getBoolean(KEY_DARK_THEME_ENABLED, false);
    }

    public void setDarkThemeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_THEME_ENABLED, enabled).apply();
    }
}
