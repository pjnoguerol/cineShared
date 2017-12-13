package com.cineshared.pjnogegonzalez.cineshared.acceso;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.BuildConfig;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.UtilitidadFtp;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Clase InsertUsuarioActivity gestiona las acciones relacionadas con activity_insert_usuario.xml
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class InsertUsuarioActivity extends AppCompatActivity {

    // Definimos las variables
    private EditText usuarioInsert, passwordInsert, emailInsert, telefonoInsert, distanciaInsert;
    private ImageView subirImagen;
    private String rutaFotoActual;
    private Button btInsert, subirBoton;
    private File ficheroImagen;

    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference firebaseBaseDatos;

    /**
     * Método onCreate controla las acciones del formulario de creación de nuevos usuarios
     *
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_usuario);

        firebaseAutenticacion = FirebaseAuth.getInstance();

        StrictMode.ThreadPolicy politicaThreadNoViolaciones = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(politicaThreadNoViolaciones);

        // Inicializamos los objetos de la pantalla para poder interactuar con ellos
        usuarioInsert = (EditText) findViewById(R.id.nombreUsuario);
        passwordInsert = (EditText) findViewById(R.id.passwordUsuario);
        emailInsert = (EditText) findViewById(R.id.emailUsuario);
        telefonoInsert = (EditText) findViewById(R.id.telefonoUsuario);
        btInsert = (Button) findViewById(R.id.insertBtn);
        subirBoton = (Button) findViewById(R.id.subirBtn);
        subirImagen = (ImageView) findViewById(R.id.imagenSubir);
        distanciaInsert = (EditText) findViewById(R.id.distanciaMaximaUsuario);

        // Antes de realizar la inserción del usuario en base de datos, comprobaremos que todos los datos están informados
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarCamposNuevoUsuario()) {
                    insertarNuevoUsuario();
                }
            }
        });

        // Establecemos la acción para subir una imagen
        subirBoton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                seleccionarImagenUsuario();

            }
        });
    }

    /**
     * Método onActivityResult espera el resultado de las activities de subida de imagen y realiza la acción
     * que corresponda.
     *
     * @param requestCode Código que se pasa en startActivityForResult e indica el tipo de selección
     *                    de fichero realizada
     * @param resultCode  Código de respuesta del activity
     * @param data        Un intent del que se puede extraer datos
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                // Opción: Tomar Foto
                Uri imageUri = Uri.parse(rutaFotoActual);
                ficheroImagen = new File(imageUri.getPath());
                Picasso.with(InsertUsuarioActivity.this).load(imageUri.toString()).into(subirImagen);
                // Scan fichero para que aparezca en la galería
                MediaScannerConnection.scanFile(InsertUsuarioActivity.this,
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                //Utilidades.establecerImagenUsuario(getContext(), selectedImage.toString(), subirImagen, false);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                ficheroImagen = new File(picturePath);

                //ImageView imageView = (ImageView) getActivity().findViewById(R.id.imgView);
                subirImagen.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                subirImagen.setRotation(270);
            }
        }
    }

    /**
     * Método comprobarCamposNuevoUsuario comprueba si todos los datos obligatorios se han introducido
     * correctamente antes de dar de alta al usuario en la base de datos
     *
     * @return Booleano con el resultado de la validación
     */
    private boolean comprobarCamposNuevoUsuario() {
        boolean resultadoValidacion = true;
        if (Constantes.CADENA_VACIA.equals(usuarioInsert.getText().toString().trim())) {
            usuarioInsert.setError("El usuario es obligatorio");
            resultadoValidacion = false;
        }
        if (!Utilidades.isPasswordValida(passwordInsert.getText().toString().trim())) {
            passwordInsert.setError("La contraseña debe contener letras y números. Longitud minima de 6");
            resultadoValidacion = false;
        }
        if (!Utilidades.isEmailValido(emailInsert.getText().toString().trim())) {
            emailInsert.setError("Email incorrecto");
            resultadoValidacion = false;
        }
        if (!Utilidades.isTelefonoValido(telefonoInsert.getText().toString().trim())) {
            telefonoInsert.setError("Teléfono con formato válido");
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(distanciaInsert.getText().toString().trim())) {
            distanciaInsert.setError("Distancia máxima es obligatoria");
            resultadoValidacion = false;
        }
        return resultadoValidacion;
    }

    /**
     * Método seleccionarImagenUsuario le muestra al usuario las distintas opciones para subir una imagen de perfil
     * y realiza la acción correspondiente
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void seleccionarImagenUsuario() {
        // Confirmamos o solicitamos los permisos necesarios
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }

        // Mostramos las opciones al usuario para seleccionar su foto de usuario
        final CharSequence[] opcionesImagen = {"Tomar foto", "Cancelar"};
        AlertDialog.Builder builderAlertDialog = new AlertDialog.Builder(this);
        builderAlertDialog.setTitle("Subir Foto");
        builderAlertDialog.setItems(opcionesImagen, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (opcionesImagen[item].equals("Tomar foto")) {
                    Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File fotoPerfilUsuario = null;
                    try {
                        fotoPerfilUsuario = crearFicheroImagen();
                    } catch (IOException ex) {
                        Toast.makeText(InsertUsuarioActivity.this, "Error creando la imagen de usuario", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fotoPerfilUsuario != null) {
                        Uri uriFoto = FileProvider.getUriForFile(InsertUsuarioActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider", fotoPerfilUsuario);
                        tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                        startActivityForResult(tomarFotoIntent, 1);
                    }
                } else if (opcionesImagen[item].equals("Elegir de la galeria")) {
                    Intent elegirFotoIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(elegirFotoIntent, 2);
                } else if (opcionesImagen[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builderAlertDialog.show();
    }

    /**
     * Método crearFicheroImagen crea un fichero con la imagen del usuario seleccionada para su perfil
     *
     * @return Fichero creado
     * @throws IOException Excepción en caso de problemas
     */
    private File crearFicheroImagen() throws IOException {
        // Creamos el nombre del fichero de Imagen
        String horaActual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImagenUsuario = "JPEG_" + horaActual + "_";
        File directorioAlmacenamiento = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File imagenPerfil = File.createTempFile(nombreImagenUsuario, ".jpg", directorioAlmacenamiento);
        //  Devolvemos la imagen con el nombre Generado
        rutaFotoActual = "file:" + imagenPerfil.getAbsolutePath();
        return imagenPerfil;
    }

    /**
     * Método insertarNuevoUsuario realiza la inserción en base de datos del nuevo usuario
     */
    private void insertarNuevoUsuario() {
        try {
            String urlInsertar = Constantes.RUTA_INSERTAR_USUARIO;
            ConnectivityManager connectivityManager = (ConnectivityManager) InsertUsuarioActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String imagenUsuario = "null";
                if (ficheroImagen != null)
                    imagenUsuario = ficheroImagen.getName();
                String usuario = usuarioInsert.getText().toString();
                String email = emailInsert.getText().toString();
                String password = passwordInsert.getText().toString();
                Uri.Builder uriBuilder = new Uri.Builder()
                        .appendQueryParameter("userinsert", usuario)
                        .appendQueryParameter("password", password)
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("telefono", telefonoInsert.getText().toString())
                        .appendQueryParameter("distancia", distanciaInsert.getText().toString())
                        .appendQueryParameter("imagen", imagenUsuario);
                HiloGenerico<Usuarios> hiloGenerico = new HiloGenerico<>(uriBuilder);
                hiloGenerico.setActivity(InsertUsuarioActivity.this);
                hiloGenerico.setTipoObjeto(Constantes.USUARIOS);
                hiloGenerico.setConversionJson(new ConversionJson<Usuarios>(Constantes.USUARIOS));
                List<Usuarios> resultadoUsuario = hiloGenerico.execute(new URL(urlInsertar)).get();
                //Comprobar que se ha insertado correctamente
                if (resultadoUsuario.get(0).isOk()) {
                    new FtpTask().execute(ficheroImagen);
                    crearUsuarioFirebaseChat(usuario, usuario + Constantes.EMAIL_FIREBASE, password);
                } else {
                    Toast.makeText(InsertUsuarioActivity.this, resultadoUsuario.get(0).getError(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo FtpTask sube la foto por FTP
     */
    public class FtpTask extends AsyncTask<File, Void, String> {

        /**
         * Método que realiza la subida de la imágenes
         *
         * @param file Fichero a subir
         * @return String Cadena con el resultado de la operación
         */
        @Override
        protected String doInBackground(File... file) {
            return UtilitidadFtp.subirArchivo(file[0]);
        }

        /**
         * Método que muestra al usuario el resultado de la operación realizada
         *
         * @param respuesta Usuario logueado
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String respuesta) {
            Toast.makeText(InsertUsuarioActivity.this, respuesta, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método crearUsuarioFirebaseChat crea al usuario en firebase para que de esa forma tenga acceso
     * a la funcionalidad del chat.
     *
     * @param usuario Nombre del usuario a crear
     * @param email Email del usuario a crear
     * @param password Contraseña del usuario a crear
     */
    private void crearUsuarioFirebaseChat(final String usuario, final String email, final String password) {
        Log.v("CrearUsuarioFirebase", "Creando usuario " + usuario + " en firebase");
        firebaseAutenticacion.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(InsertUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser usuarioRegistro = firebaseAutenticacion.getCurrentUser();
                            String uid = usuarioRegistro.getUid();

                            firebaseBaseDatos = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE)
                                    .child(uid);
                            String tokenUsuario = FirebaseInstanceId.getInstance().getToken();
                            HashMap<String, String> usuarioHashMap = new HashMap<>();
                            usuarioHashMap.put(Constantes.NOMBRE_USUARIO, usuario);
                            usuarioHashMap.put(Constantes.EMAIL_USUARIO, email);
                            usuarioHashMap.put(Constantes.IMAGEN_USUARIO, "default");
                            usuarioHashMap.put(Constantes.TOKEN_USUARIO, tokenUsuario);

                            firebaseBaseDatos.setValue(usuarioHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Si el usuario se ha creado de forma correcta, lo redirigmos al login para que acceda
                                        // directamente a la aplicación sin más acciones por su parte
                                        Intent intentLogin = new Intent(InsertUsuarioActivity.this, LoginActivity.class);
                                        intentLogin.putExtra(Constantes.USUARIO, usuario);
                                        intentLogin.putExtra(Constantes.PASSWORD, password);
                                        startActivity(intentLogin);
                                        finish();
                                    } else {
                                        Toast.makeText(InsertUsuarioActivity.this, task.toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(InsertUsuarioActivity.this, "Se ha producido un problema creando al usuario para acceder al chat",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}