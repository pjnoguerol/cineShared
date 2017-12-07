package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by elgonzalez on 07/12/2017.
 */

public class AdaptarMensajeChat extends RecyclerView.Adapter<AdaptarMensajeChat.MensajeChatViewHolder> {

    private List<MensajeChat> listaMensajes;
    private DatabaseReference referenciaBD;

    public AdaptarMensajeChat(List<MensajeChat> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    @Override
    public MensajeChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vistaMensaje = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mensaje_chat_layout, parent, false);
        return new MensajeChatViewHolder(vistaMensaje);
    }

    public class MensajeChatViewHolder extends RecyclerView.ViewHolder {

        public TextView textoMensaje;
        public TextView nombreMensaje;
        public TextView horaMensaje;

        public MensajeChatViewHolder(View view) {
            super(view);
            textoMensaje = (TextView) view.findViewById(R.id.texto_mensaje_chat);
            nombreMensaje = (TextView) view.findViewById(R.id.nombre_mensaje_chat);
            horaMensaje = (TextView) view.findViewById(R.id.hora_mensaje_chat);
        }
    }

    @Override
    public void onBindViewHolder(final MensajeChatViewHolder mensajeChatViewHolder, int i) {
        MensajeChat mensajeChat = listaMensajes.get(i);
        String remitenteMensaje = mensajeChat.getRemitenteMensaje();

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
