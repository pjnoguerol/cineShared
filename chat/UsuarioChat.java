package com.cineshared.pjnogegonzalez.cineshared.chat;

/**
 * Clase UsuarioChat con todos los atributos de un usuario del chat y los métodos getter y setter
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class UsuarioChat {

    public String emailUsuario;
    public String imagenUsuario;
    public String nombreUsuario;
    public String estadoUsuario;

    // Constructor vacío
    public UsuarioChat() {
    }

    // Constructor con parámetros
    public UsuarioChat(String emailUsuario, String imagenUsuario, String nombreUsuario, String estadoUsuario) {
        this.emailUsuario = emailUsuario;
        this.imagenUsuario = imagenUsuario;
        this.nombreUsuario = nombreUsuario;
        this.estadoUsuario = estadoUsuario;
    }

    // Métodos getter y setter de todos los atributos
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

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
}
