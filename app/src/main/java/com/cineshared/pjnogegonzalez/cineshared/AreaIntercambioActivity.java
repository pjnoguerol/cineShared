package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AreaIntercambioActivity extends AppCompatActivity {
    private ImageView imagenPelicula;
    private TextView nombrePelicula, sinopsisPelicula;
    private Spinner spinner;

    private ConversionJson<Peliculas> conversionJson = new ConversionJson<>(this, Constantes.BIBLIOTECA);


    private void loadFrameBiblioteca(int id)
    {

    }

    private Fragment establecerFragmeto(Usuarios usuarios) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(Constantes.USUARIOS, usuario);
        //bundle.putInt("historico", historico);
        //bundle.putInt("intercambio", modo);
        if (usuarios!=null)
            bundle.putSerializable(Constantes.USUARIO, usuarios);
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
        setContentView(R.layout.activity_area_intercambio);
        imagenPelicula = (ImageView) findViewById(R.id.imagenPeliculaActivity);
        nombrePelicula = (TextView) findViewById(R.id.nombrePeliculaActivity);
        sinopsisPelicula = (TextView) findViewById(R.id.sinopsis);
        spinner = (Spinner) findViewById(R.id.spIntercambio);

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
        peliculaTabs = tabs.newTabSpec("tabEstado");
        peliculaTabs.setContent(R.id.tabEstado);
        peliculaTabs.setIndicator("Resumen",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabs.addTab(peliculaTabs);
        // Se establece como pestaña inicial la de datos


        final Peliculas pelicula = (Peliculas) getIntent().getSerializableExtra(Constantes.PELICULAS);
        //Ocultamos la pelicula si no esta para intercambiar
        if (pelicula.getAlert()==0)
        {
            tabs.getTabWidget().getChildAt(0).setVisibility(View.GONE);
            tabs.getTabWidget().getChildAt(2).setVisibility(View.GONE);
            tabs.setCurrentTab(1);

        }
        else
        {
            if (pelicula.getEstado()!=0)
            {
                tabs.getTabWidget().getChildAt(0).setVisibility(View.GONE);
                tabs.setCurrentTab(2);
            }
            else {
                tabs.getTabWidget().getChildAt(2).setVisibility(View.GONE);
                tabs.setCurrentTab(0);
            }
        }
        Utility.auxPelicula = pelicula.getTitle();
        //Creamos el objetos USUARIOS
        final Usuarios usuario = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIO);

        Picasso.with(this).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(imagenPelicula);
        nombrePelicula.setText(pelicula.getTitle());
        sinopsisPelicula.setText(pelicula.getOverview());

        ArrayAdapter<Usuarios> adapter =
                new ArrayAdapter<Usuarios>(getApplicationContext(), android.R.layout.simple_spinner_item, pelicula.getUsuariointercambio());
        //adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.w("que es hisotico",pelicula.getUsuariointercambio().get(position).getHisusua()+"" );
                Fragment fragment = establecerFragmeto(pelicula.getUsuariointercambio().get(position));
                generarFragmento(fragment);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

}
