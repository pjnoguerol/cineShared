package com.cineshared.pjnogegonzalez.cineshared.utilidades;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.cineshared.pjnogegonzalez.cineshared.chat.ConversacionActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Clase Utilidades contiene diversos métodos usados en varias áreas de la aplicación
 * <p>
 * Creada por Pablo Noguerol y Elena González
 */
public class Utilidades {

    private static int resultado;
    public static String auxPelicula;

    /**
     * Método calcularNumeroColumnas que calcula el número de columnas que caben con el detalle de las películas
     * dependiendo del tamaño de la pantalla
     *
     * @param context Contexto del activity que mostrará la lista de detalle
     * @return Número de columnas posibles
     */
    public static int calcularNumeroColumnas(Context context) {
        DisplayMetrics metricasPantalla = context.getResources().getDisplayMetrics();
        float dpAncho = metricasPantalla.widthPixels / metricasPantalla.density;
        int numeroColumnas = (int) (dpAncho / 110);
        return numeroColumnas;
    }

    // Métodos getter y setter
    public static void resultado(int resultado) {
        Utilidades.resultado = resultado;
    }

    public static int getResultado() {
        return Utilidades.resultado;
    }

    /**
     * Método acotar que recorta la cadena dada a un máximo de 25 caracteres si es necesario
     *
     * @param cadena Cadena a recordar si supera la longitud de 25 caracteres
     * @return Cadena con un máxmo de 25 caracteres o la misma cadena si tiene una longitud inferior
     */
    public static String acotar(String cadena) {
        int tamanyo = (cadena.length() < 25) ? cadena.length() : 25;
        String strFinal = cadena.substring(0, tamanyo);
        if (cadena.length() >= 25)
            strFinal += "..";
        return strFinal;

    }

    /**
     * Método capitalizarCadena deja la primera letra en mayúsculas y el resto normal
     *
     * @param cadena Cadena a capitalizar
     * @return Cadena capitalizada
     */
    public static String capitalizarCadena(String cadena) {
        if (cadena == null || cadena.isEmpty()) {
            return cadena;
        } else {
            return cadena.substring(0, 1).toUpperCase() + cadena.substring(1);
        }
    }

    /**
     * Método comprobarCamposUsuario comprueba si todos los datos obligatorios se han introducido
     * correctamente antes de dar de alta o modificar al usuario en la base de datos
     *
     * @param usuarioTxt   EditText donde está el nombre del usuario, será null si estamos modificandolo
     * @param passwordTxt  EditText donde está la contraseña del usuario
     * @param emailTxt     EditText donde está el email del usuario
     * @param telefonoTxt  EditText donde está el teléfono del usuario
     * @param distanciaTxt EditText donde está la distancia máxima del usuario
     * @param esInsert     true si es alta de usuario, false si es modificación
     * @return Booleano con el resultado de la validación
     */
    public static boolean comprobarCamposUsuario(EditText usuarioTxt, EditText passwordTxt, EditText emailTxt,
                                                 EditText telefonoTxt, EditText distanciaTxt, boolean esInsert) {
        boolean resultadoValidacion = true;
        if (esInsert && usuarioTxt != null && Constantes.CADENA_VACIA.equals(usuarioTxt.getText().toString().trim())) {
            usuarioTxt.setError("El usuario es obligatorio");
            resultadoValidacion = false;
        }
        if (!Utilidades.isPasswordValida(passwordTxt.getText().toString().trim())) {
            passwordTxt.setError("La contraseña debe contener letras y números. Longitud minima de 6");
            resultadoValidacion = false;
        }
        if (!Utilidades.isEmailValido(emailTxt.getText().toString().trim())) {
            emailTxt.setError("Email incorrecto");
            resultadoValidacion = false;
        }
        if (!Utilidades.isTelefonoValido(telefonoTxt.getText().toString().trim())) {
            telefonoTxt.setError("Teléfono con formato válido");
            resultadoValidacion = false;
        }
        if (Constantes.CADENA_VACIA.equals(distanciaTxt.getText().toString().trim())) {
            distanciaTxt.setError("Distancia máxima es obligatoria");
            resultadoValidacion = false;
        }
        return resultadoValidacion;
    }

    /**
     * Método isEmailValido comprueba si el email es correcto
     *
     * @param charSequenceToCheck Cadena para comprobar si es un mail válido
     * @return Resultado de la validación
     */
    public static boolean isEmailValido(CharSequence charSequenceToCheck) {
        if (TextUtils.isEmpty(charSequenceToCheck)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(charSequenceToCheck).matches();
        }
    }

    /**
     * Método isPasswordValida comprueba si la contraseña es correcta
     *
     * @param password Cadena para comprobar si la contraseña es correcta
     * @return Resultado de la validación
     */
    public static boolean isPasswordValida(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        } else {
            return password.length() >= 6 && password.matches("[a-zA-Z0-9]*");
        }
    }

    /**
     * Método isTelefonoValido comprueba si el teléfono es correcto
     *
     * @param charSequenceTelefono Cadena para comprobar si el teléfono es correcto
     * @return Resultado de la validación
     */
    public static boolean isTelefonoValido(CharSequence charSequenceTelefono) {
        if (TextUtils.isEmpty(charSequenceTelefono)) {
            return false;
        } else {
            return Patterns.PHONE.matcher(charSequenceTelefono).matches();
        }
    }

    /**
     * Método convertirMillasKilometros convierte las millas a kilómetros
     *
     * @param millas      Millas a convertir
     * @param pasarMetros True si se quiere pasar a metros, false si se deja en kilómetros
     * @return Kilómetros con dos decimales
     */
    public static float convertirMillasKilometros(float millas, boolean pasarMetros) {
        BigDecimal decimalKilometros = new BigDecimal(millas);
        decimalKilometros = decimalKilometros.multiply(new BigDecimal(1.609));
        if (pasarMetros)
            decimalKilometros = decimalKilometros.multiply(new BigDecimal(1000));
        return decimalKilometros.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public static void iniciarChat(DatabaseReference referenciaBDUsuarios, final String nombreUsuario, final Context contexto) {
        referenciaBDUsuarios.orderByChild(Constantes.NOMBRE_USUARIO).equalTo(nombreUsuario)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot != null) {
                            Intent conversacionIntent = new Intent(contexto, ConversacionActivity.class);
                            conversacionIntent.putExtra("identificadorUsuarioDestinatario", dataSnapshot.getKey().toString());
                            conversacionIntent.putExtra("nombreUsuario", nombreUsuario);
                            contexto.startActivity(conversacionIntent);
                        } else {
                            Toast.makeText(contexto, "Se ha producido un error al inicar Chat con esta persona", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}