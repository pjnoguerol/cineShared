package com.cineshared.pjnogegonzalez.cineshared;

import java.util.List;

/**
 * Created by informatica on 05/12/2017.
 */

public class PeliculasComprobacion
{
    private boolean ok;
    private String error;
    private List<Peliculas> peliculas;


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
