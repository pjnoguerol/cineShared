package com.cineshared.pjnogegonzalez.cineshared.ubicacion;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cineshared.pjnogegonzalez.cineshared.R;
import com.cineshared.pjnogegonzalez.cineshared.acceso.Usuarios;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Constantes;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.ConversionJson;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.HiloGenerico;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.TransformacionCirculo;
import com.cineshared.pjnogegonzalez.cineshared.utilidades.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    private Context contexto;
    private SupportMapFragment soporteMapa;
    private static GoogleMap mapaGoogle;
    private MarkerOptions opcionesMarcador;
    private Marker marcadorMapa;
    private Circle circuloPosicion;
    private Usuarios usuario;
    private Target tar;
    private LatLng posicion;

    /**
     * Metodo que carga los usuarios en el maps
     */
    private void insertarUsuarios() {
        try {


            //Creamos la peticion POST con los parametros deseados
            String url = Constantes.SERVIDOR + Constantes.RUTA_CLASE_PHP;
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
           // LatLng posicion;
            if (networkInfo != null && networkInfo.isConnected()) {

                Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("usuacoord", usuario.getUsuario() );
                //Cargamos el hilo con los parametros paraejecutarlo
                Log.w("usuariocoord", builder.toString());
                HiloGenerico<Usuarios> hilo = new HiloGenerico<>(builder);
                hilo.setActivity(getActivity());
                hilo.setTipoObjeto(Constantes.USUARIOS);
                hilo.setConversionJson(new ConversionJson<Usuarios>(Constantes.USUARIOS));
                List<Usuarios> resultado = hilo.execute(new URL(url)).get();

                    if (usuario!=null)
                    {

                        final Usuarios user = resultado.get(0);
                        tar =
                       //Target picassoMarker = new PicassoMarker(opcionesMarcador);
                       new Target() {
                           /**
                            * Cargamos la imagen cuando este lista
                            * @param bitmap
                            * @param from
                            */
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                //Captamos las coordenadas del usuario
                                posicion = new LatLng(user.getLatitud(), user.getLongitud());

                                //Añadimos los marcadores
                                opcionesMarcador = new MarkerOptions().position(posicion).title(user.getUsuario()).snippet(user.getUsuario());
                                opcionesMarcador.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                marcadorMapa = mapaGoogle.addMarker(opcionesMarcador);
                                //Añadimos el circulo alrededor en funcion de la distancia
                                circuloPosicion = mapaGoogle.addCircle(new CircleOptions()
                                        .center(posicion)
                                        .radius(Utilidades.convertirMillasKilometros(usuario.getDistancia(), true) * 1000f)
                                        .strokeWidth(10)
                                        .strokeColor(R.color.colorPrimaryDark)
                                        .fillColor(R.color.colorPrimary)
                                        .clickable(false));
                                mapaGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 7f));



                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        //Añadimos la imagen y los demas datos
                        Picasso.with(getActivity()).load(Constantes.RUTA_IMAGEN+usuario.getImagen()).transform(new TransformacionCirculo()). resize(100, 100).into(tar);

                        //marker = new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario());
                        //marker =googleMap.addMarker(new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario()));

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
        mapaGoogle = null;
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
        contexto = getActivity();
        usuario = (Usuarios) getArguments().getSerializable(Constantes.USUARIOS);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        soporteMapa = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.mapaPosicion, soporteMapa).commit();
        soporteMapa.getMapAsync(this);


    }

    /**
     * Médido que se ejecuta cuando el mapa está listo para comprobar que se tienen los permisos necesarios
     * para acceder a la posición del usuario
     *
     * @param mapaGoogle Google Map a actualizar
     */
    @Override
    public void onMapReady(GoogleMap mapaGoogle) {
        this.mapaGoogle = mapaGoogle;
        if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        insertarUsuarios();


        //obtenerMapaConPosiciones();
        //insertarUsuarios();
    }

    /**
     * Método obtenerMapaConPosiciones obtiene la posición de los usuarios y los muestra en un mapa
     */
    private void obtenerMapaConPosiciones() {
        LatLng posicion = new LatLng(usuario.getLatitud(), usuario.getLongitud());
        opcionesMarcador = new MarkerOptions().position(posicion).title(usuario.getUsuario()).snippet(usuario.getUsuario());
        marcadorMapa = mapaGoogle.addMarker(opcionesMarcador);
        circuloPosicion = mapaGoogle.addCircle(new CircleOptions()
                .center(posicion)
                .radius(Utilidades.convertirMillasKilometros(usuario.getDistancia(), true) * 1000f)
                .strokeWidth(10)
                .strokeColor(R.color.colorPrimaryDark)
                .fillColor(R.color.colorPrimary)
                .clickable(false));
        mapaGoogle.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 7f));
    }
}