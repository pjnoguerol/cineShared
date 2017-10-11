package com.cineshared.pjnogegonzalez.cineshared;

import java.io.Serializable;

/**
 * Created by informatica on 10/10/2017.
 */

public class Biblioteca implements Serializable {

    private int id_pel;
    private String nombre;
    private String imagen;

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
