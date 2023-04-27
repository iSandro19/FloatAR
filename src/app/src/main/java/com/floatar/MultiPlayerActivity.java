package com.floatar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MultiPlayerActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String gameId;
    private String playerId;
    private int[][] playerBoard = new int[10][10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            playerBoard = (int[][]) bundle.getSerializable("playerBoard");
        }

        // Obtener la instancia de la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Obtener una referencia a la ubicación que deseas escribir en la base de datos
        DatabaseReference reference = database.getReference("partidas");

        // Agregar un valor inicial a la base de datos
        reference.setValue("Hello, Firebase!");

        // Generar un identificador único para la partida y el jugador actual
        gameId = generateGameId();
        playerId = generatePlayerId();

        // Conectar con la base de datos de Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Crear un nodo para la partida y guardar el tablero del jugador actual
        //mDatabase.child("games").child(gameId).child("players").child(playerId).setValue(Arrays.asList(playerBoard));

        // Escuchar los cambios en la base de datos para actualizar el tablero del jugador actual
        mDatabase.child("games").child(gameId).child("players").child(playerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerBoard = dataSnapshot.getValue(int[][].class);
                // Actualizar la pantalla del juego con el tablero del jugador actual
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(".MultiPlayerActivity", "onCancelled", databaseError.toException());
            }
        });

        // Agregar un listener a la referencia de la base de datos para leer su valor
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el valor de la base de datos y mostrarlo en un Toast
                String value = dataSnapshot.getValue(String.class);
                Toast.makeText(MultiPlayerActivity.this, value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la base de datos aquí
            }
        });


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    // Actualizar la pantalla del juego con el tablero del jugador actual
    private void updateUI() {
        // TODO: Implementar la actualización de la pantalla del juego con el tablero del jugador actual
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Eliminar el nodo del jugador actual al salir de la actividad
            mDatabase.child("games").child(gameId).child("players").child(playerId).removeValue();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}