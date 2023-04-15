package com.floatar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class SinglePlayerActivity extends AppCompatActivity {

    private final int[][] myBoard = new int[10][10];
    private final int[][] rivalBoard = new int[10][10];
    private Context mContext;

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
            if (myBoard[row][col] == 0) {

            } else if (myBoard[row][col] == 1) {

            }
        };

        // Asignar el OnClickListener a cada botón
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            child.setOnClickListener(buttonClickListener);
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
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

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private void createBoard(){
        // Obtener el GridLayout del layout XML
        GridLayout gridLayout = findViewById(R.id.gridLayout_myBoard);

        // Crear un bucle para agregar los botones
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                // Crear el botón y asignarle una etiqueta que identifique su posición
                Button button = new Button(this);
                button.setTag("pos_" + row + "_" + col);

                //button.setBackgroundResource(R.drawable.casilla);

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

    @Override
    public void onBackPressed() {       //Se puede recuperar la actividad de main????????
        super.onBackPressed();
        Intent backPress = new Intent(SinglePlayerActivity.this, MainActivity.class);
        startActivity(backPress);
    }
}