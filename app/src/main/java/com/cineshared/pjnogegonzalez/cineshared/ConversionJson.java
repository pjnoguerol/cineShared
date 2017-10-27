package com.cineshared.pjnogegonzalez.cineshared;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que convierte los JSON que se obtienen de los distintos servicios en el objeto indicado
 *
 * @param <T> Tipo de objeto que se va a parsear
 */
public class ConversionJson<T> {

    private HttpURLConnection conexion;
    private Activity activity;
    private String tipoObjeto;

    /**
     * Constructor de la clase con todos los parámetros
     *
     * @param activity   Activity desde la que se llama a la clase
     * @param tipoObjeto Tipo de objeto que convertimos: actores, peliculas, directores, sonoras y usuarios
     */
    public ConversionJson(Activity activity, String tipoObjeto) {
        this.activity = activity;
        this.tipoObjeto = tipoObjeto;
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
        int mNoOfColumns = Utility.calculateNoOfColumns(context);
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        //GRID LAYOUT MANAGER DINAMICO
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, mNoOfColumns);
       // LinearLayoutManager layoutManager
             //   = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new GridViewEspaciado(mNoOfColumns, convertir_dpApx(10, resources)));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        return recyclerView;
    }


    /**
     * Método que realiza la llamada para obtener la lista de objetos en Json y devuelve una lista
     * de esos elementos
     *
     * @param urls Url de conexión
     * @return Lista de objetos parseados
     */
    protected List<T> doInBackground(URL... urls) {
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
    protected T doInBackgroundObject(URL... urls) {
        T convertir=null;
        try {
            // Establecer la conexión
            conexion = (HttpURLConnection) urls[0].openConnection();
            conexion.setConnectTimeout(Constantes.CONNECTION_TIMEOUT);
            conexion.setReadTimeout(Constantes.READ_TIMEOUT);
            int statusCode = conexion.getResponseCode();
            if (statusCode == 200) {
                // Parsear la lista de objetos con formato JSON
                //InputStream inputStream = new BufferedInputStream(conexion.getInputStream());
                //Comprobacion de la cadena
                Gson gson = new Gson();

                JsonReader reader = new JsonReader(new InputStreamReader(conexion.getInputStream()));
                convertir = gson.fromJson(reader, FindApiBusqueda.class);
                if (convertir ==null)
                {
                    Log.w("MI Iretorno", "es NULO");
                }
                else
                {
                    Log.w("MI Iretorno",((FindApiBusqueda)convertir).getResults().get(0).getTitle()+"" );
                }

                //convertir = parsearJsonObject(inputStream);
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
    protected RecyclerView.Adapter onPostExecute(List<T> listaConvertir) {
        RecyclerView.Adapter adaptador = null;
        if (listaConvertir != null) {
            if (Constantes.BIBLIOTECA.equals(tipoObjeto))
                adaptador = new AdaptarBibliotecaCardView((List<Biblioteca>) listaConvertir) ;
             else if (Constantes.BUSQUEDA.equals(tipoObjeto))
             {
                List<FindApiBusqueda> lista = (List<FindApiBusqueda>) listaConvertir;
                adaptador = new AdaptarBusquedaApiCardView(lista.get(0).getResults());
            }


        }


        /*
        if (listaConvertir != null) {
            if (Constantes.ACTORES.equals(tipoObjeto))
                adaptador = new AdaptarActoresCardView((List<Actores>) listaConvertir);
            else if (Constantes.PELICULAS.equals(tipoObjeto))
                adaptador = new AdaptarPeliculasCardView((List<Peliculas>) listaConvertir);
            else if (Constantes.DIRECTORES.equals(tipoObjeto))
                adaptador = new AdaptarDirectoresCardView((List<Directores>) listaConvertir);
            else if (Constantes.SONORAS.equals(tipoObjeto))
                adaptador = new AdaptarBandasSonorasCardView((List<BandasSonoras>) listaConvertir);
            else if (Constantes.USUARIOS.equals(tipoObjeto))
                adaptador = new AdaptarUsuariosCardView((List<Usuarios>) listaConvertir);

        } else {
            Toast.makeText(
                    this.activity, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
        }
        */
        return adaptador;
    }


    public String readMessage(JsonReader reader) throws IOException {
        long id = -1;
        String text = null;
        //User user = null;
        List<Double> geo = null;

        reader.beginArray();
        while (reader.hasNext()) {
            text = reader.nextName();
            Log.w("MI Json", text );
        }
        reader.endObject();
        return text;
    }
    public T parsearJsonObject(InputStream inputStream) throws IOException {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));


        String line;
        String linefinal="";

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            linefinal += line;
        }
        Log.w("MI INPUTSTREAM", linefinal );

        List<T> listaConvertir = new ArrayList<>();
        T elementoConvertido = null;
        // Iniciar el lector y obtener la lista de resultados

        //readMessage(jsonReader);

            // Parseamos cada elemento y lo añadimos a la lista


        elementoConvertido = gson.fromJson(jsonReader, FindApiBusqueda.class);




            // else if (Constantes.USUARIO_RESULTADO.equals(tipoObjeto))
            //    elementoConvertido = gson.fromJson(jsonReader, Resultado.class);
            // else if (Constantes.USUARIO_GENERO.equals(tipoObjeto))
            //  elementoConvertido = gson.fromJson(jsonReader, Generos.class);



        // Finalizamos y cerramos el reader y se devuelve la lista de objetos parseados

        jsonReader.close();
        return elementoConvertido;
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
                elementoConvertido = gson.fromJson(jsonReader, Biblioteca.class);
          //  else if (Constantes.PELICULAS.equals(tipoObjeto))
             //   elementoConvertido = gson.fromJson(jsonReader, Peliculas.class);
          //  else if (Constantes.DIRECTORES.equals(tipoObjeto))
             //   elementoConvertido = gson.fromJson(jsonReader, Directores.class);
            else if (Constantes.BUSQUEDA.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, FindApiBusqueda.class);
           else if (Constantes.USUARIOS.equals(tipoObjeto))
                elementoConvertido = gson.fromJson(jsonReader, Usuarios.class);
           // else if (Constantes.USUARIO_RESULTADO.equals(tipoObjeto))
            //    elementoConvertido = gson.fromJson(jsonReader, Resultado.class);
           // else if (Constantes.USUARIO_GENERO.equals(tipoObjeto))
              //  elementoConvertido = gson.fromJson(jsonReader, Generos.class);

            listaConvertir.add(elementoConvertido);
        }
        // Finalizamos y cerramos el reader y se devuelve la lista de objetos parseados
        jsonReader.endArray();
        jsonReader.close();
        return listaConvertir;
    }

    /**
     * Método que convierte las medidas en dp a pixels
     *
     * @param dp Medida en dp a convertir
     * @return Medida en pixeles
     */
    public int convertir_dpApx(int dp, Resources resources) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }
}