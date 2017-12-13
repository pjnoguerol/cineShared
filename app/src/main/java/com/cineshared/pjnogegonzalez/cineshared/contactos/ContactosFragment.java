package com.cineshared.pjnogegonzalez.cineshared.contactos;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.*;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Clase que parsea la lista de contactos y los muestra en una CardView
 */
public class ContactosFragment extends Fragment {
    private List<String> listaTelefonos;
    private RecyclerView recyclerView;
    private ProgressDialog carga;
    private Usuarios usuario;
    private ConversionJson<Usuarios> conversionJson = new ConversionJson<>(Constantes.CONTACTOS);

    /**
     * Iniciamos el fragmento instanciado en la vista del usuario
     *
     * @param inflater           Para ampliar cualquier vista en el fragmento
     * @param container          Contenedor al que incluir el fragmento (puede ser null)
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     * @return Vista del fragmento o null
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = inflater.getContext();
        View rootView = inflater.inflate(R.layout.recyclerview_activity, container, false);
        recyclerView = conversionJson.onCreateViewHistorico(context, rootView, getResources());
        usuario = (Usuarios) getArguments().getSerializable(Constantes.USUARIOS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1540);
                new ContactosJsonTask().execute();


            }
            else
            {
                new ContactosJsonTask().execute();
            }


        return rootView;
    }
    private void CargarContactos()
    {
        try {
            String listaTelefonosString = android.text.TextUtils.join(",", listaTelefonos);
            String url = Constantes.SERVIDOR+Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                Uri.Builder  builder = new Uri.Builder()
                        .appendQueryParameter("contactos", listaTelefonosString);

                Log.w("builder", builder.toString());
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setRecyclerView(recyclerView);
                hilo.setTipo(1);
                hilo.setTipoObjeto(Constantes.CONTACTOS);
                hilo.setConversionJson(conversionJson);
                List <Usuarios>  resultado = hilo.execute(new URL(url)).get();
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
    /**
     * Inner class que parsea la lista de contactos a una CardView
     */
    public class ContactosJsonTask extends AsyncTask<Void, Void, List<String>> {

        private List<String> listaUsuarios;


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            carga = new ProgressDialog(getActivity());
            carga.setMessage("Cargando...");
            carga.show();
        }
        /**
         * Método que llama al parseo de contactos para obtener la lista a mostrar
         *
         * //@param urls URLs a conectar
         * @return Lista de contactos
         */
        @Override
        protected List<String> doInBackground(Void... vacio) {
            List<String> listaTelefonos = new ArrayList<>();
            ContentResolver contentResolver = getContext().getContentResolver();
            Cursor cursorContactos = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            // Antes de continuar se comprueba que existan contactos en el teléfono, y los recorremos todos
            if (cursorContactos.getCount() > 0) {
                while (cursorContactos.moveToNext()) {
                    String idContacto = cursorContactos.getString(cursorContactos.getColumnIndex(ContactsContract.Contacts._ID));
                    // Comprobamos si cada contacto tiene un número de teléfono asociado
                    if (cursorContactos.getInt(cursorContactos.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        // Obtenemos todos los teléfonos asociados a un contacto
                        Cursor cursorContacto = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{idContacto}, null);
                        while (cursorContacto.moveToNext()) {
                            String phoneNo = cursorContacto.getString(cursorContacto.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String telefono = "'" + phoneNo.replace("+34", "").replaceAll("[ ()-]", "") + "'";
                            if (usuario!=null )
                                if (!usuario.getTelefono().equals(telefono))
                                    listaTelefonos.add(telefono);
                        }
                        cursorContacto.close();
                    }
                }
            }
            return listaTelefonos;
        }

        /**
         * Método que asigna la lista de contactos al adaptador para obtener un cardView
         *
         * @param listaUsuarios Lista de contactos para el adaptador
         */
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(List<String> listaUsuarios) {
            carga.dismiss();
            listaTelefonos = listaUsuarios;
            CargarContactos();

        }
    }




}