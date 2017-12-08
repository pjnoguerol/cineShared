package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar barraChat;

    private RecyclerView listaConversaciones;
    private final List<MensajeChat> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    //private MessageAdapter mAdapter;

    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference referenciaBDUsuarios;
    private DatabaseReference referenciaBDChats;
    private DatabaseReference referenciaBDMensajes;
    private String identificadorUsuarioLogeado;


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

        firebaseAutenticacion = FirebaseAuth.getInstance();
        identificadorUsuarioLogeado = firebaseAutenticacion.getCurrentUser().getUid();
        referenciaBDUsuarios = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);
        referenciaBDChats = FirebaseDatabase.getInstance().getReference().child(Constantes.CHAT_FIREBASE)
                .child(identificadorUsuarioLogeado);
        referenciaBDMensajes = FirebaseDatabase.getInstance().getReference().child(Constantes.MENSAJES_FIREBASE)
                .child(identificadorUsuarioLogeado);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        listaConversaciones = (RecyclerView) findViewById(R.id.listaConversaciones);
        listaConversaciones.setHasFixedSize(true);
        listaConversaciones.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query chatsQuery = referenciaBDChats.orderByChild(Constantes.HORA_MENSAJE);

        FirebaseRecyclerAdapter<ConversacionChat, ConversacionChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ConversacionChat, ConversacionChatViewHolder>(
                        ConversacionChat.class,
                        R.layout.layout_elemento_lista_chat,
                        ConversacionChatViewHolder.class,
                        chatsQuery
                ) {
                    @Override
                    protected void populateViewHolder(final ConversacionChatViewHolder conversacionChatViewHolder,
                                                      final ConversacionChat conversacionChat, int posicion) {

                        final String identificadorUsuarioDestinatario = getRef(posicion).getKey();
                        Query ultimoMensajeQuery = referenciaBDMensajes.child(identificadorUsuarioDestinatario).limitToLast(1);
                        ultimoMensajeQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String cadena) {
                                String textoMensaje = dataSnapshot.child(Constantes.TEXTO_MENSAJE).getValue().toString();
                                conversacionChatViewHolder.setUltimoMensajeChat(textoMensaje, conversacionChat.isVistaConversacion());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String cadena) {
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String cadena) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                        referenciaBDUsuarios.child(identificadorUsuarioDestinatario).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String conexionUsuario = "false";
                                if (dataSnapshot.hasChild(Constantes.CONEXION_USUARIO)) {
                                    conexionUsuario = dataSnapshot.child(Constantes.CONEXION_USUARIO).getValue().toString();
                                }
                                final String nombreUsuarioChat = dataSnapshot.child(Constantes.NOMBRE_USUARIO).getValue().toString();
                                conversacionChatViewHolder.setNombreUsuarioChat(nombreUsuarioChat);
                                conversacionChatViewHolder.setImagenUsuarioChat(dataSnapshot.child(Constantes.IMAGEN_USUARIO).getValue().toString(), ChatActivity.this);
                                conversacionChatViewHolder.setEstadoUsuario(conexionUsuario);
                                conversacionChatViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent conversacionIntent = new Intent(ChatActivity.this, ConversacionActivity.class);
                                        conversacionIntent.putExtra("identificadorUsuarioDestinatario", identificadorUsuarioDestinatario);
                                        conversacionIntent.putExtra("nombreUsuario", nombreUsuarioChat);
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

    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(firebaseAutenticacion, referenciaBDUsuarios);
    }

    public static class ConversacionChatViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConversacionChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNombreUsuarioChat(String nombreUsuarioChat) {
            TextView nombreUsuarioTextView = (TextView) mView.findViewById(R.id.nombreUsuarioChat);
            nombreUsuarioTextView.setText(nombreUsuarioChat);
        }

        public void setUltimoMensajeChat(String ultimoMensajeChat, boolean isVisto) {
            TextView ultimoMensajeTextView = (TextView) mView.findViewById(R.id.ultimoMensajeChat);
            ultimoMensajeTextView.setText(ultimoMensajeChat);

            if (!isVisto) {
                ultimoMensajeTextView.setTypeface(ultimoMensajeTextView.getTypeface(), Typeface.BOLD);
            } else {
                ultimoMensajeTextView.setTypeface(ultimoMensajeTextView.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setImagenUsuarioChat(final String imagen, final Context contexto) {
            final ImageView imagenUsuarioChat = (ImageView) mView.findViewById(R.id.imagenUsuarioChat);
            Utilidades.establecerImagenUsuario(mView.getContext(), imagen, imagenUsuarioChat, false);
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

    @Override
    public void onPause() {
        super.onPause();
        Log.v("ELENA: onStop", "onPause");
        AccionesFirebase.establecerUsuarioOffline(firebaseAutenticacion, referenciaBDUsuarios);
    }
}
