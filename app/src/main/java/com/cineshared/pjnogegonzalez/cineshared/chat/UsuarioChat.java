package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Created by elgonzalez on 05/12/2017.
 */

public class UsuarioChat {

    public String emailUsuario;
    public String imagenUsuario;
    public String nombreUsuario;

    public UsuarioChat() {

    }

    public UsuarioChat(String emailUsuario, String imagenUsuario, String nombreUsuario) {
        this.emailUsuario = emailUsuario;
        this.imagenUsuario = imagenUsuario;
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getImagenUsuario() {
        return imagenUsuario;
    }

    public void setImagenUsuario(String imagenUsuario) {
        this.imagenUsuario = imagenUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

}
