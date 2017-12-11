package com.cineshared.pjnogegonzalez.cineshared.peliculas;

import java.util.List;

/**
 * Clase PeliculasComprobacion con todos los atributos de una película para el histórico y el intercambio.
 * También están los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class PeliculasComprobacion {
    private boolean ok;
    private String error;
    private List<Peliculas> peliculas;

    // Métodos getter y setter de todos los atributos
    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<Peliculas> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Peliculas> peliculas) {
        this.peliculas = peliculas;
    }
}
