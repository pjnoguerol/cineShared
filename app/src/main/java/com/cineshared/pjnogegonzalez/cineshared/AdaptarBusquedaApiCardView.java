package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase AdaptarActoresCardView adapta una lista de películas a un CardView para poder visualizarlo
 * correctamente
 */

public class AdaptarBusquedaApiCardView extends RecyclerView.Adapter<AdaptarBusquedaApiCardView.BusquedaViewHolder> {

    private boolean activate;
    private ConversionJson<Resultado> conversionJson;
    private Context contex;
    private int modo;
    private Usuarios usuario;
    private Button numPeliculas;

    /**
     * Inner class que contiene los datos de la película que se mostrarán en la pantalla de listado

     */
    public static class BusquedaViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewPelicula;
        TextView tituloPelicula;
        TextView datosPelicula;
        Button numPeliculas;
        ImageView imagenPelicula;

        /**
         * Constructor de la inner class
         *
         * @param itemView
         */
        BusquedaViewHolder(View itemView) {
            super(itemView);
            cardViewPelicula = (CardView) itemView.findViewById(R.id.cardViewActorDirectorPelicula);
            tituloPelicula = (TextView) itemView.findViewById(R.id.nombreActorDirectorPelicula);

            imagenPelicula = (ImageView) itemView.findViewById(R.id.imagenActorDirectorPelicula);
            datosPelicula = (TextView)itemView.findViewById(R.id.datos);

            numPeliculas = (Button) itemView.findViewById(R.id.btBusqueda);


        }

    }

    // Biblioteca a transformar
    List<Peliculas> busquedaApi;

    /**
     * Constructor de la clase
     *
     * @param busquedaApi de películas que se desean adaptar a un CardView
     */
    AdaptarBusquedaApiCardView(List<Peliculas> busquedaApi, int modo) {

        this.busquedaApi = busquedaApi;
        this.modo = modo;

        //

    }
    AdaptarBusquedaApiCardView(List<Peliculas> busquedaApi, int modo, Usuarios usuario) {

        this.usuario = usuario;
        this.busquedaApi = busquedaApi;
        this.modo = modo;
        //

    }

    public void activateButtons(boolean activate) {
        this.activate = activate;
        notifyDataSetChanged(); //need to call it for the child views to be re-created with buttons.
    }

    /**
     * Método que se llama cuando se inicia el adaptador
     *
     * @param recyclerView Instancia del que inicia el adaptador
     */

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BusquedaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemscardview, parent, false);



        // set the view's size, margins, paddings and layout parameters


        return new BusquedaViewHolder(v);
    }

    public View getView (int position, View convertView, ViewGroup parent){
        if( convertView == null ){
            //We must create a View:
            //convertView = inflater.inflate(R.layout.my_list_item, parent, false);
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_buscar_peliculas, parent, false);
        }
        //Here we can do changes to the convertView, such as set a text on a TextView
        //or an image on an ImageView.
        return convertView;
    }

    /**
     * Método al que se llama para mostrar la información procesada en la posición especificada, para
     * ello actualizará la información del bandaSonoraViewHolder
     *
     * @param busquedaViewHolder Información a mostrar y actualizar
     * @param posicion           Posición donde debe ser mostrada
     */
    @Override
    public void onBindViewHolder(BusquedaViewHolder busquedaViewHolder, int posicion) {
        // Creamos la lista de actores para mostrarla

        String listadoActoresPelicula = "Actores: ";
        final Peliculas pelicula = busquedaApi.get(posicion);

        busquedaViewHolder.tituloPelicula.setText(pelicula.getTitle());

        busquedaViewHolder.datosPelicula.setText("Distancia= "+pelicula.getDistancia()+"");

        // Al listado de actores le quitamos la última coma

        Picasso.with(busquedaViewHolder.itemView.getContext()).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(busquedaViewHolder.imagenPelicula);




        busquedaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String url2="";
                if (modo == 1)
                {
                    url2 = Constantes.RUTA_ACTUALIZAR_BUSQUEDA + pelicula.getId() +"&usuario="+usuario.getId_usua();
                }
                else
                {
                    url2 = Constantes.RUTA_PELICULAS +pelicula.getTitle()+"&imagen="+pelicula.getPoster_path()+"&sinopsis="+pelicula.getOverview()+"&api_id="+pelicula.getId();
                }

                final Context context = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.app_name);
                builder.setMessage("¿Quieres agregar la pelicula "+ pelicula.getTitle()+"? "+ usuario.getUsuario());

                final String url = url2;

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            conversionJson = new ConversionJson<>(Constantes.RESULTADO);
                            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                            if (networkInfo != null && networkInfo.isConnected()) {
                                Log.w("ERROR WEB", url);
                                new BusquedaJsonTask(context, pelicula.getTitle()).
                                        execute(new URL(url));
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
                //Intent intent = new Intent(view.getContext(), PeliculasActivity.class);
                //intent.putExtra(Constantes.PELICULAS, pelicula);
                //view.getContext().startActivity(intent);
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
        if (i==0)
        {
            BuscarPeliculasActivity.btbusqueda.setVisibility(View.VISIBLE);
            //BuscarPeliculasActivity.btBusquedaNatural.setVisibility(View.GONE);
        }

        return i;
    }
    public class BusquedaJsonTask extends AsyncTask<URL, Void, Resultado > {

        private Resultado resultado;
        private Context context;
        private String pelicula;

        public BusquedaJsonTask(Context context, String pelicula)
        {
            this.context = context;
            this.pelicula = pelicula;
        }

        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected Resultado doInBackground(URL... urls) {

            resultado = conversionJson.doInBackground(urls).get(0);

            return (resultado);
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param resultado Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Resultado resultado) {
             if (resultado!=null)
             {
                if(resultado.isOk())
                {
                    Toast.makeText(context, "Pelicula "+pelicula+" introducida correctamente" , Toast.LENGTH_SHORT).show();


                }
                else
                {
                    Toast.makeText(context, "Error "+resultado.getError() , Toast.LENGTH_SHORT).show();

                }
             }
             else
             {
                 Toast.makeText(context, "Error nullable en la captura del resultado " , Toast.LENGTH_SHORT).show();
             }


            //FindApiBusqueda busquedalista = conversionJson.onPostExecute(busqueda);
            //recyclerView.setAdapter(conversionJson.onPostExecute(lista));
        }
    }

}