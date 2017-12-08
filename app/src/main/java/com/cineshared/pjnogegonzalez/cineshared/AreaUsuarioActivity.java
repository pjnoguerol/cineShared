package com.cineshared.pjnogegonzalez.cineshared;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.chat.ConversacionActivity;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AreaUsuarioActivity extends AppCompatActivity {

    private ImageView imagenUsuario;
    private TextView sinopsisPelicula;
    private TextView nombreUsuario;
    private Button botonAceptar;
    private Button botonChat;
    private Usuarios usuario;
    private Peliculas pelicula;

    // Variables para poder determinar si el usuario está conectado a la aplicación o no
    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference referenciaBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_usuario);
        imagenUsuario = (ImageView) findViewById(R.id.imagenUsuario);
        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        sinopsisPelicula = (TextView) findViewById(R.id.sinopsis);
        botonAceptar = (Button) findViewById(R.id.acepIntercambioBtn);
        botonChat = (Button) findViewById(R.id.iniciarChatBtn);

        firebaseAutenticacion = FirebaseAuth.getInstance();
        referenciaBD = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);

        pelicula = (Peliculas) getIntent().getSerializableExtra(Constantes.PELICULAS);
        //Ocultamos la pelicula si no esta para intercambiar

        //Creamos el objetos USUARIOS
        usuario = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIO);
        Utilidades.establecerImagenUsuario(this, usuario.getImagen(), imagenUsuario, false);
        //Picasso.with(this).load(Constantes.RUTA_IMAGEN + usuario.getImagen()).transform(new TransformacionCirculo()).fit().centerCrop().rotate(270f).into(imagenUsuario);
        String mensaje = "Desear compartir tu pelicula " + Utilidades.auxPelicula + " con la pelicula " + pelicula.getTitle() + " del usuario:  " + usuario.getUsuario() + "?";
        sinopsisPelicula.setText(mensaje);
        nombreUsuario.setText(usuario.getUsuario());
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizar();
            }
        });
        final String identificadorUsuarioDestinatario;
        botonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenciaBD.orderByChild(Constantes.NOMBRE_USUARIO).equalTo(usuario.getUsuario())
                        .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(dataSnapshot != null) {
                            Intent conversacionIntent = new Intent(AreaUsuarioActivity.this, ConversacionActivity.class);
                            conversacionIntent.putExtra("identificadorUsuarioDestinatario", dataSnapshot.getKey().toString());
                            conversacionIntent.putExtra("nombreUsuario", usuario.getUsuario());
                            startActivity(conversacionIntent);
                        }
                        else {
                            Toast.makeText(AreaUsuarioActivity.this, "Se ha producido un error al inicar Chat con esta persona", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private void finalizar() {
        String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
        String url2 = Constantes.RUTA_ACTUALIZAR_INTERCAMBIO + usuario.getHisusua() + "&usuarioin=" + usuario.getId_usua() + "&peliculain=" + pelicula.getId();
        Log.w("INTERCAMBIO FINAL", url2);
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("actualizarintercambio", usuario.getHisusua() + "")
                .appendQueryParameter("usuarioin", usuario.getId_usua() + "")
                .appendQueryParameter("peliculain", pelicula.getId() + "");
        HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
        hilo.setActivity(AreaUsuarioActivity.this);
        hilo.setTipoObjeto(Constantes.USUARIOS);
        hilo.setConversionJson(new ConversionJson<Usuarios>(AreaUsuarioActivity.this, Constantes.USUARIOS));
        List<Usuarios> resultado = null;
        try {
            resultado = hilo.execute(new URL(url)).get();
            if (resultado.get(0).isOk()) {
                Toast.makeText(AreaUsuarioActivity.this, "Pelicula Intercambiada correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AreaUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
            } else
                Toast.makeText(AreaUsuarioActivity.this, resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(AreaUsuarioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(AreaUsuarioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            Toast.makeText(AreaUsuarioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(firebaseAutenticacion, referenciaBD);
    }

    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(firebaseAutenticacion, referenciaBD);
    }

}
