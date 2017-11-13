package com.cineshared.pjnogegonzalez.cineshared;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AreaIntercambioActivity extends AppCompatActivity {
    private ImageView imagenPelicula;
    private TextView nombrePelicula, sinopsisPelicula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_intercambio);
        imagenPelicula = (ImageView) findViewById(R.id.imagenPeliculaActivity);
        nombrePelicula = (TextView) findViewById(R.id.nombrePeliculaActivity);
        sinopsisPelicula = (TextView) findViewById(R.id.sinopsis);
        // Creamos las pestañas
        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec peliculaTabs = tabs.newTabSpec("tabDatos");
        peliculaTabs.setContent(R.id.tabDatos);
        peliculaTabs.setIndicator("Datos",
                ContextCompat.getDrawable(this, android.R.drawable.ic_btn_speak_now));
        tabs.addTab(peliculaTabs);
        peliculaTabs = tabs.newTabSpec("tabSinopsis");
        peliculaTabs.setContent(R.id.tabSinopsis);
        peliculaTabs.setIndicator("Sinopsis",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabs.addTab(peliculaTabs);
        peliculaTabs = tabs.newTabSpec("tabCriticas");
        peliculaTabs.setContent(R.id.tabCriticas);
        peliculaTabs.setIndicator("Criticas",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabs.addTab(peliculaTabs);
        // Se establece como pestaña inicial la de datos
        tabs.setCurrentTab(0);

        final Peliculas pelicula = (Peliculas) getIntent().getSerializableExtra(Constantes.PELICULAS);
        final Usuarios usuario = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIO);

        Picasso.with(this).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(imagenPelicula);
        nombrePelicula.setText(pelicula.getTitle());
        sinopsisPelicula.setText(pelicula.getOverview());


    }
}
