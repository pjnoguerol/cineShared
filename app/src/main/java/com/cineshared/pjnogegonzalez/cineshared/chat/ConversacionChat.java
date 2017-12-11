package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Clase ConversacionChat con todos los atributos de un mensaje del chat y los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class ConversacionChat {

    private boolean vistoMensaje;
    private long horaMensaje;

    // Constructor vacío
    public ConversacionChat() {
    }

    // Constructor con parámetros
    public ConversacionChat(boolean vistoMensaje, long horaMensaje) {
        this.vistoMensaje = vistoMensaje;
        this.horaMensaje = horaMensaje;
    }

    // Métodos getter y setter de todos los atributos
    public boolean isVistoMensaje() {
        return vistoMensaje;
    }

    public void setVistoMensaje(boolean vistoMensaje) {
        this.vistoMensaje = vistoMensaje;
    }

    public long getHoraMensaje() {
        return horaMensaje;
    }

    public void setHoraMensaje(long horaMensaje) {
        this.horaMensaje = horaMensaje;
    }

}
