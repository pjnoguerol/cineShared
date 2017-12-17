package com.cineshared.pjnogegonzalez.cineshared.busqueda;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.peliculas.Peliculas;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Resultado;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Clase AdaptarBusquedaApiCardView adapta una lista de búsquedas a un CardView para poder visualizarlo correctamente
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class AdaptarBusquedaApiCardView extends RecyclerView.Adapter<AdaptarBusquedaApiCardView.BusquedaViewHolder> {

    // Definimos las variables necesarias
    private ConversionJson<Resultado> conversionJson;
    private int areaMostrar;
    private Usuarios usuario;

    /**
     * Inner class que contiene los datos de la búsqueda que se mostrarán en la pantalla de listado
     */
    public static class BusquedaViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewPelicula;
        TextView tituloPelicula;
        TextView datosPelicula;
        TextView usuarioPelicula;
        Button numPeliculas;
        ImageView imagenPelicula;

        /**
         * Constructor de la inner class
         *
         * @param itemView
         */
        public BusquedaViewHolder(View itemView) {
            super(itemView);
            cardViewPelicula = (CardView) itemView.findViewById(R.id.cardViewActorDirectorPelicula);
            tituloPelicula = (TextView) itemView.findViewById(R.id.nombreActorDirectorPelicula);
            imagenPelicula = (ImageView) itemView.findViewById(R.id.imagenActorDirectorPelicula);
            datosPelicula = (TextView) itemView.findViewById(R.id.datos);
            usuarioPelicula = (TextView) itemView.findViewById(R.id.cardUsuario);
            numPeliculas = (Button) itemView.findViewById(R.id.btBusqueda);
        }
    }

    // Lista de películas de la biblioteca a transformar
    List<Peliculas> busquedaApi;

    /**
     * Constructor de la clase
     *
     * @param busquedaApi Lista de películas que se desean adaptar a un CardView
     * @param areaMostrar Entero que es el area a mostrar de la biblioteca (usuario o intercambio)
     */
    public AdaptarBusquedaApiCardView(List<Peliculas> busquedaApi, int areaMostrar) {
        this.busquedaApi = busquedaApi;
        this.areaMostrar = areaMostrar;
    }

    /**
     * Constructor de la clase
     *
     * @param busquedaApi Lista de películas que se desean adaptar a un CardView
     * @param areaMostrar Entero que es el area a mostrar de la biblioteca (usuario o intercambio)
     * @param usuario     Usuario logueado
     */
    public AdaptarBusquedaApiCardView(List<Peliculas> busquedaApi, int areaMostrar, Usuarios usuario) {
        this.usuario = usuario;
        this.busquedaApi = busquedaApi;
        this.areaMostrar = areaMostrar;

    }

    /**
     * Método que se llama para crear un BibliotecaViewHolder
     *
     * @param viewGroup ViewGroup al que se le añadirá la nueva vista
     * @param viewType  Tipo de vista
     * @return ViewHolder con los datos de la nueva vista
     */
    @Override
    public BusquedaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_detalle_pelicula, viewGroup, false);
        return new BusquedaViewHolder(view);
    }

    /**
     * Método al que se llama para mostrar la información procesada en la posición especificada, para
     * ello actualizará la información del busquedaViewHolder
     *
     * @param busquedaViewHolder Información a mostrar y actualizar
     * @param posicion           Posición donde debe ser mostrada
     */
    @Override
    public void onBindViewHolder(BusquedaViewHolder busquedaViewHolder, int posicion) {
        // Creamos la búsqueda para mostrarla
        final Peliculas pelicula = busquedaApi.get(posicion);
        busquedaViewHolder.tituloPelicula.setText(Utilidades.acotar(pelicula.getTitle()));
        busquedaViewHolder.usuarioPelicula.setText(pelicula.getUsuarionombre());

        if (pelicula.getDistancia() != 0.0) {
            BigDecimal decimalKilometros = new BigDecimal(pelicula.getDistancia());
            float decimalDistancia = decimalKilometros.setScale(2, RoundingMode.HALF_UP).floatValue();
            busquedaViewHolder.datosPelicula.setText("Distancia= " + decimalDistancia + "");
        }

        // Mostramos las imágenes del listado de películas y establecemos las acciones al hacer click sobre ellas
        Picasso.with(busquedaViewHolder.itemView.getContext()).load(Constantes.IMAGENES + pelicula.getPoster_path())
                .into(busquedaViewHolder.imagenPelicula);

        busquedaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String urlRealizar = "";
                String mensaje = "¿Quieres agregar la pelicula " + pelicula.getTitle() + " a tu Biblioteca "
                        + Utilidades.capitalizarCadena(usuario.getUsuario()) + "?";
                if (areaMostrar == 1) {
                    urlRealizar = Constantes.RUTA_ACTUALIZAR_BUSQUEDA + pelicula.getId() + "&usuario=" + usuario.getId_usua();
                } else if (areaMostrar == 0) {
                    urlRealizar = Constantes.RUTA_PELICULAS + pelicula.getTitle() + "&imagen=" + pelicula.getPoster_path()
                            + "&sinopsis=" + pelicula.getOverview() + "&api_id=" + pelicula.getId() + "&usuario="
                            + usuario.getId_usua();
                } else if (areaMostrar == 2) {
                    urlRealizar = Constantes.RUTA_USUARIO_INTERCAMBIO + pelicula.getUsuarioid() + "&peliculaintercambio="
                            + pelicula.getId() + "&usuario=" + usuario.getId_usua();
                    mensaje = "¿Desea agregar la película para intercambiar?, " + Utilidades.capitalizarCadena(usuario.getUsuario());
                }
                final Context context = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.app_name);
                builder.setMessage(mensaje);

                final String urlFinal = urlRealizar;

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            conversionJson = new ConversionJson<>(Constantes.RESULTADO);
                            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                new BusquedaJsonTask(context, pelicula.getTitle()).execute(new URL(urlFinal));
                            } else {
                                Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    /**
     * Método que devuelve el número de elementos en el array de películas
     *
     * @return Contador del número de elementos
     */
    @Override
    public int getItemCount() {

        //BuscarPeliculasActivity.btbusqueda.setVisibility(View.VISIBLE);
        int i = busquedaApi.size();


        return i;
    }

    /**
     * Inner class que permite la realización del parseo del resultado para conocer el mismo
     */
    public class BusquedaJsonTask extends AsyncTask<URL, Void, Resultado> {

        private Resultado resultado;
        private Context context;
        private String pelicula;

        public BusquedaJsonTask(Context context, String pelicula) {
            this.context = context;
            this.pelicula = pelicula;
        }

        /**
         * Método que llama al parseo del resultado para conocerlo
         *
         * @return Resultado
         */
        @Override
        protected Resultado doInBackground(URL... urls) {
            resultado = conversionJson.doInBackground(urls).get(0);
            return (resultado);
        }

        /**
         * Método que comprueba el resultado y muestra una traza en el log
         *
         * @param resultado Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Resultado resultado) {
            if (resultado != null) {
                if (resultado.isOk()) {
                    Log.w("CineSharedBusquedaApi", "EL resultado es OK");
                } else {
                    Log.w("CineSharedBusquedaApi", "El resultado es NO OK");
                }
            } else {
                Log.w("CineSharedBusquedaApi", "El resultado es nulo");
            }
        }
    }
}