package com.floatar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    // Métodos protegidos --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView resultTextView = findViewById(R.id.result_textview);
        Button restartButton = findViewById(R.id.restart_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String winner = extras.getString("winner");
            if (winner.equals("player"))
                resultTextView.setText(getString(R.string.you_won));
            else
                resultTextView.setText(getString(R.string.you_lost));
        }

        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(R.string.game_over);
    }
}