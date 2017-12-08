package com.cineshared.pjnogegonzalez.cineshared;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuscarPeliculasActivity extends AppCompatActivity {
    public static Button btbusqueda;
    public static Button btBusquedaNatural;
    EditText txtBusqueda;
    private Toolbar mToolbar;
    private FirebaseAuth autenticacionFirebase;
    private DatabaseReference referenciaBD;

    Usuarios usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_peliculas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btbusqueda = (Button) findViewById(R.id.btBusqueda);
        usuario = (Usuarios) getIntent().getSerializableExtra("usuarios");
        btBusquedaNatural = (Button) findViewById(R.id.btBusquedaNatural);
        //btbusqueda.setVisibility(View.GONE);
        txtBusqueda = (EditText) findViewById(R.id.userBusqueda);

        referenciaBD = FirebaseDatabase.getInstance().getReference();
        autenticacionFirebase = FirebaseAuth.getInstance();

        //Cargamos el action BAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Buscador");

        btbusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Establecemos el fragmento con MODO 0 para Busqueda por API

            }
        });

        btBusquedaNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = establecerFragmeto(0);
                generarFragmento(fragment);
            }
        });
    }

    //Establecemos el fragmento para cargar
    private Fragment establecerFragmeto(int modo) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(Constantes.USUARIOS, usuario);
        if (modo == 0) {
            bundle.putString("busqueda", txtBusqueda.getText().toString());
            bundle.putSerializable(Constantes.USUARIOS, usuario);
            fragment = new BusquedaFragment();
        } else {
            bundle.putString("natural", txtBusqueda.getText().toString());
            bundle.putSerializable(Constantes.USUARIOS, usuario);
            fragment = new BusquedaNaturalFragment();
        }


        fragment.setArguments(bundle);
        return fragment;
    }

    //Generamos el fragmento a mostrar
    private void generarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_busqueda2, fragment).commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(autenticacionFirebase, referenciaBD);
    }

    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(autenticacionFirebase, referenciaBD);
    }
}
