package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.CircleTransform;
import com.cineshared.pjnogegonzalez.cineshared.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar barraChat;

    private RecyclerView listaConversaciones;
    private final List<MensajeChat> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    //private MessageAdapter mAdapter;

    private DatabaseReference firebaseBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Cargamos el action BAR
        barraChat = (Toolbar) findViewById(R.id.barraChat);
        setSupportActionBar(barraChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat");

        //mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        firebaseBaseDatos = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);

        mLinearLayoutManager = new LinearLayoutManager(this);

        listaConversaciones = (RecyclerView) findViewById(R.id.listaConversaciones);
        listaConversaciones.setHasFixedSize(true);
        listaConversaciones.setLayoutManager(mLinearLayoutManager);

        //listaConversaciones.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<UsuarioChat, UsuarioChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UsuarioChat, UsuarioChatViewHolder>(
                        UsuarioChat.class,
                        R.layout.chat_elemento_lista_usuarios,
                        UsuarioChatViewHolder.class,
                        firebaseBaseDatos
                ) {
                    @Override
                    protected void populateViewHolder(final UsuarioChatViewHolder usuarioChatViewHolder, final UsuarioChat usuarios,
                                                      int posicion) {

                        final String identificadorUsuario = getRef(posicion).getKey();

                        firebaseBaseDatos.child(identificadorUsuario).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String conexionUsuario = "false";
                                if (dataSnapshot.hasChild(Constantes.CONEXION_USUARIO)) {
                                    conexionUsuario = dataSnapshot.child(Constantes.CONEXION_USUARIO).getValue().toString();
                                }

                                usuarioChatViewHolder.setNombreUsuarioChat(dataSnapshot.child(Constantes.NOMBRE_USUARIO).getValue().toString());
                                usuarioChatViewHolder.setImagenUsuarioChat(dataSnapshot.child(Constantes.IMAGEN_USUARIO).getValue().toString(), ChatActivity.this);
                                usuarioChatViewHolder.setEstadoUsuario(conexionUsuario);
                                usuarioChatViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent conversacionIntent = new Intent(ChatActivity.this, ConversacionActivity.class);
                                        conversacionIntent.putExtra("identificadorUsuarioDestinatario", identificadorUsuario);
                                        conversacionIntent.putExtra("nombreUsuario", usuarios.getNombreUsuario());
                                        startActivity(conversacionIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                };


        listaConversaciones.setAdapter(firebaseRecyclerAdapter);

    }


    public static class UsuarioChatViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsuarioChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNombreUsuarioChat(String nombreUsuarioChat) {
            TextView nombreUsuarioTextView = (TextView) mView.findViewById(R.id.nombreUsuarioChat);
            nombreUsuarioTextView.setText(nombreUsuarioChat);
        }

        public void setUltimoMensajeChat(String ultimoMensajeChat) {
            TextView ultimoMensajeTextView = (TextView) mView.findViewById(R.id.ultimoMensajeChat);
            ultimoMensajeTextView.setText(ultimoMensajeChat);
        }

        public void setImagenUsuarioChat(final String imagen, final Context contexto) {
            final ImageView imagenUsuarioChat = (ImageView) mView.findViewById(R.id.imagenUsuarioChat);
            Utilidades.establecerImagenUsuario(mView.getContext(), imagen, imagenUsuarioChat);
        }

        public void setEstadoUsuario(String conexionUsuario) {
            ImageView estadoConexionUsuario = (ImageView) mView.findViewById(R.id.estadoConexionUsuario);
            if ("true".equals(conexionUsuario)) {
                estadoConexionUsuario.setImageResource(R.drawable.ic_usuario_online);
            } else {
                estadoConexionUsuario.setImageResource(R.drawable.ic_usuario_offline);
            }
        }
    }
}
