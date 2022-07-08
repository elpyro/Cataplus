package com.cataplus.facturacionycatalogospdf.Utils;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.Activity_reporte_compraYventa;
import com.cataplus.facturacionycatalogospdf.Activity_reporte_ganancias;
import com.cataplus.facturacionycatalogospdf.AdminSQLiteOpenHelper;

import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.Vista_pdf;
import com.cataplus.facturacionycatalogospdf.modelos.InformeComprayventaModelo;
import com.cataplus.facturacionycatalogospdf.modelos.RegistroModelo;
import com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import modelos.ModeloProductoFactura;

public class Fragmento_reporte_compra_venta extends Fragment {

    private Button button_desde, button_hasta, button_crear_pdf;
    private RadioButton radioButton_compra, radioButton_mayor, radioButton_detal, radioButton_ganancias;
    public static String fecha_hasta, fecha_desde_busqueda;

    private int dia,mes,ano;
    private TextView editText_desde, editText_hasta;
    public static List<InformeComprayventaModelo> compraYventa=new ArrayList<>();
    public static double total=0.00;
    public static String tipo_busqueda="", tipo_busqueda2="", tipo_busqueda3="";


    public Fragmento_reporte_compra_venta() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista =inflater.inflate(R.layout.fragment_fragmento_reporte_compra_venta, container, false);

        button_desde=(Button)vista.findViewById(R.id.button_desde);
        button_hasta=(Button)vista.findViewById(R.id.button_hasta);
        button_crear_pdf=(Button)vista.findViewById(R.id.button_crear_pdf);
        radioButton_compra=(RadioButton)vista.findViewById(R.id.radioButton2);
        radioButton_mayor=(RadioButton)vista.findViewById(R.id.radioButton3);
        radioButton_ganancias=(RadioButton)vista.findViewById(R.id.radioButton_ganancias);
        radioButton_detal=(RadioButton)vista.findViewById(R.id.radioButton);
        editText_desde=(TextView) vista.findViewById(R.id.textView_desde);
        editText_hasta=(TextView) vista.findViewById(R.id.textView_hasta);

        fecha_hasta=null;

        fecha_desde_busqueda=null;
        
        button_crear_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Principal.progressDialog = ProgressDialog.show(getContext(), getString(R.string.Creando_pdf),
                        getString(R.string.Creando_pdf)  , true);


                //CONEXION A LA BASE DE DATOS
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
                SQLiteDatabase BD= admin.getWritableDatabase();

                Cursor consulta = null;

                if (radioButton_ganancias.isChecked()){


                    //filtramos todo y filtramos todos lo que sea dirente a compra(quemdamos con Detal y Mayor)

                    if (fecha_hasta != null & fecha_desde_busqueda == null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal   WHERE tipo  <>'" + "Compra" + "' AND tipo  <>'" + "Buy" + "' AND fecha <= '" + fecha_hasta + "' ORDER BY id_factura DESC", null);

                    } else if (fecha_hasta == null & fecha_desde_busqueda != null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal   WHERE tipo  <>'" + "Compra" + "' AND tipo  <>'" + "Buy" + "' AND fecha >= '" + fecha_desde_busqueda + "' ORDER BY id_factura DESC", null);

                    } else if (fecha_hasta != null & fecha_desde_busqueda != null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal  WHERE tipo  <>'" + "Compra" + "' AND tipo  <>'" + "Buy" + "' AND fecha BETWEEN  '" + fecha_desde_busqueda + "' and '" + fecha_hasta + "' ORDER BY  fecha", null);

                    } else if (fecha_hasta == null & fecha_desde_busqueda == null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal WHERE tipo  <>'" + "Compra" + "' AND tipo  <>'" + "Buy" + "' ORDER BY  fecha", null);
                    }

                    tipo_busqueda=getString(R.string.Reporte_ganancias);
                    if (compraYventa != null) compraYventa.clear();


                    if (consulta.moveToFirst()) {
                        do {
                            String id_producto = consulta.getString(0);
                            String id_factura_bd = consulta.getString(1);
                            String nombre_producto = consulta.getString(2);
                            String precio_producto = consulta.getString(3);
                            String foto1_producto = consulta.getString(4);
                            String barras_producto = consulta.getString(5);
                            String cantidad = consulta.getString(6);
                            String id_producto_tabla_productos = consulta.getString(7);
                            String tipo = consulta.getString(9);
                            String p_costo = consulta.getString(10);
                            String fecha = consulta.getString(11);
                            String descuento=consulta.getString(12);

                            compraYventa.add(new InformeComprayventaModelo(id_producto, id_factura_bd, nombre_producto, precio_producto, foto1_producto, barras_producto, cantidad, id_producto_tabla_productos, tipo, p_costo, fecha, descuento));

                        } while (consulta.moveToNext());
                    }

                    BD.close();
                    if (compraYventa.size()==0) {
                        Toast.makeText(getContext(), getString(R.string.No_hay_registros_para_mostar), Toast.LENGTH_LONG).show();
                        Principal.progressDialog.cancel();
                        return;
                    }
                    Intent intent=new Intent(getContext(), Activity_reporte_ganancias.class);
                    startActivity(intent);

                }else {
                    if (radioButton_compra.isChecked()) {
                        tipo_busqueda = getString(R.string.Compra);
                        tipo_busqueda2="Buy";
                        tipo_busqueda3="Compra";
                    };
                    if (radioButton_mayor.isChecked()){
                        tipo_busqueda = getString(R.string.Mayor);
                        tipo_busqueda2="Wholesale";
                        tipo_busqueda3="Mayor";
                    }
                    if (radioButton_detal.isChecked()) {
                        tipo_busqueda = getString(R.string.Detal);
                        tipo_busqueda2="Retail";
                        tipo_busqueda3="Detal";
                    }

                    if (fecha_hasta != null & fecha_desde_busqueda == null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal   WHERE (tipo  ='" + tipo_busqueda3 + "' OR tipo  ='" + tipo_busqueda2 + "') AND fecha <= '" + fecha_hasta + "' ORDER BY id_factura DESC", null);

                    } else if (fecha_hasta == null & fecha_desde_busqueda != null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal   WHERE (tipo  ='" + tipo_busqueda3 + "' OR tipo  ='" + tipo_busqueda2 + "') AND fecha >= '" + fecha_desde_busqueda + "' ORDER BY id_factura DESC", null);

                    } else if (fecha_hasta != null & fecha_desde_busqueda != null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal  WHERE (tipo  ='" + tipo_busqueda3 + "' OR tipo  ='" + tipo_busqueda2 + "') AND fecha BETWEEN  '" + fecha_desde_busqueda + "' and '" + fecha_hasta + "' ORDER BY  fecha", null);

                    } else if (fecha_hasta == null & fecha_desde_busqueda == null) {
                        consulta = BD.rawQuery(
                                "SELECT * FROM producto_factura_detal WHERE (tipo  ='" + tipo_busqueda3 + "' OR tipo  ='" + tipo_busqueda2 + "') ORDER BY  fecha", null);
                    }

                    if (compraYventa != null) compraYventa.clear();


                    if (consulta.moveToFirst()) {
                        do {
                            String id_producto = consulta.getString(0);
                            String id_factura_bd = consulta.getString(1);
                            String nombre_producto = consulta.getString(2);
                            String precio_producto = consulta.getString(3);
                            String foto1_producto = consulta.getString(4);
                            String barras_producto = consulta.getString(5);
                            String cantidad = consulta.getString(6);
                            String id_producto_tabla_productos = consulta.getString(7);
                            String tipo = consulta.getString(9);
                            String p_costo = consulta.getString(10);
                            String fecha = consulta.getString(11);
                            String descuento= consulta.getString(12);

                            double descuento_factura=Double.valueOf(consulta.getString(12))/100;
                            double precio= Double.valueOf(consulta.getString(3))-(Double.valueOf(consulta.getString(3))* descuento_factura) ;

                            double sub_total = (precio * Double.parseDouble(cantidad));
                            total = total + sub_total;
                            if (!radioButton_compra.isChecked()) p_costo=precio_producto;

                            compraYventa.add(new InformeComprayventaModelo(id_producto, id_factura_bd, nombre_producto, precio_producto, foto1_producto, barras_producto, cantidad, id_producto_tabla_productos, tipo, p_costo, fecha, descuento));

                        } while (consulta.moveToNext());


                    }


                    BD.close();

                    if (compraYventa.size()==0) {
                        Toast.makeText(getContext(), getString(R.string.No_hay_registros_para_mostar), Toast.LENGTH_LONG).show();
                        Principal.progressDialog.cancel();
                        return;
                    }

                    Intent intent=new Intent(getContext(), Activity_reporte_compraYventa.class);
                    startActivity(intent);
                }


                
            }
        });

        button_desde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c=Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                ano=c.get(Calendar.YEAR);

                DatePickerDialog datepickerDialogo = new DatePickerDialog (getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //se crean 2 campos de fecha desde por que la base de datos consigue los registros del mismo dia,  sino del antereior
                        month=month+1;
                        if(dayOfMonth<10){
                            fecha_desde_busqueda=year+"-"+month+"-"+"0"+dayOfMonth;
                        }else{
                            fecha_desde_busqueda=year+"-"+month+"-"+dayOfMonth;
                        }

                        editText_desde.setText(fecha_desde_busqueda);

                        editText_desde.setVisibility(View.VISIBLE);

                    }
                },ano,mes,dia);
                datepickerDialogo.show();


            }
        });

        button_hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                ano=c.get(Calendar.YEAR);

                DatePickerDialog datepickerDialogo = new DatePickerDialog (getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;

                        if(dayOfMonth<10){
                            fecha_hasta=year+"-"+month+"-"+"0"+dayOfMonth;
                        }else{
                            fecha_hasta=year+"-"+month+"-"+dayOfMonth;
                        }

                        editText_hasta.setText(fecha_hasta);
                        editText_hasta.setVisibility(View.VISIBLE);

                    }
                },ano,mes,dia);
                datepickerDialogo.show();


            }
        });



        //CARGA DE PUBLICIDAD
        AdView mAdView = vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);


        //PUBLICIDAD https://www.youtube.com/watch?v=7WwSCZLpMNc
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(),"ca-app-pub-5390342068041092/1274252406", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        HomeFragment.mInterstitialAd = interstitialAd;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        HomeFragment. mInterstitialAd = null;
                    }
                });


        return vista;
    }
}