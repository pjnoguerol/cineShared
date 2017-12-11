package com.cineshared.pjnogegonzalez.cineshared.acceso;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Clase LoginActivity gestiona las acciones relacionadas con activity_login.xml
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class LoginActivity extends AppCompatActivity {

    // Definimos las variables
    private EditText nombreUsuario;
    private EditText passwordUsuario;
    private Button botonLogin;
    private Button botonInsert;
    protected static boolean cerrarSesion;
    private ConversionJson<Usuarios> conversionJson = new ConversionJson<>(Constantes.USUARIOS);
    public static final String PREFS_NAME = "Preferencias";

    private FirebaseAuth autenticacionFirebase;
    private DatabaseReference referenciaBD;

    /**
     * Método onCreate controla las acciones del formulario de login
     *
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autenticacionFirebase = FirebaseAuth.getInstance();
        referenciaBD = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);

        // Si venimos de dar de alta al usuario y nos viene la información para loguearnos, no mostramos
        // el formulario de login se autentica al usuario y se le redirige al MainActivity
        if (getIntent().hasExtra(Constantes.USUARIO) && getIntent().hasExtra(Constantes.PASSWORD)) {
            loginUsuario(getIntent().getStringExtra(Constantes.USUARIO), getIntent().getStringExtra(Constantes.PASSWORD));
        } else {
            // Inicializamos las variables para la pantalla de login
            setContentView(R.layout.activity_login);
            nombreUsuario = (EditText) findViewById(R.id.userLogin);
            passwordUsuario = (EditText) findViewById(R.id.passwordUsuario);
            botonLogin = (Button) findViewById(R.id.btLogin);
            botonInsert = (Button) findViewById(R.id.btInsert);
            autenticacionFirebase = FirebaseAuth.getInstance();

            // Permitimos al usuario cerrar la sesión, establecemos el funcionamiento en ese caso
            LoginActivity.cerrarSesion = false;
            if (LoginActivity.cerrarSesion) {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                this.finish();
                LoginActivity.cerrarSesion = false;
            }
            // Establecemos la acción del botón de login
            botonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginUsuario(nombreUsuario.getText().toString(), passwordUsuario.getText().toString());
                }
            });
            // Establecemos la acción del botón de insert
            botonInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent insertUsuarioIntent = new Intent(LoginActivity.this, InsertUsuarioActivity.class);
                    startActivity(insertUsuarioIntent);
                }
            });
        }
    }

    /**
     * Método onResumen se ejecuta cada vez que la actividad se inicia
     */
    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(autenticacionFirebase, referenciaBD);
    }

    /**
     * Método onPause gestiona las acciones cuando se pausa la aplicación
     */
    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(autenticacionFirebase, referenciaBD);
    }

    /**
     * Método guardarDatos guarda los datos del usuario en las preferencias para que no tenga que
     * se mantenga la sesión del usuario sin necesidad de introducir nuevamente los datos
     *
     * @param usuario Usuario a almacenar
     */
    private void guardarDatos(String usuario) {
        SharedPreferences preferenciasUsuario = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor preferenciasUsuarioEditor = preferenciasUsuario.edit();
        preferenciasUsuarioEditor.putString("usuariologin", usuario);
        preferenciasUsuarioEditor.putBoolean("iniciado", true);
        preferenciasUsuarioEditor.commit();
    }

    /**
     * Inner class que parsea el usuario logueado
     */
    public class LoginJsonTask extends AsyncTask<URL, Void, Usuarios> {

        private Usuarios usuario;
        private Uri.Builder builder;

        private LoginJsonTask(Uri.Builder builder) {
            this.builder = builder;
        }

        /**
         * Método que llama al parseo del usuario logueado
         *
         * @param urls URLs a conectar
         * @return Usuario logueado
         */
        @Override
        protected Usuarios doInBackground(URL... urls) {
            return (usuario = conversionJson.doInBackgroundPost(urls[0], this.builder).get(0));
        }

        /**
         * Método que redirige al usuario a la actividad main o muestra error dependiendo del resultado
         * del login
         *
         * @param usuario Usuario logueado
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Usuarios usuario) {
            if (usuario != null) {
                if (usuario.isOk()) {
                    Toast.makeText(LoginActivity.this, Constantes.BIENVENIDO + usuario.getUsuario(), Toast.LENGTH_SHORT).show();
                    guardarDatos(usuario.getUsuario());
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.putExtra(Constantes.USUARIOS, usuario);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, usuario.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método loginUsuario realiza el login del usuario conociendo los datos de acceso del mismo
     *
     * @param usuario  Nombre de usuario a loguear
     * @param password Contraseña de usuario a loguear
     */
    public void loginUsuario(String usuario, String password) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Uri.Builder builderUri = new Uri.Builder()
                        .appendQueryParameter(Constantes.USUARIO, usuario)
                        .appendQueryParameter(Constantes.PASSWORD, password);
                new LoginJsonTask(builderUri).execute(new URL(Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP));
                // Autenticamos también al usuario en firebase para que pueda acceder al chat
                if (autenticacionFirebase != null && referenciaBD != null)
                    AccionesFirebase.loginUserFirebase(autenticacionFirebase, referenciaBD,
                            usuario + Constantes.EMAIL_FIREBASE, password, LoginActivity.this);
            } else {
                Toast.makeText(LoginActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
