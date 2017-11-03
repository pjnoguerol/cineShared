package com.cineshared.pjnogegonzalez.cineshared;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by pjnog on 08/08/2017.
 */

public class Utility {
    private static int resultado;
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 140);
        return noOfColumns;
    }

    public static void resultado(int resultado)
    {
        Utility.resultado = resultado;

    }
    public static int getResultado()
    {
        return Utility.resultado;
    }
}