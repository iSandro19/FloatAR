package com.floatar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Random;

public class SinglePlayerActivity extends AppCompatActivity {
    private int[][] playerBoard = new int[10][10];
    private int[][] opponentBoard = new int[10][10];
    private final Button[][] playerButtonGrid = new Button[10][10];
    private Context mContext;
    private boolean playerTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        GridLayout playerGridLayout = findViewById(R.id.grid_layout_player_board_single_player);
        GridLayout opponentGridLayout = findViewById(R.id.grid_layout_opponent_board_single_player);
        mContext = this;

        //MediaPlayer hitSound = MediaPlayer.create(this, R.raw.hit);
        //MediaPlayer missSound = MediaPlayer.create(this, R.raw.miss);

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

            String buttonType = String.valueOf(tag.split("_")[0]);

            // Ejecutar la lógica del juego correspondiente
            if (playerTurn && !buttonType.equals("playerbutton")) {
                playerTurn(v, row, col);
            } else {
                opponentTurn();
            }
        };

        // Añadir los botones al tablero
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(this);
                button.setTag("playerbutton_" + i + "_" + j); // Establecer una etiqueta única para cada botón
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
                playerButtonGrid[i][j] = button;
            }
        }

        // Añadir los botones al tablero
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(this);
                button.setTag("opponentbutton_" + i + "_" + j); // Establecer una etiqueta única para cada botón
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                int buttonSize = (getScreenWidth() - 200) / 10;
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
        Log.d("Tablero", Arrays.deepToString(opponentBoard));
        Log.d("Casilla valor", String.valueOf(opponentBoard[row][col]));


        if (opponentBoard[row][col] == 1) {
            // El jugador ha acertado
            opponentBoard[row][col] = 2; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
            checkGameOver();
            Log.d(".SinglePlayer", "El jugador ha acertado, matiene el turno");

        } else if (opponentBoard[row][col] == -1 || opponentBoard[row][col] == 2) {
            Toast.makeText(SinglePlayerActivity.this, "Casilla no válida", Toast.LENGTH_SHORT).show();
            //Hacer que puedas volver a tener turno////////////////////////

        } else {
            // El jugador ha fallado
            opponentBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris
            playerTurn = false; // Cambiar el turno al oponente
            opponentTurn();
            Log.d(".SinglePlayer", "El jugador ha fallado, turno para el oponente");
        }
    }

    private void opponentTurn() {
        int row, col;
        if (!playerTurn) {
            Random random = new Random();

            do {
                row = random.nextInt(10);
                col = random.nextInt(10);
            } while (opponentBoard[row][col] != 0);

            if (playerBoard[row][col] == 1) {
                // El oponente ha acertado
                playerBoard[row][col] = 2; // Actualizar la matriz "opponentBoard"
                Button button = playerButtonGrid[row][col]; // Obtener el botón correspondiente
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo

                checkGameOver();

                Log.d(".SinglePlayer", "El oponente ha acertado, mantiene el turno");
                opponentTurn();
            } else if (opponentBoard[row][col] == -1 || opponentBoard[row][col] == 2) {
                //opponentTurn();
                //Hacer que vaya descartando las casillas//////////////////////////////////////
            } else {
                // El oponente ha fallado
                playerBoard[row][col] = -1; // Actualizar la matriz "opponentBoard"
                Button button = playerButtonGrid[row][col]; // Obtener el botón correspondiente
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a negro
                playerTurn = true; // Cambiar el turno al jugador
                Log.d(".SinglePlayer", "El oponente ha fallado, turno para el jugador");
            }
        }
    }

    // Comprobar si el juego ha terminado
    private void checkGameOver() {
        boolean check = true;

        // Gana la IA
        for (int[] row : playerBoard)
            for (int cell : row)
                if (cell == 1) {
                    check = false; break;
                }
        if (check) {
            Intent intent = new Intent(SinglePlayerActivity.this, GameOverActivity.class);
            intent.putExtra("winner", "opponent");
            System.out.println("Gana la IA");
            startActivity(intent);
        }

        // Gana el jugador
        check = true;
        for (int[] row : opponentBoard)
            for (int cell : row)
                if (cell == 1) {
                    check = false; break;
                }
        if (check) {
            Intent intent = new Intent(SinglePlayerActivity.this, GameOverActivity.class);
            intent.putExtra("winner", "player");
            System.out.println("Gana el jugador");
            startActivity(intent);
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