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

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes.API_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusquedaFragment extends Fragment {

    private ConversionJson<FindApiBusqueda> conversionJson = new ConversionJson<>(getActivity(), Constantes.BUSQUEDA);
    private RecyclerView recyclerView;
    private String cadenaBusqueda;
    private Usuarios usuario;
    //private Usuarios usuario;
    public BusquedaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity2, container, false);
        cadenaBusqueda = getArguments().getString("busqueda");
        usuario = (Usuarios) getArguments().getSerializable(Constantes.USUARIOS);
        if (usuario!=null)
            conversionJson.setUsuario(usuario);
        recyclerView = conversionJson.onCreateViewScroll(context, rootView, getResources());

        String url = "";
        //url = "http://www.intraco.es/cineshared/cineshared_clase.php?prueba";
        //url = "http://www.intraco.es/cineshared/cineshared_clase.php?biblioteca=2";
        url = Constantes.RUTA_WEB_API_BUSQUEDA+cadenaBusqueda+API_KEY;
        //Log.w("myApp", url);
        //Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
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
    public class BusquedaJsonTask extends AsyncTask<URL, Void, FindApiBusqueda > {

        private FindApiBusqueda busqueda;

        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected FindApiBusqueda doInBackground(URL... urls) {
            busqueda = (conversionJson.doInBackgroundObject(urls));

            return (busqueda);
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param busqueda Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(FindApiBusqueda busqueda) {
            List<FindApiBusqueda> lista = new ArrayList<>();
            lista.add(busqueda);

            //FindApiBusqueda busquedalista = conversionJson.onPostExecute(busqueda);
            recyclerView.setAdapter(conversionJson.onPostExecute(lista));
        }
    }

}
