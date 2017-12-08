package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Clase que gestiona la transformación de cualquier imagen en una imagen circular
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class TransformacionCirculo implements Transformation {

    /**
     * Método transform transforma la imagen en un círculo
     *
     * @param imagenTransformar Imagen a transformar en un círculo
     * @return BitMap con la imagen en formato circular
     */
    @Override
    public Bitmap transform(Bitmap imagenTransformar) {
        // Al ser un círculo, el ancho y el alto deben ser iguales, por tanto, escogemos el menor de ellos
        int tamanyo = Math.min(imagenTransformar.getWidth(), imagenTransformar.getHeight());
        // Establecemos las coordenadas de los primeros pixeles en ambas direcciones
        int tamX = (imagenTransformar.getWidth() - tamanyo) / 2;
        int tamY = (imagenTransformar.getHeight() - tamanyo) / 2;
        // Y creamos un bitmap cuadrado con la nueva dimensión establecida
        Bitmap bitmapCuadrado = Bitmap.createBitmap(imagenTransformar, tamX, tamY, tamanyo, tamanyo);
        // Llamamos al método recycle para evitar tener un problema de memoria en el proceso
        if (bitmapCuadrado != imagenTransformar) {
            imagenTransformar.recycle();
        }
        // Creamos otro bitmap con el tamaño nuevo
        Bitmap bitmapCuadradoFinal = Bitmap.createBitmap(tamanyo, tamanyo, imagenTransformar.getConfig());
        // Y creamos el canvas sobre el que
        Canvas canvas = new Canvas(bitmapCuadradoFinal);
        Paint herramientaPintado = new Paint();
        BitmapShader texturaObjeto = new BitmapShader(bitmapCuadrado, BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);
        herramientaPintado.setShader(texturaObjeto);
        herramientaPintado.setAntiAlias(true);

        // Establecemos el valor del radio, que es la mitad del tamaño definido. Y creamos
        // el círculo con las coordinadas y el radio definido, así como el objeto creado para dibujar
        // dicho círculo
        float radio = tamanyo / 2f;
        canvas.drawCircle(radio, radio, radio, herramientaPintado);
        // Nuevamente llamamos al método recycle para evitar problemas de memoria
        bitmapCuadrado.recycle();
        return bitmapCuadradoFinal;
    }

    /**
     * Método key devuelve el tipo de objeto
     *
     * @return Cadena con el tipo de objeto creado
     */
    @Override
    public String key() {
        return "circle";
    }
}