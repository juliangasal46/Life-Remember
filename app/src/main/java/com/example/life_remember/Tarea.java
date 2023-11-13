package com.example.life_remember;

public class Tarea {

    private String titulo, descripcion, tiempo_recuerdo;

    public Tarea(String titulo, String descripcion, String tiempo_recuerdo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tiempo_recuerdo = tiempo_recuerdo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescipcion() {
        return descripcion;
    }

    public String getTiempo_recuerdo() {
        return tiempo_recuerdo;
    }
}
