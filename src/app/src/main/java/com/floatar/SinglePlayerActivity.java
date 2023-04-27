package com.floatar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SinglePlayerActivity extends AppCompatActivity {
    private int[][] playerBoard = new int[10][10];
    private int[][] opponentBoard = new int[10][10];
    private Context mContext;
    private boolean playerTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        GridLayout playerGridLayout = findViewById(R.id.grid_layout_player_board);
        GridLayout opponentGridLayout = findViewById(R.id.grid_layout_opponent_board);
        mContext = this;

        // Obtener el tablero del jugador
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            playerBoard = (int[][]) bundle.getSerializable("playerBoard");
        }

        // Obtener el tablero del oponente
        if (bundle != null) {
            opponentBoard = (int[][]) bundle.getSerializable("opponentBoard");
        }

        // OnClickListener para los botones del tablero
        View.OnClickListener buttonClickListener = v -> {
            // Obtener la etiqueta del botón
            String tag = (String) v.getTag();

            // Obtener la posición del botón a partir de su etiqueta
            int row = Integer.parseInt(tag.split("_")[1]);
            int col = Integer.parseInt(tag.split("_")[2]);

            // Ejecutar la lógica del juego correspondiente

            if (playerTurn) {
                playerTurn(v, row, col);
            } else {
                opponentTurn(v, row, col);
            }
        };

        // Añadir los botones al tablero
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(this);
                button.setTag("player_button_" + i + "_" + j); // Establecer una etiqueta única para cada botón
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                int buttonSize = (getScreenWidth()/2) / 10;
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(buttonSize, buttonSize);
                params.setMargins(3, 3, 3, 3);
                button.setLayoutParams(params);
                button.setOnClickListener(buttonClickListener);
                // Establecer el color del botón según el contenido de la celda en myBoard
                if (playerBoard[i][j] == 1) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue)); // color para las celdas con barco
                }
                playerGridLayout.addView(button);
            }
        }

        // Añadir los botones al tablero
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(this);
                button.setTag("opponent_button_" + i + "_" + j); // Establecer una etiqueta única para cada botón
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                int buttonSize = (getScreenWidth() - 100) / 10;
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(buttonSize, buttonSize);
                params.setMargins(3, 3, 3, 3);
                button.setLayoutParams(params);
                button.setOnClickListener(buttonClickListener);
                opponentGridLayout.addView(button);
            }
        }

        // Establecer el título de la actividad en la ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.single_player);
        }
    }

    // Ejecutar la lógica del jugador
    private void playerTurn(View v, int row, int col) {
        if (opponentBoard[row][col] == 1) {
            // El jugador ha acertado
            opponentBoard[row][col] = 2; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
        } else {
            // El jugador ha fallado
            opponentBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris
            playerTurn = false; // Cambiar el turno al oponente
        }
    }

    // Ejecutar la lógica del oponente
    private void opponentTurn(View v, int row, int col) {
        if (playerBoard[row][col] == 1) {
            // El jugador ha acertado
            playerBoard[row][col] = 2; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
        } else {
            // El jugador ha fallado
            playerBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris
            playerTurn = true; // Cambiar el turno al oponente
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backPress = new Intent(SinglePlayerActivity.this, MainActivity.class);
        startActivity(backPress);
    }
}