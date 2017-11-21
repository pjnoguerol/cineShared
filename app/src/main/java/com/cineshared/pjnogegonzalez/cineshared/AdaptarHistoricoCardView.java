package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Clase AdaptarActoresCardView adapta una lista de películas a un CardView para poder visualizarlo
 * correctamente
 */
public class AdaptarHistoricoCardView extends RecyclerView.Adapter<AdaptarHistoricoCardView.BibliotecaViewHolder> {

    private Usuarios usuario;


    /**
     * Inner class que contiene los datos de la película que se mostrarán en la pantalla de listado
     */
    public static class BibliotecaViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewPelicula;
        TextView tituloPelicula;
        TextView usuarioPelicula;
        ImageView imagenPelicula;

        /**
         * Constructor de la inner class
         *
         * @param itemView
         */
        BibliotecaViewHolder(View itemView) {
            super(itemView);
            cardViewPelicula = (CardView) itemView.findViewById(R.id.cardViewHistorico);
            tituloPelicula = (TextView) itemView.findViewById(R.id.nombreHistorico);
            usuarioPelicula = (TextView) itemView.findViewById(R.id.cardintercambio);
            imagenPelicula = (ImageView) itemView.findViewById(R.id.imagenHistorico);
        }

    }

    // Biblioteca a transformar
    List<Peliculas> listaPeliculas;

    /**
     * Constructor de la clase
     *
     * @param listaPeliculas Lista de películas que se desean adaptar a un CardView
     */



    AdaptarHistoricoCardView(List<Peliculas> listaPeliculas, Usuarios usuario) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemshistoricardview, viewGroup, false);
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

        peliculaViewHolder.tituloPelicula.setText(Utility.acotar(pelicula.getTitle()));

        peliculaViewHolder.usuarioPelicula.setText("Usuario Petición: "+Utility.capitalizar(pelicula.getUsuarionombre()));


        Picasso.with(peliculaViewHolder.itemView.getContext()).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(peliculaViewHolder.imagenPelicula);

        peliculaViewHolder.imagenPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
