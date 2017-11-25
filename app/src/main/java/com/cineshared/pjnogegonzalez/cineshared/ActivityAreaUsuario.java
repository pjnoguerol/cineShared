package com.cineshared.pjnogegonzalez.cineshared;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ActivityAreaUsuario extends AppCompatActivity {
    private ImageView imagenUsuario;
    private TextView nombrePelicula, sinopsisPelicula;
    private TextView nombreUsuario;


    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(this, Constantes.BIBLIOTECA);


    private void loadFrameBiblioteca(int id)
    {

    }

    private Fragment establecerFragmeto(int modo, int historico) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(Constantes.USUARIOS, usuario);
        bundle.putInt("historico", historico);
        bundle.putInt("intercambio", modo);
        fragment = new FragmentIntercambioBiblioteca();

        fragment.setArguments(bundle);


        return fragment;
    }
    private void generarFragmento (Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_intercambio, fragment).commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_usuario);
        imagenUsuario = (ImageView) findViewById(R.id.imagenUsuario);
        nombreUsuario = (TextView) findViewById(R.id.nombreUsuario);
        sinopsisPelicula = (TextView) findViewById(R.id.sinopsis);

        // Creamos las pestañas
        TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec peliculaTabs = tabs.newTabSpec("tabDatos");
        peliculaTabs.setContent(R.id.tabDatos);
        peliculaTabs.setIndicator("Intercambio",
                ContextCompat.getDrawable(this, android.R.drawable.ic_btn_speak_now));
        tabs.addTab(peliculaTabs);
        peliculaTabs = tabs.newTabSpec("tabSinopsis");
        peliculaTabs.setContent(R.id.tabSinopsis);
        peliculaTabs.setIndicator("Sinopsis",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabs.addTab(peliculaTabs);
       // peliculaTabs = tabs.newTabSpec("tabCriticas");
        ////peliculaTabs.setContent(R.id.tabCriticas);
       // peliculaTabs.setIndicator("Criticas",
                //ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        //tabs.addTab(peliculaTabs);
        // Se establece como pestaña inicial la de datos
        tabs.setCurrentTab(1);

        final Peliculas pelicula = (Peliculas) getIntent().getSerializableExtra(Constantes.PELICULAS);
        //Ocultamos la pelicula si no esta para intercambiar
        if (pelicula.getAlert()==0)
        {
            tabs.getTabWidget().getChildAt(0).setVisibility(View.GONE);
        }
        //Creamos el objetos USUARIOS
        final Usuarios usuario = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIO);

        Picasso.with(this).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(imagenUsuario);
//        nombreUsuario.setText(usuario.getId_usua());
        //sinopsisPelicula.setText(pelicula.getOverview());






    }

}
