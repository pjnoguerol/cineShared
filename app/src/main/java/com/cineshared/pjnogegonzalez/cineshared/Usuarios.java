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
    private String imagen;
    private int distancia;
    private int id_gen;
    private double longitud;
    private double latitud;
    //Historio de intercambio
    private int hisusua;


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

    public int getHisusua() {
        return hisusua;
    }

    public void setHisusua(int hisusua) {
        this.hisusua = hisusua;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    @Override
    public String toString() {
        return usuario;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}