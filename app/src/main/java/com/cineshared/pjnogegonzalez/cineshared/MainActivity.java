package com.cineshared.pjnogegonzalez.cineshared;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "Preferencias";
    private TextView usuarioLogin;
    private TextView usuarioEmail;
    private Fragment fragment;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ConversionJson<Usuarios> conversionJson = new ConversionJson<>(this, Constantes.USUARIOS);
    private ConversionJson<Resultado> conversionJson2 = new ConversionJson<>(Constantes.RESULTADO);
    private ConversionJson<Peliculas> conversionJson3 = new ConversionJson<>(Constantes.BUSQUEDA_NATURAL);
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

    /**
     *
     */
    private void cargarCoordenadas()
    {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 1355);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationManager = (LocationManager)
                this.getSystemService(this.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        String loca=null;
        //if (gps_enabled)
        // loca = LocationManager.GPS_PROVIDER;
        //else if (network_enabled)
        //  loca = LocationManager.NETWORK_PROVIDER;
        loca = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(loca, 300000, 0, locationListener);

    }

    private class MyLocationListener implements LocationListener {

        Context con;
        public String longitude;
        public String latitude;
        public MyLocationListener(Context context)
        {
            con = context;
        }
        @Override
        public void onLocationChanged(Location loc) {

            //pb.setVisibility(View.INVISIBLE);

            longitude = ""+loc.getLongitude();
            //Log.v(TAG, longitude);
            latitude = ""+loc.getLatitude();
            //Log.v(TAG, latitude);
            //salida.setText(longitude+ ""+  latitude);

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(con, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    //System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            try {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    String url = Constantes.RUTA_INSERTAR_COORDENADAS+longitude+"&latitud="+latitude+"&usuario="+usuario.getId_usua();
                    String url2 = Constantes.RUTA_PELICULAS_COORDENADAS+usuario.getId_usua()+"&longitud="+longitude+"&latitud="+latitude+"&distancia=10000000";

                    new MainActivity.BusquedaJsonTask().execute(new URL(Constantes.RUTA_INSERTAR_COORDENADAS+longitude+"&latitud="+latitude+"&usuario="+usuario.getId_usua()));
                    establecerFragmeto(Constantes.PELICULAS, url2);
                    cargarFragmmento();

                } else {
                    Toast.makeText(MainActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        //Aqui ponemos el boton para añadir peliculas
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), BuscarPeliculasActivity.class);
                intent.putExtra(Constantes.USUARIOS, usuario);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
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
    protected void onDestroy() {
        super.onDestroy();
        Log.w("destruyendo", "destruyendo");
        if (locationListener!=null)
            locationManager.removeUpdates(locationListener);

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
            establecerFragmeto(Constantes.USUARIOS, "");
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
    private void establecerFragmeto(String tipo, String variable) {
        Bundle bundle = new Bundle();
        if (Constantes.USUARIOS.equals(tipo))
        {
            bundle.putSerializable(Constantes.USUARIOS, usuario);

            fragment = new BibliotecaFragment();
        }
        else if (Constantes.PELICULAS.equals(tipo))
        {
            bundle.putString("web", variable);
            bundle.putSerializable(Constantes.USUARIOS, usuario);
            fragment = new PeliculasCoorFragment();
        }


        fragment.setArguments(bundle);
    }

    private void cargarFragmmento()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
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
                    if (usuario!=null)
                        cargarCoordenadas();


                } else {
                    Toast.makeText(MainActivity.this, usuarioTask.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class BusquedaJsonTask extends AsyncTask<URL, Void, Resultado > {

        private Resultado resultado;
        //private Context context;
        private String pelicula;

        /*
        public BusquedaJsonTask(Context context, String pelicula)
        {
            this.context = context;
            this.pelicula = pelicula;
        }
        */

        /**
         * Método que llama al parseo de biblioteca para obtener la lista a mostrar
         *
         * @return Biblioteca
         */
        @Override
        protected Resultado doInBackground(URL... urls) {

            resultado = conversionJson2.doInBackground(urls).get(0);

            return (resultado);
        }

        /**
         * Método que asigna la lista de películas al adaptador para obtener un cardView
         *
         * @param resultado Lista de películas para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Resultado resultado) {
            if (resultado!=null)
            {
                if(resultado.isOk())
                {
                    //Toast.makeText(context, "Pelicula "+pelicula+" introducida correctamente" , Toast.LENGTH_SHORT).show();
                    Log.w("resultado ok", "EL RESULTADO ES OK");


                }
                else
                {
                    //Toast.makeText(context, "Error "+resultado.getError() , Toast.LENGTH_SHORT).show();
                    Log.w("resultado ok", "EL RESULTADO NO OK");

                }
            }
            else
            {
                //Toast.makeText(context, "Error nullable en la captura del resultado " , Toast.LENGTH_SHORT).show();
                Log.w("resultado ok", "EL RESULTADO ES NULLO");
            }


            //FindApiBusqueda busquedalista = conversionJson.onPostExecute(busqueda);
            //recyclerView.setAdapter(conversionJson.onPostExecute(lista));
        }
    }

}
