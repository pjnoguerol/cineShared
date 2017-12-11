package com.cineshared.pjnogegonzalez.cineshared.historico;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.peliculas.PeliculasComprobacion;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Clase FragmentHistoricoIntercambio contiene las acciones relativas al histórico de intercambios
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class FragmentHistoricoIntercambio extends Fragment {

    // Definimos las variables necesarias
    private ConversionJson<PeliculasComprobacion> conversionJson = new ConversionJson<>(Constantes.PELICULAS_CHECK);
    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private Usuarios usuario;

    // Constructor vacío
    public FragmentHistoricoIntercambio() {
    }

    /**
     * Método onCreateView se ejecuta al inicializarse el fragmento
     *
     * @param inflater           Layout donde se cargará el fragmento
     * @param container          Contenedor de dicho layout
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     * @return Vista con el fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity_radio, container, false);
        usuario = (Usuarios) getArguments().getSerializable("usuarios");
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_grupo);
        RadioButton rb1 = (RadioButton) rootView.findViewById(R.id.radio_todas);
        rb1.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Comprobamos cual de los radio buttons está activo y construimos la query correspondiente
                RadioButton radioButtonHistorico = (RadioButton) getActivity().findViewById(checkedId);
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("usuariohistorico", usuario.getId_usua() + "");
                if (radioButtonHistorico.getText().equals("Todas")) {
                    builder.appendQueryParameter("estadohistorico", "3");
                } else if (radioButtonHistorico.getText().equals("Abiertas")) {
                    builder.appendQueryParameter("estadohistorico", "1");
                } else if (radioButtonHistorico.getText().equals("Cerradas")) {
                    builder.appendQueryParameter("estadohistorico", "2");
                }
                cargarHistorico(builder);
            }
        });

        //Cargamos el usuario si existiera
        if (usuario != null)
            conversionJson.setUsuario(usuario);

        //Cargamos el tipo de recylerView
        recyclerView = conversionJson.onCreateViewHistorico(context, rootView, getResources());

        //Cargamos los parametros para el POST
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("usuariohistorico", usuario.getId_usua() + "")
                .appendQueryParameter("estadohistorico", "3");
        cargarHistorico(builder);

        return rootView;
    }

    /**
     * Cargamos el histórico de intercambios del usuario en el fragment
     *
     * @param builder Uri builder
     */
    private void cargarHistorico(Uri.Builder builder) {
        {
            try {
                String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    HiloGenerico<PeliculasComprobacion> hilo = new HiloGenerico<>(builder);
                    hilo.setActivity(getActivity());
                    hilo.setRecyclerView(recyclerView);
                    hilo.setTipo(1);
                    hilo.setTipoObjeto(Constantes.PELICULAS_CHECK);
                    hilo.setConversionJson(conversionJson);
                    hilo.execute(new URL(url)).get();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
