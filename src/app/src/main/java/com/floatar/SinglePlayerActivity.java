package com.floatar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerActivity extends AppCompatActivity {
    private final int[][] myBoard = new int[10][10];
    private Context mContext;
    int numBoats, sizeBoat, orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        numBoats = 0;
        sizeBoat = 2;
        orientation = 1;

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        mContext = this;
        createBoard();

        // Meter botones de selección de barcos y de orientación

        // OnClickListener para los botones del tablero
        View.OnClickListener buttonClickListener = v -> {
            // Obtener la etiqueta del botón
            String tag = (String) v.getTag();

            // Obtener la posición del botón a partir de su etiqueta
            int row = Integer.parseInt(tag.split("_")[1]);
            int col = Integer.parseInt(tag.split("_")[2]);

            // Ejecutar la lógica del juego correspondiente
            if (myBoard[row][col] == 0) {
                createBoat(v, row, col);
            } else if (myBoard[row][col] == 1) {
                deleteBoat(v, row, col);
            }
        };

        // Asignar el OnClickListener a cada botón
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            child.setOnClickListener(buttonClickListener);
        }

    }

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private void createBoard(){
        // Obtener el GridLayout del layout XML
        GridLayout gridLayout = findViewById(R.id.gridLayout);

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
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple_200));
            }
        }
    }

    private void createBoat(View v, int row, int col){
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        List<View> adjacentButtons = new ArrayList<>();
        if (numBoats < 10){
            if (orientation == 0){
                if (checkHorizontalBoat(row, col)){
                    for (int j = col; j < col+sizeBoat; j++) {
                        myBoard[row][j] = 1;
                        View button = gridLayout.getChildAt(row * 10 + j);
                        adjacentButtons.add(button);
                    }
                    // Cambiar el color de los botones adyacentes
                    for (View button : adjacentButtons) {
                        button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                    }

                    // Cambiar el color del botón pulsado
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                    numBoats++;
                } else {
                    Toast.makeText(SinglePlayerActivity.this, "Imposible crear barco", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                if (checkVerticalBoat(row, col)){
                    for (int i = row; i < row+sizeBoat; i++) {
                        myBoard[i][col] = 1;
                        View button = gridLayout.getChildAt(i * 10 + col);
                        adjacentButtons.add(button);
                    }
                    // Cambiar el color de los botones adyacentes
                    for (View button : adjacentButtons) {
                        button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                    }

                    // Cambiar el color del botón pulsado
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                    numBoats++;
                } else {
                    Toast.makeText(SinglePlayerActivity.this, "Imposible crear barco", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(SinglePlayerActivity.this, "Demasiados barcos", Toast.LENGTH_SHORT).show();
            return;
        }

        myBoard[row][col] = 1;
        v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
    }

    private void deleteBoat(View v ,int row, int col){ ///////////Propagar la eliminación/////////7
        myBoard[row][col] = 0;
        v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple_200));
        numBoats--;
    }

    private Boolean checkHorizontalBoat(int row, int col){
        return true;
    }

    private Boolean checkVerticalBoat(int row, int col){
        return true;
    }

    /*
    private Boolean checkPositions(int row, int col){

        // Comprobar si ya se han seleccionado los 10 barcos
        if (numBoats < 10) {
            // Comprobar si la casilla seleccionada es adyacente a otra casilla previamente seleccionada
            checkAdjacency(row, col);

            if (checkValidBoat(row, col)){
                return true;
            } else {
                Toast.makeText(SinglePlayerActivity.this, "Barco no válido", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {

        }
    }

    private void checkMarkedBoats(int row, int col){

        for (int i = 0; i < myBoard.length; i++) {
            for (int j = 0; j < myBoard[i].length; j++) {
                if (myBoard[i][j] == 1) {
                    if ((row == i && Math.abs(col - j) == 1) || (col == j && Math.abs(row - i) == 1)) {
                        Log.d("Adyaciencia", "Adyacente");
                        numBoats++;
                    }
                }
            }
        }
    }*/
}