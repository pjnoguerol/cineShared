package com.cineshared.pjnogegonzalez.cineshared.configuracion;

import android.Manifest;
import android.annotation.TargetApi;
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
import com.cineshared.pjnogegonzalez.cineshared.utilidades.UtilitidadFtp;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;
import com.cineshared.pjnogegonzalez.cineshared.acceso.MainActivity;
import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;

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
    private Context context;
    private Usuarios usuarioConectado;
    private TextView usuarioUpdate;
    private EditText passwordUpdate, emailUpdate, telefonoUpdate, distanciaUpdate;
    private ImageView subirImagen;
    private String rutaFotoActual;
    private Button btInsert, subirBoton;
    private File file;

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
        context = inflater.getContext();
        final View vistaRaiz = inflater.inflate(R.layout.fragment_configuracion, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        Utilidades.establecerImagenUsuario(vistaRaiz.getContext(), usuarioConectado.getImagen(), subirImagen, false);

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

        // Antes de realizar la modificación del usuario en base de datos, comprobaremos que todos los datos están informados
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarCamposActualizarUsuario()) {
                    modificarUsuario();
                }
            }
        });

        return vistaRaiz;
    }

    /**
     * Método comprobarCamposActualizarUsuario comprueba si todos los datos obligatorios se han introducido
     * correctamente antes de modificar al usuario en la base de datos
     *
     * @return Booleano con el resultado de la validación
     */
    private boolean comprobarCamposActualizarUsuario() {
        boolean resultadoValidacion = true;
        if (Constantes.CADENA_VACIA.equals(usuarioUpdate.getText().toString().trim())) {
            usuarioUpdate.setError("El usuario es obligatorio");
            resultadoValidacion = false;
        }
        if (!Utilidades.isPasswordValida(passwordUpdate.getText().toString().trim())) {
            passwordUpdate.setError("La contraseña debe contener letras y números. Longitud minima de 6");
            resultadoValidacion = false;
        }
        if (!Utilidades.isEmailValido(emailUpdate.getText().toString().trim())) {
            emailUpdate.setError("Email incorrecto");
            resultadoValidacion = false;
        }
        if (!Utilidades.isTelefonoValido(telefonoUpdate.getText().toString().trim())) {
            telefonoUpdate.setError("Teléfono con formato válido");
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(distanciaUpdate.getText().toString().trim())) {
            distanciaUpdate.setError("Distancia máxima es obligatoria");
            resultadoValidacion = false;
        }
        return resultadoValidacion;
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
                String imagen = usuarioConectado.getImagen();
                if (file != null && file.exists()) {
                    imagen = file.getName();
                }
                String password = passwordUpdate.getText().toString();
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("userioactu", usuarioConectado.getUsuario())
                        .appendQueryParameter("passactu", password)
                        .appendQueryParameter("emailactu", emailUpdate.getText().toString())
                        .appendQueryParameter("telactu", telefonoUpdate.getText().toString())
                        .appendQueryParameter("disactu", distanciaUpdate.getText().toString())
                        .appendQueryParameter("imactu", imagen);
                Log.w("builderconfiguracion ", builder.toString());
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(Constantes.USUARIOS));
                List<Usuarios> resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha modificado correctament
                if (resultado.get(0).isOk()) {
                    Toast.makeText(getContext(), resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
                    actualizarUsuarioFirebase(password, imagen);
                    // Se sube la imagen del usuario
                    if (!imagen.equals(usuarioConectado.getImagen()))
                        new FtpTask().execute(file);
                    else {
                        Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                        startActivity(mainActivity);
                    }
                }
                else
                    Toast.makeText(getContext(), resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
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
            return UtilitidadFtp.subirArchivo(file[0]);
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
                file = new File(imageUri.getPath());
                Utilidades.establecerImagenUsuario(getContext(), imageUri.toString(), subirImagen, false);

                MediaScannerConnection.scanFile(getContext(),
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContext().getContentResolver().query(selectedImage, filePath, null, null, null);
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
        final CharSequence[] opcionesImagen = {"Tomar foto", "Elegir de la galeria" ,"Cancelar"};
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
                        fotoPerfilUsuario = crearFicheroImagen();
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
     * @param imagen String imagen
     */
    private void actualizarUsuarioFirebase(String password, String imagen) {
        // TODO
    }
}
