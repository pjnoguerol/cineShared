package com.cineshared.pjnogegonzalez.cineshared;

/**
 * Created by informatica on 25/09/2017.
 */

public class Constantes {

    //Constantes de Error
    // Constantes de error
    public static String ERROR_JSON = "Ocurrió un error de Parsing Json";
    public static String ERROR_CONEXION = "Error de conexión";
    public static String ERROR_REPRODUCCION = "Se ha producido un error reproduciendo el audio";
    public static String ERROR_CONTACTOS = "La lista de contactos no se ha leido correctamente";
    public static String ERROR_INSERT = "Error creando el usuario: ";
    public static String ERROR_UPDATE = "Error actualizando el usuario: ";

    public static String USUARIOS = "usuarios";
    // Constantes login
    public static String USUARIO = "usuario";
    public static String USUARIO_DATOS = "usuariodatos";
    public static String PASSWORD = "password";
    public static String BIENVENIDO = "Bienvenido: ";
    // Constantes URLs y conexiones
    public static String SERVIDOR = "http://www.intraco.es";
    public static String RUTA_IMAGEN = SERVIDOR + "/cineshared/img/";
    public static String RUTA_MUSICA = SERVIDOR + "/cineshared/music/";
    public static String RUTA_CLASE_PHP = "/cineshared/cineshared_clase.php?";
    public static String RUTA_CONTACTOS = SERVIDOR + RUTA_CLASE_PHP + "contactos=";
    public static String RUTA_GENEROS = SERVIDOR + RUTA_CLASE_PHP + "generos";
    public static String RUTA_GENEROS_USUARIO = SERVIDOR + RUTA_CLASE_PHP + "generousuario=";
    public static String RUTA_INSERTAR_USUARIO = SERVIDOR + RUTA_CLASE_PHP + "userinsert=";
    public static String RUTA_LISTA_ACTORES = SERVIDOR + RUTA_CLASE_PHP + "actorlist";
    //public static String RUTA_BANDA_SONORA = SERVIDOR + RUTA_CLASE_PHP + SONORAS;
    //public static String RUTA_DIRECTORES = SERVIDOR + RUTA_CLASE_PHP + DIRECTORES;
    public static String RUTA_LOGIN = SERVIDOR + RUTA_CLASE_PHP + USUARIO + "=";
    public static String RUTA_USUARIO_DATOS = SERVIDOR + RUTA_CLASE_PHP + USUARIO_DATOS + "=";
    //public static String RUTA_PELICULAS = SERVIDOR + RUTA_CLASE_PHP + PELICULAS;
    public static String RUTA_PELICULAS_GENERO = SERVIDOR + RUTA_CLASE_PHP + "genero=";
    public static String RUTA_PELICULAS_BUSQUEDA = SERVIDOR + RUTA_CLASE_PHP + "busqueda=";
    public static int CONNECTION_TIMEOUT = 15000;
    public static int READ_TIMEOUT = 15000;
}
