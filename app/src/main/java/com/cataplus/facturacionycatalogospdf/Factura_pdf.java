package com.cataplus.facturacionycatalogospdf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

//pdf
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cataplus.facturacionycatalogospdf.R;

public class Factura_pdf extends AppCompatActivity implements PDFUtility_factura.OnDocumentClose{
    public static String foto_vacia;
    private static final String TAG = Factura_pdf.class.getSimpleName();
    public List<String[]> temp_datos_cliente = new ArrayList<>();
    public String ultima_factura="";
    private String id_factura;
    private List<byte[]> imagen_byte = new ArrayList<>();
    private String telefono="";
    private List<String[]> id_productos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foto_vacia= "android.resource://"+  getPackageName() + "/drawable/agregar_foto";
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            if(extras.getString("id_factura")!=null){
                id_factura=extras.getString("id_factura");

            }
        }


        Fragment_factura_pdf.checkBox_informacion_superior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Fragment_factura_pdf.checkBox_informacion_superior.isChecked()){
                    Fragment_factura_pdf.informacion_superior.setVisibility(View.VISIBLE);
                }else{
                    Fragment_factura_pdf.informacion_superior.setVisibility(View.GONE);
                }
            }
        });
        Fragment_factura_pdf.checkBox_informacion_inferior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Fragment_factura_pdf.checkBox_informacion_inferior.isChecked()){
                    Fragment_factura_pdf.informacion_inferior.setVisibility(View.VISIBLE);
                }else{
                    Fragment_factura_pdf.informacion_inferior.setVisibility(View.GONE);
                }
            }
        });
        generar_pdf(null);
    }


    public void generar_pdf(View v){

        Lista_datos_cliente();
        Lista_productos_facturados();
        try
        {
            String path = Environment.getExternalStorageDirectory().toString() + "/Reporte_CATAPLUS.pdf";
            imagenes();
            PDFUtility_factura.createPdf(this,Factura_pdf.this,imagen_byte,Lista_productos_facturados(),temp_datos_cliente, Lista_cabezera(),path,true);
            imagen_byte.clear();
            id_productos.clear();
            temp_datos_cliente.clear();
        }
        catch (Exception e)
        {
            Principal.progressDialog.cancel();
            e.printStackTrace();
            Log.e(TAG,"Error Creating Pdf");

        }
    }

    @Override
    public void onPDFDocumentClose(File file)
    {
        finish();
        //ABRI PDF EN ACTIVITY
        Intent intent=new Intent(this,Vista_pdf.class);
        intent.putExtra("path",file.getAbsolutePath());
        intent.putExtra("telefono", telefono);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Principal.progressDialog.cancel();
    }

    private List<String[]> Lista_productos_facturados()
    {
        List<String[]> temp = new ArrayList<>();




        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        Cursor consulta=BD.rawQuery("SELECT * FROM producto_factura_detal WHERE id_factura = '"+id_factura+"'",null);

        int x=0;
        if (consulta.moveToFirst()) {

            do {
                String nombre_producto = consulta.getString(2);
                String precio_producto = consulta.getString(3);
                String foto1_producto=consulta.getString(4);
                String barras_producto=consulta.getString(5);
                String cantidad = consulta.getString(6);

                id_productos.add(new String[]{consulta.getString(7)});

                double total=Double.parseDouble(precio_producto)*Double.parseDouble(cantidad);
                String Total_parceado= String.valueOf(total);
                x=x+1;

                if(Fragment_factura_pdf.checkBoximagenes.isChecked()&& Fragment_factura_pdf.checkBox_barras.isChecked()){
                    temp.add(new String[]{x+"", nombre_producto,cantidad,precio_producto,Total_parceado, foto1_producto,barras_producto});
                }else if (Fragment_factura_pdf.checkBoximagenes.isChecked()){
                    temp.add(new String[]{x+"", nombre_producto,cantidad,precio_producto,Total_parceado, foto1_producto});
                }else if(Fragment_factura_pdf.checkBox_barras.isChecked()){
                    temp.add(new String[]{x+"", nombre_producto,cantidad,precio_producto,Total_parceado, barras_producto});
                }else{
                    temp.add(new String[]{x+"", nombre_producto,cantidad,precio_producto,Total_parceado});
                }
            } while (consulta.moveToNext());
        }
            consulta.close();
            BD.close();




        return  temp;
    }

    private List<byte[]> imagenes(){
        //array imagenes
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();




        Cursor consulta;
        for (int p = 0; p < id_productos.size(); p++) {
            String[] imagenes = id_productos.get(p);

                consulta =BD.rawQuery("SELECT imagen FROM productos WHERE id_producto = '"+imagenes[0]+"'",null);

                if (consulta.moveToFirst()) {
                    if (!consulta.getBlob(0).equals(null)) {
                        byte[] imagen = consulta.getBlob(0);
                        imagen_byte.add(imagen);
                    }else{
                        imagen_byte.add(null);
                    }
                }
            consulta.close();
                consulta=null;

        }
        BD.close();
        return imagen_byte;
    }

    private List<String[]> Lista_cabezera()
    {
        List<String[]> temp_cabezera = new ArrayList<>();

        temp_cabezera.add(new String[]{"ID", getString(R.string.nombre), getString(R.string.Precio), getString(R.string.Foto), getString(R.string.Barras), getString(R.string.Cant), getString(R.string.SubTotal)});


        return temp_cabezera;
    }


    private List<String[]> Lista_datos_cliente()
    {

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();
        //ULITMO CLIENTE AGREAGDO
        Cursor consulta;
        consulta = BD.rawQuery("SELECT * FROM    factura_detal   WHERE   id_factura= '"+id_factura+"'", null);
        int x=0;
        if (consulta.moveToFirst()) {

                ultima_factura =consulta.getString(0);
                String id_factura =consulta.getString(0);
                String id_empresa =consulta.getString(1);
                String estado =consulta.getString(2);
                String fecha =consulta.getString(3);
                String id_usuario =consulta.getString(4);
                String impuesto =consulta.getString(5);
                String descuento =consulta.getString(6);
                String envio =consulta.getString(7);
                String nombre =consulta.getString(8);
                String documento =consulta.getString(9);
                String correo =consulta.getString(10);
                String telefono1 =consulta.getString(11);
                String telefono2 =consulta.getString(12);
                String pais =consulta.getString(13);
                String ciudad =consulta.getString(14);
                String direccion =consulta.getString(15);

                if (nombre.equals("")){
                    nombre="______________________";
                }
                if (documento.equals("")){
                    documento="____________";
                }
                if (telefono2.equals("")){
                    telefono2="_____";
                }
                if (telefono1.equals("")){
                    telefono1="_______________";
                }
                if(direccion.equals("")){
                    direccion="_________________________________________";
                }
            telefono=telefono2+telefono1;
            temp_datos_cliente.add(new String[]{id_factura,id_empresa,estado,fecha,id_usuario,descuento,impuesto,envio,nombre,documento,correo,telefono1,telefono2,pais,ciudad,direccion});

        }
        BD.close();
        return  temp_datos_cliente;
    }

}