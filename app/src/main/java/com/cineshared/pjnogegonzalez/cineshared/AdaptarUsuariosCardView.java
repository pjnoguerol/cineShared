package com.cineshared.pjnogegonzalez.cineshared;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Clase AdaptarUsuariosCardView adapta una lista de usuarios a un CardView para poder visualizarlo
 * correctamente
 */
public class AdaptarUsuariosCardView extends RecyclerView.Adapter<AdaptarUsuariosCardView.UsuarioViewHolder> {

    /**
     * Inner class que contiene todos los datos del usuario
     */
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewUsuario;
        TextView nombreUsuario;
        TextView telefonoUsuario;

        /**
         * Constructor de la inner class
         *
         * @param itemView
         */
        UsuarioViewHolder(View itemView) {
            super(itemView);
            cardViewUsuario = (CardView) itemView.findViewById(R.id.cardViewUsuario);
            nombreUsuario = (TextView) itemView.findViewById(R.id.nombreUsuario);
            telefonoUsuario = (TextView) itemView.findViewById(R.id.telefonoUsuario);
        }
    }

    // Lista de usuarios a transformar
    List<Usuarios> listaUsuarios;

    /**
     * Constructor de la clase
     *
     * @param listaUsuarios Lista de usuarios que se desean adaptar a un CardView
     */
    AdaptarUsuariosCardView(List<Usuarios> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
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
     * Método que se llama para crear un UsuarioViewHolder
     *
     * @param viewGroup ViewGroup al que se le añadirá la nueva vista
     * @param viewType  Tipo de vista
     * @return ViewHolder con los datos de la nueva vista
     */
    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_usuarios, viewGroup, false);
        return new UsuarioViewHolder(view);
    }

    /**
     * Método al que se llama para mostrar la información procesada en la posición especificada, para
     * ello actualizará la información del usuarioViewHolder
     *
     * @param usuarioViewHolder Información a mostrar y actualizar
     * @param posicion          Posición donde debe ser mostrada
     */
    @Override
    public void onBindViewHolder(final UsuarioViewHolder usuarioViewHolder, int posicion) {
        usuarioViewHolder.nombreUsuario.setText(listaUsuarios.get(posicion).getUsuario());
        usuarioViewHolder.telefonoUsuario.setText(listaUsuarios.get(posicion).getTelefono());
    }

    /**
     * Método que devuelve el número de elementos en el array de usuarios
     *
     * @return Contador del número de elementos
     */
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }
}