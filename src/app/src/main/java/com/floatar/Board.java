package com.floatar;

public class Board {
    public static final int SIZE = 10; // tamaño del tablero
    private final int[][] board; // matriz para almacenar el estado de cada celda
    private int score; // puntaje del jugador

    public Board() {
        board = new int[SIZE][SIZE];
        initializeBoard();
    }

    // inicializa todas las celdas del tablero en 0
    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }

    // establece el estado de una celda en la fila i, columna j
    public void setCell(int i, int j, int value) {
        board[i][j] = value;
    }

    // devuelve el estado de una celda en la fila i, columna j
    public int getCell(int i, int j) {
        return board[i][j];
    }

    // devuelve el puntaje del jugador
    public int getScore() {
        return score;
    }

    // establece el puntaje del jugador
    public void setScore(int score) {
        this.score = score;
    }

    // devuelve true si la celda en la fila i, columna j está vacía
    public boolean isEmpty(int i, int j) {
        return board[i][j] == 0;
    }

    // devuelve true si la celda en la fila i, columna j tiene una parte de un barco
    public boolean hasShip(int i, int j) {
        return board[i][j] == 1;
    }

    // devuelve true si la celda en la fila i, columna j ha sido disparada
    public boolean hasBeenFired(int i, int j) {
        return board[i][j] == 2 || board[i][j] == 3;
    }

    // establece el estado de la celda en la fila i, columna j como disparada y devuelve true si tenía una parte de un barco
    public boolean fireAt(int i, int j) {
        if (board[i][j] == 0) {
            board[i][j] = 2; // agua
            return false;
        } else if (board[i][j] == 1) {
            board[i][j] = 3; // parte del barco
            return true;
        } else {
            return false; // ya había sido disparada
        }
    }

    // devuelve true si todas las partes de los barcos en el tablero han sido destruidas
    public boolean allShipsDestroyed() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    return false;
                }
            }
        }
        return true;
    }
}
