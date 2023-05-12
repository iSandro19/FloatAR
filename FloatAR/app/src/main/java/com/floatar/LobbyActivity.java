package com.floatar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    private List<Lobby> lobbies;
    private ArrayAdapter<Lobby> lobbyAdapter;
    private DatabaseReference lobbiesRef;

    // Métodos públicos ----------------------------------------------------------------------------

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    /**
     * Método llamado cuando se hace clic en el botón "Crear lobby"
     * @param view La vista que llamó al método
     */
    public void onCreateLobbyClicked(View view) {
        // Mostrar un cuadro de diálogo para que el usuario ingrese el nombre de la lobby y seleccione el número de jugadores
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear lobby");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_lobby, null);
        builder.setView(dialogView);

        // Name
        EditText lobbyNameEditText = dialogView.findViewById(R.id.lobby_name_edit_text);

        builder.setPositiveButton("Crear", (dialog, which) -> {
            String lobbyName = lobbyNameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(lobbyName)) {
                Toast.makeText(this, "El nombre de la lobby no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear la lobby y agregarla a la base de datos
            DatabaseReference newLobbyRef = lobbiesRef.push();
            Lobby lobby = new Lobby(lobbyName);
            lobby.setKey(newLobbyRef.getKey());
            newLobbyRef.setValue(lobby);

            // Agregar el creador de la lobby a la lista de jugadores
            DatabaseReference playersRef = newLobbyRef.child("players");
            String creatorKey = playersRef.push().getKey();
            assert creatorKey != null;
            playersRef.child(creatorKey).setValue(getIntent().getStringExtra("playerName"));

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Lobby creada", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }

    // Métodos protegidos --------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        ListView lobbyList = findViewById(R.id.lobby_list);

        lobbies = new ArrayList<>();
        lobbyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lobbies);
        lobbyList.setAdapter(lobbyAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ps-floatar-default-rtdb.europe-west1.firebasedatabase.app/");
        lobbiesRef = database.getReference("lobbies");

        // Escuchar los cambios en la lista de lobbies
        lobbiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Lobby lobby = snapshot.getValue(Lobby.class);
                if (lobby != null) {
                    lobby.setKey(snapshot.getKey());
                    lobbies.add(lobby);
                    lobbyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // No implementado
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Lobby lobby = snapshot.getValue(Lobby.class);
                if (lobby != null) {
                    lobbies.remove(lobby);
                    lobbyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // No implementado
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No implementado
            }
        });

        // Cuando el usuario seleccione una lobby de la lista, llamar a onLobbySelected
        lobbyList.setOnItemClickListener((parent, view, position, id) -> onLobbySelected(lobbies.get(position)));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.lobby);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Métodos privados ----------------------------------------------------------------------------

    /**
     * Método llamado cuando el usuario selecciona una lobby de la lista
     * @param lobby La lobby seleccionada
     */
    private void onLobbySelected(Lobby lobby) {
        // Mostrar un cuadro de diálogo para que el usuario ingrese su nombre y se una a la lobby
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unirse a " + lobby.getName());

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_join_lobby, null);
        builder.setView(dialogView);

        EditText playerNameEditText = dialogView.findViewById(R.id.player_name_edit_text);

        builder.setPositiveButton("Unirse", (dialog, which) -> {
            String playerName = playerNameEditText.getText().toString().trim();

            if (playerName.isEmpty()) {
                // Si el nombre del jugador está vacío, mostrar un mensaje de error
                Toast.makeText(this, "Por favor ingrese su nombre", Toast.LENGTH_SHORT).show();
            } else {
                // Agregar el jugador a la lista de jugadores de la lobby
                DatabaseReference playersRef = lobbiesRef.child(lobby.getKey()).child("players");
                String key = playersRef.push().getKey();
                if (key == null) {
                    // Si no se pudo obtener una clave, mostrar un mensaje de error
                    Toast.makeText(this, "No se pudo unir a la lobby", Toast.LENGTH_SHORT).show();
                } else {
                    playersRef.child(key).child("name").setValue(playerName);
                    playersRef.child(key).child("key").setValue(key);

                    // Iniciar la actividad del juego y pasar la información de la lobby
                    Intent intent = new Intent(this, CreateBoardActivity.class);
                    intent.putExtra("lobbyName", lobby.getName());
                    intent.putExtra("playerId", key);
                    intent.putExtra("lobbyKey", lobby.getKey());
                    intent.putExtra("gameMode", "multiplayer");
                    startActivity(intent);
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }
}