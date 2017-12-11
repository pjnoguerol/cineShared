package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Clase MarcadorPicasso
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class MarcadorPicasso implements Target {

    Marker mMarker;
    MarkerOptions markerOptions;

    public MarcadorPicasso(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    @Override
    public int hashCode() {
        return markerOptions.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MarcadorPicasso) {
            MarkerOptions markerOptions = ((MarcadorPicasso) o).markerOptions;
            return mMarker.equals(markerOptions);
        } else {
            return false;
        }
    }

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
}