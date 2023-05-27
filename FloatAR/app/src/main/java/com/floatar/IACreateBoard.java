package com.floatar;

import android.util.Log;
import android.widget.GridLayout;

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
                    this.board[row][i] = 1;
                }
            }else{
                for(int i = row; i <= row + boatType; i++){
                    this.board[i][col] = 1;
                }
            }

            return true;

        }

        return false;
    }

    private boolean isValidPosition(int boatType, int col, int row, int orientation){   // 1 = Vertical, 0 = Horizontal

        int sizeBoat = boatType + 1;

        // Comprobación para colocación horizontal
        if(orientation == 0){
            if(col+sizeBoat <= 10) {

                if (col > 0 && this.board[row][col - 1] == 1) {
                    return false;
                }

                int j = col;

                for (; j < Math.min(col + sizeBoat, 10); j++) {

                    if (row > 0 && row < 9 && (this.board[row][j] == 1 || this.board[row - 1][j] == 1 || this.board[row + 1][j] == 1)) {
                        return false;
                    } else if (row == 0 && (this.board[0][j] == 1 || this.board[row + 1][j] == 1)) {
                        return false;
                    } else if (row == 9 && (this.board[9][j] == 1 || this.board[row - 1][j] == 1)) {
                        return false;
                    }
                }

                if (col == 9) {
                    j++;
                }

                if (j <= 9 && this.board[row][j] == 1) {
                    return false;
                } else if (j == 10) {
                    if (row == 0 && this.board[row + 1][9] == 1) {
                        return false;
                    } else if (row == 9 && this.board[row - 1][9] == 1) {
                        return false;
                    } else
                        return row <= 0 || row >= 9 || (this.board[row - 1][9] != 1 && this.board[row + 1][9] != 1);
                }
                return true;
            } else {
                return false;
            }

            // Lo mismo, pero para barcos verticales
        } else {
            if(row+sizeBoat <= 10) {
                if (row > 0 && this.board[row - 1][col] == 1) {
                    return false;
                }

                int i = row;

                for (; i < Math.min(row + sizeBoat, 10); i++) {
                    if (col > 0 && col < 9 && (this.board[i][col] == 1 || this.board[i][col - 1] == 1 || this.board[i][col + 1] == 1)) {
                        return false;
                    } else if (col == 0 && (this.board[i][0] == 1 || this.board[i][col + 1] == 1)) {
                        return false;
                    } else if (col == 9 && (this.board[i][9] == 1 || this.board[i][col - 1] == 1)) {
                        return false;
                    }
                }

                if (row == 9) {
                    i++;
                }

                if (i <= 9 && this.board[i][col] == 1) {
                    return false;
                } else if (i == 10) {
                    if (col == 0 && this.board[9][col + 1] == 1) {
                        return false;
                    } else if (col == 9 && this.board[9][col - 1] == 1) {
                        return false;
                    } else
                        return col <= 0 || col >= 9 || (this.board[9][col - 1] != 1 && this.board[9][col + 1] != 1);
                }
                return true;
            } else {
                return false;
            }
        }
    }

}
