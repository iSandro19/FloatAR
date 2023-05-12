package com.floatar;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;

public class SettingsActivity extends AppCompatActivity {
    private AppPreferences appPrefs;
    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    // Métodos públicos ----------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Métodos protegidos --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inicializar
        SwitchCompat notificationsSwitch = findViewById(R.id.notificationsSwitch);
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        SeekBar volumeSeekBar = findViewById(R.id.volumeSeekBar);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        appPrefs = new AppPreferences(this);

        // Notificaciones
        notificationsSwitch.setChecked(appPrefs.areNotificationsEnabled());
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPrefs.setNotificationsEnabled(isChecked);
            toggleNotifications(isChecked);
        });

        // Configurar opciones del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Obtener el objeto SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Establecer el valor del Spinner según las preferencias guardadas
        String selectedLanguage = sharedPreferences.getString("language", "default");
        int position = adapter.getPosition(selectedLanguage);
        languageSpinner.setSelection(position);

        // Listener del Spinner para guardar el valor seleccionado en las preferencias
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", selectedLanguage);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nada que hacer
            }
        });

        // Configurar botón de guardar cambios
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            // Guardar cambios en SharedPreferences o en una base de datos
            Toast.makeText(SettingsActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            finish(); // Regresar a la actividad anterior
        });

        // Agregar listener para actualizar el volumen
        volumeSeekBar.setProgress(appPrefs.getVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                appPrefs.setVolume(progress);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            appPrefs.setDarkThemeEnabled(isChecked);
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
        });
        switchDarkMode.setChecked(appPrefs.areDarkThemeEnabled());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Métodos privados ----------------------------------------------------------------------------

    /**
     * Habilita o deshabilita las notificaciones.
     * @param enabled true para habilitar, false para deshabilitar.
     */
    private void toggleNotifications(boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putBoolean("notifications_enabled", enabled);
        editor.apply();

        if (enabled) {
            // Habilitar notificaciones
            NotificationManagerCompat.from(this).cancelAll();
        } else {
            // Deshabilitar notificaciones
            NotificationManagerCompat.from(this).cancelAll();
        }
    }
}

