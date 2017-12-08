package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by pjnog on 08/08/2017.
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
     * @param contexto Contexto del activity donde se cargará la imagen
     * @param imagenUrl URL de la imagen que se cargará
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
}