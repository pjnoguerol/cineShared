package com.cineshared.pjnogegonzalez.cineshared;

import java.io.Serializable;

/**
 * Created by informatica on 30/10/2017.
 */

public class Resultado implements Serializable {

    private boolean ok;
    private String error;


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
