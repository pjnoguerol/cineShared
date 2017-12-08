package com.cineshared.pjnogegonzalez.cineshared;

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
 * Created by elgonzalez on 05/12/2017.
 */

public class CineShared extends Application {

    private DatabaseReference usuarioBD;
    private FirebaseAuth autenticacionFirebase;

    @Override
    public void onCreate() {
        super.onCreate();

        /* Picasso */
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

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
