package com.cineshared.pjnogegonzalez.cineshared;


import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;


/**
 * A simple {@link Fragment} subclass.
 * Clase fragment que lista el historico de intercambio
 */
public class FragmentHistoricoIntercambio extends Fragment {

    private ConversionJson<PeliculasComprobacion> conversionJson = new ConversionJson<PeliculasComprobacion>(getActivity(),Constantes.PELICULAS_CHECK);
    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private Usuarios usuario;
    //private Uri.Builder builder;
    public FragmentHistoricoIntercambio() {
        // Required empty public constructor
    }


    /**
     * Cargamos la biblioteca de usuario en el fragment
     * @param builder
     */
    private void cargarBiblioteca(Uri.Builder builder){
        {

            try {
                String url = Constantes.SERVIDOR+Constantes.RUTA_CLASE_PHP;
                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {


                    Log.w("builder", builder.toString());
                    HiloGenerico<PeliculasComprobacion> hilo = new HiloGenerico<>(builder);
                    hilo.setActivity(getActivity());
                    hilo.setRecyclerView(recyclerView);
                    hilo.setTipo(1);
                    hilo.setTipoObjeto(Constantes.PELICULAS_CHECK);
                    hilo.setConversionJson(conversionJson);
                    List <PeliculasComprobacion>  resultado = hilo.execute(new URL(url)).get();
                    //Comprobar que se ha insertado correctament
                    if (resultado.get(0).isOk()) {

                    }
                    else
                        Toast.makeText(getActivity(), resultado.get(0).getError(), Toast.LENGTH_SHORT).show();
                } else {

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // cargamos el contexto
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity_radio, container, false);
        usuario = (Usuarios) getArguments().getSerializable("usuarios");
        radioGroup = (RadioGroup)rootView.findViewById(R.id.radio_grupo);
        RadioButton rb1=(RadioButton)rootView.findViewById(R.id.radio_todas);
        rb1.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // comprobamos cual de los radios están activos
                RadioButton rb=(RadioButton)getActivity().findViewById(checkedId);
                Uri.Builder  builder = new Uri.Builder();
                builder.appendQueryParameter("usuariohistorico", usuario.getId_usua()+"");
                if(rb.getText().equals("Todas"))
                {
                    builder.appendQueryParameter("estadohistorico", "3");
                }
                else if (rb.getText().equals("Abiertas"))
                {
                    builder.appendQueryParameter("estadohistorico", "1");
                }
                else if (rb.getText().equals("Cerradas"))
                {
                    builder.appendQueryParameter("estadohistorico", "2");
                }
                cargarBiblioteca(builder);

            }
        });

        //Cargamos el usuario si existiera
        if (usuario!=null)
            conversionJson.setUsuario(usuario);

        //Cargamos el tipo de recylerView
        recyclerView = conversionJson.onCreateViewHistorico(context, rootView, getResources());
        String url = "";

        //Cargamos los parametros para el POST
        Uri.Builder  builder = new Uri.Builder()
                .appendQueryParameter("usuariohistorico", usuario.getId_usua()+"")
                .appendQueryParameter("estadohistorico", "3");
        cargarBiblioteca(builder);

        return rootView;
    }



}
