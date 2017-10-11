package com.cineshared.pjnogegonzalez.cineshared;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Clase AdaptarActoresCardView adapta una lista de películas a un CardView para poder visualizarlo
 * correctamente
 */
public class AdaptarBibliotecaCardView extends RecyclerView.Adapter<AdaptarBibliotecaCardView.BibliotecaViewHolder> {

    /**
     * Inner class que contiene los datos de la película que se mostrarán en la pantalla de listado
     */
    public static class BibliotecaViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewPelicula;
        TextView tituloPelicula;

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

            imagenPelicula = (ImageView) itemView.findViewById(R.id.imagenActorDirectorPelicula);
        }

    }

    // Biblioteca a transformar
    List<Biblioteca> listaBiblioteca;

    /**
     * Constructor de la clase
     *
     * @param listaPeliculas Lista de películas que se desean adaptar a un CardView
     */
    AdaptarBibliotecaCardView(List<Biblioteca> listaPeliculas) {
        this.listaBiblioteca = listaPeliculas;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemscardview, viewGroup, false);
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
        String listadoActoresPelicula = "Actores: ";
        final Biblioteca biblioteca = listaBiblioteca.get(posicion);
        peliculaViewHolder.tituloPelicula.setText(biblioteca.getNombre());

        // Al listado de actores le quitamos la última coma

        Picasso.with(peliculaViewHolder.itemView.getContext()).load(
                Constantes.RUTA_IMAGEN + biblioteca.getImagen()).into(peliculaViewHolder.imagenPelicula);
        /*
        peliculaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PeliculasActivity.class);
                intent.putExtra(Constantes.PELICULAS, pelicula);
                view.getContext().startActivity(intent);
            }
        });
        */
    }

    /**
     * Método que devuelve el número de elementos en el array de películas
     *
     * @return Contador del número de elementos
     */
    @Override
    public int getItemCount() {
        return listaBiblioteca.size();
    }
}