package com.cineshared.pjnogegonzalez.cineshared;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Clase que parsea los datos del usuario logueado y los muestra
 */
public class ConfiguracionFragment extends Fragment {

    private Context context;
    private Usuarios usuarioConectado;
    private EditText usuarioInsert;
    private EditText passwordInsert;
    private EditText emailInsert;
    private EditText telefonoInsert, distanciaInsert;
    private ImageView subirImagen;
    private String mCurrentPhotoPath;
    private Button btInsert, subirBoton;
    private File file;



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
        View rootView = inflater.inflate(R.layout.fragment_configuracion, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        usuarioInsert = (EditText) rootView.findViewById(R.id.nombreUsuario);
        passwordInsert = (EditText) rootView.findViewById(R.id.passwordUsuario);
        emailInsert = (EditText) rootView.findViewById(R.id.emailUsuario);
        telefonoInsert = (EditText) rootView.findViewById(R.id.telefonoUsuario);
        btInsert = (Button) rootView.findViewById(R.id.btInsert);
        subirBoton = (Button) rootView.findViewById(R.id.botonSubir);
        subirImagen = (ImageView) rootView.findViewById(R.id.imagenSubir);
        distanciaInsert =(EditText) rootView.findViewById(R.id.distancia);
        usuarioConectado = (Usuarios) getArguments().getSerializable("usuarios");

        usuarioInsert.setText(usuarioConectado.getUsuario());

        //nombreUsuario.setText(Html.fromHtml("<b>Usuario: </b>" + usuarioConectado.getUsuario()));
        Picasso.with(rootView.getContext()).load(Constantes.RUTA_IMAGEN+usuarioConectado.getImagen()).into(subirImagen);
        passwordInsert.setText(usuarioConectado.getPassword());
        emailInsert.setText((usuarioConectado.getEmail()));
        telefonoInsert.setText((usuarioConectado.getTelefono()));
        distanciaInsert.setText(usuarioConectado.getDistancia()+"");

        subirBoton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                selectImage();

            }
        });

        // Antes de realizar la inserción del usuario en base de datos, comprobaremos que todos los datos están informados
        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String resultado = FtpUtility.subirArchivo(file);
                //Toast.makeText(InsertUsuarioActivity.this,resultado , Toast.LENGTH_LONG).show();
                if (comprobarCamposNuevoUsuario()) {
                    insertarNuevoUsuario();
                }
            }
        });


        // Permitimos que el usuario modifique el valor del género elegido como favorito

        return rootView;
    }

    /**
     * Método que realiza la inserción en base de datos del nuevo usuario
     */
    private void insertarNuevoUsuario() {
        try {
            String url = Constantes.RUTA_INSERTAR_USUARIO;
            ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String imagen="null";
                if (file!=null)
                    imagen = file.getName();

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("userinsert", usuarioInsert.getText().toString())
                        .appendQueryParameter("password", passwordInsert.getText().toString())
                        .appendQueryParameter("email", emailInsert.getText().toString())
                        .appendQueryParameter("telefono", telefonoInsert.getText().toString())
                        .appendQueryParameter("distancia", distanciaInsert.getText().toString())
                        .appendQueryParameter("imagen", imagen );
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(getActivity(),Constantes.USUARIOS));
                List <Usuarios>  resultado = hilo.execute(new URL(url)).get();
                //Comprobar que se ha insertado correctament
                if (resultado.get(0).isOk())
                    new FtpTask().execute(file);
                    //new HiloGenerico<Resultado>(builder).execute(new URL(url));
                    //new InsertUsuarioResultadoJsonTask().execute(new URL(url));
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
            Toast.makeText(getContext(), respuesta, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                // Show the thumbnail on ImageView
                Uri imageUri = Uri.parse(mCurrentPhotoPath);
                file = new File(imageUri.getPath());
                Picasso.with(getContext()).load(imageUri).into(subirImagen);

                try {
                    //InputStream ims = new FileInputStream(file);
                    //ivPreview.setImageBitmap(BitmapFactory.decodeStream(ims));
                } catch (Exception e) {
                    return;
                }

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(getContext(),
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContext().getContentResolver().query(selectedImage,filePath, null, null, null);
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
        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    /**
     * Aqui crearemos las imagenes
     * @return
     * @throws IOException
     */
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

        if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    50);
        }

        final CharSequence[] options = { "Toma foto", "Elige de la galeria","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Añade Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Toma foto"))
                {
                    int MY_CAMERA_REQUEST_CODE = 100;
                    if (getContext().checkSelfPermission(Manifest.permission.CAMERA  )
                            != PackageManager.PERMISSION_GRANTED ) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_CAMERA_REQUEST_CODE);
                    }

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (photoFile != null) {
                        Uri photoURI = null;
                        try {
                            photoURI = FileProvider.getUriForFile(getContext(),
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    createImageFile());
                        } catch (IOException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
     * Método que actualiza el género seleccionado por el usuario logueado
     */
    private void updateGeneros() {
        try {
            //String url = Constantes.RUTA_GENEROS_USUARIO + usuarioConectado.getId_usua() + "&generoupdate=" + generoSeleccionado.getId_gen();
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected())
                Log.w("hola","");
                //new ConfiguracionUsuarioResultadoJsonTask().execute(new URL(url));
            else
                Toast.makeText(context, Constantes.ERROR_CONEXION, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class que parsea la lista de géneros a un Spinner
     */

}