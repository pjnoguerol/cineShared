package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

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
    private String emailUsuario;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    // TODO Revisar
    private ProgressDialog procesoOnGoing;

    private void guardarDatos(String usuario) {
        SharedPreferences preferencia = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //String usuario = userlogin.getText().toString();
        //String password = pass.getText().toString();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("usuariologin", usuario);
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
        // Si venimos de dar de alta al usuario y nos viene la información para loguearnos, no mostramos
        // el formulario de login se autentica al usuario y se le redirige al MainActivity
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);

        if (getIntent().hasExtra(Constantes.USUARIO) && getIntent().hasExtra(Constantes.PASSWORD)) {
            loginUsuario(getIntent().getStringExtra(Constantes.USUARIO), getIntent().getStringExtra(Constantes.PASSWORD));
        } else {
            setContentView(R.layout.activity_login);
            nombreUsuario = (EditText) findViewById(R.id.userLogin);
            passwordUsuario = (EditText) findViewById(R.id.passwordUsuario);
            botonLogin = (Button) findViewById(R.id.btLogin);
            botonInsert = (Button) findViewById(R.id.btInsert);

            procesoOnGoing = new ProgressDialog(this);

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
                    /*procesoOnGoing.setTitle("Autenticando usuario");
                    procesoOnGoing.setMessage("Por favor, espera mientras se inicia sesión");
                    procesoOnGoing.setCanceledOnTouchOutside(false);
                    procesoOnGoing.show();*/
                    loginUsuario(nombreUsuario.getText().toString(), passwordUsuario.getText().toString());

                }
            });
            // Establecemos la acción del botón de insert
            botonInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, InsertUsuarioActivity.class);
                    startActivity(intent);
                }
            });
        }
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
                    if (procesoOnGoing != null)
                        procesoOnGoing.dismiss();
                    Toast.makeText(LoginActivity.this, Constantes.BIENVENIDO + usuario.getUsuario(), Toast.LENGTH_SHORT).show();
                    guardarDatos(usuario.getUsuario());
                    emailUsuario = usuario.getEmail();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.putExtra(Constantes.USUARIOS, usuario);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, usuario.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                procesoOnGoing.hide();
                Toast.makeText(LoginActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loginUsuario(String usuario, String password) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(Constantes.USUARIO, usuario)
                        .appendQueryParameter(Constantes.PASSWORD, password);

                new LoginJsonTask(builder).execute(new URL(Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP));
                //new LoginJsonTask().execute(new URL(Constantes.RUTA_LOGIN + nombreUsuario.getText() + "&"
                // + Constantes.PASSWORD + "=" + passwordUsuario.getText()));
                loginUserFirebase(usuario + Constantes.EMAIL_FIREBASE, password);
            } else {
                Toast.makeText(LoginActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void loginUserFirebase(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String current_user_id = mAuth.getCurrentUser().getUid();

                } else {

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(LoginActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            }
        });


    }
}
