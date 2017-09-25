package com.cineshared.pjnogegonzalez.cineshared;

import java.io.Serializable;

/**
 * Clase Usuarios con todos los atributos de un usuario y los métodos getter y setter
 */
public class Usuarios implements Serializable {

    private boolean ok;
    private String error;
    private int id_usua;
    private String usuario;
    private String password;
    private String email;
    private String telefono;
    private int id_gen;

    // Métodos getter y setter de todos los atributos
    public int getId_usua() {
        return id_usua;
    }

    public void setId_usua(int id_usua) {
        this.id_usua = id_usua;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getId_gen() {
        return id_gen;
    }

    public void setId_gen(int id_gen) {
        this.id_gen = id_gen;
    }

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