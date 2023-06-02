package com.floatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import java.util.Objects;

public class MultiPlayerActivity extends AppCompatActivity {
    private final Button[][] playerButtonGrid = new Button[10][10];
    private final Button[][] opponentButtonGrid = new Button[10][10];
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://ps-floatar-default-rtdb.europe-west1.firebasedatabase.app/");

    private String lobbyKey;
    private String playerId;
    private String opponentId;

    private boolean isPlayerTurn;
    private boolean isPlayerTurnSet = false;
    private boolean isOpponentReady = false;

    private int playerCount = -1;

    private int[][] playerBoard = new int[10][10];
    private int[][] opponentBoard = new int[10][10];

    private Context mContext;

    MediaPlayer settingsSound;
    MediaPlayer aboutHelpSound;

    // Métodos públicos ----------------------------------------------------------------------------

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
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, LobbyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    // Métodos protegidos ----------------------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);

        settingsSound = MediaPlayer.create(this, R.raw.settings_in);
        aboutHelpSound = MediaPlayer.create(this, R.raw.about_help);

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

        database.getReference("lobbies")
                .child(lobbyKey)
                .child("players")
                .child(playerId)
                .child("ready")
                .setValue("true");

        // Obtener el tablero del oponente de la base de datos
        database.getReference("lobbies")
            .child(lobbyKey)
            .child("players")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    playerCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                        String playerId = playerSnapshot.getKey();
                        assert playerId != null;
                        if (!playerId.equals(MultiPlayerActivity.this.playerId)) {

                            Object readyValue = playerSnapshot.child("ready").getValue(String.class);
                            if (readyValue != null) {
                                isOpponentReady = Boolean.parseBoolean(readyValue.toString());
                            }
                            Log.d("isOpponentReady", String.valueOf(isOpponentReady));
                            if (isOpponentReady) {
                                opponentId = playerId;
                                String value = playerSnapshot.child("playerBoard").getValue(String.class);
                                Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                                assert value != null;
                                updateOpponentBoard(value);

                                Log.d("isPlayerReady", String.valueOf(isOpponentReady));
                                isPlayerTurn = true;
                            }
                        } else {
                            String value = playerSnapshot.child("playerBoard").getValue(String.class);

                            assert value != null;
                            updatePlayerBoard(value);
                        }
                    }
                    if(!isPlayerTurnSet){
                        isPlayerTurn = playerCount == 1;
                        isPlayerTurnSet = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(".MultiPlayerActivity", "onCancelled", databaseError.toException());
                }
            });

        database.getReference("lobbies")
            .child(lobbyKey)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Verificar si la sala ha sido eliminada
                    if (!dataSnapshot.exists()) {
                        // Salir de la actividad
                        Intent intent = new Intent(MultiPlayerActivity.this, LobbyActivity.class);
                        startActivity(intent);
                        finish();

                        // Mostrar Toast indicando que la sala ha sido eliminada
                        Toast.makeText(MultiPlayerActivity.this, "La sala ha sido eliminada", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error de base de datos si es necesario
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
            if (!buttonType.equals("playerbutton") && isPlayerTurn && isOpponentReady) {
                playerTurn(v, row, col);
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

    @Override
    protected void onPause() {
        super.onPause();

        // Verificar si el jugador está en una sala
        if (lobbyKey != null && playerId != null) {
            // Eliminar la sala
            database.getReference("lobbies")
                    .child(lobbyKey)
                    .removeValue();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
     * Método que se ejecuta cuando el jugador local hace click en un botón del tablero
     * @param updatedOpponentBoard Tablero del oponente a actualizar
     */
    private void updateOpponentBoard(String updatedOpponentBoard) {
        opponentBoard = convertStringBoardToArrayBoard(updatedOpponentBoard);

        // Actualizar el tablero del oponente
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = opponentButtonGrid[i][j];
                int cellValue = opponentBoard[i][j];
                if (cellValue == -1) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
                } else if (cellValue == 2) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                } else {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
                }
            }
        }
    }

    /**
     * Actualiza el tablero del jugador local
     * @param updatePlayerBoard String con el tablero del jugador local
     */
    private void updatePlayerBoard(String updatePlayerBoard) {
        playerBoard = convertStringBoardToArrayBoard(updatePlayerBoard);

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

    /**
     * Comprobar si el juego ha terminado
     * @param v Botón pulsado
     * @param row Fila del botón pulsado
     * @param col Columna del botón pulsado
     */
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
        } else {
            // El jugador ha fallado
            opponentBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris

            isPlayerTurn = false;

            // Actualizar tablero en la BD
            database.getReference("lobbies")
                    .child(lobbyKey)
                    .child("players")
                    .child(opponentId)
                    .child("playerBoard")
                    .setValue(Arrays.deepToString(opponentBoard));


        }
    }

    /**
     * Comprueba si el juego ha terminado
     */
    private void checkGameOver() {
        boolean check = true;

        // Gana la IA
        for (int[] row : playerBoard)
            for (int cell : row)
                if (cell == 1) {
                    check = false; break;
                }
        if (check) {
            // Verificar si el jugador está en una sala
            if (lobbyKey != null && playerId != null) {
                // Eliminar la sala
                database.getReference("lobbies")
                        .child(lobbyKey)
                        .removeValue();
            }

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
            // Verificar si el jugador está en una sala
            if (lobbyKey != null && playerId != null) {
                // Eliminar  la sala
                database.getReference("lobbies")
                        .child(lobbyKey)
                        .removeValue();
            }

            Intent intent = new Intent(MultiPlayerActivity.this, GameOverActivity.class);
            intent.putExtra("winner", "player");
            startActivity(intent);
        }
    }

    /**
     * Obtiene el ancho de la pantalla
     * @return ancho de la pantalla
     */
    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Convierte el tablero de tipo String a tipo int[][]
     * @param stringBoard string con el tablero
     * @return tablero de tipo int[][]
     */
    private int[][] convertStringBoardToArrayBoard(String stringBoard) {
        int[][] board = new int[10][10];
        // Eliminar los corchetes de los extremos
        stringBoard = stringBoard.replace("[[", "[");
        stringBoard = stringBoard.replace("]]", "]");

        // Obtener las filas (Ej: "[1,2,3,4,5,6,7,8,9,10],...")
        String[] rows = stringBoard.split("],");
        for (int i = 0; i < 10; i++) {
            // Eliminar los corchetes de los extremos
            rows[i] = rows[i].replace("[", "");
            rows[i] = rows[i].replace("]", "");

            // Obtener las columnas (Ej: "1,2,3,4,5,6,7,8,9,10")
            String[] columns = rows[i].split(",");
            for (int j = 0; j < 10; j++) {
                board[i][j] = Integer.parseInt(columns[j].trim());
            }
        }

        // Devolver el tablero
        Log.d("Tablero", Arrays.deepToString(board));
        return board;
    }
}