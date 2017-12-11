package com.cineshared.pjnogegonzalez.cineshared.biblioteca;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.intercambio.AreaIntercambioActivity;
import com.cineshared.pjnogegonzalez.cineshared.peliculas.Peliculas;
import com.cineshared.pjnogegonzalez.cineshared.usuario.AreaUsuarioActivity;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;

import java.util.List;

/**
 * Clase AdaptarBibliotecaCardView adapta una lista de películas a un CardView para poder visualizarlo correctamente
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class AdaptarBibliotecaCardView extends RecyclerView.Adapter<AdaptarBibliotecaCardView.BibliotecaViewHolder> {

    // Definimos las variables necesarias
    private Usuarios usuario;
    private int areaMostrar;

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
        public BibliotecaViewHolder(View itemView) {
            super(itemView);
            cardViewPelicula = (CardView) itemView.findViewById(R.id.cardViewActorDirectorPelicula);
            tituloPelicula = (TextView) itemView.findViewById(R.id.nombreActorDirectorPelicula);
            imagenIcon = (ImageView) itemView.findViewById(R.id.alericon);
            imagenPelicula = (ImageView) itemView.findViewById(R.id.imagenActorDirectorPelicula);
        }

    }

    // Lista de películas de la biblioteca a transformar
    List<Peliculas> listaPeliculas;

    /**
     * Constructor de la clase
     *
     * @param listaPeliculas Lista de películas que se desean adaptar a un CardView
     * @param usuario        Usuario logueado
     */
    public AdaptarBibliotecaCardView(List<Peliculas> listaPeliculas, Usuarios usuario) {
        this.usuario = usuario;
        this.listaPeliculas = listaPeliculas;
    }

    /**
     * Constructor de la clase
     *
     * @param listaPeliculas Lista de películas que se desean adaptar a un CardView
     * @param areaMostrar    Entero que es el area a mostrar de la biblioteca (usuario o intercambio)
     * @param usuario        Usuario logueado
     */
    public AdaptarBibliotecaCardView(List<Peliculas> listaPeliculas, int areaMostrar, Usuarios usuario) {
        this.areaMostrar = areaMostrar;
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
     * ello actualizará la información del peliculaViewHolder
     *
     * @param peliculaViewHolder Información a mostrar y actualizar
     * @param posicion           Posición donde debe ser mostrada
     */
    @Override
    public void onBindViewHolder(final BibliotecaViewHolder peliculaViewHolder, int posicion) {
        // Creamos la lista de películas para mostrarla
        final Peliculas pelicula = listaPeliculas.get(posicion);
        peliculaViewHolder.tituloPelicula.setText(Utilidades.acotar(pelicula.getTitle()));

        if (pelicula.getEstado() == 0) {
            if (pelicula.getAlert() > 0)
                Utilidades.establecerImagen(peliculaViewHolder.itemView.getContext(), Constantes.RUTA_IMAGEN
                        + "alert.png", peliculaViewHolder.imagenIcon);
        } else {
            Utilidades.establecerImagen(peliculaViewHolder.itemView.getContext(), Constantes.RUTA_IMAGEN
                    + "candado.png", peliculaViewHolder.imagenIcon);
        }

        // Mostramos las imágenes del listado de películas y establecemos las acciones al hacer click sobre ellas
        Utilidades.establecerImagen(peliculaViewHolder.itemView.getContext(), Constantes.IMAGENES
                + pelicula.getPoster_path(), peliculaViewHolder.imagenPelicula);
        peliculaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areaMostrar == 1) {
                    Intent areaIntercambioIntent = new Intent(view.getContext(), AreaIntercambioActivity.class);
                    areaIntercambioIntent.putExtra(Constantes.PELICULAS, pelicula);
                    areaIntercambioIntent.putExtra(Constantes.USUARIO, usuario);
                    view.getContext().startActivity(areaIntercambioIntent);
                } else if (areaMostrar == 5) {
                    Intent areaUsuarioIntent = new Intent(view.getContext(), AreaUsuarioActivity.class);
                    areaUsuarioIntent.putExtra(Constantes.PELICULAS, pelicula);
                    areaUsuarioIntent.putExtra(Constantes.USUARIO, usuario);
                    view.getContext().startActivity(areaUsuarioIntent);
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

}