package com.cataplus.facturacionycatalogospdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;


//pdf
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatEditText;

import com.cataplus.facturacionycatalogospdf.R;

//CREAR PDF DE CATALOGOS
public class MainActivity extends AppCompatActivity implements PDFUtility.OnDocumentClose{
    public static String foto_vacia;
    public static String catalogo="";
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    foto_vacia="android.resource://"+  getPackageName() + "/drawable/agregar_foto";
                try
                {

                   String path = Environment.getExternalStorageDirectory().toString() + "/Catalogo.pdf";

                   PDFUtility.createPdf(this,MainActivity.this, getSampleData(),path,true);
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



            String descripcion="";
            String proveedor="";
            String color="";
            String talla="";
            String medida="";

            if(Fragmento_catalogos.checkBox_descripcion.isChecked()){
                descripcion="\n"+temp_descripcion;
            }
            if(Fragmento_catalogos.checkBox_proveedor.isChecked()){
                proveedor="\n"+temp_proveedor;
            }
            if(Fragmento_catalogos.checkBox_color.isChecked()){
                color="\n"+temp_color;
            }
            if(Fragmento_catalogos.checkBox_talla.isChecked()){
                talla="\n"+temp_talla;
            }
            if(Fragmento_catalogos.checkBox_medida.isChecked()){
                medida="\n"+temp_medida;
            }

            temp_nombre=temp_nombre+descripcion+proveedor+color+talla+medida;

            String primer_caracter= String.valueOf((temp_categoria.charAt(0)));
            primer_caracter=primer_caracter.toUpperCase();
            primer_caracter=primer_caracter+temp_id;


            if (Fragmento_catalogos.radioButton_catalogo_detal.isChecked()){
                catalogo=getString(R.string.catalogo_detal);
            }else if(Fragmento_catalogos.radioButton_catalogo_mayor.isChecked()){
                catalogo=getString(R.string.catalogo_mayor);
            }


            if(temp_estado.equals("Activo")) {
                if(temp_seguimiento.equals(getString(R.string.No))){
                    temp_cantidad="";
                    temp2.add(new ProductosModelo(primer_caracter, temp_nombre, temp_codigo, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen));
                }else{
                    int cantidad= Integer.parseInt(temp_cantidad);
                    if (cantidad>0){
                        temp2.add(new ProductosModelo(primer_caracter, temp_nombre, temp_codigo, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen));
                    }
            }




            }
        } while (consulta.moveToNext());

    }
        BD.close();
        return  temp2;
    }

}