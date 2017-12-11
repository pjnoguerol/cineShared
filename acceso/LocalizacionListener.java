package com.cineshared.pjnogegonzalez.cineshared.acceso;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Resultado;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Clase LocalizacionListener realiza las comprobaciones necesarias para actualizar la posición del usuario
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class LocalizacionListener implements LocationListener {

    // Definimos las variables
    private Context contexto;
    private String longitud;
    private String latitud;
    private int identificadorUsuario;

    private ConversionJson<Resultado> conversionJson = new ConversionJson<>(Constantes.RESULTADO);

    /**
     * Constructor de la clase
     *
     * @param contexto             Contexto de la actividad que desea localizar al usuario
     * @param identificadorUsuario Identificador de dicho usuario
     */
    public LocalizacionListener(Context contexto, int identificadorUsuario) {
        this.contexto = contexto;
        this.identificadorUsuario = identificadorUsuario;
    }

    /**
     * Método onLocationChanged actualiza la localización del usuario cuando esta cambia
     *
     * @param localizacion Nueva localización del usuario
     */
    @Override
    public void onLocationChanged(Location localizacion) {
        longitud = String.valueOf(localizacion.getLongitude());
        latitud = String.valueOf(localizacion.getLatitude());

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                new LocalizacionListener.LocalizacionJsonTask().execute(new URL(Constantes.RUTA_INSERTAR_COORDENADAS
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

    public class LocalizacionJsonTask extends AsyncTask<URL, Void, Resultado> {
        private Resultado resultado;

        /**
         * Método que llama al parseo de resultados para conocer el mismo
         *
         * @return Biblioteca
         */
        @Override
        protected Resultado doInBackground(URL... urls) {
            resultado = conversionJson.doInBackground(urls).get(0);
            return (resultado);
        }

        /**
         * Método que comprueba el resultado y muestra una traza en el log
         *
         * @param resultado Resultado de la accion
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Resultado resultado) {
            if (resultado != null) {
                if (resultado.isOk()) {
                    Log.w("CineSharedLocalizacion", "EL resultado es OK");
                } else {
                    Log.w("CineSharedLocalizacion", "El resultado es NO OK");
                }
            } else {
                Log.w("CineSharedLocalizacion", "El resultado es nulo");
            }
        }
    }
}