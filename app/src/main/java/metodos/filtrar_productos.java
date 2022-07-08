package metodos;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cataplus.facturacionycatalogospdf.AdminSQLiteOpenHelper;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


public class filtrar_productos extends Activity  {





    public static List<ProductosModelo> filtrar(Context context, String buscar_codigo)  {
         Cursor consulta;
         List<ProductosModelo> lista_productos = new ArrayList<>();
        lista_productos.clear();

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        if (buscar_codigo.equals("")) {
            consulta = BD.rawQuery(

                    "SELECT * FROM productos ORDER BY estado, categoria, nombre", null);
        }else{
            consulta = BD.rawQuery(
                    "SELECT * FROM productos WHERE codigo= '" + buscar_codigo + "' ORDER BY estado, categoria, nombre", null);

        }

        if (consulta.moveToFirst()) {
            do {
                String temp_id = consulta.getString(0);
                String temp_nombre = consulta.getString(1);
                String temp_barras = consulta.getString(2);
                String temp_categoria = consulta.getString(3);
                String temp_seguimiento = consulta.getString(4);
                String  temp_cantidad = consulta.getString(5);
                String temp_costo = consulta.getString(7);
                String temp_mayor = consulta.getString(8);
                String  temp_detal = consulta.getString(9);
                String temp_estado = consulta.getString(10);
                String temp_foto1 = consulta.getString(11);

                byte[] imagen_byte = consulta.getBlob(23);



                lista_productos.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1,imagen_byte));
            } while (consulta.moveToNext());
        }

        BD.close();

        return lista_productos;
    }


}
