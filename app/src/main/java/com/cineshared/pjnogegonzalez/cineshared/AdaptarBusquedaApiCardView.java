package com.cineshared.pjnogegonzalez.cineshared;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Clase AdaptarActoresCardView adapta una lista de películas a un CardView para poder visualizarlo
 * correctamente
 */
public class AdaptarBusquedaApiCardView extends RecyclerView.Adapter<AdaptarBusquedaApiCardView.BusquedaViewHolder> {

    private boolean activate;
    /**
     * Inner class que contiene los datos de la película que se mostrarán en la pantalla de listado

     */
    public static class BusquedaViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewPelicula;
        TextView tituloPelicula;

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
        }

    }

    // Biblioteca a transformar
    List<Peliculas> busquedaApi;

    /**
     * Constructor de la clase
     *
     * @param busquedaApi de películas que se desean adaptar a un CardView
     */
    AdaptarBusquedaApiCardView(List<Peliculas> busquedaApi) {

        this.busquedaApi = busquedaApi;
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
        Peliculas pelicula = busquedaApi.get(posicion);

        busquedaViewHolder.tituloPelicula.setText(pelicula.getTitle());

        // Al listado de actores le quitamos la última coma

        Picasso.with(busquedaViewHolder.itemView.getContext()).load(
                "https://image.tmdb.org/t/p/w500"+pelicula.getPoster_path()).into(busquedaViewHolder.imagenPelicula);


        busquedaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        return busquedaApi.size();
    }
}