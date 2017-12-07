package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.MainActivity;
import com.cineshared.pjnogegonzalez.cineshared.Resultado;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Creada por Pablo Noguerol y Elena González
 */

public class LocalizacionListener implements LocationListener {

    private Context contexto;
    private String longitud;
    private String latitud;
    private int identificadorUsuario;

    private ConversionJson<Resultado> conversionJson = new ConversionJson<>(Constantes.RESULTADO);

    public LocalizacionListener(Context contexto, int identificadorUsuario) {
        this.contexto = contexto;
        this.identificadorUsuario = identificadorUsuario;
    }

    @Override
    public void onLocationChanged(Location location) {
        longitud = "" + location.getLongitude();
        latitud = "" + location.getLatitude();

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                new LocalizacionListener.BusquedaJsonTask().execute(new URL(Constantes.RUTA_INSERTAR_COORDENADAS
                        + longitud + "&latitud=" + latitud + "&usuario=" + identificadorUsuario));
            } else {
                Toast.makeText(contexto, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public class BusquedaJsonTask extends AsyncTask<URL, Void, Resultado> {
        private Resultado resultado;

        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected Resultado doInBackground(URL... urls) {
            resultado = conversionJson.doInBackground(urls).get(0);
            return (resultado);
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param resultado Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Resultado resultado) {
            if (resultado != null) {
                if (resultado.isOk()) {
                    Log.w("resultado ok", "EL RESULTADO ES OK");
                } else {
                    Log.w("resultado ok", "EL RESULTADO NO OK");
                }
            } else {
                Log.w("resultado ok", "EL RESULTADO ES NULLO");
            }
        }
    }
}