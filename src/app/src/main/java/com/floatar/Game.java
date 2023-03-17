package com.floatar;

import java.util.Random;

public class Game {
    private Board playerBoard; // tablero del jugador
    private Board computerBoard; // tablero de la computadora

    public Game(Board playerBoard, Board computerBoard) {
        this.playerBoard = playerBoard;
        this.computerBoard = computerBoard;
    }

    // devuelve el tablero del jugador
    public Board getPlayerBoard() {
        return playerBoard;
    }

    // devuelve el tablero de la computadora
    public Board getComputerBoard() {
        return computerBoard;
    }

    // inicializa el tablero del jugador con barcos en ubicaciones aleatorias
    public void initializePlayerBoard() {
        int[] shipSizes = {5, 4, 3, 3, 2}; // tamaños de los barcos
        Random random = new Random();
        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int orientation = random.nextInt(2); // horizontal o vertical
                int row = random.nextInt(Board.SIZE);
                int col = random.nextInt(Board.SIZE);
                if (orientation == 0) { // horizontal
                    if (col + size <= Board.SIZE) { // barco encaja en el tablero
                        boolean overlap = false;
                        for (int j = col; j < col + size; j++) { // comprueba si hay solapamiento con otros barcos
                            if (!playerBoard.isEmpty(row, j)) {
                                overlap = true;
                                break;
                            }
                        }
                        if (!overlap) { // no hay solapamiento
                            for (int j = col; j < col + size; j++) { // coloca el barco
                                playerBoard.setCell(row, j, 1);
                            }
                            placed = true;
                        }
                    }
                } else { // vertical
                    if (row + size <= Board.SIZE) { // barco encaja en el tablero
                        boolean overlap = false;
                        for (int i = row; i < row + size; i++) { // comprueba si hay solapamiento con otros barcos
                            if (!playerBoard.isEmpty(i, col)) {
                                overlap = true;
                                break;
                            }
                        }
                        if (!overlap) { // no hay solapamiento
                            for (int i = row; i < row + size; i++) { // coloca el barco
                                playerBoard.setCell(i, col, 1);
                            }
                            placed = true;
                        }
                    }
                }
            }
        }
    }

    // inicializa el tablero de la computadora con barcos en ubicaciones aleatorias
    public void initializeComputerBoard() {
        int[] shipSizes = {5, 4, 3, 3, 2}; // tamaños de los barcos
        Random random = new Random();
        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int orientation = random.nextInt(2); // horizontal o vertical
                int row = random.nextInt(Board.SIZE);
                int col = random.nextInt(Board.SIZE);
                if (orientation == 0) { // horizontal
                    if (col + size <= Board.SIZE) { // barco encaja en el tablero
                        boolean overlap = false;
                        for (int j = col; j < col + size; j++) { // comprueba si hay solapamiento con otros barcos
                            if (!computerBoard.isEmpty(row, j)) {
                                overlap = true;
                                break;
                            }
                        }
                        if (!overlap) { // no hay solapamiento
                            for (int j = col; j < col + size; j++) { // coloca el barco
                                computerBoard.setCell(row, j, 1);
                            }
                            placed = true;
                        }
                    }
                } else { // vertical
                    if (row + size <= Board.SIZE) { // barco encaja en el tablero
                        boolean overlap = false;
                        for (int i = row; i < row + size; i++) { // comprueba si hay solapamiento con otros barcos
                            if (!computerBoard.isEmpty(i, col)) {
                                overlap = true;
                                break;
                            }
                        }
                        if (!overlap) { // no hay solapamiento
                            for (int i = row; i < row + size; i++) { // coloca el barco
                                computerBoard.setCell(i, col, 1);
                            }
                            placed = true;
                        }
                    }
                }
            }
        }
    }

    // devuelve true si el jugador ha ganado, false en caso contrario
    public boolean playerWon() {
        return computerBoard.allShipsDestroyed();
    }

    // devuelve true si la computadora ha ganado, false en caso contrario
    public boolean computerWon() {
        return playerBoard.allShipsDestroyed();
    }

    public int getPlayerScore() {
        return playerBoard.getScore();
    }

    public int getComputerScore() {
        return computerBoard.getScore();
    }
}
