package com.floatar;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

public class SettingsActivityNice extends AppCompatActivity {

    Button confirmButton;

    MediaPlayer settingsOut;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            settingsOut.start();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_nice);

        //Sonidos
        settingsOut = MediaPlayer.create(this, R.raw.settings_out);

        confirmButton = findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(v -> {
            settingsOut.start();
            // Guardar cambios en SharedPreferences o en una base de datos
            Toast.makeText(SettingsActivityNice.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            finish(); // Regresar a la actividad anterior
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_settings_fragment, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(settingsOut != null){
            settingsOut.release();
            settingsOut = null;
        }
    }

    // Fragmento donde están Notificaciones, Tema, Idioma y volumen
    public static class SettingsFragment extends PreferenceFragmentCompat {

        SharedPreferences sharedPreferences;
        SwitchPreference switchPreferenceNotification, switchPreferenceTheme;
        ListPreference listPreference;
        SeekBarPreference seekBarPreference;

        Context context;
        AppPreferences appPrefs;
        AudioManager audioManager;

        MediaPlayer volumeCheck;

        @Override
        public void onCreatePreferences(Bundle savedInstance, String rootKey){
            setPreferencesFromResource(R.xml.activity_settings_preference_screen, rootKey);

            context = getActivity();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            appPrefs = new AppPreferences(context);

            volumeCheck = MediaPlayer.create(context, R.raw.volume_check);

            // Notificaciones
            switchPreferenceNotification = findPreference("NOTIFICATIONS");
            switchPreferenceNotification.setChecked(appPrefs.areNotificationsEnabled());
            switchPreferenceNotification.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isChecked = (boolean) newValue;
                appPrefs.setNotificationsEnabled(isChecked);
                toggleNotifications(isChecked);
                switchPreferenceNotification.setChecked(appPrefs.areNotificationsEnabled());
                return isChecked;
            });

            // Tema del dispositivo
            switchPreferenceTheme = findPreference("THEME");
            switchPreferenceTheme.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isChecked = (boolean) newValue;
                appPrefs.setDarkThemeEnabled(isChecked);
                int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
                AppCompatDelegate.setDefaultNightMode(mode);
                return isChecked;
            });
            switchPreferenceTheme.setChecked(appPrefs.areDarkThemeEnabled());

            // Idioma
            listPreference = findPreference("LANGUAGE_LIST");
            String selectedValue = listPreference.getValue();
            if (selectedValue == null){
                listPreference.setValue("Español");
            }
            listPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            listPreference.setOnPreferenceChangeListener((preference, newValue) ->{
                String selectedLanguage = (String) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", selectedLanguage);
                editor.apply();

                return true;
            });

            // Volumen
            audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            seekBarPreference = findPreference("VOLUME");
            seekBarPreference.setMax(100);
            seekBarPreference.setMin(0);
            seekBarPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                int progress = (int) newValue;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                seekBarPreference.setValue(progress);
                appPrefs.setVolume(progress);
                volumeCheck.start();
                return true;
            });
        }

        private void toggleNotifications(boolean enabled) {
            SharedPreferences.Editor editor = context.getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("notifications_enabled", enabled);
            editor.apply();

            if (enabled) {
                // Habilitar notificaciones
                NotificationManagerCompat.from(context).cancelAll();
            } else {
                // Deshabilitar notificaciones
                NotificationManagerCompat.from(context).cancelAll();
            }
        }

        @Override
        public void onDestroy(){
            super.onDestroy();
            if(volumeCheck != null){
                volumeCheck.release();
                volumeCheck = null;
            }
        }

    }

}
