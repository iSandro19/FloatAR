package com.floatar;

import androidx.annotation.NonNull;

public class Lobby {
    private String name;
    private String key;

    /**
     * Constructor vacío requerido para Firebase
     */
    public Lobby() {
        // Constructor vacío requerido para Firebase
    }

    /**
     * Constructor
     * @param name Nombre del lobby
     */
    public Lobby(String name) {
        this.name = name;
    }

    /**
     * Obtener el nombre del lobby
     * @return El nombre del lobby
     */
    public String getName() {
        return name;
    }

    /**
     * Obtener la key del lobby
     * @return La key del lobby
     */
    public String getKey() {
        return key;
    }

    /**
     * Establecer la key del lobby
     * @param key Key del lobby
     */
    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
