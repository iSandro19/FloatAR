package com.floatar;

public class Lobby {
    private String name;
    private String key;

    public Lobby() {
        // Constructor vacío requerido para Firebase
    }

    public Lobby(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
