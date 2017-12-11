package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clase AdaptarMensajeChat gestiona lo necesario para mostrar los mensajes del chat por pantalla
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class AdaptarMensajeChat extends RecyclerView.Adapter<AdaptarMensajeChat.MensajeChatViewHolder> {
    // Definimos las variables
    private List<MensajeChat> listaMensajes;
    private DatabaseReference referenciaBD;
    private View vistaMensajeChat;

    // Constructor inicial
    public AdaptarMensajeChat(List<MensajeChat> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    /**
     * Inner class que contiene los datos de los mensajes que se mostrarán en la pantalla del chat
     */
    public class MensajeChatViewHolder extends RecyclerView.ViewHolder {

        public TextView textoMensaje;
        public TextView nombreMensaje;
        public TextView horaMensaje;

        // Constructor de la inner class
        public MensajeChatViewHolder(View view) {
            super(view);
            textoMensaje = (TextView) view.findViewById(R.id.texto_mensaje_chat);
            nombreMensaje = (TextView) view.findViewById(R.id.nombre_mensaje_chat);
            horaMensaje = (TextView) view.findViewById(R.id.hora_mensaje_chat);
        }
    }

    /**
     * Método que se llama para crear un MensajeChatViewHolder
     *
     * @param viewGroup ViewGroup al que se le añadirá la nueva vista
     * @param viewType  Tipo de vista
     * @return ViewHolder con los datos de la nueva vista
     */
    @Override
    public MensajeChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View vistaMensaje = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_mensaje_chat, viewGroup, false);
        this.vistaMensajeChat = vistaMensaje;
        return new MensajeChatViewHolder(vistaMensaje);
    }

    /**
     * Método al que se llama para mostrar la información procesada en la posición especificada, para
     * ello actualizará la información del mensajeChatViewHolder
     *
     * @param mensajeChatViewHolder Información a mostrar y actualizar
     * @param posicion              Posición donde debe ser mostrada
     */
    @Override
    public void onBindViewHolder(final MensajeChatViewHolder mensajeChatViewHolder, int posicion) {
        // Creamos el mensaje para mostrarlo
        MensajeChat mensajeChat = listaMensajes.get(posicion);
        String remitenteMensaje = mensajeChat.getRemitenteMensaje();
        // Dependiendo de si el usuario es el remitente o el receptor se aplica un estilo u otro a los mensajes
        if (remitenteMensaje.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            vistaMensajeChat.setBackground(vistaMensajeChat.getResources().getDrawable(R.drawable.fondo_mensaje_chat_remitente));
            mensajeChatViewHolder.textoMensaje.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            vistaMensajeChat.setBackground(vistaMensajeChat.getResources().getDrawable(R.drawable.fondo_mensaje_chat));
            mensajeChatViewHolder.textoMensaje.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        referenciaBD = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE).child(remitenteMensaje);
        referenciaBD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nombreMensaje = dataSnapshot.child(Constantes.NOMBRE_USUARIO).getValue().toString();
                mensajeChatViewHolder.nombreMensaje.setText(nombreMensaje);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mensajeChatViewHolder.textoMensaje.setText(mensajeChat.getTextoMensaje());
        mensajeChatViewHolder.horaMensaje.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(mensajeChat.getHoraMensaje())));
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
