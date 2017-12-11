package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.widget.ImageView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase Utilidades contiene diversos métodos usados en varias áreas de la aplicación
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class Utilidades {

    private static int resultado;
    public static String auxPelicula;

    /**
     * Método calcularNumeroColumnas que calcula el número de columnas que caben con el detalle de las películas
     * dependiendo del tamaño de la pantalla
     *
     * @param context Contexto del activity que mostrará la lista de detalle
     * @return Número de columnas posibles
     */
    public static int calcularNumeroColumnas(Context context) {
        DisplayMetrics metricasPantalla = context.getResources().getDisplayMetrics();
        float dpAncho = metricasPantalla.widthPixels / metricasPantalla.density;
        int numeroColumnas = (int) (dpAncho / 110);
        return numeroColumnas;
    }

    // Métodos getter y setter
    public static void resultado(int resultado) {
        Utilidades.resultado = resultado;
    }

    public static int getResultado() {
        return Utilidades.resultado;
    }

    /**
     * Método acotar que recorta la cadena dada a un máximo de 25 caracteres si es necesario
     *
     * @param cadena Cadena a recordar si supera la longitud de 25 caracteres
     * @return Cadena con un máxmo de 25 caracteres o la misma cadena si tiene una longitud inferior
     */
    public static String acotar(String cadena) {
        int tamanyo = (cadena.length() < 25) ? cadena.length() : 25;
        String strFinal = cadena.substring(0, tamanyo);
        if (cadena.length() >= 25)
            strFinal += "..";
        return strFinal;

    }

    /**
     * Método capitalizarCadena deja la primera letra en mayúsculas y el resto normal
     *
     * @param cadena Cadena a capitalizar
     * @return Cadena capitalizada
     */
    public static String capitalizarCadena(String cadena) {
        if (cadena == null || cadena.isEmpty()) {
            return cadena;
        } else {
            return cadena.substring(0, 1).toUpperCase() + cadena.substring(1);
        }
    }

    /**
     * Método establecerImagenUsuario establece la imagen del usuario con la librería Picasso y le da
     * formato circular. Además, establece el color de fondo dependiendo de si la imagen está informada o no.
     *
     * @param contexto        Contexto del activity donde se cargará la imagen
     * @param imagenUrl       URL de la imagen que se cargará
     * @param imagenViewField Campo donde se realiza la visualización de la imagen
     */
    public static void establecerImagenUsuario(final Context contexto, final String imagenUrl,
                                               final ImageView imagenViewField, boolean cambiarFondo) {
        String urlImagen = imagenUrl;
        if (!imagenUrl.contains("http"))
            urlImagen = Constantes.RUTA_IMAGEN + urlImagen;

        Picasso.with(contexto).load(urlImagen)
                .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_chat_img_defecto)
                .transform(new TransformacionCirculo()).fit().centerCrop().rotate(270f)
                .into(imagenViewField, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(contexto).load(Constantes.RUTA_IMAGEN + imagenUrl)
                                .placeholder(R.drawable.ic_chat_img_defecto)
                                .transform(new TransformacionCirculo()).fit().centerCrop().rotate(270f).into(imagenViewField);
                    }
                });
        if (cambiarFondo) {
            if (!"null".equals(imagenUrl) && !"default".equals(imagenUrl)) {
                imagenViewField.setBackgroundResource(R.color.colorPrimary);
            } else {
                imagenViewField.setBackgroundResource(R.color.colorBlanco);
            }
        }
    }

    /**
     * Método establecerImagen establece la imagen del usuario con la librería Picasso y establece el color de
     * fondo dependiendo de si la imagen está informada o no.
     *
     * @param contexto        Contexto del activity donde se cargará la imagen
     * @param imagenUrl       URL de la imagen que se cargará
     * @param imagenViewField Campo donde se realiza la visualización de la imagen
     */
    public static void establecerImagen(final Context contexto, final String imagenUrl, final ImageView imagenViewField) {
        String urlImagen = imagenUrl;
        if (!imagenUrl.contains("http"))
            urlImagen = Constantes.RUTA_IMAGEN + urlImagen;

        Picasso.with(contexto).load(urlImagen).networkPolicy(NetworkPolicy.OFFLINE).into(imagenViewField);
    }

    /**
     * Método isEmailValido comprueba si el email es correcto
     *
     * @param charSequenceToCheck Cadena para comprobar si es un mail válido
     * @return Resultado de la validación
     */
    public static boolean isEmailValido(CharSequence charSequenceToCheck) {
        if (TextUtils.isEmpty(charSequenceToCheck)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(charSequenceToCheck).matches();
        }
    }

    /**
     * Método isPasswordValida comprueba si la contraseña es correcta
     *
     * @param password Cadena para comprobar si la contraseña es correcta
     * @return Resultado de la validación
     */
    public static boolean isPasswordValida(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        } else {
            return password.length() >= 6 && password.matches("[a-zA-Z0-9]*");
        }
    }

    /**
     * Método isTelefonoValido comprueba si el teléfono es correcto
     *
     * @param charSequenceTelefono Cadena para comprobar si el teléfono es correcto
     * @return Resultado de la validación
     */
    public static boolean isTelefonoValido(CharSequence charSequenceTelefono) {
        if (TextUtils.isEmpty(charSequenceTelefono)) {
            return false;
        } else {
            return Patterns.PHONE.matcher(charSequenceTelefono).matches();
        }
    }

    /**
     * Método convertirMillasKilometros convierte las millas a kilómetros
     *
     * @param millas Millas a convertir
     * @param pasarMetros True si se quiere pasar a metros, false si se deja en kilómetros
     * @return Kilómetros con dos decimales
     */
    public static float convertirMillasKilometros(float millas, boolean pasarMetros) {
        BigDecimal decimalKilometros = new BigDecimal(millas);
        decimalKilometros = decimalKilometros.multiply(new BigDecimal(1.609));
        if (pasarMetros)
            decimalKilometros = decimalKilometros.multiply(new BigDecimal(1000));
        return decimalKilometros.setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}