package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Clase InsertUsuarioActivity gestiona las acciones relacionadas con activity_insert_usuario.xml
 */
public class InsertUsuarioActivity extends AppCompatActivity {


    private EditText usuarioInsert;
    private EditText passwordInsert;
    private EditText emailInsert;
    private EditText telefonoInsert;
    private Button btInsert;


    private ConversionJson<Resultado> conversionJsonResultado = new ConversionJson<>(this, Constantes.RESULTADO);


    /**
     * Método que controla las acciones del formulario de creación de nuevos usuarios
     *
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_usuario);

        usuarioInsert = (EditText) findViewById(R.id.nombreUsuario);
        passwordInsert = (EditText) findViewById(R.id.passwordUsuario);
        emailInsert = (EditText) findViewById(R.id.emailUsuario);
        telefonoInsert = (EditText) findViewById(R.id.telefonoUsuario);
        btInsert = (Button) findViewById(R.id.btInsert);

        // Antes de realizar la inserción del usuario en base de datos, comprobaremos que todos los datos están informados
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarCamposNuevoUsuario()) {
                    insertarNuevoUsuario();
                }
            }
        });
    }

    /**
     * Inner class que parsea la lista de géneros a un Spinner
     */


    /**
     * Inner class que parsea el resultado de la inserción en base de datos
     */
    public class InsertUsuarioResultadoJsonTask extends AsyncTask<URL, Void, Resultado> {

        private Resultado resultadoInsert;

        /**
         * Método que llama al parseo de resultados para obtener los datos del mismo
         *
         * @param urls URLs a conectar
         * @return Resultado de la inserción
         */
        @Override
        protected Resultado doInBackground(URL... urls) {
            return (resultadoInsert = (conversionJsonResultado.doInBackground(urls)).get(0));
        }

        /**
         * Método que comprueba el resultado de la inserción y redirige al usuario al login o muestra
         * un mensaje de error dependiendo del mismo
         *
         * @param resultadoInsert Resultado obtenido de la inserción del usuario en base de datos
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(Resultado resultadoInsert) {
            if (resultadoInsert != null) {
                if (resultadoInsert.isOk()) {
                    Toast.makeText(InsertUsuarioActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(InsertUsuarioActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(InsertUsuarioActivity.this, Constantes.ERROR_INSERT + resultadoInsert.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(InsertUsuarioActivity.this, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Método que comprueba si todos los datos obligatorios se han introducido antes de dar de alta
     * al usuario en la base de datos
     *
     * @return Booleano con el resultado de la validación
     */
    private boolean comprobarCamposNuevoUsuario() {
        boolean resultadoValidacion = true;
        if (Constantes.CADENA_VACIA.equals(usuarioInsert.getText().toString().trim())) {
            usuarioInsert.setError(getString(R.string.error_field_required));
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(passwordInsert.getText().toString().trim())) {
            passwordInsert.setError(getString(R.string.error_field_required));
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(emailInsert.getText().toString().trim())) {
            emailInsert.setError(getString(R.string.error_field_required));
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(telefonoInsert.getText().toString().trim())) {
            telefonoInsert.setError(getString(R.string.error_field_required));
            resultadoValidacion = false;
        }
        return resultadoValidacion;
    }

    /**
     * Método que realiza la inserción en base de datos del nuevo usuario
     */
    private void insertarNuevoUsuario() {
        try {
            String url = Constantes.RUTA_INSERTAR_USUARIO + usuarioInsert.getText() + "&passinsert=" + passwordInsert.getText()
                    + "&emailinsert=" + emailInsert.getText() + "&telefonoinsert=" + telefonoInsert.getText()
                    + "&generoinsert=" ;
            ConnectivityManager connMgr = (ConnectivityManager) InsertUsuarioActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                new InsertUsuarioResultadoJsonTask().execute(new URL(url));
            } else {
                Toast.makeText(InsertUsuarioActivity.this, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}