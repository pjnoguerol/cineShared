package com.cineshared.pjnogegonzalez.cineshared;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BuscarPeliculasActivity extends AppCompatActivity {
    Button btbusqueda;
    EditText txtBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_peliculas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btbusqueda = (Button)findViewById(R.id.btBusqueda);
        //btbusqueda.setVisibility(View.GONE);
        txtBusqueda = (EditText)findViewById(R.id.userBusqueda);

        btbusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = establecerFragmeto();
                generarFragmento(fragment);
            }
        });



    }
    //Establecemos el fragmento para cargar
    private Fragment establecerFragmeto() {
        Fragment fragment;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(Constantes.USUARIOS, usuario);
        bundle.putString("busqueda", txtBusqueda.getText().toString());

        fragment = new BusquedaFragment();


        fragment.setArguments(bundle);
        return fragment;
    }
    //Generamos el fragmento a mostrar
    private void generarFragmento (Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_busqueda, fragment).commit();

    }

}
