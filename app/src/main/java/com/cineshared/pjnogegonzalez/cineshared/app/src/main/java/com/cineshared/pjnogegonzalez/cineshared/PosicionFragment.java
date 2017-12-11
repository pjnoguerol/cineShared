package com.cineshared.pjnogegonzalez.cineshared;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.squareup.picasso.Target;import com.squareup.picasso.Target;
/**
 * Clase que parsea la posición del usuario y la muestra en un mapa
 */
public class PosicionFragment extends Fragment implements OnMapReadyCallback {

    private Context context;
    private SupportMapFragment supportMapFragment;
    private static GoogleMap googleMap;
 private MarkerOptions markerOptions;
    Marker marker ;
    private void insertarUsuarios() {
        try {



            String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            LatLng posicion;
            if (networkInfo != null && networkInfo.isConnected()) {

                Uri.Builder builder = new Uri.Builder()
                          .appendQueryParameter("liscoord", "" );
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(getActivity(),Constantes.USUARIOS));
                List<Usuarios> resultado = hilo.execute(new URL(url)).get();
                for (Usuarios usuario: resultado)
                {
                    if (usuario!=null)
                    {
                        //urlImagen = Constantes.RUTA_IMAGEN + urlImagen;
                        posicion = new LatLng(usuario.getLatitud(), usuario.getLongitud());
                        markerOptions = new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario());

                        marker = googleMap.addMarker(markerOptions);

                       /* Target picassoMarker = new PicassoMarker(markerOptions);
                       Picasso.with(getActivity()).load("http://192.168.1.3/cineshared/img/xxxxx.jpg"). resize(100, 100).into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                        */

                        //marker = new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario());
                        //marker =googleMap.addMarker(new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario()));

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
        insertarUsuarios();
        //this.googleMap.setMyLocationEnabled(true);
        //this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}