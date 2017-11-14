package com.cineshared.pjnogegonzalez.cineshared;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private Usuarios usuario;
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
    AdaptarBibliotecaCardView(List<Peliculas> listaPeliculas) {
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
        //Log.w("Adapter", "Dentro adaptador biblioteca");
        final Peliculas pelicula = listaPeliculas.get(posicion);
        peliculaViewHolder.tituloPelicula.setText(pelicula.getTitle());

        if (pelicula.getAlert()>0)
            Picasso.with(peliculaViewHolder.itemView.getContext()).load(Constantes.RUTA_IMAGEN+"alert.png").into(peliculaViewHolder.imagenIcon);
        // Al listado de actores le quitamos la última coma
        Picasso.with(peliculaViewHolder.itemView.getContext()).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(peliculaViewHolder.imagenPelicula);

        peliculaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AreaIntercambioActivity.class);
                intent.putExtra(Constantes.PELICULAS, pelicula);
                intent.putExtra(Constantes.USUARIO, usuario);
                view.getContext().startActivity(intent);
                return;
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
}