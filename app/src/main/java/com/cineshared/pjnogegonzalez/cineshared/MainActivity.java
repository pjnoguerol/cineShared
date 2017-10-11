package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "Preferencias";
    private TextView usuarioLogin;
    private TextView usuarioEmail;
    private Fragment fragment;
    private ConversionJson<Usuarios> conversionJson = new ConversionJson<>(this, Constantes.USUARIOS);
    Usuarios usuario = new Usuarios();

    /**
     * Metdo que preguntaremos al usuario si quiere desloguearse
     */
    private void cerrarSession()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("¿Quieres cerrar la sesión?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences preferencia =  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                preferencia.edit().clear().commit();
                cargarDatos();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void desLoguearse()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains("iniciado"))
        {
            if (settings.getBoolean("iniciado", false))
            {
                cerrarSession();
            }
        }
        else
        {


                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);


        }
    }

    /**
     * Cargamos  los datos para que se Loguee el usuario
     */
    private void cargarDatos()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.contains("iniciado"))
        {
          if (settings.getBoolean("iniciado", false))
          {
              try {
                  ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                  NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                  if (networkInfo != null && networkInfo.isConnected()) {
                      new MainActivity.LoginJsonTask().execute(new URL(Constantes.RUTA_USUARIO_DATOS+ settings.getString("usuariologin","")));
                  } else {
                      Toast.makeText(MainActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
                  }
              } catch (MalformedURLException e) {
                  e.printStackTrace();
              }

              //usuarioLogin.setText("Cargado");
              //usuarioEmail.setText("Pulsa aqui desloguearte");
          }
        }
        else
        {

            if (usuarioLogin!=null && usuarioEmail!=null)
            {
                usuarioLogin.setText("Anónimo");
                usuarioEmail.setText("Pulsa aqui para loguearte o registrarte");
            }

        }
        //String usuario = settings.getString("usuariologin", "VACIO");
        //Toast.makeText(this, usuario, Toast.LENGTH_SHORT).show();


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        usuario = (Usuarios) getIntent().getSerializableExtra("usuarios");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        LinearLayout linUsua = (LinearLayout) header.findViewById(R.id.layoutUsuario);
        usuarioLogin = (TextView)header.findViewById(R.id.usuarioLogin);
        usuarioEmail = (TextView)header.findViewById(R.id.usuarioEmail);
        cargarDatos();
        linUsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desLoguearse();
                //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        boolean fragmentTransaction = false;
        fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_biblioteca) {
            // Handle the camera action
            establecerFragmeto();
            fragmentTransaction = true;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (fragmentTransaction) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }
        drawer.closeDrawers();
        return true;
    }
    /**
     * Método que establece un fragmento de tipo búsqueda o configuración dependiendo del parámetro
     *
     *
     */
    private void establecerFragmeto() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.USUARIOS, usuario);

        fragment = new BibliotecaFragment();

        fragment.setArguments(bundle);
    }
    /**
     * Inner class que parsea el usuario logueado
     */
    public class LoginJsonTask extends AsyncTask<URL, Void, Usuarios> {

        private Usuarios usuariox;

        /**
         * Método que llama al parseo del usuario logueado
         *
         * @param urls URLs a conectar
         * @return Usuario logueado
         */
        @Override
        protected Usuarios doInBackground(URL... urls) {
            return (usuariox = conversionJson.doInBackground(urls).get(0));


        }

        /**
         * Método que redirige al usuario a la actividad main o muestra error dependiendo del resultado
         * del login
         *
         * @param usuarioTask Usuario logueado
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Usuarios usuarioTask) {
            if (usuarioTask != null) {
                if (usuarioTask.isOk()) {
                    usuario = usuarioTask;
                    Toast.makeText(MainActivity.this, Constantes.BIENVENIDO + usuario.getUsuario(), Toast.LENGTH_SHORT).show();
                    usuarioLogin.setText(usuario.getUsuario());
                    usuarioEmail.setText(usuario.getEmail());


                } else {
                    Toast.makeText(MainActivity.this, usuarioTask.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
