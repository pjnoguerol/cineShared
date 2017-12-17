package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase UtilidadesImagenes contiene las utilidades relacionadas con imágenes
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class UtilidadesImagenes {

    /**
     * Método subirArchivo sube el archivo al FTP
     *
     * @param archivo Archivo a subir
     * @return Resultado de dicha subida
     */
    public static String subirArchivoFtp(File archivo) {
        String resultado = "";
        try {
            FTPClient ftpCliente = new FTPClient();
            ftpCliente.connect(InetAddress.getByName(Constantes.IP_FTP), 27);
            ftpCliente.login(Constantes.USUARIO_FTP, Constantes.PASS_FTP);
            ftpCliente.changeWorkingDirectory("/cineshared/img");
            ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
            BufferedInputStream bufferedInputStream = null;
            bufferedInputStream = new BufferedInputStream(new FileInputStream(archivo));
            ftpCliente.enterLocalPassiveMode();
            ftpCliente.storeFile(archivo.getName(), bufferedInputStream);
            ftpCliente.getSendDataSocketBufferSize();
            bufferedInputStream.close();
            ftpCliente.logout();
            ftpCliente.disconnect();
            resultado = "Imagen " + archivo.getName() + "  subida correctamente";
        } catch (SocketException e) {
            resultado = e.getMessage();
        } catch (IOException e) {
            resultado = e.getMessage();
        } finally {
            return resultado;
        }
    }

    /**
     * Método establecerImagenUsuario establece la imagen del usuario con la librería Picasso y le da
     * formato circular. Además, establece el color de fondo dependiendo de si la imagen está informada o no.
     *
     * @param contexto        Contexto del activity donde se cargará la imagen
     * @param imagenUrl       URL de la imagen que se cargará
     * @param imagenViewField Campo donde se realiza la visualización de la imagen
     */
    public static void establecerImagenUsuario(final Context contexto, final String imagenUrl,
                                               final ImageView imagenViewField, boolean cambiarFondo) {
        String urlImagen = imagenUrl;
        if (!imagenUrl.contains("http"))
            urlImagen = Constantes.RUTA_IMAGEN + urlImagen;

        Picasso.with(contexto).load(urlImagen)
                .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_chat_img_defecto)
                .transform(new TransformacionCirculo()).fit().centerCrop()
                .into(imagenViewField, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Picasso.with(contexto).load(Constantes.RUTA_IMAGEN + imagenUrl)
                                .placeholder(R.drawable.ic_chat_img_defecto)
                                .transform(new TransformacionCirculo()).fit().centerCrop().into(imagenViewField);
                    }
                });
        if (cambiarFondo) {
            if (!"null".equals(imagenUrl) && !"default".equals(imagenUrl)) {
                imagenViewField.setBackgroundResource(R.color.colorPrimary);
            } else {
                imagenViewField.setBackgroundResource(R.color.colorBlanco);
            }
        }
    }

    /**
     * Método establecerImagen establece la imagen del usuario con la librería Picasso y establece el color de
     * fondo dependiendo de si la imagen está informada o no.
     *
     * @param contexto        Contexto del activity donde se cargará la imagen
     * @param imagenUrl       URL de la imagen que se cargará
     * @param imagenViewField Campo donde se realiza la visualización de la imagen
     */
    public static void establecerImagen(final Context contexto, final String imagenUrl, final ImageView imagenViewField) {
        String urlImagen = imagenUrl;
        if (!imagenUrl.contains("http"))
            urlImagen = Constantes.RUTA_IMAGEN + urlImagen;

        Picasso.with(contexto).load(urlImagen).networkPolicy(NetworkPolicy.OFFLINE).into(imagenViewField);
    }

    /**
     * Método crearFicheroImagen crea un fichero con la imagen del usuario seleccionada para su perfil
     *
     * @return Fichero creado
     * @throws IOException Excepción en caso de problemas
     */
    public static File crearFicheroImagen() throws IOException {
        // Creamos el nombre del fichero de Imagen
        String horaActual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreImagenUsuario = "JPEG_" + horaActual + "_";
        File directorioAlmacenamiento = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File imagenPerfil = File.createTempFile(nombreImagenUsuario, ".jpg", directorioAlmacenamiento);
        //  Devolvemos la imagen con el nombre Generado
        return imagenPerfil;
    }
}

