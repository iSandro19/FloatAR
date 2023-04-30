package com.floatar;

import java.util.Random;

public class IACreateBoard {

    private int[][] board = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    public int[][] getBoard() {
        return this.board;
    }

    public void fillBoard(){

        int maxNumBoats1 = 3;
        int maxNumBoats2 = 2;
        int maxNumBoats3 = 3;
        int maxNumBoats4 = 2;

        Random rand = new Random();
        int randomBoat;

        int randomCol;
        int randomRow;
        int randomOrientation;

        // Rellenar tablero
        while(maxNumBoats1 > 0 || maxNumBoats2 > 0 || maxNumBoats3 > 0 || maxNumBoats4 > 0) {

            randomBoat = rand.nextInt(4);

            if (randomBoat == 0 && maxNumBoats1 > 0){

                // Escoger posición válida

                do {
                    randomCol = rand.nextInt(10);
                    randomRow = rand.nextInt(10);
                    randomOrientation = rand.nextInt(2);
                }while(!addBoat(randomBoat, randomCol, randomRow, randomOrientation));

                maxNumBoats1 = maxNumBoats1 - 1;

            }else if (randomBoat == 1 && maxNumBoats2 > 0){

                // Escoger posición válida

                do {
                    randomCol = rand.nextInt(10);
                    randomRow = rand.nextInt(10);
                    randomOrientation = rand.nextInt(2);
                }while(!addBoat(randomBoat, randomCol, randomRow, randomOrientation));

                maxNumBoats2 = maxNumBoats2 - 1;

            }else if (randomBoat == 2 && maxNumBoats3 > 0){

                // Escoger posición válida

                do {
                    randomCol = rand.nextInt(10);
                    randomRow = rand.nextInt(10);
                    randomOrientation = rand.nextInt(2);
                }while(!addBoat(randomBoat, randomCol, randomRow, randomOrientation));

                maxNumBoats3 = maxNumBoats3 - 1;

            }else if (randomBoat == 3 && maxNumBoats4 > 0){

                // Escoger posición válida

                do {
                    randomCol = rand.nextInt(10);
                    randomRow = rand.nextInt(10);
                    randomOrientation = rand.nextInt(2);
                }while(!addBoat(randomBoat, randomCol, randomRow, randomOrientation));

                maxNumBoats4 = maxNumBoats4 - 1;

            }
        }

    }

    private boolean addBoat(int boatType, int col, int row, int orientation){

        if(isValidPosition(boatType, col, row, orientation)){
            if(orientation == 0){
                for(int i = col; i <= col + boatType; i++){
                    this.board[i][row] = 1;
                }
            }else{
                for(int i = row; i <= row + boatType; i++){
                    this.board[col][i] = 1;
                }
            }

            return true;

        }

        return false;
    }

    private boolean isValidPosition(int boatType, int col, int row, int orientation){   // 1 = Vertical, 0 = Horizontal
        int max = 9;
        int min = 0;

        int actualPos;
        int izq, der;

        // Comprobación para colocación horizontal
        if(orientation == 0){

            // Primero, si no se encuentra en la columna 0, se comprueba si antes ya hay un barco
            if((col - 1) > min){
                if(this.board[col-1][row] == 1) return false;
            }

            // Después los laterales
            for(int i = 0; i<=boatType; i++){

                actualPos = col + i;
                izq = row-1;
                der = row+1;

                if(actualPos <= max) {

                    if (this.board[actualPos][row] == 0){

                        // Hacemos estas comprobaciones de izq y der para evitar salirse de los límites y que de un NullPointerException
                        if(izq < min){
                            if(this.board[actualPos][der] == 1) return false;

                        }else {

                            if (der > max){
                                if(this.board[actualPos][izq] == 1) return false;
                            }
                            else if(this.board[actualPos][der] == 1 || this.board[actualPos][izq] == 1) return false;
                        }

                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }

            // Por último, si su última posición no es la columna 9, si ya lo tiene después
            if((col + boatType + 1) < max){
                if(this.board[col + boatType + 1][row] == 1) return false;
            }


        // Lo mismo, pero para barcos verticales
        }else{
            // Primero, si no se encuentra en la columna 0, se comprueba si antes ya hay un barco
            if((row - 1) > min){
                if(this.board[col][row-1] == 1) return false;
            }

            // Después los laterales
            for(int i = 0; i<=boatType; i++){

                actualPos = row + i;
                izq = col-1;
                der = col+1;

                if(actualPos <= max) {

                    if (this.board[col][actualPos] == 0){

                        // Hacemos estas comprobaciones de izq y der para evitar salirse de los límites y que de un NullPointerException
                        if(izq < min){
                            if(this.board[der][actualPos] == 1) return false;

                        }else {

                            if (der > max){
                                if(this.board[izq][actualPos] == 1) return false;
                            }
                            else if(this.board[der][actualPos] == 1 || this.board[izq][actualPos] == 1) return false;
                        }

                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }

            // Por último, si su última posición no es la columna 9, si ya lo tiene después
            if((row + boatType + 1) < max){
                if(this.board[col][row + boatType + 1] == 1) return false;
            }
        }

        // Si pasa entero el método, es que se puede colocar ahí
        return true;
    }

}
