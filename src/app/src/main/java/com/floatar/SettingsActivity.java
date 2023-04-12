package com.floatar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat notificationsSwitch;
    private SwitchCompat switchDarkMode;
    private Spinner languageSpinner;
    private SeekBar volumeSeekBar;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Obtener el AudioManager del sistema
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Inicializar vistas
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleNotifications(isChecked);
            }
        });

        // Configurar opciones del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Configurar botón de guardar cambios
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Guardar cambios en SharedPreferences o en una base de datos
                Toast.makeText(SettingsActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                finish(); // Regresar a la actividad anterior
            }
        });

        // Agregar listener para actualizar el volumen
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

