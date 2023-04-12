package com.floatar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private SwitchCompat notificationsSwitch;
    private Spinner languageSpinner;
    private SeekBar volumeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inicializar vistas
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);

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
    }
}
