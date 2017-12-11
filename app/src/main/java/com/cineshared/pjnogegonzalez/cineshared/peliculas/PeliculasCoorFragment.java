package com.cineshared.pjnogegonzalez.cineshared.peliculas;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Clase PeliculasCoorFragment contiene las acciones relativas a las coordenadas de las películas
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class PeliculasCoorFragment extends Fragment {

    // Definimos las variables necesarias
    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(Constantes.PELICULAS);
    private RecyclerView recyclerView;
    private Usuarios usuario;

    // Constructor vacío
    public PeliculasCoorFragment() {
    }

    /**
     * Método onCreateView se ejecuta al inicializarse el fragmento
     *
     * @param inflater           Layout donde se cargará el fragmento
     * @param container          Contenedor de dicho layout
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     * @return Vista con el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity, container, false);
        usuario = (Usuarios) getArguments().getSerializable("usuarios");
        String cadena = getArguments().getString("cadena");
        if (usuario != null) {
            conversionJson.setUsuario(usuario);
        }
        conversionJson.setMode(2);
        recyclerView = conversionJson.onCreateView(context, rootView, getResources());
        conversionJson.setUsuario(usuario);
        String url = "";
        if (cadena != null) {
            url = Constantes.RUTA_PELICULAS_COORDENADAS_CADENA + usuario.getId_usua() + "&distancia=" + usuario.getDistancia() + "&cadenabiblio=" + cadena;
        } else {
            url = Constantes.RUTA_PELICULAS_COORDENADAS + usuario.getId_usua() + "&distancia=" + usuario.getDistancia();
        }

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new CoordenadaJsonTask().execute(new URL(url));
            } else {
                Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    /**
     * Inner class que parsea la lista de películas a una CardView
     */
    public class CoordenadaJsonTask extends AsyncTask<URL, Void, List<Peliculas>> {

        private List<Peliculas> peliculas;

        /**
         * Método que llama al parseo de la lista de películas para mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected List<Peliculas> doInBackground(URL... urls) {
            peliculas = conversionJson.doInBackground(urls);
            return (peliculas);
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param lista Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(List<Peliculas> lista) {
            recyclerView.setAdapter(conversionJson.onPostExecute(lista));
        }
    }
}
