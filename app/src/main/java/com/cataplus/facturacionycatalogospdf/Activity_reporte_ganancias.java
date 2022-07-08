package com.cataplus.facturacionycatalogospdf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cataplus.facturacionycatalogospdf.Utils.Fragmento_reporte_compra_venta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//CREAR PDF
public class Activity_reporte_ganancias extends AppCompatActivity implements PDFUtility_ganacias.OnDocumentClose{
    public static String foto_vacia;
    public static String catalogo="";
    private static final String TAG = Activity_reporte_ganancias.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    foto_vacia="android.resource://"+  getPackageName() + "/drawable/agregar_foto";
                try
                {

                   String path = Environment.getExternalStorageDirectory().toString() + "/Reporte_CATAPLUS.pdf";


                    List<String[]> temp = new ArrayList<>();
                    for (int i = 0; i < 10; i++)
                    {
                        temp.add(new String[] {"C1-R"+ (i+1),"C2-R"+ (i+1)});
                    }
                   PDFUtility_ganacias.createPdf(this, Activity_reporte_ganancias.this, temp,path,true);
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




}