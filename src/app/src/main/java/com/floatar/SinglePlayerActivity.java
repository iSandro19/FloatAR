package com.floatar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

public class SinglePlayerActivity extends AppCompatActivity {
    private final int[][] myBoard = new int[10][10];
    private Context mContext;
    int numBoats, sizeBoat, orientation;

    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        numBoats = 0;

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
                int size = (getScreenWidth() - 100) / 10;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                params.setMargins(2, 2, 2, 2);
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                button.setLayoutParams(params);
                gridLayout.addView(button);
                button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple_200));
            }
        }
    }


    private void createBoat(View v, int row, int col){
        if (numBoats < 10){
            if (orientation == 0){
                if (checkHorizontalBoat(row, col)){
                    for (int i = 0; i < sizeBoat; i++) {
                        System.out.println("Check horizontal");
                    }
                    numBoats++;
                } else {
                    Toast.makeText(SinglePlayerActivity.this, "Imposible crear barco", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                if (checkVerticalBoat(row, col)){
                    for (int j = 0; j < sizeBoat; j++) {
                        System.out.println("Check vertical");
                    }
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