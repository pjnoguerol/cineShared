package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;

import java.net.URL;
import java.util.List;

/**
 * Clase HiloGenerico contiene las utilidades de las tareas asíncronas del proyecto
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class HiloGenerico <T> extends AsyncTask<URL, Void, List<T>> {

    private Activity activity;
    private String tipoObjeto;
    private RecyclerView recyclerView;
    private Uri.Builder builder;
    private ConversionJson<T> conversionJson;
    private List<T> listaGenerica;
    private int tipo;
    private ProgressDialog carga;
    public HiloGenerico(Uri.Builder builder)
    {
        this.builder = builder;
    }

    // Constructor vacío
    public HiloGenerico()
    {
    }

    // Métodos getter y setter
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

    /**
     * Método onPreExecute se ejecuta antes de realizar cualquier otra acción
     */
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        carga = new ProgressDialog(this.activity);
        carga.setMessage("Cargando...");
        carga.show();
    }

    /**
     * Método doInBackground ontieme la lista de la URL dada
     *
     * @param url URL de la que obtener la lista
     * @return Lista de objetos
     */
    @Override
    protected List<T> doInBackground(URL... url) {
        return (listaGenerica=conversionJson.doInBackgroundPost(url[0],builder));
    }

    /**
     * Método onPostExecute se ejecuta despúes de realizar cualquier otra acción
     *
     * @param lista lista sobre la que aplicar el adaptador
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onPostExecute(List<T> lista) {

        carga.dismiss();
        if (tipo==1)
        {
            recyclerView.setAdapter(conversionJson.onPostExecute(lista));
        }
    }
}
