package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Created by elgonzalez on 04/12/2017.
 */

public class MensajeChat {

    private String textoMensaje;
    private long horaMensaje;
    private boolean vistoMensaje;
    private String remitenteMensaje;

    public MensajeChat() {
    }

    public MensajeChat(String remitenteMensaje) {
        this.remitenteMensaje = remitenteMensaje;
    }

    public MensajeChat(String textoMensaje, long horaMensaje, boolean vistoMensaje) {
        this.textoMensaje = textoMensaje;
        this.horaMensaje = horaMensaje;
        this.vistoMensaje = vistoMensaje;
    }

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

    public boolean isVistoMensaje() {
        return vistoMensaje;
    }

    public void setVistoMensaje(boolean vistoMensaje) {
        this.vistoMensaje = vistoMensaje;
    }

}
