package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

/**
 * Created by informatica on 21/11/2017.
 */

public class HiloGenerico <T> extends AsyncTask<URL, Void, List<T>> {
    private Activity activity;
    private String tipoObjeto;
    private RecyclerView recyclerView;
    private Uri.Builder builder;
    private ConversionJson<T> conversionJson;
    private List<T> listaGenerica;
    private int tipo;
    public HiloGenerico(Uri.Builder builder)
    {
        this.builder = builder;
    }

    public HiloGenerico()

    {

    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public Uri.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Uri.Builder builder) {
        this.builder = builder;
    }

    public ConversionJson<T> getConversionJson() {
        return conversionJson;
    }

    public void setConversionJson(ConversionJson<T> conversionJson) {
        this.conversionJson = conversionJson;
    }



    public List<T> getListaGenerica() {
        return listaGenerica;
    }

    public void setListaGenerica(List<T> listaGenerica) {
        this.listaGenerica = listaGenerica;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    @Override
    protected List<T> doInBackground(URL... url) {
        return (listaGenerica=conversionJson.doInBackgroundPost(url[0],builder));
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPostExecute(List<T> lista) {
        /**
        if (tipo==0)
        {
            Usuarios usuario = (Usuarios) lista.get(0);
            if (usuario != null) {
                if (usuario.isOk()) {
                    Toast.makeText(activity, "Insertado correctamente", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, usuario.getError(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, Constantes.ERROR_JSON, Toast.LENGTH_SHORT).show();
            }
        }
         **/
        if (tipo==1)
        {
            recyclerView.setAdapter(conversionJson.onPostExecute(lista));
        }


    }
}
