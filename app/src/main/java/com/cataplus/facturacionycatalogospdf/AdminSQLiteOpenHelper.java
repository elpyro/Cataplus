package com.cataplus.facturacionycatalogospdf;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {


    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREA LA TABLA Y LOS ELEMENTOS, SI YA ESTA CREADA NO HACE NADA.
        db.execSQL("CREATE TABLE productos(" +
                "id_producto  INTEGER PRIMARY KEY," +//0
                " nombre text," +//1
                "codigo text," +//2
                "categoria text," +//3
                "seguimiento text," +//4
                "cantidad int," +//5
                "medida text," +//6
                "p_costo text," +//7
                "p_mayor text," +//8
                "p_detal text," +//9
                "estado text," +//10
                "foto1 text," +//11
                "foto2 text," +
                "foto3 text," +
                "foto4 text," +
                "oferta int," +//15
                "fecha_modificacion text," +//16
                "usuario_modificacion text," +//17
                "descripcion text," +//18
                "proveedor text,"+//19
                "talla text," +//20
                "color text,"+//21
                "id_catalogo text,"+//22
                "imagen blob)");//23

        db.execSQL("CREATE TABLE clientes(" +
                "id_cliente  INTEGER PRIMARY KEY," +//0
                " nombre text," +//1
                " documento text," +//2
                " correo text," +//3
                " telefono1 text," +//4
                " telefono2 text," +//5
                " pais text," +//6
                " ciudad  text," +//7
                " direccion text," +//8
                " fecha_modificacion text," +//9
                " usuario_modificacion text)");//10


        db.execSQL("CREATE TABLE empresa(" +
                "id_empresa  INTEGER PRIMARY KEY," +//0
                " nombre text," +//1
                " documento text," +//2
                " correo text," +//3
                " dominio text,"+//4
                " telefono1 text," +//5
                " telefono2 text," +//6
                " pais text," +//7
                " ciudad  text," +//8
                " direccion text," +//9
                " fecha_modificacion text," +//10
                " usuario_modificacion text,"+ //11
                " logo text,"+//12
                "imagen blob)");//13

        db.execSQL("CREATE TABLE factura_detal(" +
                "id_factura  INTEGER PRIMARY KEY," +//0
                " id_empresa text," +//1
                " estado text," +//2
                " fecha date,"+//3
                " id_usuario text," +//4
                " impuesto text," +//5
                " descuento text," +//6
                " envio text," +//7
                " nombre text," +//8
                " documento text," +//9
                " correo text," +//10
                " telefono1 text," +//11
                " telefono2 text," +//12
                " pais text," +//13
                " ciudad  text," +//14

                " direccion direccion)");//15


        db.execSQL("CREATE TABLE producto_factura_detal(" +
                "id_producto  INTEGER PRIMARY KEY," +//0
                " id_factura text," +//1
                " nombre_producto text," +//2
                " precio_producto text," +//3
                " foto1_producto text," +//4
                " barras_producto text," +//5
                " cantidad text," +//6
                "id_producto_tabla_productos text," +//7
                "imagen blob," +//8
                "tipo text," +//9
                "p_costo text," +//10
                "fecha date," +//11
                "descuento text)");//12
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
