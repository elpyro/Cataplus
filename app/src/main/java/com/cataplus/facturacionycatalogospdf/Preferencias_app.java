package com.cataplus.facturacionycatalogospdf;
//https://www.youtube.com/watch?v=l9M_DSdeNXc

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import java.util.ArrayList;
import java.util.List;

import modelos.Modelo_empresa;


public class Preferencias_app {

    public static double impuesto_preferencia, descuento_preferencia, envio_preferencia;
    public static String informacion_superior, informacion_inferior,autoescaner;
    public static List<Modelo_empresa> datos_empresa = new ArrayList<>();
    public static boolean linterna, autofoco;

    public static void obtener_preferencias(SharedPreferences preferences, Context context){
        try{
            descuento_preferencia= Double.parseDouble(preferences.getString("descuento","0"));
            impuesto_preferencia= Double.parseDouble(preferences.getString("impuesto","0"));
            envio_preferencia= Double.parseDouble((preferences.getString("envio","0")));
            informacion_superior=preferences.getString("informacion_superior","");
            informacion_inferior=preferences.getString("informacion_inferior","");
            linterna=preferences.getBoolean("linterna",true);
            autofoco=preferences.getBoolean("autofoco",true);
            autoescaner=preferences.getString("autoescaner","Ninguno");
        }catch (Exception e){

        }

        try {
            //CONEXION A LA BASE DE DATOs
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context.getApplicationContext(), "basedatos",null,1);
            SQLiteDatabase BD= admin.getWritableDatabase();
            Cursor cursor=BD.rawQuery("SELECT * FROM empresa",null);

            if (cursor.moveToFirst()) {
                datos_empresa.clear();
                datos_empresa.add(new Modelo_empresa(cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(12),cursor.getBlob(13)));
            }else{
                String foto_empresa = "android.resource://"+ context.getPackageName() + "/drawable/agregar_foto";
                datos_empresa.add(new Modelo_empresa("", context.getString(R.string.app_name), "","","","","","","","",foto_empresa,null));
            }
        }catch (Exception e){

        }

    }
}
