package com.floatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateBoardActivity extends AppCompatActivity {
    private final int[][] playerBoard = new int[10][10];
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://ps-floatar-default-rtdb.europe-west1.firebasedatabase.app/");
    private Button boat1, boat2, boat3, boat4, horizontal, vertical, confirm;
    private Context mContext;
    private int numBoats1, numBoats2, numBoats3, numBoats4, sizeBoat, orientation;

    // Sounds

    private MediaPlayer settingsSound;
    private MediaPlayer abouthelpSound;
    private MediaPlayer pianoD;
    private MediaPlayer pianoE;
    private MediaPlayer pianoF;
    private MediaPlayer pianoG;
    private MediaPlayer pianoA;

    private List<MediaPlayer> melody = new ArrayList<>();
    private int index;
    private ColorStateList originalColor;


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
                abouthelpSound.start();
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.layout_menu_main_settings:
                settingsSound.start();
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.layout_menu_main_about:
                abouthelpSound.start();
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

    // Métodos protegidos --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);

        // Sounds
        abouthelpSound = MediaPlayer.create(this, R.raw.about_help);
        settingsSound = MediaPlayer.create(this, R.raw.settings_in);
        pianoD = MediaPlayer.create(this, R.raw.piano_d);
        pianoE = MediaPlayer.create(this, R.raw.piano_e);
        pianoF = MediaPlayer.create(this, R.raw.piano_f);
        pianoG = MediaPlayer.create(this, R.raw.piano_g);
        pianoA = MediaPlayer.create(this, R.raw.piano_a);

        // Create melody
        initialiceMelody();

        numBoats1 = 0;
        numBoats2 = 0;
        numBoats3 = 0;
        numBoats4 = 0;
        sizeBoat = 1;
        orientation = 0;

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        mContext = this;
        createBoard();

        boat1 = findViewById(R.id.but_boat_s1);
        boat2 = findViewById(R.id.but_boat_s2);
        boat3 = findViewById(R.id.but_boat_s3);
        boat4 = findViewById(R.id.but_boat_s4);
        originalColor = boat4.getTextColors(); //Coge el color naranja
        horizontal = findViewById(R.id.but_horizontal_boat);
        vertical = findViewById(R.id.but_vertical_boat);
        confirm = findViewById(R.id.confirm);

        // Lista de barcos creados y por crear
        updateBoatsLeftTextView();

        View.OnClickListener optionsClickListener = v -> {
            if (v == boat1){
                sizeBoat = 1;
                boat1.setTextColor(Color.BLACK);
                boat2.setTextColor(originalColor);
                boat3.setTextColor(originalColor);
                boat4.setTextColor(originalColor);

            } else if (v == boat2) {
                sizeBoat = 2;
                boat1.setTextColor(originalColor);
                boat2.setTextColor(Color.BLACK);
                boat3.setTextColor(originalColor);
                boat4.setTextColor(originalColor);

            } else if (v == boat3) {
                sizeBoat = 3;
                boat1.setTextColor(originalColor);
                boat2.setTextColor(originalColor);
                boat3.setTextColor(Color.BLACK);
                boat4.setTextColor(originalColor);

            } else if (v == boat4) {
                sizeBoat = 4;
                boat1.setTextColor(originalColor);
                boat2.setTextColor(originalColor);
                boat3.setTextColor(originalColor);
                boat4.setTextColor(Color.BLACK);

            } else if (v == horizontal) {
                orientation = 0;
                horizontal.setTextColor(Color.BLACK);
                vertical.setTextColor(originalColor);
            } else if (v == vertical) {
                orientation = 1;
                horizontal.setTextColor(originalColor);
                vertical.setTextColor(Color.BLACK);
            } else if (v == confirm) {
                if (numBoats1 == 3 && numBoats2 == 2 && numBoats3 == 3 && numBoats4 == 2){
                    Bundle extras = getIntent().getExtras();
                    // Play melody
                    melody.get(index).start();
                    index = 0;
                    if(extras != null) {
                        if(extras.getString("gameMode").equals("multiplayer")) {
                            database.getReference("lobbies")
                                    .child(extras.getString("lobbyKey"))
                                    .child("players")
                                    .child(extras.getString("playerId"))
                                    .child("playerBoard")
                                    .setValue(Arrays.deepToString(playerBoard));
                            Intent startGame = new Intent(CreateBoardActivity.this, MultiPlayerActivity.class);
                            startGame.putExtra("playerBoard", playerBoard);
                            startGame.putExtra("lobbyName", extras.getString("lobbyName"));
                            startGame.putExtra("playerId", extras.getString("playerId"));
                            startGame.putExtra("lobbyKey", extras.getString("lobbyKey"));
                            startActivity(startGame);

                        } else if(extras.getString("gameMode").equals("singleplayer")) {
                            Intent startGame = new Intent(CreateBoardActivity.this, SinglePlayerActivity.class);
                            startGame.putExtra("playerBoard", playerBoard);
                            startGame.putExtra("opponentBoard", createRivalBoard());
                            startActivity(startGame);
                        } else {
                            Log.e("CreateBoardActivity", "Error al obtener el modo de juego");
                        }
                    }
                } else {
                    Toast.makeText(CreateBoardActivity.this, "Faltan barcos por colocar", Toast.LENGTH_SHORT).show();
                }
            }
        };

        boat1.setOnClickListener(optionsClickListener);
        boat2.setOnClickListener(optionsClickListener);
        boat3.setOnClickListener(optionsClickListener);
        boat4.setOnClickListener(optionsClickListener);
        horizontal.setOnClickListener(optionsClickListener);
        vertical.setOnClickListener(optionsClickListener);
        confirm.setOnClickListener(optionsClickListener);

        boat1.setTextColor(Color.BLACK);
        horizontal.setTextColor(Color.BLACK);

        // OnClickListener para los botones del tablero
        View.OnClickListener buttonClickListener = v -> {
            // Obtener la etiqueta del botón
            String tag = (String) v.getTag();

            // Obtener la posición del botón a partir de su etiqueta
            int row = Integer.parseInt(tag.split("_")[1]);
            int col = Integer.parseInt(tag.split("_")[2]);

            // Ejecutar la lógica del juego correspondiente
            if (playerBoard[row][col] == 0) {
                if (sizeBoat == 1 && numBoats1 < 3){
                    createBoat(v, row, col);
                } else if (sizeBoat == 2 && numBoats2 < 2){
                    createBoat(v, row, col);
                } else if (sizeBoat == 3 && numBoats3 < 3) {
                    createBoat(v, row, col);
                } else if (sizeBoat == 4 && numBoats4 < 2){
                    createBoat(v, row, col);
                } else {
                    Toast.makeText(CreateBoardActivity.this, "No puedes colocar más barcos de este tamaño", Toast.LENGTH_SHORT).show();
                }
            } else if (playerBoard[row][col] == 1) {
                index--;
                deleteBoat(v, row, col);
            }
        };

        // Asignar el OnClickListener a cada botón
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            child.setOnClickListener(buttonClickListener);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.create_board);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pianoD != null){
            pianoD.release();
            pianoD = null;
        }
        if(pianoE != null){
            pianoE.release();
            pianoE = null;
        }
        if(pianoF != null){
            pianoF.release();
            pianoF = null;
        }
        if(pianoG != null){
            pianoG.release();
            pianoG = null;
        }
        if(pianoA != null){
            pianoA.release();
            pianoA = null;
        }
    }

    // Métodos privados ----------------------------------------------------------------------------

    /**
     * Actualiza el texto de la parte inferior de la pantalla con los barcos que quedan por colocar
     */
    private void updateBoatsLeftTextView() {
        String boatsLeftString = getString(R.string.boats_left);
        boatsLeftString += (3 - numBoats1) + " " + getString(R.string.boats_left_1);
        boatsLeftString += (2 - numBoats2) + " " + getString(R.string.boats_left_2);
        boatsLeftString += (3 - numBoats3) + " " + getString(R.string.boats_left_3);
        boatsLeftString += (2 - numBoats4) + " " + getString(R.string.boats_left_4);

        TextView boats = findViewById(R.id.text_view_boats_left);
        boats.setText(boatsLeftString);
        boats.setGravity(Gravity.CENTER);
    }

    /**
     * Obtiene el ancho de la pantalla
     */
    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Crea el tablero de juego
     */
    private void createBoard(){
        // Obtener el GridLayout del layout XML
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // Crear un bucle para agregar los botones
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                // Crear el botón y asignarle una etiqueta que identifique su posición
                Button button = new Button(this);
                button.setTag("pos_" + row + "_" + col);

                //button.se tBackgroundResource(R.drawable.casilla);

                // Agregar el botón al GridLayout
                int size = (getScreenWidth() - 100)/10;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                params.setMargins(3, 3, 3, 3);
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                button.setLayoutParams(params);
                gridLayout.addView(button);
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
            }
        }
    }

    /**
     * Crea el tablero de la IA
     * @return tablero de la IA
     */
    private int[][] createRivalBoard(){
        IACreateBoard iaBoard = new IACreateBoard();
        iaBoard.fillBoard();

        return iaBoard.getBoard();
    }

    /**
     * Crea un barco en el tablero
     * @param v botón pulsado
     * @param row fila del botón
     * @param col columna del botón
     */
    private void createBoat(View v, int row, int col){
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        Log.d("Barcos1: ", String.valueOf(numBoats1));
        Log.d("Barcos2: ", String.valueOf(numBoats2));
        Log.d("Barcos3: ", String.valueOf(numBoats3));
        Log.d("Barcos4: ", String.valueOf(numBoats4));

        if (orientation == 0){
            if (checkHorizontalBoat(row, col)){
                if(col+sizeBoat <= 10){
                    for (int j = col; j < col+sizeBoat; j++) {
                        playerBoard[row][j] = 1;
                        View button = gridLayout.getChildAt(row * 10 + j);
                        button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                    }
                    // Cambiar el color del botón pulsado
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));

                    sumBoat();  //Sumamos el tipo de barco
                    // Play melody
                    melody.get(index).start();
                    index++;
                } else {
                    Toast.makeText(CreateBoardActivity.this, "Espacio insuficiente", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CreateBoardActivity.this, "Imposible crear barco", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (checkVerticalBoat(row, col)){
                if(row+sizeBoat <= 10){
                    for (int i = row; i < row+sizeBoat; i++) {
                        playerBoard[i][col] = 1;
                        View button = gridLayout.getChildAt(i * 10 + col);
                        button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                    }

                    // Cambiar el color del botón pulsado
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));

                    sumBoat();  //Sumamos el tipo de barco
                } else {
                    Toast.makeText(CreateBoardActivity.this, "Espacio insuficiente", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CreateBoardActivity.this, "Imposible crear barco", Toast.LENGTH_SHORT).show();
            }
        }

        // Lista de barcos creados y por crear
        updateBoatsLeftTextView();
    }

    /**
     * Comprueba si se puede crear un barco en horizontal
     * @param row fila
     * @param col columna
     * @return true si se puede crear, false si no
     */
    private Boolean checkHorizontalBoat(int row, int col){
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        if (col > 0 && getValueFromButton(gridLayout.getChildAt(row * 10 + col - 1)) == 1) {
            return false;
        }

        int j = col;

        for (; j < Math.min(col+sizeBoat, 10); j++) {
            if (row > 0 && row < 9 && (getValueFromButton(gridLayout.getChildAt(row * 10 + j)) == 1 || getValueFromButton(gridLayout.getChildAt((row - 1) * 10 + j)) == 1 || getValueFromButton(gridLayout.getChildAt((row + 1) * 10 + j)) == 1)) {
                return false;
            } else if (row == 0 && (getValueFromButton(gridLayout.getChildAt(j)) == 1 || getValueFromButton(gridLayout.getChildAt((row + 1) * 10 + j)) == 1)) {
                return false;
            } else if (row == 9 && (getValueFromButton(gridLayout.getChildAt(row * 10 + j)) == 1 || getValueFromButton(gridLayout.getChildAt((row - 1) * 10 + j)) == 1)) {
                return false;
            }
        }

        if (col == 9) {
            j++;
        }

        if (j <= 9 && getValueFromButton(gridLayout.getChildAt(row * 10 + j)) == 1) {
            return false;
        } else if (j == 10) {
            if (row == 0 && getValueFromButton(gridLayout.getChildAt((row + 1) * 10 + 9)) == 1) {
                return false;
            } else if (row == 9 && getValueFromButton(gridLayout.getChildAt((row - 1) * 10 + 9)) == 1) {
                return false;
            } else return row <= 0 || row >= 9 || (getValueFromButton(gridLayout.getChildAt((row - 1) * 10 + 9)) != 1 && getValueFromButton(gridLayout.getChildAt((row + 1) * 10 + 9)) != 1);
        }
        return true;
    }

    /**
     * Comprueba si se puede crear un barco en vertical
     * @param row fila
     * @param col columna
     * @return true si se puede crear, false si no
     */
    private Boolean checkVerticalBoat(int row, int col){
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        if (row > 0 && getValueFromButton(gridLayout.getChildAt((row - 1) * 10 + col)) == 1){
            return false;
        }

        int i = row;

        for (; i < Math.min(row+sizeBoat, 10); i++) {
            if (col > 0 && col < 9 && (getValueFromButton(gridLayout.getChildAt(i * 10 + col)) == 1 || getValueFromButton(gridLayout.getChildAt(i * 10 + col - 1)) == 1 || getValueFromButton(gridLayout.getChildAt(i * 10 + col + 1)) == 1)) {
                return false;
            } else if (col == 0 && (getValueFromButton(gridLayout.getChildAt(i * 10 + col)) == 1 || getValueFromButton(gridLayout.getChildAt(i * 10 + col + 1)) == 1)) {
                return false;
            } else if (col == 9 && (getValueFromButton(gridLayout.getChildAt(i * 10 + col)) == 1 || getValueFromButton(gridLayout.getChildAt(i * 10 + col - 1)) == 1)) {
                return false;
            }
        }

        if (row == 9) {
            i++;
        }

        if (i <= 9 && getValueFromButton(gridLayout.getChildAt(i * 10 + col)) == 1) {
            return false;
        } else if (i == 10) {
            if (col == 0 && getValueFromButton(gridLayout.getChildAt(9 * 10 + col + 1)) == 1) {
                return false;
            } else if (col == 9 && getValueFromButton(gridLayout.getChildAt(9 * 10 + col - 1)) == 1) {
                return false;
            } else return col <= 0 || col >= 9 || (getValueFromButton(gridLayout.getChildAt(9 * 10 + col - 1)) != 1 && getValueFromButton(gridLayout.getChildAt(9 * 10 + col + 1)) != 1);
        }
        return true;
    }

    /**
     * Obtiene los botones adyacentes a la casilla seleccionada
     * @param row fila
     * @param col columna
     * @return lista de botones adyacentes
     */
    private List<View> getAdjacentButtons(int row, int col) {
        List<View> adjacentButtons = new ArrayList<>();

        // Obtener el GridLayout del layout XML
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // Obtener las casillas adyacentes
        if (row > 0) {
            View topButton = gridLayout.getChildAt((row - 1) * 10 + col);
            if (getValueFromButton(topButton) == 1) {
                adjacentButtons.add(topButton);
                int i = 2;
                while ((row - i) >= 0 && getValueFromButton(gridLayout.getChildAt((row - i) * 10 + col)) == 1){
                    adjacentButtons.add(gridLayout.getChildAt((row - i) * 10 + col));
                    i++;
                }
            }
        }

        if (row < 9) {
            View bottomButton = gridLayout.getChildAt((row + 1) * 10 + col);
            if (getValueFromButton(bottomButton) == 1) {
                adjacentButtons.add(bottomButton);
                int i = 2;
                while ((row + i) <= 9 && getValueFromButton(gridLayout.getChildAt((row + i) * 10 + col)) == 1){
                    adjacentButtons.add(gridLayout.getChildAt((row + i) * 10 + col));
                    i++;
                }
            }
        }

        if (col > 0) {
            View leftButton = gridLayout.getChildAt(row * 10 + col - 1);
            if (getValueFromButton(leftButton) == 1) {
                adjacentButtons.add(leftButton);
                int i = 2;
                while ((col - i) >= 0 && getValueFromButton(gridLayout.getChildAt(row * 10 + col - i)) == 1){
                    adjacentButtons.add(gridLayout.getChildAt(row * 10 + col - i));
                    i++;
                }
            }
        }

        if (col < 9) {
            View rightButton = gridLayout.getChildAt(row * 10 + col + 1);
            if (getValueFromButton(rightButton) == 1) {
                adjacentButtons.add(rightButton);
                int i = 2;
                while ((col + i) <= 9 && getValueFromButton(gridLayout.getChildAt(row * 10 + col + i)) == 1){
                    adjacentButtons.add(gridLayout.getChildAt(row * 10 + col + i));
                    i++;
                }
            }
        }

        return adjacentButtons;
    }

    /**
     * Obtiene el valor de la casilla seleccionada
     * @param button casilla seleccionada
     * @return valor de la casilla
     */
    private int getValueFromButton(View button){
        String tag = (String) button.getTag();

        // Obtener la posición del botón a partir de su etiqueta
        int row = Integer.parseInt(tag.split("_")[1]);
        int col = Integer.parseInt(tag.split("_")[2]);

        return playerBoard[row][col];
    }

    /**
     * Elimina el barco seleccionado
     * @param v vista del botón
     * @param row fila
     * @param col columna
     */
    private void deleteBoat(View v ,int row, int col){
        playerBoard[row][col] = 0;
        v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));

        List<View> adjacentButtons = getAdjacentButtons(row, col);
        int i = 1;
        for (View button : adjacentButtons) {
            String tag = (String) button.getTag();
            int adjRow = Integer.parseInt(tag.split("_")[1]);
            int adjCol = Integer.parseInt(tag.split("_")[2]);
            playerBoard[adjRow][adjCol] = 0;
            button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.light_brown));
            i++;
        }
        subBoat(i);
        updateBoatsLeftTextView();
    }

    /**
     * Actualiza el contador de los barcos restantes (sumando)
     */
    private void sumBoat(){
        switch (sizeBoat) {
            case 1:
                numBoats1++;
                break;
            case 2:
                numBoats2++;
                break;
            case 3:
                numBoats3++;
                break;
            case 4:
                numBoats4++;
                break;
            default:
                Log.d("Error", "Tamaño imposible");
        }
    }

    /**
     * Actualiza el contador de los barcos restantes (restando)
     * @param deleteBoat tamaño del barco eliminado
     */
    private void subBoat(int deleteBoat){
        switch (deleteBoat) {
            case 1:
                numBoats1--;
                break;
            case 2:
                numBoats2--;
                break;
            case 3:
                numBoats3--;
                break;
            case 4:
                numBoats4--;
                break;
            default:
                Log.d("Error", "Tamaño imposible");
        }
    }

    private void initialiceMelody(){
        index = 0;
        melody.add(pianoD);
        melody.add(pianoE);
        melody.add(pianoF);
        melody.add(pianoA);
        melody.add(pianoG);
        melody.add(pianoF);
        melody.add(pianoE);
        melody.add(pianoF);
        melody.add(pianoG);
        melody.add(pianoA);
        melody.add(pianoG);
    }
}