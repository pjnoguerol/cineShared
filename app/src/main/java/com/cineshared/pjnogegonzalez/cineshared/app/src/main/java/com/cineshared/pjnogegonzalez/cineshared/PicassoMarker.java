package com.cineshared.pjnogegonzalez.cineshared;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by informatica on 08/12/2017.
 */

public class PicassoMarker implements Target {
    Marker mMarker;
    MarkerOptions markerOptions;

    PicassoMarker(MarkerOptions markerOptions) {
        //mMarker = marker;
        this.markerOptions = markerOptions;

    }

    @Override
    public int hashCode() {
        return markerOptions.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PicassoMarker) {
            MarkerOptions markerOptions = ((PicassoMarker) o).markerOptions;
            return mMarker.equals(markerOptions);
        } else {
            return false;
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
       // mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
        ;
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}