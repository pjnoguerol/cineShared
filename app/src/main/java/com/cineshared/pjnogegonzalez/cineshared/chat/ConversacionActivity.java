package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversacionActivity extends AppCompatActivity {

    private Toolbar barraConversacion;
    private TextView nombreUsuarioBarra;
    private TextView conexionUsuarioBarra;
    private ImageView imagenUsuarioBarra;

    // Variables de firebase
    private DatabaseReference referenciaBD;
    private DatabaseReference referenciaBDNotificaciones;
    private FirebaseAuth autenticacionFirebase;
    private String identificadorUsuarioLogueado;
    private String identificadorUsuarioDestinatario;

    // Variables de envío de mensajes
    private ImageButton enviarMensajeBtn;
    private EditText textoMensaje;
    private RecyclerView listaMensajes;
    private LinearLayoutManager conversacionesLayout;
    private AdaptarMensajeChat adaptarMensajeChat;

    private final List<MensajeChat> listaMensajesChat = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);

        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        identificadorUsuarioDestinatario = getIntent().getStringExtra("identificadorUsuarioDestinatario");
        referenciaBD = FirebaseDatabase.getInstance().getReference();
        referenciaBDNotificaciones = FirebaseDatabase.getInstance().getReference().child(Constantes.NOTIFICACIONES_FIREBASE);
        autenticacionFirebase = FirebaseAuth.getInstance();
        identificadorUsuarioLogueado = autenticacionFirebase.getCurrentUser().getUid();

        barraConversacion = (Toolbar) findViewById(R.id.barraConversacion);
        setSupportActionBar(barraConversacion);
        ActionBar barraCustomChat = getSupportActionBar();
        barraCustomChat.setDisplayHomeAsUpEnabled(true);
        barraCustomChat.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.app_bar_chat, null);
        barraCustomChat.setCustomView(action_bar_view);

        nombreUsuarioBarra = (TextView) findViewById(R.id.nombreUsuarioBarra);
        conexionUsuarioBarra = (TextView) findViewById(R.id.estadoConexionBarra);
        imagenUsuarioBarra = (ImageView) findViewById(R.id.imagenUsuarioBarra);

        enviarMensajeBtn = (ImageButton) findViewById(R.id.enviarMensajeChatBtn);
        textoMensaje = (EditText) findViewById(R.id.mensajeChatTexto);
        listaMensajes = (RecyclerView) findViewById(R.id.listaMensajes);
        conversacionesLayout = new LinearLayoutManager(this);

        adaptarMensajeChat = new AdaptarMensajeChat(listaMensajesChat);

        listaMensajes.setHasFixedSize(true);
        listaMensajes.setLayoutManager(conversacionesLayout);
        listaMensajes.setAdapter(adaptarMensajeChat);

        referenciaBD.child(Constantes.CHAT_FIREBASE).child(identificadorUsuarioLogueado).child(identificadorUsuarioDestinatario)
                .child(Constantes.VISTO_MENSAJE).setValue(true);

        cargarMensajes();

        nombreUsuarioBarra.setText("Chat - " + nombreUsuario);
        referenciaBD.child(Constantes.USUARIOS_FIREBASE).child(identificadorUsuarioDestinatario)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String conexionUsuario = "false";
                if (dataSnapshot.hasChild(Constantes.CONEXION_USUARIO)) {
                    conexionUsuario = dataSnapshot.child(Constantes.CONEXION_USUARIO).getValue().toString();
                }

                String imagen = dataSnapshot.child(Constantes.IMAGEN_USUARIO).getValue().toString();
                Utilidades.establecerImagenUsuario(ConversacionActivity.this, imagen, imagenUsuarioBarra, true);

                if(conexionUsuario.equals("true")) {
                    conexionUsuarioBarra.setText("Conectado");
                } else {
                    conexionUsuarioBarra.setText("Desconectado");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        referenciaBD.child(Constantes.CHAT_FIREBASE).child(identificadorUsuarioLogueado)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Si no existe una conversación entre dos usuarios la creamos
                if(!dataSnapshot.hasChild(identificadorUsuarioDestinatario)){
                    Map chatNuevoMap = new HashMap();
                    chatNuevoMap.put("visto", false);
                    chatNuevoMap.put("horaMensaje", ServerValue.TIMESTAMP);

                    Map chatUsuariosMap = new HashMap();
                    chatUsuariosMap.put(Constantes.CHAT_FIREBASE + "/" + identificadorUsuarioLogueado + "/"
                            + identificadorUsuarioDestinatario, chatNuevoMap);
                    chatUsuariosMap.put(Constantes.CHAT_FIREBASE + "/" + identificadorUsuarioDestinatario + "/"
                            + identificadorUsuarioLogueado, chatNuevoMap);
                    referenciaBD.updateChildren(chatUsuariosMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.v("CineSharedConversacion", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        enviarMensajeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensajeChat();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(autenticacionFirebase, referenciaBD.child(Constantes.USUARIOS_FIREBASE));
    }

    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(autenticacionFirebase, referenciaBD.child(Constantes.USUARIOS_FIREBASE));
    }

    /**
     * Método enviarMensajeChat envía los mensajes y los almacena en firebase
     */
    private void enviarMensajeChat() {
        String mensajeEscrito = textoMensaje.getText().toString();

        if(!TextUtils.isEmpty(mensajeEscrito.trim())){
            // Definimos los nodos donde se crearán los mensajes en firebase y los datos que tendrán
            String referenciaMensajeRemitente = Constantes.MENSAJES_FIREBASE + "/" + identificadorUsuarioLogueado
                    + "/" + identificadorUsuarioDestinatario;
            String referenciaMensajeDestinatario = Constantes.MENSAJES_FIREBASE + "/" + identificadorUsuarioDestinatario + "/"
                    + identificadorUsuarioLogueado;
            DatabaseReference pushMensaje = referenciaBD.child(Constantes.MENSAJES_FIREBASE)
                    .child(identificadorUsuarioLogueado).child(identificadorUsuarioDestinatario).push();
            String identificadorPush = pushMensaje.getKey();

            Map mapaMensajeNuevo = new HashMap();
            mapaMensajeNuevo.put(Constantes.TEXTO_MENSAJE, mensajeEscrito);
            mapaMensajeNuevo.put(Constantes.HORA_MENSAJE, ServerValue.TIMESTAMP);
            mapaMensajeNuevo.put(Constantes.VISTO_MENSAJE, false);
            mapaMensajeNuevo.put(Constantes.REMITENTE_MENSAJE, identificadorUsuarioLogueado);

            Map mapaMensajeUsuarios = new HashMap();
            mapaMensajeUsuarios.put(referenciaMensajeRemitente + "/" + identificadorPush, mapaMensajeNuevo);
            mapaMensajeUsuarios.put(referenciaMensajeDestinatario + "/" + identificadorPush, mapaMensajeNuevo);
            // Una vez creados los mapas para crear en la base de datos de firebase, limpiamos el texto del chat
            textoMensaje.setText("");
            // Creamos los mensajes con sus propiedades en la base de datos de firebase
            referenciaBD.child(Constantes.CHAT_FIREBASE).child(identificadorUsuarioLogueado).child(identificadorUsuarioDestinatario)
                    .child(Constantes.VISTO_MENSAJE).setValue(true);
            referenciaBD.child(Constantes.CHAT_FIREBASE).child(identificadorUsuarioLogueado).child(identificadorUsuarioDestinatario)
                    .child(Constantes.HORA_MENSAJE).setValue(ServerValue.TIMESTAMP);

            referenciaBD.child(Constantes.CHAT_FIREBASE).child(identificadorUsuarioDestinatario).child(identificadorUsuarioLogueado)
                    .child(Constantes.VISTO_MENSAJE).setValue(false);
            referenciaBD.child(Constantes.CHAT_FIREBASE).child(identificadorUsuarioDestinatario).child(identificadorUsuarioLogueado)
                    .child(Constantes.HORA_MENSAJE).setValue(ServerValue.TIMESTAMP);

            referenciaBD.updateChildren(mapaMensajeUsuarios, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.v("CineSharedConversacion", databaseError.getMessage().toString());
                    }
                    else {
                        HashMap<String, String> hashMapNotificacion = new HashMap<>();
                        hashMapNotificacion.put("remitenteId", identificadorUsuarioLogueado);

                        referenciaBDNotificaciones.child(identificadorUsuarioDestinatario).push().setValue(hashMapNotificacion);
                    }
                }
            });
        }
    }

    /**
     * Método cargarMensajes carga los mensajes enviados entre los dos usuarios para mostrarlos por pantalla
     */
    private void cargarMensajes() {
        referenciaBD.child(Constantes.MENSAJES_FIREBASE)
                .child(identificadorUsuarioLogueado).child(identificadorUsuarioDestinatario)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String string) {
                        MensajeChat mensajeChat = dataSnapshot.getValue(MensajeChat.class);
                        listaMensajesChat.add(mensajeChat);
                        adaptarMensajeChat.notifyDataSetChanged();
                        listaMensajes.scrollToPosition(listaMensajesChat.size() - 1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String string) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String string) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
