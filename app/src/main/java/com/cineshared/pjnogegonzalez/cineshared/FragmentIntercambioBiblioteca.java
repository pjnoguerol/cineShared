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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentIntercambioBiblioteca extends Fragment {

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(getActivity(), Constantes.BIBLIOTECA);
    private RecyclerView recyclerView;
    private int user;
    private int historico;
    private Usuarios usuario;
    public FragmentIntercambioBiblioteca() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity2, container, false);
        //usuario = (Usuarios) getArguments().getSerializable("usuarios");
        //if (usuario!=null)
       // {
            //conversionJson.setUsuario(usuario);
        //}
        Log.w("HEMOS ENTRADO AQUI", "AQUIIII");
        user =  getArguments().getInt("intercambio");
        Usuarios usuario = new Usuarios();
        usuario.setId_usua(user);
        conversionJson.setUsuario(usuario);

        historico = getArguments().getInt("historico");
        recyclerView = conversionJson.onCreateViewScroll(context, rootView, getResources());
        String url = "";
        conversionJson.setMode(5);
        conversionJson.setHistorico(historico);
        url = Constantes.RUTA_BIBLIOTECA+user;
        //Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
        Log.w("MI INPUTSTREAM", url );
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
     * Inner class que parsea la Biblioteca a una CardView
     */
    public class PeliculasJsonTask extends AsyncTask<URL, Void, List<Peliculas>> {

        private List<Peliculas> listaPelicula;

        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
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