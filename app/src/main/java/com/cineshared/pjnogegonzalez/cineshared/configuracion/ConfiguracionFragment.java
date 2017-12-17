package com.cineshared.pjnogegonzalez.cineshared.configuracion;

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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.BuildConfig;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.MainActivity;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.UtilidadesImagenes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Clase ConfiguracionFragment que parsea los datos del usuario logueado y los muestra para su modificación
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class ConfiguracionFragment extends Fragment {

    // Definimos las variables necesarias
    private Usuarios usuarioConectado;
    private TextView usuarioUpdate;
    private EditText passwordUpdate, emailUpdate, telefonoUpdate, distanciaUpdate;
    private ImageView subirImagen;
    private String rutaFotoActual;
    private Button btInsert, subirBoton;
    private File ficheroImagen;
    private FirebaseUser usuarioFirebase;
    private FirebaseAuth firebaseAutenticacion;
    private DatabaseReference referenciaBDUsuarios;

    /**
     * Iniciamos el fragmento instanciado en la vista del usuario
     *
     * @param inflater           Para ampliar cualquier vista en el fragmento
     * @param container          Contenedor al que incluir el fragmento (puede ser null)
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     * @return Vista del fragmento o null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View vistaRaiz = inflater.inflate(R.layout.fragment_configuracion, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
        usuarioUpdate = (TextView) vistaRaiz.findViewById(R.id.nombreUsuario);
        passwordUpdate = (EditText) vistaRaiz.findViewById(R.id.passwordUsuario);
        emailUpdate = (EditText) vistaRaiz.findViewById(R.id.emailUsuario);
        telefonoUpdate = (EditText) vistaRaiz.findViewById(R.id.telefonoUsuario);
        btInsert = (Button) vistaRaiz.findViewById(R.id.btInsert);
        subirBoton = (Button) vistaRaiz.findViewById(R.id.botonSubir);
        subirImagen = (ImageView) vistaRaiz.findViewById(R.id.imagenSubir);
        distanciaUpdate = (EditText) vistaRaiz.findViewById(R.id.distancia);
        usuarioConectado = (Usuarios) getArguments().getSerializable("usuarios");
        usuarioUpdate.setText(Html.fromHtml("<b>Usuario: </b>" + usuarioConectado.getUsuario()));
        passwordUpdate.setText(usuarioConectado.getPassword());
        emailUpdate.setText((usuarioConectado.getEmail()));
        telefonoUpdate.setText((usuarioConectado.getTelefono()));
        distanciaUpdate.setText(usuarioConectado.getDistancia() + "");
        subirBoton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                seleccionarImagenUsuario();

            }
        });
        firebaseAutenticacion = FirebaseAuth.getInstance();
        referenciaBDUsuarios = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE);

        UtilidadesImagenes.establecerImagenUsuario(vistaRaiz.getContext(), usuarioConectado.getImagen(), subirImagen, false);

        // Antes de realizar la modificación del usuario en base de datos, comprobaremos que todos los datos están informados
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilidades.comprobarCamposUsuario(null, passwordUpdate, emailUpdate, telefonoUpdate,
                        distanciaUpdate, false)) {
                    modificarUsuario();
                }
            }
        });
        return vistaRaiz;
    }

    /**
     * Método onResumen se ejecuta cada vez que la actividad se inicia
     */
    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(firebaseAutenticacion, referenciaBDUsuarios);
    }

    /**
     * Método que realiza la modificación en base de datos del usuario
     */
    private void modificarUsuario() {
        try {
            String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String imagenUrl = usuarioConectado.getImagen();
                if (ficheroImagen != null && ficheroImagen.exists()) {
                    imagenUrl = ficheroImagen.getName();
                }
                String password = passwordUpdate.getText().toString();
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("userioactu", usuarioConectado.getUsuario())
                        .appendQueryParameter("passactu", password)
                        .appendQueryParameter("emailactu", emailUpdate.getText().toString())
                        .appendQueryParameter("telactu", telefonoUpdate.getText().toString())
                        .appendQueryParameter("disactu", distanciaUpdate.getText().toString())
                        .appendQueryParameter("imactu", imagenUrl);
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(Constantes.USUARIOS));
                List<Usuarios> resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha modificado correctamente
                if (resultado.get(0).isOk()) {
                    actualizarUsuarioFirebase(password, imagenUrl);
                    // Se sube la imagen del usuario
                    if (!imagenUrl.equals(usuarioConectado.getImagen()))
                        new FtpTask().execute(ficheroImagen);
                    else {
                        Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                        mainActivity.putExtra(Constantes.USUARIOS, usuarioConectado);
                        startActivity(mainActivity);
                    }
                } else {
                    Toast.makeText(getContext(), resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
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
     * Metodo FtpTask es un método asíncrono que sube la foto por ftp
     */
    public class FtpTask extends AsyncTask<File, Void, String> {

        /**
         * Método doInBackground realiza la subida al FTP
         *
         * @param file URLs a conectar
         * @return String resultado
         */
        @Override
        protected String doInBackground(File... file) {
            return UtilidadesImagenes.subirArchivoFtp(file[0]);
        }

        /**
         * Método que redirige al usuario a la actividad main
         *
         * @param respuesta Respuesta de la subida de la imagen
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String respuesta) {
            Toast.makeText(getContext(), respuesta, Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                final Uri imageUri = Uri.parse(rutaFotoActual);
                ficheroImagen = new File(imageUri.getPath());
                subirImagen.setImageBitmap(BitmapFactory.decodeFile(imageUri.getPath()));
                MediaScannerConnection.scanFile(getContext(),
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else if (requestCode == 2) {
                Uri imagenSeleccionada = data.getData();
                String[] rutaFicheroColumna = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(imagenSeleccionada, rutaFicheroColumna, null, null, null);
                cursor.moveToFirst();
                int indiceColumna = cursor.getColumnIndex(rutaFicheroColumna[0]);
                String rutaImagen = cursor.getString(indiceColumna);
                cursor.close();
                ficheroImagen = new File(rutaImagen);
                subirImagen.setImageBitmap(BitmapFactory.decodeFile(rutaImagen));
            }
        }
    }

    /**
     * Método onPause gestiona las acciones cuando se pausa la aplicación
     */
    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(firebaseAutenticacion, referenciaBDUsuarios);
    }

    /**
     * Método seleccionarImagenUsuario le muestra al usuario las distintas opciones para subir una imagen de perfil
     * y realiza la acción correspondiente
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void seleccionarImagenUsuario() {
        // Confirmamos o solicitamos los permisos necesarios
        if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);
        }
        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }

        // Mostramos las opciones al usuario para seleccionar su foto de usuario
        final CharSequence[] opcionesImagen = {"Tomar foto", "Elegir de la galeria", "Cancelar"};
        AlertDialog.Builder builderAlertDialog = new AlertDialog.Builder(getContext());
        builderAlertDialog.setTitle("Subir Foto");
        builderAlertDialog.setItems(opcionesImagen, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (opcionesImagen[item].equals("Tomar foto")) {
                    Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File fotoPerfilUsuario = null;
                    try {
                        fotoPerfilUsuario = UtilidadesImagenes.crearFicheroImagen();
                        rutaFotoActual = fotoPerfilUsuario.getAbsolutePath();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(), "Error creando la imagen de usuario", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (fotoPerfilUsuario != null) {
                        Uri uriFoto = FileProvider.getUriForFile(getContext(),
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
     * Método actualizarUsuarioFirebase actualiza la información del usuario en firebase
     *
     * @param password String contraseña
     * @param imagen   String imagen
     */
    private void actualizarUsuarioFirebase(final String password, String imagen) {
        // Si la contraseña ha cambiado, se actualiza
        if (usuarioConectado != null && password != null && !password.equals(usuarioConectado.getPassword())) {
            AuthCredential credencial = EmailAuthProvider.getCredential(usuarioConectado.getUsuario() + Constantes.EMAIL_FIREBASE,
                    usuarioConectado.getPassword());
            usuarioFirebase.reauthenticate(credencial)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                usuarioFirebase.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("CineSharedActFirebase", "Contraseña actualizada");
                                        } else {
                                            Log.d("CineSharedActFirebase", "Error actualizando contraseña");
                                        }
                                    }
                                });
                            } else {
                                Log.d("CineSharedActFirebase", "Error de autenticación");
                            }
                        }
                    });
        }
        referenciaBDUsuarios.child(firebaseAutenticacion.getCurrentUser().getUid())
                .child(Constantes.IMAGEN_USUARIO).setValue(imagen);
    }
}
