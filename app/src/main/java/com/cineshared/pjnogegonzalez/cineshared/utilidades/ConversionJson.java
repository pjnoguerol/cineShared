package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.biblioteca.AdaptarBibliotecaCardView;
import com.cineshared.pjnogegonzalez.cineshared.busqueda.AdaptarBusquedaApiCardView;
import com.cineshared.pjnogegonzalez.cineshared.historico.AdaptarHistoricoCardView;
import com.cineshared.pjnogegonzalez.cineshared.peliculas.Peliculas;
import com.cineshared.pjnogegonzalez.cineshared.peliculas.PeliculasComprobacion;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Clase que convierte los JSON que se obtienen de los distintos servicios en el objeto indicado
 * <p>
 * Creada por Pablo Noguerol y Elena González
 *
 * @param <T> Tipo de objeto que se va a parsear *
 */
public class ConversionJson<T> {

    // Definimos las variables
    private HttpURLConnection conexion;
    private String tipoObjeto;
    private Usuarios usuario;
    private int mode;
    private int historico;

    /**
     * Constructor de la clase con todos los parámetros
     *
     * @param tipoObjeto Tipo de objeto que convertimos: actores, peliculas, directores, sonoras y usuarios
     */
    public ConversionJson(String tipoObjeto) {
        this.mode = 1;
        this.tipoObjeto = tipoObjeto;
    }

    // Métodos getter y setter de todos los atributos
    public int getHistorico() {
        return historico;
    }

    public void setHistorico(int historico) {
        this.historico = historico;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Método que crea el RecyclerView para los fragmentos necesarios
     *
     * @param context   Contexto
     * @param rootView  Vista principal
     * @param resources Recursos
     * @return RecyclerView creado
     */
    public RecyclerView onCreateView(Context context, View rootView, Resources resources) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        int mNoOfColumns = Utilidades.calcularNumeroColumnas(context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, mNoOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }

    /**
     * Metodo que crea los reciclyerView para los scroll views
     *
     * @param context   Contexto
     * @param rootView  Vista principal
     * @param resources Recursos
     * @return RecyclerView creado
     */
    public RecyclerView onCreateViewScroll(Context context, View rootView, Resources resources) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }

    /**
     * Metodo que crea los reciclyerView para los históricos
     *
     * @param context   Contexto
     * @param rootView  Vista principal
     * @param resources Recursos
     * @return RecyclerView creado
     */
    public RecyclerView onCreateViewHistorico(Context context, View rootView, Resources resources) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }

    /**
     * Método que realiza la llamada para obtener la lista de objetos en Json y devuelve una lista
     * de esos elementos
     *
     * @param urls Url de conexión
     * @return Lista de objetos parseados
     */
    public List<T> doInBackground(URL... urls) {
        List<T> listaConvertir = new ArrayList<>();
        try {
            // Establecer la conexión
            conexion = (HttpURLConnection) urls[0].openConnection();
            conexion.setConnectTimeout(Constantes.CONNECTION_TIMEOUT);
            conexion.setReadTimeout(Constantes.READ_TIMEOUT);
            int statusCode = conexion.getResponseCode();
            if (statusCode == 200) {
                // Parsear la lista de objetos con formato JSON
                InputStream inputStream = new BufferedInputStream(conexion.getInputStream());
                listaConvertir = parsearJson(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.disconnect();
        }
        return listaConvertir;
    }

    public List<T> doInBackgroundPost(URL urls, Uri.Builder builder) {
        List<T> listaConvertir = new ArrayList<>();
        try {
            conexion = (HttpURLConnection) urls.openConnection();
            conexion.setReadTimeout(10000);
            conexion.setConnectTimeout(15000);
            conexion.setRequestMethod("POST");
            conexion.setDoInput(true);
            conexion.setDoOutput(true);
            String query = builder.build().getEncodedQuery();
            OutputStream os = conexion.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int statusCode = conexion.getResponseCode();
            if (statusCode == HttpsURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(conexion.getInputStream());
                listaConvertir = parsearJson(inputStream);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.disconnect();
            return listaConvertir;
        }


    }

    /**
     * Método doInBackgroundObject realiza el parseo y devuelve el objeto
     *
     * @param urls URL de la que obtener los datos
     * @return Objeto parseado
     */
    public T doInBackgroundObject(URL... urls) {
        T convertir = null;
        try {
            // Establecer la conexión
            conexion = (HttpURLConnection) urls[0].openConnection();
            conexion.setConnectTimeout(Constantes.CONNECTION_TIMEOUT);
            conexion.setReadTimeout(Constantes.READ_TIMEOUT);
            int statusCode = conexion.getResponseCode();
            if (statusCode == 200) {
                // Parsear la lista de objetos con formato JSON
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(conexion.getInputStream()));
                convertir = gson.fromJson(reader, FindApiBusqueda.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conexion.disconnect();
        }
        return convertir;
    }

    /**
     * Método que asigna la lista de objetos al adaptador para obtener un cardView. Dependiendo del tipo
     * de objeto a convertir se llamará aun adaptador o a otro
     *
     * @param listaConvertir Lista de objetos para el adaptador
     */
    public RecyclerView.Adapter onPostExecute(List<T> listaConvertir) {
        RecyclerView.Adapter adaptador = null;
        if (listaConvertir != null) {
            if (Constantes.BIBLIOTECA.equals(tipoObjeto))
                adaptador = new AdaptarBibliotecaCardView((List<Peliculas>) listaConvertir, mode, usuario);
            else if (Constantes.BUSQUEDA.equals(tipoObjeto)) {
                List<FindApiBusqueda> lista = (List<FindApiBusqueda>) listaConvertir;
                adaptador = new AdaptarBusquedaApiCardView(lista.get(0).getResults(), 0, usuario);
            } else if (Constantes.BUSQUEDA_NATURAL.equals(tipoObjeto)) {
                if (usuario != null)
                    adaptador = new AdaptarBusquedaApiCardView((List<Peliculas>) listaConvertir, 1, usuario);
            } else if ((Constantes.PELICULAS.equals(tipoObjeto))) {
                if (usuario != null)

                    adaptador = new AdaptarBusquedaApiCardView((List<Peliculas>) listaConvertir, mode, usuario);
            } else if ((Constantes.INTERCAMBIO.equals(tipoObjeto))) {

                if (usuario != null)

                    adaptador = new AdaptarHistoricoCardView((List<Peliculas>) listaConvertir, usuario);
            } else if (Constantes.PELICULAS_CHECK.equals(tipoObjeto)) {
                List<PeliculasComprobacion> lista = (List<PeliculasComprobacion>) listaConvertir;
                adaptador = new AdaptarHistoricoCardView(lista.get(0).getPeliculas(), usuario);
            }
        }
        return adaptador;
    }

    /**
     * Método que recibe la lista de objetos en un Json y lo parsea a una lista
     *
     * @param inputStream Flujo de entrada de bytes
     * @return Lista de elementos parseados
     * @throws IOException Excepción de red
     */
    public List<T> parsearJson(InputStream inputStream) throws IOException {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        List<T> listaConvertir = new ArrayList<>();

        // Iniciar el lector y obtener la lista de resultados
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            // Parseamos cada elemento y lo añadimos a la lista
            T elementoConvertido = null;
            if (Constantes.BIBLIOTECA.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, Peliculas.class);

            else if (Constantes.BUSQUEDA.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, FindApiBusqueda.class);
            else if (Constantes.USUARIOS.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, Usuarios.class);
            else if (Constantes.RESULTADO.equals((tipoObjeto)))
                elementoConvertido = gson.fromJson(jsonReader, Resultado.class);
            else if (Constantes.BUSQUEDA_NATURAL.equals((tipoObjeto)))
                elementoConvertido = gson.fromJson(jsonReader, Peliculas.class);
            else if (Constantes.PELICULAS.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, Peliculas.class);
            else if (Constantes.INTERCAMBIO.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, Peliculas.class);
            else if (Constantes.PELICULAS_CHECK.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, PeliculasComprobacion.class);

            listaConvertir.add(elementoConvertido);
        }
        // Finalizamos y cerramos el reader y se devuelve la lista de objetos parseados
        jsonReader.endArray();
        jsonReader.close();
        return listaConvertir;
    }
}