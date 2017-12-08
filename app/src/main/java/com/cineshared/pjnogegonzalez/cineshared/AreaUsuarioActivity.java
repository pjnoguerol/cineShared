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

import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AreaUsuarioActivity extends AppCompatActivity {

    private ImageView imagenUsuario;
    private TextView sinopsisPelicula;
    private TextView nombreUsuario;
    private Button botonAceptar;
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
        botonAceptar = (Button) findViewById(R.id.acepIntercambio);

        firebaseAutenticacion = FirebaseAuth.getInstance();
        referenciaBD = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);

        // Creamos las pestañas
        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec peliculaTabs = tabs.newTabSpec("tabDatos");
        peliculaTabs.setContent(R.id.tabDatos);
        peliculaTabs.setIndicator("Intercambio",
                ContextCompat.getDrawable(this, android.R.drawable.ic_btn_speak_now));
        tabs.addTab(peliculaTabs);
        peliculaTabs = tabs.newTabSpec("Resumen");
        peliculaTabs.setContent(R.id.tabSinopsis);
        peliculaTabs.setIndicator("Resumen",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabs.addTab(peliculaTabs);
        // Se establece como pestaña inicial la de datos
        tabs.setCurrentTab(1);

        pelicula = (Peliculas) getIntent().getSerializableExtra(Constantes.PELICULAS);
        //Ocultamos la pelicula si no esta para intercambiar
        tabs.getTabWidget().getChildAt(0).setVisibility(View.GONE);

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
