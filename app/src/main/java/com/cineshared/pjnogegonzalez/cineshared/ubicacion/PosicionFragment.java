package com.cineshared.pjnogegonzalez.cineshared.ubicacion;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Clase PosicionFragment contiene las acciones relativas a la posición del usuario
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class PosicionFragment extends Fragment implements OnMapReadyCallback {

    // Definimos las variables
    private Context context;
    private SupportMapFragment supportMapFragment;
    private static GoogleMap googleMap;
    private MarkerOptions markerOptions;
    private Marker marker;

    /**
     * Iniciamos el fragmento instanciado en la vista del usuario
     *
     * @param inflater           Para ampliar cualquier vista en el fragmento
     * @param container          Contenedor al que incluir el fragmento (puede ser null)
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     * @return Vista del fragmento o null
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        googleMap = null;
        return inflater.inflate(R.layout.fragment_posicion, container, false);
    }

    /**
     * Método que inicializa el mapa
     *
     * @param savedInstanceState Si no es nulo, es porque el fragmento existía con anterioridad
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        supportMapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.mapaPosicion, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    /**
     * Médido que se ejecuta cuando el mapa está listo para comprobar que se tienen los permisos necesarios
     * para acceder a la posición del usuario
     *
     * @param googleMap Google Map a actualizar
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        obtenerMapaConPosiciones();
    }

    /**
     * Método obtenerMapaConPosiciones obtiene la posición de los usuarios y los muestra en un mapa
     */
    private void obtenerMapaConPosiciones() {
        try {
            String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            LatLng posicion;
            if (networkInfo != null && networkInfo.isConnected()) {
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("liscoord", "");
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(Constantes.USUARIOS));
                List<Usuarios> resultado = hilo.execute(new URL(url)).get();
                for (Usuarios usuario : resultado) {
                    if (usuario != null) {
                        posicion = new LatLng(usuario.getLatitud(), usuario.getLongitud());
                        markerOptions = new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario());
                        marker = googleMap.addMarker(markerOptions);
                    }
                }
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