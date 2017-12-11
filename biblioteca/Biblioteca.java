package com.cineshared.pjnogegonzalez.cineshared.biblioteca;

import java.io.Serializable;

/**
 * Clase Biblioteca con todos los atributos de una biblioteca y los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class Biblioteca implements Serializable {

    private int id_pel;
    private String nombre;
    private String imagen;

    // Métodos getter y setter de todos los atributos
    public int getId_pel() {
        return id_pel;
    }

    public void setId_pel(int id_pel) {
        this.id_pel = id_pel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
