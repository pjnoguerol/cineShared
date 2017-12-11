package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Clase AdaptarActoresCardView adapta una lista de películas a un CardView para poder visualizarlo
 * correctamente
 */
public class AdaptarBibliotecaCardView extends RecyclerView.Adapter<AdaptarBibliotecaCardView.BibliotecaViewHolder> {

    private Usuarios usuario;
    private int mode;
    private int historico;
    private ConversionJson<Resultado> conversionJson;
    /**
     * Inner class que contiene los datos de la película que se mostrarán en la pantalla de listado
     */
    public static class BibliotecaViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewPelicula;
        TextView tituloPelicula;
        ImageView imagenIcon;
        ImageView imagenPelicula;

        /**
         * Constructor de la inner class
         *
         * @param itemView
         */
        BibliotecaViewHolder(View itemView) {
            super(itemView);
            cardViewPelicula = (CardView) itemView.findViewById(R.id.cardViewActorDirectorPelicula);
            tituloPelicula = (TextView) itemView.findViewById(R.id.nombreActorDirectorPelicula);
            imagenIcon = (ImageView) itemView.findViewById(R.id.alericon);
            imagenPelicula = (ImageView) itemView.findViewById(R.id.imagenActorDirectorPelicula);
        }

    }

    // Biblioteca a transformar
    List<Peliculas> listaPeliculas;

    /**
     * Constructor de la clase
     *
     * @param listaPeliculas Lista de películas que se desean adaptar a un CardView
     */
    AdaptarBibliotecaCardView(List<Peliculas> listaPeliculas, int mode, int historico, Usuarios usuario) {
        this.historico = historico;
        this.mode = mode;
        this.usuario = usuario;
        this.listaPeliculas = listaPeliculas;
    }


    AdaptarBibliotecaCardView(List<Peliculas> listaPeliculas, Usuarios usuario) {
        this.usuario = usuario;

        this.listaPeliculas = listaPeliculas;
    }

    /**
     * Método que se llama cuando se inicia el adaptador
     *
     * @param recyclerView Instancia del que inicia el adaptador
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Método que se llama para crear un BibliotecaViewHolder
     *
     * @param viewGroup ViewGroup al que se le añadirá la nueva vista
     * @param viewType  Tipo de vista
     * @return ViewHolder con los datos de la nueva vista
     */
    @Override
    public BibliotecaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_detalle_pelicula, viewGroup, false);
        return new BibliotecaViewHolder(view);
    }

    /**
     * Método al que se llama para mostrar la información procesada en la posición especificada, para
     * ello actualizará la información del bandaSonoraViewHolder
     *
     * @param peliculaViewHolder Información a mostrar y actualizar
     * @param posicion           Posición donde debe ser mostrada
     */
    @Override
    public void onBindViewHolder(final BibliotecaViewHolder peliculaViewHolder, int posicion) {
        // Creamos la lista de actores para mostrarla
        final Peliculas pelicula = listaPeliculas.get(posicion);

        final int id = pelicula.getId();

        //Log.w("Adapter", "Dentro adaptador biblioteca");

        peliculaViewHolder.tituloPelicula.setText(Utilidades.acotar(pelicula.getTitle()));

        if (pelicula.getEstado()==0)
        {
            if (pelicula.getAlert()>0)
                Picasso.with(peliculaViewHolder.itemView.getContext()).load(Constantes.RUTA_IMAGEN+"alert.png").into(peliculaViewHolder.imagenIcon);
        }
        else
        {
            Picasso.with(peliculaViewHolder.itemView.getContext()).load(Constantes.RUTA_IMAGEN+"candado.png").into(peliculaViewHolder.imagenIcon);
        }

        // Al listado de actores le quitamos la última coma
        Picasso.with(peliculaViewHolder.itemView.getContext()).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(peliculaViewHolder.imagenPelicula);

        peliculaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                if (mode==1)
                {
                    Intent intent = new Intent(view.getContext(), AreaIntercambioActivity.class);
                    intent.putExtra(Constantes.PELICULAS, pelicula);
                    intent.putExtra(Constantes.USUARIO, usuario);
                    view.getContext().startActivity(intent);

                }
                else if (mode==5)
                {
                    /*
                    try {
                        String url = Constantes.RUTA_ACTUALIZAR_INTERCAMBIO+historico+"&usuarioin="+usuario.getId_usua()+"&peliculain="+id;

                        conversionJson = new ConversionJson<>(Constantes.RESULTADO);
                        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                        if (networkInfo != null && networkInfo.isConnected()) {
                            Log.w("HISTORICO", url);
                            new ResultadoJsonTask(context).
                                    execute(new URL(url));
                        } else {
                            Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    */
                    Intent intent = new Intent(view.getContext(), AreaUsuarioActivity.class);
                    intent.putExtra(Constantes.PELICULAS, pelicula);
                    intent.putExtra(Constantes.USUARIO, usuario);
                    view.getContext().startActivity(intent);
                }

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
        return listaPeliculas.size();
    }

    public class ResultadoJsonTask extends AsyncTask<URL, Void, Resultado > {

        private Resultado resultado;
        private Context context;
        private String pelicula;

        public ResultadoJsonTask(Context context)
        {
            this.context = context;
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