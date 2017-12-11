package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Clase MensajeChat con todos los atributos de un chat y los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class MensajeChat {

    private String textoMensaje;
    private long horaMensaje;
    private String remitenteMensaje;

    // Constructor vacío
    public MensajeChat() {
    }

    // Constructor con parámetros
    public MensajeChat(String textoMensaje, long horaMensaje, String remitenteMensaje) {
        this.textoMensaje = textoMensaje;
        this.horaMensaje = horaMensaje;
        this.remitenteMensaje = remitenteMensaje;
    }

    // Métodos getter y setter de todos los atributos
    public String getRemitenteMensaje() {
        return remitenteMensaje;
    }

    public void setRemitenteMensaje(String remitenteMensaje) {
        this.remitenteMensaje = remitenteMensaje;
    }

    public String getTextoMensaje() {
        return textoMensaje;
    }

    public void setTextoMensaje(String textoMensaje) {
        this.textoMensaje = textoMensaje;
    }

    public long getHoraMensaje() {
        return horaMensaje;
    }

    public void setHoraMensaje(long horaMensaje) {
        this.horaMensaje = horaMensaje;
    }

}
