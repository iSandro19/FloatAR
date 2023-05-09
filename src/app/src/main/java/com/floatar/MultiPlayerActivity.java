package com.floatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MultiPlayerActivity extends AppCompatActivity {
    private String lobbyKey;
    private String playerId;
    private String opponentId;

    private int[][] playerBoard = new int[10][10];
    private int[][] opponentBoard = new int[10][10];

    private final Button[][] playerButtonGrid = new Button[10][10];
    private final Button[][] opponentButtonGrid = new Button[10][10];

    private Context mContext;
    private boolean playerTurn = true;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://ps-floatar-default-rtdb.europe-west1.firebasedatabase.app/");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);

        GridLayout playerGridLayout = findViewById(R.id.grid_layout_player_board_multi_player);
        GridLayout opponentGridLayout = findViewById(R.id.grid_layout_opponent_board_multi_player);
        mContext = this;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            playerBoard = (int[][]) bundle.getSerializable("playerBoard");
            lobbyKey = bundle.getString("lobbyKey");
            playerId = bundle.getString("playerId");
        }

        // Obtener el tablero del oponente de la base de datos
        database.getReference("lobbies")
            .child("games")
            .child(lobbyKey)
            .child("players")
            .orderByKey()
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        String playerId = playerSnapshot.getKey();
                        assert playerId != null;
                        if (!playerId.equals(MultiPlayerActivity.this.playerId)) {
                            opponentId = playerId;
                            // Es el jugador oponente
                            String value = playerSnapshot.child("playerBoard").getValue(String.class);
                            // Actualizar el tablero del oponente
                            assert value != null;
                            updateOpponentBoard(value);
                            playerTurn = true;
                        } else {
                            // Es el jugador local
                            String value = playerSnapshot.child("playerBoard").getValue(String.class);
                            // Actualizar el tablero del jugador local
                            assert value != null;
                            updatePlayerBoard(value);
                            playerTurn = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(".MultiPlayerActivity", "onCancelled", databaseError.toException());
                }
            });

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
                opponentTurn(v, row, col);
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

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button(this);
                button.setTag("opponentbutton_" + i + "_" + j); // Establecer una etiqueta única para cada botón
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                int buttonSize = (int) ((getScreenWidth()/1.5) / 10);
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(buttonSize, buttonSize);
                params.setMargins(3, 3, 3, 3);
                button.setLayoutParams(params);
                button.setOnClickListener(buttonClickListener);
                opponentGridLayout.addView(button);

                opponentButtonGrid[i][j] = button;
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.multi_player);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void updateOpponentBoard(String updatedOpponentBoard) {
        int[][] board = new int[10][10];
        String[] rows = updatedOpponentBoard.split("\n");
        for (int i = 0; i < 10; i++) {
            String[] columns = rows[i].split(",");
            for (int j = 0; j < 10; j++) {
                board[i][j] = Integer.parseInt(columns[j]);
            }
        }
        opponentBoard = board;

        // Actualizar el tablero del oponente
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = opponentButtonGrid[i][j];
                int cellValue = opponentBoard[i][j];
                if (cellValue == 1) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
                } else if (cellValue == 2) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                } else {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                }
            }
        }
    }

    private void updatePlayerBoard(String updatePlayerBoard) {
        int[][] board = new int[10][10];
        String[] rows = updatePlayerBoard.split("\n");
        for (int i = 0; i < 10; i++) {
            String[] columns = rows[i].split(",");
            for (int j = 0; j < 10; j++) {
                board[i][j] = Integer.parseInt(columns[j]);
            }
        }
        playerBoard = board;

        // Actualizar el tablero del jugador local
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = playerButtonGrid[i][j];
                int cellValue = playerBoard[i][j];
                if (cellValue == 1) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue));
                } else {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                }
            }
        }
    }


    private void playerTurn(View v, int row, int col) {
        Log.d("Tablero", Arrays.deepToString(opponentBoard));
        Log.d("Casilla valor", String.valueOf(opponentBoard[row][col]));

        if (opponentBoard[row][col] == 1) {
            // El jugador ha acertado
            opponentBoard[row][col] = 2; // Actualizar la matriz "myBoard"

            database.getReference("lobbies")
                    .child("games")
                    .child(lobbyKey)
                    .child("players")
                    .child(opponentId)
                    .child("playerBoard")
                    .setValue((Arrays.deepToString(opponentBoard)));

            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
            checkGameOver();

        } else if (opponentBoard[row][col] == -1 || opponentBoard[row][col] == 2) {
            Toast.makeText(MultiPlayerActivity.this, "Casilla no válida", Toast.LENGTH_SHORT).show();
            //Hacer que puedas volver a tener turno////////////////////////

        } else {
            // El jugador ha fallado
            opponentBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris
            playerTurn = false; // Cambiar el turno al oponente
            opponentTurn(v, row, col);
        }
    }

    private void opponentTurn(View v, int row, int col) {
        if (opponentBoard[row][col] == 1) {
            database.getReference("lobbies")
                    .child("games")
                    .child(lobbyKey)
                    .child("players")
                    .child(playerId)
                    .child("playerBoard")
                    .setValue((Arrays.deepToString(playerBoard)));

            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
            checkGameOver();
            Log.d(".SinglePlayer", "El jugador ha acertado, matiene el turno");

        } else if (opponentBoard[row][col] == -1 || opponentBoard[row][col] == 2) {
            Toast.makeText(MultiPlayerActivity.this, "Casilla no válida", Toast.LENGTH_SHORT).show();
            //Hacer que puedas volver a tener turno////////////////////////

        } else {
            // El jugador ha fallado
            opponentBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris
            playerTurn = false; // Cambiar el turno al oponente
            opponentTurn(v, row, col);
            Log.d(".SinglePlayer", "El jugador ha fallado, turno para el oponente");
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
            Intent intent = new Intent(MultiPlayerActivity.this, GameOverActivity.class);
            intent.putExtra("winner", "opponent");
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
            Intent intent = new Intent(MultiPlayerActivity.this, GameOverActivity.class);
            intent.putExtra("winner", "player");
            startActivity(intent);
        }
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Eliminar el nodo del jugador actual al salir de la actividad
                //database...
                onBackPressed();
                return true;
            case R.id.layout_menu_main_help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.layout_menu_main_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.layout_menu_main_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}