package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Clase UtilitidadFtp contiene las utilidades del FTP para la subida de imágenes
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class UtilitidadFtp {

    /**
     * Método subirArchivo sube el archivo al FTP
     *
     * @param archivo Archivo a subir
     * @return Resultado de dicha subida
     */
    public static String subirArchivo(File archivo) {
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
}

