package com.floatar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Board playerBoard;
    private Board computerBoard;
    private Game game;
    private boolean isPlayerTurn;

    private Button[][] playerButtons;
    private Button[][] computerButtons;
    private TextView turnTextView;
    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencias a los elementos de la interfaz de usuario
        turnTextView = findViewById(R.id.turnTextView);
        scoreTextView = findViewById(R.id.scoreTextView);

        // Crear los tableros y el juego
        playerBoard = new Board();
        computerBoard = new Board();
        game = new Game(playerBoard, computerBoard);

        // Inicializar las variables de los botones y los indicadores de turno y puntaje
        playerButtons = new Button[10][10];
        computerButtons = new Button[10][10];
        isPlayerTurn = true;
        updateTurnTextView();

        // Configurar los botones del jugador
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int buttonId = getResources().getIdentifier("playerButton" + i + j, "id", getPackageName());
                playerButtons[i][j] = findViewById(buttonId);
                playerButtons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Lógica para el jugador hace clic en un botón
                    }
                });
            }
        }

        // Configurar los botones de la computadora
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int buttonId = getResources().getIdentifier("computerButton" + i + j, "id", getPackageName());
                computerButtons[i][j] = findViewById(buttonId);
                computerButtons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Lógica para el jugador hace clic en un botón de la computadora
                    }
                });
            }
        }
    }

    // Método para actualizar el indicador de turno
    private void updateTurnTextView() {
        if (isPlayerTurn) {
            turnTextView.setText(R.string.player_turn);
        } else {
            turnTextView.setText(R.string.computer_turn);
        }
    }

    // Método para actualizar el indicador de puntaje
    private void updateScoreTextView() {
        scoreTextView.setText(getString(Integer.parseInt("Score"), game.getPlayerScore(), game.getComputerScore()));
    }
}