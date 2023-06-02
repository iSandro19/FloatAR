package com.floatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
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
    private String opponentName;

    private boolean isPlayerTurn;
    private boolean isPlayerTurnSet = false;
    private boolean isOpponentReady = false;
    private boolean firstTurn = false;



    private int playerCount = -1;

    private CountDownTimer myTimer;
    private CountDownTimer opponentTimer;
    private int innerTimer;
    private int opponentSecondsRemaining;

    private int[][] playerBoard = new int[10][10];
    private int[][] opponentBoard = new int[10][10];

    private TextView turnTextView, jugadorText, oponenteText;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
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

        turnTextView = findViewById(R.id.text_view_turn);
        jugadorText = findViewById(R.id.layout_multiplayer_jugador);
        oponenteText = findViewById(R.id.layout_multiplayer_oponente);

        settingsSound = MediaPlayer.create(this, R.raw.settings_in);
        aboutHelpSound = MediaPlayer.create(this, R.raw.about_help);

        turnTextView.setText("Esperando al rival...");

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

        database.getReference("lobbies")
                .child(lobbyKey)
                .child("players")
                .child(playerId)
                .child("timer")
                .setValue("300");

        innerTimer = 300;

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
                                try {
                                    opponentSecondsRemaining = Integer.parseInt(Objects.requireNonNull(playerSnapshot.child("timer").getValue(String.class)));
                                    String newString = getString(R.string.opponent_board) + " ("+ opponentSecondsRemaining + ")";
                                    oponenteText.setText(newString);
                                } catch (NullPointerException ignored) {
                                    Log.d("NullPointerException", "Contador del rival sin inicializar");
                                }
                                try {
                                    if (!Arrays.deepEquals(opponentBoard,
                                            convertStringBoardToArrayBoard(playerSnapshot.
                                                    child("playerBoard").
                                                    getValue(String.class))) && myTimer == null) {

                                        Object readyValue = playerSnapshot.child("ready").getValue(String.class);
                                        opponentName = playerSnapshot.child("name").getValue(String.class);

                                        if (isPlayerTurn)
                                            turnTextView.setText("Tu turno");

                                        if (readyValue != null) {
                                            isOpponentReady = Boolean.parseBoolean(readyValue.toString());
                                        }

                                        Log.d("isOpponentReady0000000000000000", String.valueOf(isOpponentReady));

                                        if (isOpponentReady) {
                                            opponentId = playerId;
                                            String value = playerSnapshot.child("playerBoard").getValue(String.class);
                                            Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                                            assert value != null;
                                            updateOpponentBoard(value);

                                            if (firstTurn) {
                                                myTimer = createMyTimer(innerTimer);
                                                myTimer.start();
                                            }
                                        }

                                        Log.d("isOpponentReady222222222222222", String.valueOf(isOpponentReady));
                                    }
                                } catch (NullPointerException ignored) {
                                    Log.d("NullPointerException", "Tablero del rival sin inicializar");
                                }
                            }
                            else if (!Arrays.deepEquals(playerBoard,
                                    convertStringBoardToArrayBoard(playerSnapshot
                                            .child("playerBoard")
                                            .getValue(String.class)))
                            ) {
                                String value = playerSnapshot.child("playerBoard").getValue(String.class);
                                turnTextView.setText("Tu turno");

                                assert value != null;
                                updatePlayerBoard(value);

                                isPlayerTurn = true;
                                // Iniciar el contador de tiempo
                                if (opponentTimer != null)
                                    opponentTimer.cancel();
                                myTimer = createMyTimer(innerTimer);
                                myTimer.start();
                            }
                        }
                        if(!isPlayerTurnSet) {
                            if (playerCount == 1) {
                                isPlayerTurn = true;
                                firstTurn = true;
                            }
                            else {
                                String turnName = getString(R.string.turn) + " " + opponentName;
                                turnTextView.setText(turnName);
                            }
                            isPlayerTurnSet = true;
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
            Log.d("isOpponentReady3333333333333333333333", String.valueOf(isOpponentReady));
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
                } else if (cellValue == 2) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                } else if (cellValue == -1) {
                    button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
                }
                else {
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

        firstTurn = false;
        if (opponentBoard[row][col] == 1) {
            // El jugador ha acertado
            opponentBoard[row][col] = 2; // Actualizar la matriz "myBoard"

            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red)); // Cambiar el color del botón a rojo
            checkGameOver();

        } else if (opponentBoard[row][col] == -1 || opponentBoard[row][col] == 2) {
            Toast.makeText(MultiPlayerActivity.this, "Casilla no válida", Toast.LENGTH_SHORT).show();
        } else {
            // El jugador ha fallado
            opponentBoard[row][col] = -1; // Actualizar la matriz "myBoard"
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray)); // Cambiar el color del botón a gris

            if(myTimer != null)
                myTimer.cancel();
            isPlayerTurn = false;

            String turnName = getString(R.string.turn) + " " + opponentName;
            turnTextView.setText(turnName);


            // Actualizar tablero en la BD
            database.getReference("lobbies")
                    .child(lobbyKey)
                    .child("players")
                    .child(opponentId)
                    .child("playerBoard")
                    .setValue(Arrays.deepToString(opponentBoard));


            // Contador del rival
            opponentTimer = createCheckOpponentTimer();
            opponentTimer.start();
        }
    }

    private CountDownTimer createMyTimer(int finishSeconds){
        return new CountDownTimer(finishSeconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                // El tiempo está en progreso (se llama cada segundo)
                long secondsRemaining = millisUntilFinished / 1000;
                String message = "Tiempo restante: " + secondsRemaining + " segundos";
                Log.d("Countdown", message);

                innerTimer = (int) secondsRemaining;
                String newString = getString(R.string.player_board) + " (" + innerTimer + ")";
                jugadorText.setText(newString);

                if (database.getReference("lobbies")
                        .child(lobbyKey).getKey() == null){
                    database.getReference("lobbies")
                            .child(lobbyKey)
                            .child("players")
                            .child(playerId)
                            .child("timer")
                            .setValue(String.valueOf(innerTimer));
                }
            }

            public void onFinish() {
                String message = "El tiempo ha expirado";
                Log.d("Countdown", message);

                //PERDERRRRRRRRRRRR
                Log.d("Pierdooooooooooooooooo yoooooooooo", "pierdo");

            }
        };
    }

    private CountDownTimer createCheckOpponentTimer(){
        return new CountDownTimer(360000, 5000) {
            int turnTimer = 0;
            public void onTick(long millisUntilFinished) {
                Log.d("opponentSecondsRemaining", String.valueOf(opponentSecondsRemaining));
                Log.d("turnTimer", String.valueOf(turnTimer));
                if(opponentSecondsRemaining == turnTimer) {
                    Toast.makeText(MultiPlayerActivity.this, "Conexión perdida", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MultiPlayerActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    this.cancel();
                } else {
                    turnTimer = opponentSecondsRemaining;
                }
            }

            public void onFinish() {
                String message = "El tiempo ha expirado";
                Log.d("Countdown", message);

                Toast.makeText(MultiPlayerActivity.this, "Conexión perdida", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MultiPlayerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        };
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
        return board;
    }
}