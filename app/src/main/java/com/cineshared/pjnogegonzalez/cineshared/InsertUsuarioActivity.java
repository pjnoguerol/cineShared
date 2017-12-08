package com.cineshared.pjnogegonzalez.cineshared;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Clase InsertUsuarioActivity gestiona las acciones relacionadas con activity_insert_usuario.xml
 */
public class InsertUsuarioActivity extends AppCompatActivity {


    private EditText usuarioInsert;
    private EditText passwordInsert;
    private EditText emailInsert;
    private EditText telefonoInsert, distanciaInsert;
    private ImageView subirImagen;
    private String mCurrentPhotoPath;
    private Button btInsert, subirBoton;
    private File file;

    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference firebaseBaseDatos;

    //ProgressDialog
    private ProgressDialog procesoOnGoing;

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

        firebaseAutenticacion = FirebaseAuth.getInstance();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        usuarioInsert = (EditText) findViewById(R.id.nombreUsuario);
        passwordInsert = (EditText) findViewById(R.id.passwordUsuario);
        emailInsert = (EditText) findViewById(R.id.emailUsuario);
        telefonoInsert = (EditText) findViewById(R.id.telefonoUsuario);
        btInsert = (Button) findViewById(R.id.btInsert);
        subirBoton = (Button) findViewById(R.id.botonSubir);
        subirImagen = (ImageView) findViewById(R.id.imagenSubir);
        distanciaInsert =(EditText) findViewById(R.id.distancia);
//TODO ver el procesoOnGoing
        procesoOnGoing = new ProgressDialog(this);

        // Antes de realizar la inserción del usuario en base de datos, comprobaremos que todos los datos están informados
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //String resultado = FtpUtility.subirArchivo(file);
                //Toast.makeText(InsertUsuarioActivity.this,resultado , Toast.LENGTH_LONG).show();
                if (comprobarCamposNuevoUsuario()) {
                    procesoOnGoing.setTitle("Registrando usuario");
                    procesoOnGoing.setMessage("Por favor, espera mientras se crea tu cuenta!");
                    procesoOnGoing.setCanceledOnTouchOutside(false);
                    procesoOnGoing.show();
                    insertarNuevoUsuario();
               }
            }
        });

        subirBoton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                selectImage();

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                // Show the thumbnail on ImageView
                Uri imageUri = Uri.parse(mCurrentPhotoPath);
                file = new File(imageUri.getPath());
                Utilidades.establecerImagen(InsertUsuarioActivity.this, imageUri.toString(), subirImagen);

                try {
                    //InputStream ims = new FileInputStream(file);
                    //ivPreview.setImageBitmap(BitmapFactory.decodeStream(ims));
                } catch (Exception e) {
                    return;
                }

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(InsertUsuarioActivity.this,
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                file = new File(filePath[0]);
                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(file));
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String getPath(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void selectImage() {

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    50);
        }

        final CharSequence[] options = { "Toma foto", "Elige de la galeria","Cancel" };

        if (checkSelfPermission(Manifest.permission.CAMERA  )
                != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    100);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añade Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Toma foto"))
                {


                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(InsertUsuarioActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (photoFile != null) {
                        Uri photoURI = null;
                        try {
                            photoURI = FileProvider.getUriForFile(InsertUsuarioActivity.this,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    createImageFile());
                        } catch (IOException e) {
                            Toast.makeText(InsertUsuarioActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 1);
                    }

                }
                else if (options[item].equals("Elige de la galeria"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
     * Comprobamos si el email es correcto
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
        if (!isValidEmail(emailInsert.getText().toString().trim())) {
            emailInsert.setError("Email incorrecto");
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(telefonoInsert.getText().toString().trim())) {
            telefonoInsert.setError(getString(R.string.error_field_required));
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(distanciaInsert.getText().toString().trim())) {
            distanciaInsert.setError(getString(R.string.error_field_required));
            resultadoValidacion = false;
        }
        return resultadoValidacion;
    }



    /**
     * Método que realiza la inserción en base de datos del nuevo usuario
     */
    private void insertarNuevoUsuario() {
        try {
            String url = Constantes.RUTA_INSERTAR_USUARIO;
            ConnectivityManager connMgr = (ConnectivityManager) InsertUsuarioActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String imagen="null";
                if (file!=null)
                    imagen = file.getName();
                String usuario = usuarioInsert.getText().toString();
                String email = emailInsert.getText().toString();
                String password = passwordInsert.getText().toString();
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("userinsert", usuario)
                        .appendQueryParameter("password", password)
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("telefono", telefonoInsert.getText().toString())
                        .appendQueryParameter("distancia", distanciaInsert.getText().toString())
                        .appendQueryParameter("imagen", imagen );
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(InsertUsuarioActivity.this);
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(InsertUsuarioActivity.this,Constantes.USUARIOS));
                List <Usuarios>  resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha insertado correctament
                if (resultado.get(0).isOk()) {

                    new FtpTask().execute(file);
                    crearUsuarioFirebaseChat(usuario, usuario + Constantes.EMAIL_FIREBASE, password);
                    procesoOnGoing.dismiss();
                }
                //new HiloGenerico<Resultado>(builder).execute(new URL(url));
                //new InsertUsuarioResultadoJsonTask().execute(new URL(url));
                else {
                    procesoOnGoing.hide();
                    Toast.makeText(InsertUsuarioActivity.this, resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
                }
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

    /**
     * Metodo asincrono que sube la foto por ftp
     */
    public class FtpTask extends AsyncTask<File, Void, String> {






        /**
         * Método que llama al parseo del usuario logueado
         *
         * @param file URLs a conectar
         * @return Usuario logueado
         */
        @Override
        protected String doInBackground(File... file) {

            return FtpUtility.subirArchivo(file[0]);

        }

        /**
         * Método que redirige al usuario a la actividad main o muestra error dependiendo del resultado
         * del login
         *
         * @param respuesta Usuario logueado
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String respuesta) {
            Toast.makeText(InsertUsuarioActivity.this, respuesta, Toast.LENGTH_SHORT).show();
        }
    }

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

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put(Constantes.NOMBRE_USUARIO, usuario);
                            userMap.put(Constantes.EMAIL_USUARIO, email);
                            userMap.put(Constantes.IMAGEN_USUARIO, "default");
                            userMap.put(Constantes.TOKEN_USUARIO, tokenUsuario);

                            //TODO ver si implementamos el regProgress
                            firebaseBaseDatos.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
/*
                                mRegProgress.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();*/
                                Intent intent = new Intent(InsertUsuarioActivity.this, LoginActivity.class);
                                intent.putExtra(Constantes.USUARIO,usuario);
                                intent.putExtra(Constantes.PASSWORD,password);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(InsertUsuarioActivity.this, task.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                        } else {
                            procesoOnGoing.hide();
                            Toast.makeText(InsertUsuarioActivity.this, "Se ha producido un problema creando al usuario para acceder al chat",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}