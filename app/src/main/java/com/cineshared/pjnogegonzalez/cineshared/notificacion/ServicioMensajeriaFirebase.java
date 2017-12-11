package com.cineshared.pjnogegonzalez.cineshared.notificacion;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Clase ServicioMensajeriaFirebase gestiona las notificaciones en el movil por los mensajes de chat
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class ServicioMensajeriaFirebase extends FirebaseMessagingService {

    /**
     * Método onMessageReceived se ejecuta cuando se recibe un mensaje, y se actualizará la información
     * de la notificación a enviar al usuario
     *
     * @param remoteMessage Datos del mensaje enviado
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String tituloNotificacion = remoteMessage.getNotification().getTitle();
        String mensajeNotificacion = remoteMessage.getNotification().getBody();
        String accionClickNotificacion = remoteMessage.getNotification().getClickAction();

        // Estas son las notificaciones que se muestran cuando la aplicación está en primer plano.
        // Cuando la aplicación está minimizada, recibe la notificación que forma la función de firebase
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(tituloNotificacion)
                        .setContentText(mensajeNotificacion);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_cineshared);

        Intent intentDestinatario = new Intent(accionClickNotificacion);
        PendingIntent intentDestinatarioPending =
                PendingIntent.getActivity(this, 0,
                        intentDestinatario, PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(intentDestinatarioPending);

        // Obtenemos un identificador único para cada notificación
        int notificationId = (int) System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
