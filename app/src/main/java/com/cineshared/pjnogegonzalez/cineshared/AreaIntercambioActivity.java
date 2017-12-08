package com.cineshared.pjnogegonzalez.cineshared;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AreaIntercambioActivity extends AppCompatActivity {
    private ImageView imagenPelicula;
    private TextView nombrePelicula, sinopsisPelicula, textEstado;
    private Spinner spinner;
    private Peliculas datosResumen;
    private Button botonLiberar;

    private FirebaseAuth autenticacionFirebase;
    private DatabaseReference referenciaBD;

    private Fragment establecerFragmeto(Usuarios usuarios) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        if (usuarios != null)
            bundle.putSerializable(Constantes.USUARIO, usuarios);
        fragment = new FragmentIntercambioBiblioteca();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void generarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAreaIntercambio, fragment).commit();

    }

    private void liberarIntercambio() {
        try {
            String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("libhis", datosResumen.getHistorico() + "")
                        .appendQueryParameter("libpelipropia", datosResumen.getId() + "")
                        .appendQueryParameter("libpeliusuario", datosResumen.getPeliusuario().toString());

                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(this);
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(this, Constantes.USUARIOS));
                List<Usuarios> resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha insertado correctament
                if (resultado.get(0).isOk())
                    Toast.makeText(this, "pelicula liberada correctamente", Toast.LENGTH_SHORT).show();
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

    private void cargarResumen(int id_pel, int id_usu) {

        try {
            String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("peliresumen", id_pel + "")
                        .appendQueryParameter("usuaresumen", id_usu + "");
                Log.w("builder", builder.toString());
                HiloGenerico<PeliculasComprobacion> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(this);
                hilo.setTipoObjeto(Constantes.PELICULAS_CHECK);
                hilo.setConversionJson(new ConversionJson<PeliculasComprobacion>(this, Constantes.PELICULAS_CHECK));
                List<PeliculasComprobacion> resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha insertado correctament
                if (resultado.get(0).isOk()) {
                    datosResumen = resultado.get(0).getPeliculas().get(0);
                    String mensaje = "Película " + Utilidades.auxPelicula + " intercambiada con "
                            + resultado.get(0).getPeliculas().get(0).getTitle() + " del usuario:  "
                            + Utilidades.capitalizarCadena(resultado.get(0).getPeliculas().get(0).getUsuarionombre())
                            + "";

                    textEstado.setText(mensaje);
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
        // Inicializamos los objetos del activity
        setContentView(R.layout.activity_area_intercambio);
        imagenPelicula = (ImageView) findViewById(R.id.imagenAreaIntercambio);
        nombrePelicula = (TextView) findViewById(R.id.nombreAreaIntercambio);
        textEstado = (TextView) findViewById(R.id.textEstadoIntercambio);
        botonLiberar = (Button) findViewById(R.id.estadoIntercambioBtn);
        sinopsisPelicula = (TextView) findViewById(R.id.sinopsisIntercambio);
        spinner = (Spinner) findViewById(R.id.spinnerIntercambio);

        referenciaBD = FirebaseDatabase.getInstance().getReference();
        autenticacionFirebase = FirebaseAuth.getInstance();

        // Creamos las pestañas y se establece como pestaña inicial la de datos
        TabHost tabsIntercambio = (TabHost) findViewById(R.id.tabHostIntercambio);
        tabsIntercambio.setup();

        TabHost.TabSpec peliculaTabs = tabsIntercambio.newTabSpec("tabDatos");
        peliculaTabs.setContent(R.id.tabDatosIntercambio);
        peliculaTabs.setIndicator("Intercambio",
                ContextCompat.getDrawable(this, android.R.drawable.ic_btn_speak_now));
        tabsIntercambio.addTab(peliculaTabs);
        peliculaTabs = tabsIntercambio.newTabSpec("tabSinopsis");
        peliculaTabs.setContent(R.id.tabSinopsisIntercambio);
        peliculaTabs.setIndicator("Sinopsis",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabsIntercambio.addTab(peliculaTabs);
        peliculaTabs = tabsIntercambio.newTabSpec("tabEstado");
        peliculaTabs.setContent(R.id.tabEstadoIntercambio);
        peliculaTabs.setIndicator("Resumen",
                ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));
        tabsIntercambio.addTab(peliculaTabs);

        final Peliculas pelicula = (Peliculas) getIntent().getSerializableExtra(Constantes.PELICULAS);
        final Usuarios usuario = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIO);
        Utilidades.auxPelicula = pelicula.getTitle();
        //Ocultamos la pelicula si no esta para intercambiar
        if (pelicula.getAlert() == 0) {
            tabsIntercambio.getTabWidget().getChildAt(0).setVisibility(View.GONE);
            tabsIntercambio.getTabWidget().getChildAt(2).setVisibility(View.GONE);
            tabsIntercambio.setCurrentTab(1);

        } else {
            if (pelicula.getEstado() != 0) {
                tabsIntercambio.getTabWidget().getChildAt(0).setVisibility(View.GONE);
                tabsIntercambio.setCurrentTab(2);
                cargarResumen(pelicula.getId(), usuario.getId_usua());
                botonLiberar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        liberarIntercambio();
                    }
                });

            } else {
                tabsIntercambio.getTabWidget().getChildAt(2).setVisibility(View.GONE);
                tabsIntercambio.setCurrentTab(0);
            }
        }

        //Creamos el objetos USUARIOS


        Picasso.with(this).load(
                Constantes.IMAGENES + pelicula.getPoster_path()).into(imagenPelicula);
        nombrePelicula.setText(pelicula.getTitle());
        sinopsisPelicula.setText(pelicula.getOverview());

        ArrayAdapter<Usuarios> adapter =
                new ArrayAdapter<Usuarios>(getApplicationContext(), android.R.layout.simple_spinner_item, pelicula.getUsuariointercambio());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = establecerFragmeto(pelicula.getUsuariointercambio().get(position));
                generarFragmento(fragment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(autenticacionFirebase, referenciaBD);
    }

    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(autenticacionFirebase, referenciaBD);
    }
}
