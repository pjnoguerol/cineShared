package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Created by elgonzalez on 04/12/2017.
 */

public class MensajeChat {

    private String textoMensaje, tipoMensaje;
    private long horaMensaje;
    private boolean vistoMensaje;

    private String emisorMensaje;

    public MensajeChat() {
    }

    public MensajeChat(String emisorMensaje) {
        this.emisorMensaje = emisorMensaje;
    }

    public MensajeChat(String textoMensaje, String tipoMensaje, long horaMensaje, boolean vistoMensaje) {
        this.textoMensaje = textoMensaje;
        this.tipoMensaje = tipoMensaje;
        this.horaMensaje = horaMensaje;
        this.vistoMensaje = vistoMensaje;
    }

    public String getEmisorMensaje() {
        return emisorMensaje;
    }

    public void setEmisorMensaje(String emisorMensaje) {
        this.emisorMensaje = emisorMensaje;
    }

    public String getTextoMensaje() {
        return textoMensaje;
    }

    public void setTextoMensaje(String textoMensaje) {
        this.textoMensaje = textoMensaje;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
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
