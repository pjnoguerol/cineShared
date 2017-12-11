package com.cineshared.pjnogegonzalez.cineshared.busqueda;

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
import com.cineshared.pjnogegonzalez.cineshared.peliculas.Peliculas;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Clase BusquedaFragment contiene las acciones relativas a la búsqueda
 * BusquedaNaturalFragment
 * Creada por Pablo Noguerol y Elena González
 */
public class BusquedaNaturalFragment extends Fragment {

    // Definimos las variables necesarias
    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(Constantes.BUSQUEDA_NATURAL);
    private RecyclerView recyclerView;
    private String cadenaBusqueda;
    private Usuarios usuario;

    // Constructorvacío
    public BusquedaNaturalFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = inflater.getContext();
        View vistaRaiz = inflater.inflate(R.layout.recyclerview_activity2, container, false);
        cadenaBusqueda = getArguments().getString("natural");
        usuario = (Usuarios) getArguments().getSerializable(Constantes.USUARIOS);
        if (usuario != null)
            conversionJson.setUsuario(usuario);
        recyclerView = conversionJson.onCreateViewScroll(context, vistaRaiz, getResources());
        // Obtenemos la URL de la que realizará la búsqueda
        String url = Constantes.RUTA_PELICULAS_NATURAL + cadenaBusqueda;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new BusquedaJsonTask().execute(new URL(url));
            } else {
                Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return vistaRaiz;
    }

    /**
     * Inner class que parsea la búsqueda de películas a una CardView
     */
    public class BusquedaJsonTask extends AsyncTask<URL, Void, List<Peliculas>> {

        private List<Peliculas> listaPeliculas;

        /**
         * Método que llama al parseo de búsqueda para obtener la lista a mostrar
         *
         * @return Lista de películas
         */
        @Override
        protected List<Peliculas> doInBackground(URL... urls) {
            listaPeliculas = (conversionJson.doInBackground(urls));
            return (listaPeliculas);
        }

        /**
         * Método que asigna la búsqueda al adaptador para obtener un cardView
         *
         * @param listaPeliculas Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(List<Peliculas> listaPeliculas) {
            recyclerView.setAdapter(conversionJson.onPostExecute(listaPeliculas));
        }
    }
}
