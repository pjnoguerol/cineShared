package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Clase LoginActivity gestiona las acciones relacionadas con activity_login.xml
 */
public class LoginActivity extends AppCompatActivity {
    private EditText nombreUsuario;
    private EditText passwordUsuario;
    private Button botonLogin;
    private Button botonInsert;
    protected static boolean cerrarSesion;
    private ConversionJson<Usuarios> conversionJson = new ConversionJson<>(this, Constantes.USUARIOS);
    public static final String PREFS_NAME = "Preferencias";

    private void guardarDatos()
    {
        SharedPreferences preferencia =  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //String usuario = userlogin.getText().toString();
        //String password = pass.getText().toString();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("usuariologin", nombreUsuario.getText().toString());
        editor.putBoolean("iniciado", true);
        //settings.getBoolean("iniciado");
        // Commit the edits!
        editor.commit();



    }

    /**
     * Método que controla las acciones del formulario de login
     *
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nombreUsuario = (EditText) findViewById(R.id.userLogin);
        passwordUsuario = (EditText) findViewById(R.id.passwordUsuario);
        botonLogin = (Button) findViewById(R.id.btLogin);
        botonInsert = (Button) findViewById(R.id.btInsert);
        // Permitimos al usuario cerrar la sesión, establecemos el funcionamiento en ese caso
        LoginActivity.cerrarSesion = false;
        if (LoginActivity.cerrarSesion) {
            Intent pantallaLogin = new Intent(this, LoginActivity.class);
            pantallaLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(pantallaLogin);
            this.finish();
            LoginActivity.cerrarSesion = false;
        }
        // Establecemos la acción del botón de login
       ;
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new LoginJsonTask().execute(new URL(Constantes.RUTA_LOGIN + nombreUsuario.getText() + "&"
                                + Constantes.PASSWORD + "=" + passwordUsuario.getText()));
                    } else {
                        Toast.makeText(LoginActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        // Establecemos la acción del botón de insert
        botonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Inner class que parsea el usuario logueado
     */
    public class LoginJsonTask extends AsyncTask<URL, Void, Usuarios> {

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
                    guardarDatos();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(Constantes.USUARIOS, usuario);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, usuario.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
