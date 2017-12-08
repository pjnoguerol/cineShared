package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Created by elgonzalez on 05/12/2017.
 */

public class ConversacionChat {

    private boolean vistoMensaje;
    private long horaMensaje;

    public ConversacionChat() {
    }

    public ConversacionChat(boolean vistoMensaje, long horaMensaje) {
        this.vistoMensaje = vistoMensaje;
        this.horaMensaje = horaMensaje;
    }

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
