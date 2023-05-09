package com.floatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MultiPlayerActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String gameId;
    private String playerId;
    private int[][] playerBoard = new int[10][10];
    private final int[][] opponentBoard = new int[10][10];
    private final Button[][] playerButtonGrid = new Button[10][10];
    private Context mContext;
    private boolean playerTurn = true;

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
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();

        // Generar un identificador único para la partida y el jugador actual
        gameId = generateGameId();
        playerId = generatePlayerId();

        TextView tv = findViewById(R.id.text_view_multi_player);
        tv.setText("Game ID: " + gameId + "\nPlayer ID: " + playerId);

        // Conectar con la base de datos de Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Crear un nodo para la partida y guardar el tablero del jugador actual
        mDatabase
            .child("games")
            .child(gameId)
            .child("players")
            .child(playerId)
            .setValue(Arrays.deepToString(playerBoard));

        // Escuchar los cambios en la base de datos para actualizar el tablero del jugador actual
        mDatabase
            .child("games")
            .child(gameId)
            .child("players")
            .child(playerId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    Toast.makeText(MultiPlayerActivity.this, value, Toast.LENGTH_SHORT).show();
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
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.multi_player);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void playerTurn(View v, int row, int col) {
        // TODO
    }

    private void opponentTurn() {
        // TODO: Implementar la lógica del juego para el multijador
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

    // Generar un identificador único para la partida
    private String generateGameId() {
        // Implementación para generar un identificador único usando la fecha y hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Generar un identificador único para el jugador actual
    private String generatePlayerId() {
        // Implementación para generar un identificador único usando el ID del dispositivo y la fecha y hora actual
        //String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return Settings.Secure.ANDROID_ID + "_" + sdf.format(new Date());
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
                mDatabase.child("games").child(gameId).child("players").child(playerId).removeValue();
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