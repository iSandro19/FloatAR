package com.floatar;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class AppPreferences {
    private static final String PREFS_NAME = "my_app_prefs";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_DARK_THEME_ENABLED = "dark_theme_enabled";
    private final SharedPreferences prefs;

    /**
     * Constructor
     * @param context Contexto
     */
    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Obtener el volumen
     * @return El volumen
     */
    public int getVolume() {
        return prefs.getInt(KEY_VOLUME, 50);
    }

    /**
     * Establecer el volumen
     * @param volume Volumen
     */
    public void setVolume(int volume) {
        prefs.edit().putInt(KEY_VOLUME, volume).apply();
    }

    /**
     * Obtener si las notificaciones están activadas
     * @return Si las notificaciones están activadas
     */
    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    /**
     * Establecer si las notificaciones están activadas
     * @param enabled Si las notificaciones están activadas
     */
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    /**
     * Obtener si el tema oscuro está activado
     * @return Si el tema oscuro está activado
     */
    public boolean areDarkThemeEnabled() {
        return prefs.getBoolean(KEY_DARK_THEME_ENABLED, false);
    }

    /**
     * Establecer si el tema oscuro está activado
     * @param enabled Si el tema oscuro está activado
     */
    public void setDarkThemeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_THEME_ENABLED, enabled).apply();
    }

    /**
     * Aplicar las preferencias de la aplicación
     */
    public void applyAppPreferences(){
        if (areDarkThemeEnabled())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
