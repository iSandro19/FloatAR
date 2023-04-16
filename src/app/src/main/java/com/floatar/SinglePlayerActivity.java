package com.floatar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.Random;

public class SinglePlayerActivity extends AppCompatActivity {
    private final int[][] myBoard = new int[10][10];
    private final int[][] opponentBoard = new int[10][10];
    private Context mContext;
    private boolean playerTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        GridLayout gridLayout = findViewById(R.id.gridLayout_myBoard);
        mContext = this;
        createBoard();

        // OnClickListener para los botones del tablero
        View.OnClickListener buttonClickListener = v -> {
            // Obtener la etiqueta del botón
            String tag = (String) v.getTag();

            // Obtener la posición del botón a partir de su etiqueta
            int row = Integer.parseInt(tag.split("_")[1]);
            int col = Integer.parseInt(tag.split("_")[2]);

            // Ejecutar la lógica del juego correspondiente
            if (playerTurn) {
                if (myBoard[row][col] == 1) {
                    // El jugador ha acertado
                    myBoard[row][col] = 2; // Actualizar la matriz "myBoard"
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
                    checkGameOver(); // Comprobar si el juego ha terminado
                } else {
                    // El jugador ha fallado
                    myBoard[row][col] = -1; // Actualizar la matriz "myBoard"
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris
                    playerTurn = false; // Cambiar el turno al oponente
                    opponentTurn(); // Ejecutar la lógica del oponente
                }
            }
        };

        // Añadir los botones al tablero
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(this);
                button.setTag("button_" + i + "_" + j); // Establecer una etiqueta única para cada botón
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                int buttonSize = (getScreenWidth() - 100) / 10;
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(buttonSize, buttonSize);
                params.setMargins(3, 3, 3, 3);
                button.setLayoutParams(params);
                button.setOnClickListener(buttonClickListener);
                gridLayout.addView(button);
            }
        }

        // Establecer el título de la actividad en la ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.single_player);
        }
    }

    // Generar los barcos aleatorios
    private void createBoard() {
        // Barcos de tamaño 5
        createShip(5);

        // Barcos de tamaño 4
        createShip(4);
        createShip(4);

        // Barcos de tamaño 3
        createShip(3);
        createShip(3);
        createShip(3);

        // Barcos de tamaño 2
        createShip(2);
        createShip(2);
        createShip(2);
        createShip(2);
    }

    // Generar un barco de tamaño "size" aleatorio
    private void createShip(int size) {
        Random random = new Random();
        boolean vertical = random.nextBoolean();
        int row, col;

        if (vertical) {
            row = random.nextInt(10 - size + 1);
            col = random.nextInt(10);
            for (int i = row; i < row + size; i++) {
                myBoard[i][col] = 1;
            }
        } else {
            row = random.nextInt(10);
            col = random.nextInt(10 - size + 1);
            for (int j = col; j < col + size; j++) {
                myBoard[row][j] = 1;
            }
        }
    }


    // Ejecutar la lógica del oponente
    private void opponentTurn() {
        Random random = new Random();
        int row, col;

        do {
            row = random.nextInt(10);
            col = random.nextInt(10);
        } while (opponentBoard[row][col] != 0);

        if (myBoard[row][col] == 1) {
            opponentBoard[row][col] = 2;
            int buttonId = getResources().getIdentifier("button_" + row + "_" + col, "id", getPackageName());
            Button button = findViewById(buttonId);
            if (button != null) {
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            }
            checkGameOver();
        } else {
            opponentBoard[row][col] = -1;
            playerTurn = true;
            int buttonId = getResources().getIdentifier("button_" + row + "_" + col, "id", getPackageName());
            Button button = findViewById(buttonId);
            if (button != null) {
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
            }
        }
    }


    // Comprobar si el juego ha terminado
    private void checkGameOver() {
        /*boolean gameOver = true;
        for (int[] row : myBoard) {
            for (int cell : row) {
                if (cell == 1) {
                    gameOver = false;
                    break;
                }
            }
            if (!gameOver) {
                break;
            }
        }
        if (gameOver) {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.game_over)
                    .setMessage(R.string.you_lost)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        }*/
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
    public void onBackPressed() { // Se puede recuperar la actividad de main????????
        super.onBackPressed();
        Intent backPress = new Intent(SinglePlayerActivity.this, MainActivity.class);
        startActivity(backPress);
    }
}