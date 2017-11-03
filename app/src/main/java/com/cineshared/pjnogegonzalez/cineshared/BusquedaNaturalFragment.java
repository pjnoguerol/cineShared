package com.cineshared.pjnogegonzalez.cineshared;


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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.cineshared.pjnogegonzalez.cineshared.Constantes.API_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusquedaNaturalFragment extends Fragment {

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(getActivity(), Constantes.BUSQUEDA_NATURAL);
    private RecyclerView recyclerView;
    private String cadenaBusqueda;
    private Usuarios usuario;
    //private Usuarios usuario;
    public BusquedaNaturalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity, container, false);
        cadenaBusqueda = getArguments().getString("natural");
        usuario = (Usuarios) getArguments().getSerializable(Constantes.USUARIOS);
        if (usuario!=null)
            conversionJson.setUsuario(usuario);
        //int modo = getArguments().getInt("modo");

        recyclerView = conversionJson.onCreateView(context, rootView, getResources());

        String url = "";
        //url = "http://www.intraco.es/cineshared/cineshared_clase.php?prueba";
        //url = "http://www.intraco.es/cineshared/cineshared_clase.php?biblioteca=2";
        url = Constantes.RUTA_PELICULAS_NATURAL+cadenaBusqueda;
        //Log.w("myApp", url);
        //Toast.makeText(context, "BUSCANDO DE USUARIO"+usuario.getUsuario(), Toast.LENGTH_SHORT).show();
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new BusquedaJsonTask().
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
     * Inner class que parsea la Biblioteca a una CardView
     */
    public class BusquedaJsonTask extends AsyncTask<URL, Void, List<Peliculas> > {

        private List<Peliculas> listaPeliculas;




        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected List<Peliculas> doInBackground(URL... urls) {
            listaPeliculas = (conversionJson.doInBackground(urls));

            return (listaPeliculas);
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param listaPeliculas Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(List<Peliculas> listaPeliculas) {

            //Utility.resultado(listaPeliculas.size());
            //FindApiBusqueda busquedalista = conversionJson.onPostExecute(busqueda);
            recyclerView.setAdapter(conversionJson.onPostExecute(listaPeliculas));
        }
    }

}
