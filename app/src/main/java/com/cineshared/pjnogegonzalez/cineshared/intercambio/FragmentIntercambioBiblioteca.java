package com.cineshared.pjnogegonzalez.cineshared.intercambio;

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
 * Clase FragmentIntercambioBiblioteca es el fragmento que muestra las películas en la sección intercambios
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class FragmentIntercambioBiblioteca extends Fragment {

    // Definimos las variables

    private RecyclerView recyclerView;
    private Usuarios usuario;

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(Constantes.BIBLIOTECA);

    // Constructor vacío
    public FragmentIntercambioBiblioteca() {
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
        // Inflate the layout for this fragment
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity2, container, false);
        usuario = (Usuarios) getArguments().getSerializable("usuario");
        conversionJson.setUsuario(usuario);

        recyclerView = conversionJson.onCreateViewScroll(context, rootView, getResources());
        String url = "";
        conversionJson.setMode(5);
        url = Constantes.RUTA_BIBLIOTECA + usuario.getId_usua() + "&estadobiblo=3";
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new PeliculasJsonTask().
                        execute(new URL(url));
            } else {
                Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    /**
     * Inner class que parsea los intercambios a una CardView
     */
    public class PeliculasJsonTask extends AsyncTask<URL, Void, List<Peliculas>> {

        private List<Peliculas> listaPelicula;

        /**
         * Método que llama al parseo de intercambios para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected List<Peliculas> doInBackground(URL... urls) {
            return (listaPelicula = conversionJson.doInBackground(urls));
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param listaPelicula Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(List<Peliculas> listaPelicula) {
            recyclerView.setAdapter(conversionJson.onPostExecute(listaPelicula));
        }
    }
}
