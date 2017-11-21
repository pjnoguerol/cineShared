package com.cineshared.pjnogegonzalez.cineshared;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

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
    private ConversionJson<T> conversionJson = new ConversionJson<T>(activity, tipoObjeto);
    private List<T> listaGenerica;
    private HiloGenerico(Uri.Builder builder)
    {
        this.builder = builder;
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



    @Override
    protected List<T> doInBackground(URL... url) {
        return (listaGenerica=conversionJson.doInBackgroundPost(url[0],builder));
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPostExecute(List<T> lista) {
        recyclerView.setAdapter(conversionJson.onPostExecute(lista));
    }
}
