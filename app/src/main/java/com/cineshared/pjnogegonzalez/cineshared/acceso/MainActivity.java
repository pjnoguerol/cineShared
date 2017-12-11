package com.cineshared.pjnogegonzalez.cineshared.acceso;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.biblioteca.BibliotecaFragment;
import com.cineshared.pjnogegonzalez.cineshared.busqueda.BuscarPeliculasActivity;
import com.cineshared.pjnogegonzalez.cineshared.chat.ChatActivity;
import com.cineshared.pjnogegonzalez.cineshared.configuracion.ConfiguracionFragment;
import com.cineshared.pjnogegonzalez.cineshared.historico.FragmentHistoricoIntercambio;
import com.cineshared.pjnogegonzalez.cineshared.peliculas.PeliculasCoorFragment;
import com.cineshared.pjnogegonzalez.cineshared.ubicacion.PosicionFragment;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Clase MainActivity gestiona las acciones principales de acceso a la aplicación
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Se definen las variables
    public static final String PREFS_NAME = "Preferencias";
    private Usuarios usuarioLogeado = new Usuarios();
    private TextView usuarioLogin;
    private TextView usuarioEmail;
    private ImageView usuarioImagen;
    private Fragment fragment;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference usuarioBD;

    // Iniciamos las variables con las que realizaremos las conversiones a Json necesarias
    private ConversionJson<Usuarios> conversionJson = new ConversionJson<>(Constantes.USUARIOS);

    // Variable auxiliar que nos permite regresar al fragmento correspondiente tras una búsqueda
    // de película
    private static int auxFragment = 0;

    /**
     * Método onCreate se ejecuta cuando se inicia la aplicación sin estar en segundo plano
     *
     * @param savedInstanceState Instancia guardada con los datos del activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("CineSharedMainActivity", "Un usuario ha inicializado la aplicación por primera vez");
        super.onCreate(savedInstanceState);

        // Inicializamos las variables del activity
        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Toolbar barraPrincipal = (Toolbar) findViewById(R.id.barraPrincipal);
        setSupportActionBar(barraPrincipal);
        DrawerLayout mainCineShared = (DrawerLayout) findViewById(R.id.mainCineShared);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mainCineShared, barraPrincipal, R.string.navegacion_abrir, R.string.navegacion_cerrar);
        mainCineShared.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Botón que permite al usuario buscar y añadir peliculas a su biblioteca
        FloatingActionButton buscarAnyadirPeliculaBtn = (FloatingActionButton) findViewById(R.id.buscarAnyadirPeliculaBtn);
        buscarAnyadirPeliculaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBuscarPeliculas = new Intent(view.getContext(), BuscarPeliculasActivity.class);
                intentBuscarPeliculas.putExtra(Constantes.USUARIOS, usuarioLogeado);
                startActivity(intentBuscarPeliculas);
            }
        });

        // Obtenemos los datos del usuario conectado
        usuarioLogeado = (Usuarios) getIntent().getSerializableExtra(Constantes.USUARIOS);
        // Información del usuario en cabecera
        View infoCabecera = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        usuarioLogin = (TextView) infoCabecera.findViewById(R.id.usuarioNombre);
        usuarioEmail = (TextView) infoCabecera.findViewById(R.id.usuarioEmail);
        usuarioImagen = (ImageView) infoCabecera.findViewById(R.id.imagenUsuario);

        LinearLayout layoutUsuario = (LinearLayout) infoCabecera.findViewById(R.id.layoutUsuario);
        layoutUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickarEnUsuarioInfo();
            }
        });

        // Cargamos los datos del usuario en caso de ser necesario
        cargarDatos();
    }

    /**
     * Método onResumen se ejecuta cada vez que la actividad se inicia
     */
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAutenticacion = FirebaseAuth.getInstance();
        usuarioBD = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);
        if (usuarioLogeado != null && usuarioLogeado.isOk() && usuarioBD != null) {
            AccionesFirebase.loginUserFirebase(firebaseAutenticacion, usuarioBD,
                    usuarioLogeado.getUsuario() + Constantes.EMAIL_FIREBASE, usuarioLogeado.getPassword(),
                    MainActivity.this);
        }
    }

    /**
     * Método clickarEnUsuarioInfo comprueba si el usuario está conectado para desloquear, o no lo
     * está y le redirige a la página de login.
     */
    private void clickarEnUsuarioInfo() {
        SharedPreferences preferenciasUsuario = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (preferenciasUsuario.contains(Constantes.INICIADO) && preferenciasUsuario.getBoolean(Constantes.INICIADO, false))
            cerrarSession();
        else {
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    /**
     * Método cargarDatos carga los datos del usuario logueado o muestra una información por defecto
     */
    private void cargarDatos() {
        SharedPreferences preferenciasUsuario = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (preferenciasUsuario.contains(Constantes.INICIADO) && preferenciasUsuario.getBoolean(Constantes.INICIADO, false)) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new MainActivity.MainLoginJsonTask()
                            .execute(new URL(Constantes.RUTA_USUARIO_DATOS +
                                    preferenciasUsuario.getString("usuariologin", "")));
                    mostrarNavegacion();
                } else {
                    Toast.makeText(MainActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            if (usuarioLogin != null && usuarioEmail != null && usuarioImagen != null) {
                usuarioLogin.setText("Anónimo");
                usuarioEmail.setText("Pulsa aqui para loguearte o registrarte");
                Picasso.with(MainActivity.this).load(R.drawable.ic_chat_img_defecto).into(usuarioImagen);
                usuarioImagen.setBackgroundResource(R.color.colorBlanco);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    fragment = null;
                }
                ocultarNavegacion();
            }
        }
    }

    /**
     * Método cargarCoordenadas carga las coordenadas del usuario y las actualiza cada 5 minutos
     */
    private void cargarCoordenadas() {
        // Se solicitan al usuario los permisos necesarios en caso de no tenerlos ya
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 1355);

        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        locationListener = new LocalizacionListener(MainActivity.this, usuarioLogeado.getId_usua());
        // Se confirma que se tienen los permisos y en caso contrario se sale de la aplicación
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Se pide que se actualice la localización cada 5 minutos
        Criteria criterioLocalizacion = new Criteria();
        criterioLocalizacion.setAccuracy(Criteria.ACCURACY_COARSE);
        String localizacionUsuario = locationManager.getBestProvider(criterioLocalizacion, true);
        locationManager.requestLocationUpdates(localizacionUsuario, 300000, 0, locationListener);
    }

    /**
     * Método onCreateOptionsMenu crea las distintas opciones de los menús de la aplicación
     *
     * @param menu Menú a mostrar
     * @return True si se quiere mostrar el menú y false en caso contraro
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuracion, menu);
        final MenuItem menuItem = menu.findItem(R.id.busqueda);
        final SearchView vistaBusqueda = (SearchView) menuItem.getActionView();
        // Establecemos la acción del botón de búsqueda
        vistaBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                if (auxFragment == 1) {
                    bundle.putSerializable(Constantes.USUARIOS, usuarioLogeado);
                    bundle.putString("cadena", query);
                    fragment = new BibliotecaFragment();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    getSupportActionBar().setTitle("Biblioteca");
                } else if (auxFragment == 2) {
                    establecerFragmeto(Constantes.PELICULAS);
                    bundle.putString("cadena", query);
                    bundle.putSerializable(Constantes.USUARIOS, usuarioLogeado);
                    fragment = new PeliculasCoorFragment();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    getSupportActionBar().setTitle("Intercambio");
                } else if (auxFragment == 3) {
                    establecerFragmeto(Constantes.HISTORICO);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                } else {
                    if (!vistaBusqueda.isIconified())
                        vistaBusqueda.setIconified(true);
                }
                menuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    /**
     * Método onOptionsItemSelected realiza la carga de la sección de configuración si el usuario clicka
     * en esa opción
     *
     * @param item Elemento del menú que se selecciona
     * @return Devuelve un booleano sobre la forma de consumir el menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idendificacionOpcion = item.getItemId();
        if (idendificacionOpcion == R.id.configuracion) {
            establecerFragmeto(Constantes.CONFIGURACION);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            getSupportActionBar().setTitle("Configuración");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método onNavigationItemSelected controla las acciones a realizar al seleccionar una opción del
     * menú de navegación
     *
     * @param item Elemento que se selecciona
     * @return True para que se muestre como seleccionado, false en caso contrario.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean fragmentTransaction = false;
        fragment = null;
        int identificadorOpcion = item.getItemId();
        if (identificadorOpcion == R.id.nav_biblioteca) {
            establecerFragmeto(Constantes.USUARIOS);
            auxFragment = 1;
            fragmentTransaction = true;
        } else if (identificadorOpcion == R.id.nav_trans) {
            establecerFragmeto(Constantes.PELICULAS);
            auxFragment = 2;
            fragmentTransaction = true;
        } else if (identificadorOpcion == R.id.nav_historico) {
            establecerFragmeto(Constantes.HISTORICO);
            auxFragment = 3;
            fragmentTransaction = true;
        } else if (identificadorOpcion == R.id.nav_localizacion) {
            establecerFragmeto(Constantes.LOCALIZACION);
            auxFragment = 4;
            fragmentTransaction = true;
        } else if (identificadorOpcion == R.id.nav_chat) {
            Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(chatIntent);
        }

        DrawerLayout seccionPrincipal = (DrawerLayout) findViewById(R.id.mainCineShared);
        if (fragmentTransaction) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }
        seccionPrincipal.closeDrawers();
        return true;
    }

    /**
     * Método cerrarSession pregunta al usuario si quiere desloguearse y, en caso afirmativo, realiza
     * la acción. También informaremos a FireBase que el usuario ya no se encuentra online.
     */
    private void cerrarSession() {
        AlertDialog.Builder alertaCerrarSesion = new AlertDialog.Builder(this);
        alertaCerrarSesion.setTitle(R.string.app_name);
        alertaCerrarSesion.setMessage("¿Quieres cerrar la sesión?");

        alertaCerrarSesion.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences preferenciasUsuario = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                preferenciasUsuario.edit().clear().commit();
                cargarDatos();
                AccionesFirebase.establecerUsuarioOffline(firebaseAutenticacion, usuarioBD);
                FirebaseAuth.getInstance().signOut();
            }
        });
        alertaCerrarSesion.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertaCerrarSesion.show();
    }

    /**
     * Método mostrarNavegacion habilita los menús de navegación
     */
    private void mostrarNavegacion() {
        Menu menuNavegacion = navigationView.getMenu();
        menuNavegacion.findItem(R.id.nav_biblioteca).setVisible(true);
        menuNavegacion.findItem(R.id.nav_trans).setVisible(true);
        menuNavegacion.findItem(R.id.nav_historico).setVisible(true);
        menuNavegacion.findItem(R.id.nav_chat).setVisible(true);
        menuNavegacion.findItem(R.id.nav_localizacion).setVisible(true);
    }

    /**
     * Método ocultarNavegacion deshabilita los menús de navegación
     */
    private void ocultarNavegacion() {
        Menu menuNavegacion = navigationView.getMenu();
        menuNavegacion.findItem(R.id.nav_biblioteca).setVisible(false);
        menuNavegacion.findItem(R.id.nav_trans).setVisible(false);
        menuNavegacion.findItem(R.id.nav_historico).setVisible(false);
        menuNavegacion.findItem(R.id.nav_chat).setVisible(false);
        menuNavegacion.findItem(R.id.nav_localizacion).setVisible(false);
    }

    /**
     * Método onBackPressed controla las acciones a realizar al hacer click en el botón "back"
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mainCineShared);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Método onPause gestiona las acciones cuando se pausa la aplicación
     */
    @Override
    protected void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(firebaseAutenticacion, usuarioBD);
    }

    /**
     * Método onDestroy gestiona las acciones cuando se cierra la aplicación
     */
    @Override
    protected void onDestroy() {
        if (locationListener != null)
            locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    /**
     * Método establecerFragmeto establece un fragmento u otro dependiendo del parámetro
     */
    private void establecerFragmeto(String tipo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.USUARIOS, usuarioLogeado);
        if (Constantes.USUARIOS.equals(tipo)) {
            fragment = new BibliotecaFragment();
        } else if (Constantes.PELICULAS.equals(tipo)) {
            fragment = new PeliculasCoorFragment();
        } else if (Constantes.HISTORICO.equals((tipo))) {
            fragment = new FragmentHistoricoIntercambio();
        } else if (Constantes.CONFIGURACION.equals(tipo)) {
            fragment = new ConfiguracionFragment();
        } else if (Constantes.LOCALIZACION.equals(tipo)) {
            fragment = new PosicionFragment();
        }

        fragment.setArguments(bundle);
    }

    /**
     * Inner class que parsea el usuario logueado
     */
    public class MainLoginJsonTask extends AsyncTask<URL, Void, Usuarios> {

        private Usuarios usuario;

        /**
         * Método que llama al parseo del usuario logueado
         *
         * @param urls URLs a conectar
         * @return Usuario logueado
         */
        @Override
        protected Usuarios doInBackground(URL... urls) {
            return (usuario = conversionJson.doInBackground(urls).get(0));
        }

        /**
         * Método que carga los datos del usuario logueado en la sección correspondiente
         *
         * @param usuarioTask Usuario logueado
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Usuarios usuarioTask) {
            if (usuarioTask != null) {
                if (usuarioTask.isOk()) {
                    usuarioLogeado = usuarioTask;
                    Toast.makeText(MainActivity.this, Constantes.BIENVENIDO + usuarioLogeado.getUsuario(), Toast.LENGTH_SHORT).show();
                    usuarioLogin.setText(usuarioLogeado.getUsuario());
                    usuarioEmail.setText(usuarioLogeado.getEmail());
                    Utilidades.establecerImagenUsuario(MainActivity.this, usuarioLogeado.getImagen(), usuarioImagen, true);
                    // Si la imagen del FTP y la de firebase no coinciden, se actualiza la de firebase
                    if (usuarioBD != null && firebaseAutenticacion.getCurrentUser() != null
                            && !usuarioLogeado.getImagen().equals(usuarioBD.child(Constantes.IMAGEN_USUARIO)))
                        usuarioBD.child(firebaseAutenticacion.getCurrentUser().getUid()).child(Constantes.IMAGEN_USUARIO).setValue(usuario.getImagen());
                    cargarCoordenadas();
                } else {
                    Toast.makeText(MainActivity.this, usuarioTask.getError(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_LONG).show();
            }
        }
    }
}
