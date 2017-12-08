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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BibliotecaFragment extends Fragment {

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(getActivity(), Constantes.BIBLIOTECA);
    private RecyclerView recyclerView;
    private Usuarios usuario;
    private RadioGroup radioGroup;
    public BibliotecaFragment() {
        // Required empty public constructor
    }


    private void cargarBiblioteca(Context context, String url)
    {
        try {
            Log.w("URL BIBLIOTECA",url);
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity_radio, container, false);
        usuario = (Usuarios) getArguments().getSerializable("usuarios");
        final String cadena = getArguments().getString("cadena");
        if (usuario!=null)
        {
            conversionJson.setUsuario(usuario);
        }
        recyclerView = conversionJson.onCreateView(context, rootView, getResources());
        String url = "";
        if (cadena!=null)
            url = Constantes.RUTA_BIBLIOTECA_CADENA+usuario.getId_usua()+"&cadena="+cadena;
        else
            url = Constantes.RUTA_BIBLIOTECA+usuario.getId_usua();
        url += "&estadobiblo=3";
        cargarBiblioteca(context, url);
        radioGroup = (RadioGroup)rootView.findViewById(R.id.radio_grupo);
        RadioButton rb1=(RadioButton)rootView.findViewById(R.id.radio_todas);
        rb1.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // comprobamos cual de los radios están activos
                RadioButton rb=(RadioButton)getActivity().findViewById(checkedId);
                Uri.Builder  builder = new Uri.Builder();
                builder.appendQueryParameter("usuariohistorico", usuario.getId_usua()+"");
                String url;
                if (cadena!=null)
                    url = Constantes.RUTA_BIBLIOTECA_CADENA+usuario.getId_usua()+"&cadena="+cadena;
                else
                    url = Constantes.RUTA_BIBLIOTECA+usuario.getId_usua();
                url += "&estadobiblo=";
                if(rb.getText().equals("Todas"))
                {
                    url +="3";
                }
                else if (rb.getText().equals("Abiertas"))
                {
                    url +="1";
                }
                else if (rb.getText().equals("Cerradas"))
                {
                    url +="2";
                }
                cargarBiblioteca(getContext(), url);

            }
        });
        //Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
        //Log.w("MI INPUTSTREAM", url );



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
