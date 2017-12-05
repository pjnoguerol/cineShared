package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AreaIntercambioActivity extends AppCompatActivity {
    private ImageView imagenPelicula;
    private TextView nombrePelicula, sinopsisPelicula, testado;
    private Spinner spinner;
    private Peliculas datosREsumen;
    private Button botonLiberar;

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
    private void liberarIntercambio()
    {
        try {
            String url = Constantes.SERVIDOR+Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("libhis", datosREsumen.getHistorico()+"")
                        .appendQueryParameter("libpelipropia", datosREsumen.getId()+"")
                        .appendQueryParameter("libpeliusuario", datosREsumen.getPeliusuario().toString());

                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(this);
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(this,Constantes.USUARIOS));
                List <Usuarios>  resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha insertado correctament
                if (resultado.get(0).isOk())
                    Toast.makeText(this, "pelicula liberada correctamente", Toast.LENGTH_SHORT).show();
                    //new HiloGenerico<Resultado>(builder).execute(new URL(url));
                    //new InsertUsuarioResultadoJsonTask().execute(new URL(url));
                else
                    Toast.makeText(this, resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
            } else {

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void cargarResumen(int id_pel, int id_usu)
    {

        try {
            String url = Constantes.SERVIDOR+Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("peliresumen", id_pel+"")
                        .appendQueryParameter("usuaresumen", id_usu+"" );
                Log.w("builder", builder.toString());
                HiloGenerico<PeliculasComprobacion> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(this);
                hilo.setTipoObjeto(Constantes.PELICULAS_CHECK);
                hilo.setConversionJson(new ConversionJson<PeliculasComprobacion>(this,Constantes.PELICULAS_CHECK));
                List <PeliculasComprobacion>  resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha insertado correctament
                if (resultado.get(0).isOk())
                {
                     datosREsumen = resultado.get(0).getPeliculas().get(0);
                     String mensaje = "Pelicula "+Utility.auxPelicula +" intercambiada con "+resultado.get(0).getPeliculas().get(0).getTitle()+" del usuario:  "+Utility.capitalizar(resultado.get(0).getPeliculas().get(0).getUsuarionombre())+"";

                     testado.setText(mensaje);
                }
                //new HiloGenerico<Resultado>(builder).execute(new URL(url));
                //new InsertUsuarioResultadoJsonTask().execute(new URL(url));
                else
                    Toast.makeText(this, resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
            } else {

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_intercambio);
        imagenPelicula = (ImageView) findViewById(R.id.imagenPeliculaActivity);
        nombrePelicula = (TextView) findViewById(R.id.nombrePeliculaActivity);
        testado =(TextView)findViewById(R.id.testado);
        botonLiberar = (Button)findViewById(R.id.btstado);
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
        final Usuarios usuario = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIO);
        Utility.auxPelicula = pelicula.getTitle();
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
                cargarResumen(pelicula.getId(), usuario.getId_usua());
                botonLiberar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        liberarIntercambio();
                        //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        //startActivity(intent);
                    }
                });

            }
            else {
                tabs.getTabWidget().getChildAt(2).setVisibility(View.GONE);
                tabs.setCurrentTab(0);
            }
        }

        //Creamos el objetos USUARIOS


        Picasso.with(this).load(
                Constantes.IMAGENES+pelicula.getPoster_path()).into(imagenPelicula);
        nombrePelicula.setText(pelicula.getTitle());
        sinopsisPelicula.setText(pelicula.getOverview());

        ArrayAdapter<Usuarios> adapter =
                new ArrayAdapter<Usuarios>(getApplicationContext(), android.R.layout.simple_spinner_item, pelicula.getUsuariointercambio());
        //adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
       // String mensaje = "Pelicula "+Utility.auxPelicula +" intercambiada con "+pelicula.getPeliusuario()+" del usuario:  "+usuario.getUsuario()+"?";

        //testado.setText(mensaje);

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
