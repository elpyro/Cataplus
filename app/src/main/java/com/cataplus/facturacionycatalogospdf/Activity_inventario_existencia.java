package com.cataplus.facturacionycatalogospdf;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//import com.cataplus.facturacionycatalogospdf.databinding.ActivityInventarioExistenciaBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Activity_inventario_existencia  extends AppCompatActivity implements  PDFUtility_inventario_existencia.OnDocumentClose {

    public static String foto_vacia;
    public static Double Total_inventario_existencia=0.00;
    public static String catalogo="";
    private static final String TAG = MainActivity.class.getSimpleName();
    private AppCompatEditText rowCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foto_vacia="android.resource://"+  getPackageName() + "/drawable/agregar_foto";
        try
        {

            String path = Environment.getExternalStorageDirectory().toString() + "/Reporte_CATAPLUS.pdf";

            PDFUtility_inventario_existencia.createPdf(this,Activity_inventario_existencia.this, getSampleData(),path,true);
        }
        catch (Exception e)
        {
            Principal.progressDialog.cancel();
            e.printStackTrace();
            Log.e(TAG,"Error Creating Pdf");
            Toast.makeText(this,getString(R.string.Error_creando_pdf)+e,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onPDFDocumentClose(File file)
    {


//LLAMAR OTRO ACTIVITY PARA MOSTRARLO EN PANTALLA
        finish();

        Intent intent=new Intent(this,Vista_pdf.class);
        intent.putExtra("path",file.getAbsolutePath());
        startActivity(intent);
        Principal.progressDialog.cancel();

    }


    private List<ProductosModelo> getSampleData()
    {

        List<String[]> temp = new ArrayList<>();
        List<ProductosModelo> temp2 = new ArrayList<>();

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();
        Cursor consulta;
        consulta = BD.rawQuery("SELECT * FROM productos ORDER BY estado, categoria, nombre", null);


        if (consulta.moveToFirst()) {
            do {
                String temp_id = consulta.getString(0);
                String temp_nombre = consulta.getString(1);
                String temp_codigo=consulta.getString(2);
                String temp_categoria=consulta.getString(3);
                String temp_seguimiento = consulta.getString(4);
                String  temp_cantidad = consulta.getString(5);
                String  temp_medida = consulta.getString(6);
                String temp_costo = consulta.getString(7);
                String temp_mayor = consulta.getString(8);
                String  temp_detal = consulta.getString(9);
                String temp_estado = consulta.getString(10);
                String temp_foto1 = consulta.getString(11);
                String temp_descripcion= consulta.getString(18);
                String temp_proveedor= consulta.getString(19);
                String temp_talla= consulta.getString(20);
                String temp_color= consulta.getString(21);

                //agregar array de imagenes.
                byte[] imagen=consulta.getBlob(23);



                if(temp_estado.equals("Activo")) {
                    if(temp_seguimiento.equals("Si")){
                        int cantidad= Integer.parseInt(temp_cantidad);

                            temp2.add(new ProductosModelo(temp_id, temp_nombre, temp_codigo, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen));
                            Total_inventario_existencia= Total_inventario_existencia+(Double.valueOf(temp_costo)*Double.valueOf(temp_cantidad));

                    }
                }
            } while (consulta.moveToNext());

        }

        BD.close();
        return  temp2;
    }

}