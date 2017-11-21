package com.cineshared.pjnogegonzalez.cineshared;


import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
public class FragmentHistoricoIntercambio extends Fragment {

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(getActivity(), Constantes.INTERCAMBIo);
    private RecyclerView recyclerView;

    private Usuarios usuario;
    public FragmentHistoricoIntercambio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity, container, false);
        usuario = (Usuarios) getArguments().getSerializable("usuarios");
        //if (usuario!=null)
       // {
            //conversionJson.setUsuario(usuario);
        //}


        if (usuario!=null)
            conversionJson.setUsuario(usuario);


        recyclerView = conversionJson.onCreateViewHistorico(context, rootView, getResources());
        String url = "";
        Log.w("FRAGMENT HISTORICO", "aqui no ENTRA o que?" );

        //Toast.makeText(context, url, Toast.LENGTH_SHORT).show();

        try {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("usuariohistorico", usuario.getId_usua()+"");


                new PeliculasJsonTask(builder).execute(new URL(Constantes.SERVIDOR+Constantes.RUTA_CLASE_PHP));
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
        private Uri.Builder builder;

        private PeliculasJsonTask(Uri.Builder builder)
        {
            this.builder = builder;
        }

        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected List<Peliculas> doInBackground(URL... urls) {
            return (listaPelicula = conversionJson.doInBackgroundPost(urls[0], this.builder));
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param listaPelicula Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(List<Peliculas> listaPelicula) {
            Log.w("LLEGA AL ADAPTADOR","SEGURO?");
            recyclerView.setAdapter(conversionJson.onPostExecute(listaPelicula));
        }
    }

}
