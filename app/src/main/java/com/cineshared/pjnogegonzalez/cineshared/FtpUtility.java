package com.cineshared.pjnogegonzalez.cineshared;

/**
 * Created by informatica on 23/11/2017.
 */
import android.util.Log;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class FtpUtility {



    public static String subirArchivo(File archivo)  {
        String ok = "";
        try {

            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(InetAddress.getByName(Constantes.IP_FTP),27);
            ftpClient.login(Constantes.USUARIO_FTP, Constantes.PASS_FTP);

            ftpClient.changeWorkingDirectory("/cineshared/img");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            BufferedInputStream buffIn=null;
            buffIn=new BufferedInputStream(new FileInputStream(archivo));
            ftpClient.enterLocalPassiveMode();

            //File file = new File("pajaro.jp");

            ftpClient.storeFile(archivo.getName(), buffIn);
            ftpClient.getSendDataSocketBufferSize();


            buffIn.close();
            ftpClient.logout();
            ftpClient.disconnect();
            ok = "Imagen "+archivo.getName()+"  subida correctamente";

        }
        catch (SocketException e) {
        // TODO Auto-generated catch block
        Log.w("ERROR CONEXION IP FTP",e.getMessage());
        ok = e.getMessage();
    } catch (IOException e) {
        // TODO Auto-generated catch block
            Log.w("ERROR IO FTP",e.getMessage());

            ok = e.getMessage();
    }finally {
            return ok;
        }
    }

}

