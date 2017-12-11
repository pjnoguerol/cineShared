package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Clase AccionesFirebase gestiona las acciones relacionadas con firebase y el chat
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class AccionesFirebase {

    /**
     * Método que conecta con la base de datos de firebase y obtiene la información de login del usuario que
     * accede a la aplicación. Además, establece su estado de conexión como activo
     *
     * @param firebaseAutenticacion Objeto de autenticación de Firebase
     * @param referenciaBD          Referencia a la base de datos de Firebase
     * @param email                 Email del usuario que se desea logear
     * @param password              Contraseña del usuario que se desea logear
     * @param contexto              Contexto del activity en el que se mostrará un mensaje en caso de error
     */
    public static void loginUserFirebase(final FirebaseAuth firebaseAutenticacion, final DatabaseReference referenciaBD,
                                         String email, String password, final Context contexto) {
        if (email != null && password != null) {
            firebaseAutenticacion.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.v("CineSharedFirebase", "Usuario logeado con firebase");
                                String identificadorUsuario = firebaseAutenticacion.getCurrentUser().getUid();
                                String tokenUsuario = FirebaseInstanceId.getInstance().getToken();
                                referenciaBD.child(identificadorUsuario).child(Constantes.TOKEN_USUARIO).setValue(tokenUsuario);
                                AccionesFirebase.establecerUsuarioOnline(firebaseAutenticacion, referenciaBD);
                            } else {
                                String task_result = task.getException().getMessage().toString();
                                Toast.makeText(contexto, "Error: " + task_result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Método establecerUsuarioOffline establece al usuario como no conectado en firebase
     */
    public static void establecerUsuarioOffline(FirebaseAuth firebaseAutenticacion, DatabaseReference referenciaBD) {
        if (firebaseAutenticacion != null && referenciaBD != null && firebaseAutenticacion.getCurrentUser() != null) {
            referenciaBD.child(firebaseAutenticacion.getCurrentUser().getUid()).child(Constantes.CONEXION_USUARIO).setValue(false);
        }
    }

    /**
     * Método establecerUsuarioOnline establece al usuario como conectado en firebase
     */
    public static void establecerUsuarioOnline(FirebaseAuth firebaseAutenticacion, DatabaseReference referenciaBD) {
        if (firebaseAutenticacion != null && referenciaBD != null && firebaseAutenticacion.getCurrentUser() != null) {
            referenciaBD.child(firebaseAutenticacion.getCurrentUser().getUid()).child(Constantes.CONEXION_USUARIO).setValue(true);
        }
    }
}
