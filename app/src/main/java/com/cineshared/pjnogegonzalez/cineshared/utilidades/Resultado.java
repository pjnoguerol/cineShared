package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import java.io.Serializable;

/**
 * Clase Resultado con todos los atributos de un resultado y los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class Resultado implements Serializable {

    private boolean ok;
    private String error;

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

}
