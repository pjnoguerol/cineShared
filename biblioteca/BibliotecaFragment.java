package com.cineshared.pjnogegonzalez.cineshared.biblioteca;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * Clase BibliotecaFragment contiene las acciones relativas a la biblioteca
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class BibliotecaFragment extends Fragment {

    // Definimos las variables necesarias
    private RecyclerView recyclerView;
    private Usuarios usuario;
    private RadioGroup radioGroup;

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(Constantes.BIBLIOTECA);

    // Constructor vacío
    public BibliotecaFragment() {
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
        Context contexto = inflater.getContext();
        View vistaRaiz = inflater.inflate(R.layout.recyclerview_activity_radio, container, false);
        // Obtenemos los argumentos presentes para el fragmento y poder cargar los datos
        usuario = (Usuarios) getArguments().getSerializable("usuarios");
        final String cadena = getArguments().getString("cadena");
        if (usuario != null) {
            conversionJson.setUsuario(usuario);
        }
        recyclerView = conversionJson.onCreateView(contexto, vistaRaiz, getResources());
        // Obtenemos la URL de la que se conseguirán las películas
        String url = "";
        if (cadena != null)
            url = Constantes.RUTA_BIBLIOTECA_CADENA + usuario.getId_usua() + "&cadena=" + cadena;
        else
            url = Constantes.RUTA_BIBLIOTECA + usuario.getId_usua();
        url += "&estadobiblo=3";
        // Cargamos los datos de la biblioteca
        cargarBiblioteca(contexto, url);
        radioGroup = (RadioGroup) vistaRaiz.findViewById(R.id.radio_grupo);
        // Marcamos por defecto que se muestren todas las coincidencias
        RadioButton radioButtonTodas = (RadioButton) vistaRaiz.findViewById(R.id.radio_todas);
        radioButtonTodas.setChecked(true);
        // En caso de cseleccionar otro radio button se realiza la query correspondiente dependiendo del seleccionado
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButtonSeleccionado = (RadioButton) getActivity().findViewById(checkedId);
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("usuariohistorico", usuario.getId_usua() + "");
                String url;
                if (cadena != null)
                    url = Constantes.RUTA_BIBLIOTECA_CADENA + usuario.getId_usua() + "&cadena=" + cadena;
                else
                    url = Constantes.RUTA_BIBLIOTECA + usuario.getId_usua();
                url += "&estadobiblo=";
                if (radioButtonSeleccionado.getText().equals("Todas")) {
                    url += "3";
                } else if (radioButtonSeleccionado.getText().equals("Abiertas")) {
                    url += "1";
                } else if (radioButtonSeleccionado.getText().equals("Cerradas")) {
                    url += "2";
                }
                cargarBiblioteca(getContext(), url);
            }
        });
        return vistaRaiz;
    }

    /**
     * Método cargarBiblioteca realiza la llamada y obtiene la lista de películas de la biblioteca
     *
     * @param context Contexto donde se mostrarán los resultados
     * @param url     URL de la que se obtienen dichos resultados
     */
    private void cargarBiblioteca(Context context, String url) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new PeliculasJsonTask().execute(new URL(url));
            } else {
                Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class que parsea las Películas para mostrarlas
     */
    public class PeliculasJsonTask extends AsyncTask<URL, Void, List<Peliculas>> {

        private List<Peliculas> listaPelicula;

        /**
         * Método que llama al parseo de películas para obtener la lista a mostrar
         *
         * @return Lista de películas
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
