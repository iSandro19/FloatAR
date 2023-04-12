package com.floatar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_main);

        Button buttonSinglePlayer = findViewById(R.id.layout_main_button_singleplayer);
        buttonSinglePlayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, SinglePlayerActivity.class);
            startActivity(intent);
        });

        Button buttonMultiplayer = findViewById(R.id.layout_main_button_multiplayer);
        buttonMultiplayer.setOnClickListener(v ->
                Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
        );

        Button buttonExit = findViewById(R.id.layout_main_button_exit);
        buttonExit.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}