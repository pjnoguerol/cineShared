package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.UtilidadesImagenes;
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

/**
 * Clase ChatActivity gestiona las acciones relacionadas con activity_chat.xml
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class ChatActivity extends AppCompatActivity {

    // Definimos las variables necesarias
    private Toolbar barraChat;
    private RecyclerView listaConversaciones;
    private final List<MensajeChat> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference referenciaBDUsuarios;
    private DatabaseReference referenciaBDChats;
    private DatabaseReference referenciaBDMensajes;
    private String identificadorUsuarioLogeado;

    /**
     * Método onCreate se ejecuta cuando se inicia el chat sin estar en segundo plano
     *
     * @param savedInstanceState Instancia guardada con los datos del activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Establecemos los valores de las variables
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

    /**
     * Método onStart se ejecuta cada vez que el chat se abre y se encarga de recopilar la información
     * sobre cada chat y mostrarla por pantalla
     */
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
                                conversacionChatViewHolder.setUltimoMensajeChat(textoMensaje, conversacionChat.isVistoMensaje());
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
                                conversacionChatViewHolder.vistaChat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Si el usuaro selecciona uno de los chats listados, se le redirige a la actividad
                                        // ConversacionActivity
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

    /**
     * Método onResumen se ejecuta cada vez que la actividad se inicia
     */
    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(firebaseAutenticacion, referenciaBDUsuarios);
    }

    /**
     * Inner class que gestiona la infomación que muestra cada uno de los chats listados
     */
    public static class ConversacionChatViewHolder extends RecyclerView.ViewHolder {
        View vistaChat;

        // Constructor de la inner class
        public ConversacionChatViewHolder(View itemView) {
            super(itemView);
            vistaChat = itemView;
        }

        // Setter del nombre del usuario
        public void setNombreUsuarioChat(String nombreUsuarioChat) {
            TextView nombreUsuarioTextView = (TextView) vistaChat.findViewById(R.id.nombreUsuarioChat);
            nombreUsuarioTextView.setText(nombreUsuarioChat);
        }

        // Setter del último mensaje visto. Si el mensaje no se ha visto se mostrará en negrita, en caso contrario
        // se mostrará normal
        public void setUltimoMensajeChat(String ultimoMensajeChat, boolean isVisto) {
            TextView ultimoMensajeTextView = (TextView) vistaChat.findViewById(R.id.ultimoMensajeChat);
            ultimoMensajeTextView.setText(ultimoMensajeChat);
            if (!isVisto) {
                ultimoMensajeTextView.setTypeface(ultimoMensajeTextView.getTypeface(), Typeface.BOLD);
            } else {
                ultimoMensajeTextView.setTypeface(ultimoMensajeTextView.getTypeface(), Typeface.NORMAL);
            }
        }

        // Setter de la imagen de cada usuario o de la de por defecto si no tiene una
        public void setImagenUsuarioChat(final String imagen, final Context contexto) {
            final ImageView imagenUsuarioChat = (ImageView) vistaChat.findViewById(R.id.imagenUsuarioChat);
            UtilidadesImagenes.establecerImagenUsuario(vistaChat.getContext(), imagen, imagenUsuarioChat, false);
        }

        // Setter que nos permite conocer si el usuario se encuentra conectado a la aplicación
        public void setEstadoUsuario(String conexionUsuario) {
            ImageView estadoConexionUsuario = (ImageView) vistaChat.findViewById(R.id.estadoConexionUsuario);
            if ("true".equals(conexionUsuario)) {
                estadoConexionUsuario.setImageResource(R.drawable.ic_usuario_online);
            } else {
                estadoConexionUsuario.setImageResource(R.drawable.ic_usuario_offline);
            }
        }
    }

    /**
     * Método onPause gestiona las acciones cuando se pausa la aplicación
     */
    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(firebaseAutenticacion, referenciaBDUsuarios);
    }
}
