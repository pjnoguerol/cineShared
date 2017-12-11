package com.cineshared.pjnogegonzalez.cineshared.acceso;

import android.app.Application;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Clase CineShared contiene unas personalizaciones que afectan a toda la aplicación
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class CineShared extends Application {

    // Definimos las variables
    private DatabaseReference usuarioBD;
    private FirebaseAuth autenticacionFirebase;

    /**
     * Método onCreate ejecuta las acciones necesarias al abrir la aplicación. En este caso,
     * crearemos una instancia de Picasso que permita el cacheo de imágenes para evitar cargarlas
     * cada vez y, además, necesitamos que cualquiera que ante cualquier problema, se establezca
     * al usuario como desconectado de la aplicación
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Picasso customizado para que cachee las imágenes
        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
        picassoBuilder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = picassoBuilder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        // Instancia para establecer al usuario como desconectado ante cualquier problema
        autenticacionFirebase = FirebaseAuth.getInstance();
        if (autenticacionFirebase.getCurrentUser() != null) {
            usuarioBD = FirebaseDatabase.getInstance().getReference().child(Constantes.USUARIOS_FIREBASE)
                    .child(autenticacionFirebase.getCurrentUser().getUid());
            usuarioBD.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        usuarioBD.child(Constantes.CONEXION_USUARIO).onDisconnect().setValue(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
