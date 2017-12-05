package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Created by elgonzalez on 05/12/2017.
 */

public class ConversacionChat {

    private boolean vistaConversacion;
    private long horaConversacion;

    public ConversacionChat() {
    }

    public ConversacionChat(boolean vistaConversacion, long horaConversacion) {
        this.vistaConversacion = vistaConversacion;
        this.horaConversacion = horaConversacion;
    }

    public boolean isVistaConversacion() {
        return vistaConversacion;
    }

    public void setVistaConversacion(boolean vistaConversacion) {
        this.vistaConversacion = vistaConversacion;
    }

    public long getHoraConversacion() {
        return horaConversacion;
    }

    public void setHoraConversacion(long horaConversacion) {
        this.horaConversacion = horaConversacion;
    }

}
