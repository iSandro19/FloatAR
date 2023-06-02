package com.floatar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    Button confirmButton;
    MediaPlayer settingsOut;
    static SharedPreferences sharedPreferences;

    // Métodos públicos ----------------------------------------------------------------------------

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
    public void onBackPressed() {
        super.onBackPressed();

        /*Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();*/
    }

    // Métodos protegidos --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Sonidos
        settingsOut = MediaPlayer.create(this, R.raw.settings_out);

        confirmButton = findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(v -> {
            settingsOut.start();
            // Guardar cambios en SharedPreferences o en una base de datos
            Toast.makeText(SettingsActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();

            String selectedLanguage = sharedPreferences.getString("language", "es");
            Locale locale = new Locale(selectedLanguage);
            Locale.setDefault(locale);

            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            finish(); // Regresar a la actividad anterior

            /* MODO AUTOMÁTICO DE SELECCIÓN DE IDIOMA
             * Para facilitar el testeo de los idiomas, se ha dejado de forma manual
            // Obtener el idioma del dispositivo
            String deviceLanguage = Locale.getDefault().getLanguage();

            // Verificar si el idioma del dispositivo es compatible con tu aplicación
            if (deviceLanguage.equals("es") || deviceLanguage.equals("en") || deviceLanguage.equals("fr") || deviceLanguage.equals("de") || deviceLanguage.equals("pt")) {
                // Establecer el idioma del dispositivo como idioma seleccionado
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().putString("language", deviceLanguage).apply();
            }
            */
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

    // Fragmento donde están Notificaciones, Tema, Idioma y volumen --------------------------------

    // Fragmento donde están Notificaciones, Tema, Idioma y volumen
    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreference switchPreferenceNotification, switchPreferenceTheme;
        ListPreference listPreference;
        SeekBarPreference seekBarPreference;

        Context context;
        AppPreferences appPrefs;
        AudioManager audioManager;

        MediaPlayer volumeCheck;

        @Override
        public void onCreatePreferences(Bundle savedInstance, String rootKey) {
            setPreferencesFromResource(R.xml.activity_settings_preference_screen, rootKey);

            context = getActivity();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            appPrefs = new AppPreferences(context);

            volumeCheck = MediaPlayer.create(context, R.raw.volume_check);

            // Notificaciones
            switchPreferenceNotification = findPreference("NOTIFICATIONS");
            assert switchPreferenceNotification != null;
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
            assert switchPreferenceTheme != null;
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
            assert listPreference != null;
            String selectedValue = listPreference.getValue();
            if (selectedValue == null){
                listPreference.setValue("es");
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
            assert seekBarPreference != null;
            seekBarPreference.setMax(15);   // Valor maximo de volumen del audiomanager
            seekBarPreference.setMin(0);
            seekBarPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                int progress = (int) newValue;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                seekBarPreference.setValue(progress);
                appPrefs.setVolume(progress);
                volumeCheck.seekTo(0);
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
