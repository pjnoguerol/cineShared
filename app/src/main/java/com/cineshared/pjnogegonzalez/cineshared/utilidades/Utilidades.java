package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.cineshared.pjnogegonzalez.cineshared.CircleTransform;
import com.cineshared.pjnogegonzalez.cineshared.Constantes;
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

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 130);
        return noOfColumns;
    }

    public static void resultado(int resultado) {
        Utilidades.resultado = resultado;
    }

    public static int getResultado() {
        return Utilidades.resultado;
    }


    public static String acotar(String cadena) {
        int size = (cadena.length() < 25) ? cadena.length() : 25;
        String retorno = cadena.substring(0, size);
        if (cadena.length() >= 25)
            retorno += "..";
        return retorno;

    }

    public static String capitalizar(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    public static void establecerImagenUsuario(final Context contexto, final String imagen, final ImageView imagenUsuarioChat) {
        Picasso.with(contexto).load(Constantes.RUTA_IMAGEN + imagen)
                .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_chat_img_defecto)
                .transform(new CircleTransform()).fit().centerCrop().rotate(270f)
                .into(imagenUsuarioChat, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(contexto).load(Constantes.RUTA_IMAGEN + imagen)
                                .placeholder(R.drawable.ic_chat_img_defecto)
                                .transform(new CircleTransform()).fit().centerCrop().rotate(270f).into(imagenUsuarioChat);
                    }
                });
    }
}