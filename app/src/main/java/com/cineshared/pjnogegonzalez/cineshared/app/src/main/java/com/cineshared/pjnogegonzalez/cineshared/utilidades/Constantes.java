package com.cineshared.pjnogegonzalez.cineshared.utilidades;

/**
 * Clase Constantes contiene las constantes con texto que van a ser empleadas en la aplicación
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class Constantes {

    // Constantes de Error
    public static String ERROR_JSON = "Ocurrió un error de Parsing Json";
    public static String ERROR_CONEXION = "Error de conexión";
    public static String ERROR_INSERT = "Error creando el usuario: ";
    public static String ERROR_CONTACTOS = "La lista de contactos no se ha leido correctamente";

    public static String USUARIOS = "usuarios";
    public static String BIBLIOTECA = "biblioteca";
    public static String BUSQUEDA = "FindApiBusqueda";
    public static String BUSQUEDA_NATURAL = "natural";
    public static String BUSQUEDA_CONTACTOS ="contactos";
    public static String RESULTADO = "resultado";
    public static String IMAGENES = "https://image.tmdb.org/t/p/w500";
    public static String PELICULAS = "peliculas";
    public static String HISTORICO = "historico";
    public static String LOCALIZACION = "localizacion";
    public static String INTERCAMBIO = "intercambio";
    public static String CONFIGURACION = "configuracion";
    public static String PELICULAS_CHECK = "peliculascheck";
    public static String CONTACTOS = "contactos";

    // Constantes ftp
    public static String IP_FTP = "intracosl.ddns.net";
    public static String USUARIO_FTP = "ftp2";
    public static String PASS_FTP = "Intraco2017;";

    // Constantes login
    public static String CADENA_VACIA = "";
    public static String USUARIO = "usuario";
    public static String USUARIO_DATOS = "usuariodatos";
    public static String PASSWORD = "password";
    public static String BIENVENIDO = "Bienvenido: ";
    public static String INICIADO = "iniciado";

    // Constantes FirebaseDatabase
    public static String USUARIOS_FIREBASE = "Usuarios";
    public static String CHAT_FIREBASE = "Chats";
    public static String MENSAJES_FIREBASE = "Mensajes";
    public static String NOTIFICACIONES_FIREBASE = "Notificaciones";
    public static String NOMBRE_USUARIO = "nombreUsuario";
    public static String IMAGEN_USUARIO = "imagenUsuario";
    public static String EMAIL_USUARIO = "emailUsuario";
    public static String TOKEN_USUARIO = "tokenUsuario";
    public static String TEXTO_MENSAJE = "textoMensaje";
    public static String HORA_MENSAJE = "horaMensaje";
    public static String REMITENTE_MENSAJE = "remitenteMensaje";
    public static String VISTO_MENSAJE = "vistoMensaje";
    public static String CONEXION_USUARIO = "conexionUsuario";
    public static String EMAIL_FIREBASE = "@cineShared.com";

    // Constantes URLs y conexiones
    //public static String SERVIDOR = "http://www.intraco.es";
    public static String SERVIDOR = "http://192.168.1.3";
    public static String RUTA_IMAGEN = SERVIDOR + "/cineshared/img/";
    public static String RUTA_CLASE_PHP = "/cineshared/cineshared_clase.php?";
    public static String RUTA_BIBLIOTECA = SERVIDOR + RUTA_CLASE_PHP + "biblioteca=";
    public static String RUTA_BIBLIOTECA_CADENA = SERVIDOR + RUTA_CLASE_PHP + "bibliotecacadena=";
    public static String RUTA_INSERTAR_USUARIO = SERVIDOR + RUTA_CLASE_PHP + "userinsert=";
    public static String RUTA_PELICULAS = SERVIDOR + RUTA_CLASE_PHP + "nombre=";
    public static String RUTA_CONTACTOS = SERVIDOR + RUTA_CLASE_PHP + "contactos=";

    // Constantes api
    public static String RUTA_WEB_API_BUSQUEDA = "https://api.themoviedb.org/3/search/movie?query=";
    public static final String API_KEY = "&api_key=7d435a80e43f4dfee8958553e9a8b257&language=es";

    public static String RUTA_USUARIO_DATOS = SERVIDOR + RUTA_CLASE_PHP + USUARIO_DATOS + "=";
    public static String RUTA_PELICULAS_NATURAL = SERVIDOR + RUTA_CLASE_PHP + "buscarnatural=";
    public static String RUTA_ACTUALIZAR_BUSQUEDA = SERVIDOR + RUTA_CLASE_PHP + "actualizarpelicula=";
    public static String RUTA_INSERTAR_COORDENADAS = SERVIDOR + RUTA_CLASE_PHP + "longitud=";
    public static String RUTA_PELICULAS_COORDENADAS = SERVIDOR + RUTA_CLASE_PHP + "usuariocoordenada=";
    public static String RUTA_PELICULAS_COORDENADAS_CADENA = SERVIDOR + RUTA_CLASE_PHP + "usuariocoordenadacadena=";
    public static String RUTA_USUARIO_INTERCAMBIO = SERVIDOR + RUTA_CLASE_PHP + "usuariointercambio=";
    public static String RUTA_ACTUALIZAR_INTERCAMBIO = SERVIDOR + RUTA_CLASE_PHP + "actualizarintercambio=";
    public static int CONNECTION_TIMEOUT = 15000;
    public static int READ_TIMEOUT = 15000;
}
