package com.floatar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer buttonsSound;
    private MediaPlayer settingsSound;
    private MediaPlayer aboutHelpSound;

    // Métodos públicos ----------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.layout_menu_main_help:
                aboutHelpSound.start();
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.layout_menu_main_settings:
                settingsSound.start();
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.layout_menu_main_about:
                aboutHelpSound.start();
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Métodos protegidos --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyAppPreferences();

        // Sonidos
        buttonsSound = MediaPlayer.create(this, R.raw.button);
        settingsSound = MediaPlayer.create(this, R.raw.settings_in);
        aboutHelpSound = MediaPlayer.create(this, R.raw.about_help);

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Button buttonSinglePlayer = findViewById(R.id.layout_main_button_singleplayer);
        buttonSinglePlayer.setOnClickListener(v -> {
            buttonsSound.start();
            Intent intent = new Intent(this, CreateBoardActivity.class);
            intent.putExtra("gameMode", "singleplayer");
            startActivity(intent);
        });

        Button buttonMultiplayer = findViewById(R.id.layout_main_button_multiplayer);
        buttonMultiplayer.setOnClickListener(v -> {
            buttonsSound.start();
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
        });

        Button buttonAR = findViewById(R.id.layout_main_button_ar);
        buttonAR.setOnClickListener(v -> {
            buttonsSound.start();
            Intent intent = new Intent(this, ARActivity.class);
            startActivity(intent);
        });

        Button buttonExit = findViewById(R.id.layout_main_button_exit);
        buttonExit.setOnClickListener(v -> {
            buttonsSound.start();
            finish();
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buttonsSound != null) {
            buttonsSound.release();
            buttonsSound = null;
        }
        if (settingsSound != null) {
            settingsSound.release();
            settingsSound = null;
        }
        if (aboutHelpSound != null) {
            aboutHelpSound.release();
            aboutHelpSound = null;
        }
    }

    // Métodos privados ----------------------------------------------------------------------------

    /**
     * Aplica las preferencias de la aplicación
     */
    private void applyAppPreferences(){
        AppPreferences ap = new AppPreferences(this);
        ap.applyAppPreferences();
    }
}