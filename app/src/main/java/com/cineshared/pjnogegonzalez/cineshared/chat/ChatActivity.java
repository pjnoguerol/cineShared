package com.cineshared.pjnogegonzalez.cineshared.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cineshared.pjnogegonzalez.cineshared.BuscarPeliculasActivity;
import com.cineshared.pjnogegonzalez.cineshared.CircleTransform;
import com.cineshared.pjnogegonzalez.cineshared.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.MainActivity;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                        R.layout.chat_elemento_conversaciones,
                        UsuarioChatViewHolder.class,
                        firebaseBaseDatos
                ) {
                    @Override
                    protected void populateViewHolder(UsuarioChatViewHolder usuarioChatViewHolder, final UsuarioChat users, int position) {

                        usuarioChatViewHolder.setNombreUsuarioChat(users.getNombreUsuario());
                        usuarioChatViewHolder.setUltimoMensajeChat(users.getEmailUsuario());
                        usuarioChatViewHolder.setImagenUsuarioChat(users.getImagenUsuario(), getApplicationContext());

                        final String identificadorUsuario = getRef(position).getKey();

                        usuarioChatViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent conversacionIntent = new Intent(ChatActivity.this, ConversacionActivity.class);
                                conversacionIntent.putExtra("identificadorUsuario", identificadorUsuario);
                                conversacionIntent.putExtra("nombreUsuario", users.getNombreUsuario());
                                startActivity(conversacionIntent);
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

        public void setImagenUsuarioChat(String imagen, Context contexto) {
            ImageView imagenUsuarioChat = (ImageView) mView.findViewById(R.id.imagenUsuarioChat);
            Picasso.with(contexto).load(Constantes.RUTA_IMAGEN + imagen).placeholder(R.drawable.ic_chat_img_defecto)
                    .transform(new CircleTransform()).fit().centerCrop().rotate(270f).into(imagenUsuarioChat);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
