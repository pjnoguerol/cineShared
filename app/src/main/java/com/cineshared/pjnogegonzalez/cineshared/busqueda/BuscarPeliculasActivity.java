package com.cineshared.pjnogegonzalez.cineshared.busqueda;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.AccionesFirebase;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Clase BuscarPeliculasActivity gestiona las acciones relacionadas con activity_buscar_peliculas.xml
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class BuscarPeliculasActivity extends AppCompatActivity {

    // Definimos las variables
    private static Button btBusquedaNatural;
    private EditText txtBusqueda;
    private Toolbar mToolbar;
    private Usuarios usuario;

    private FirebaseAuth autenticacionFirebase;
    private DatabaseReference referenciaBD;

    /**
     * Método onCreate controla las acciones del formulario de búsqueda
     *
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_peliculas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializamos los objetos de la pantalla para poder interactuar con ellos
        usuario = (Usuarios) getIntent().getSerializableExtra("usuarios");
        btBusquedaNatural = (Button) findViewById(R.id.btBusquedaNatural);
        txtBusqueda = (EditText) findViewById(R.id.userBusqueda);
        referenciaBD = FirebaseDatabase.getInstance().getReference();
        autenticacionFirebase = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Buscador");

        btBusquedaNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = establecerFragmeto(0);
                generarFragmento(fragment);
            }
        });
    }

    /**
     * Método establecerFragmeto establece el fragmento para cargar
     *
     * @param busquedaRealizar Define qué búsqueda realizar
     * @return Fragmento a mostrar
     */
    private Fragment establecerFragmeto(int busquedaRealizar) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        if (busquedaRealizar == 0) {
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

    /**
     * Método generarFragmento genera el fragmento a mostrar
     *
     * @param fragment Fragmento a mostrar
     */
    private void generarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_busqueda2, fragment).commit();

    }

    /**
     * Método onResumen se ejecuta cada vez que la actividad se inicia
     */
    @Override
    public void onResume() {
        super.onResume();
        AccionesFirebase.establecerUsuarioOnline(autenticacionFirebase, referenciaBD.child(Constantes.USUARIOS_FIREBASE));
    }

    /**
     * Método onPause gestiona las acciones cuando se pausa la aplicación
     */
    @Override
    public void onPause() {
        super.onPause();
        AccionesFirebase.establecerUsuarioOffline(autenticacionFirebase, referenciaBD.child(Constantes.USUARIOS_FIREBASE));
    }
}
