package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Registro_Producto;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_registro;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_registro_AgregarProducto;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.modelos.RegistroModelo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class Fragmento_Registros extends Fragment {



    private RecyclerView recyclerViewRegistro;
    private RecyclerViewAdaptador_registro adaptador_registro;
    private EditText edittext_buscar;
    private TextView editText_desde, editText_hasta;
    private List<RegistroModelo> registro ;
    private Cursor consulta;
    private RadioButton radioButton_nombre, radioButton_factura, radioButton_fecha;
    private LinearLayout linearLayout_fecha, linearLayout_buscar;
    private Button button_buscar_fecha, button_desde, button_hasta;
    private String fecha_hasta, fecha_desde;

    private int dia,mes,ano;

    public Fragmento_Registros() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vista= inflater.inflate(R.layout.fragment_fragmento__registros, container, false);
        registro= new ArrayList<>();
        edittext_buscar=(EditText) vista.findViewById(R.id.editTextTextPersonName4);

        radioButton_nombre=(RadioButton)vista.findViewById(R.id.radioButton15);
        radioButton_factura=(RadioButton)vista.findViewById(R.id.radioButton17);
        radioButton_fecha=(RadioButton)vista.findViewById(R.id.radioButton16);
        linearLayout_fecha=(LinearLayout)vista.findViewById(R.id.linearLayout_fecha);
        linearLayout_buscar=(LinearLayout)vista.findViewById(R.id.linearLayout_buscar);
        editText_desde=(TextView) vista.findViewById(R.id.textView_desde);
        editText_hasta=(TextView) vista.findViewById(R.id.textView_hasta);
        button_buscar_fecha=(Button)vista.findViewById(R.id.button_buscar_fecha);
        button_desde=(Button)vista.findViewById(R.id.button_desde);
        button_hasta=(Button)vista.findViewById(R.id.button_hasta);


        recyclerViewRegistro= (RecyclerView) vista.findViewById(R.id.RecyclerView_registro);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewRegistro.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptador_registro = new RecyclerViewAdaptador_registro(obtener_registros());
       // adaptador_registro.setHasStableIds (true);
      //  recyclerViewRegistro.setHasFixedSize(true);
        recyclerViewRegistro.setItemViewCacheSize(20);
        recyclerViewRegistro.setDrawingCacheEnabled(true);
        recyclerViewRegistro.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewRegistro.setAdapter(adaptador_registro);

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        // RESUELVE EL ERROR Row too big to fit into CursorWindow requiredPos=0, totalRows=1; query: CUANDO SE GUARDA BLOB EN LA BASE DE DATOS
        //PEGAR EN EL MAIN ACTIVITY O ACTIVITY INICIAL
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }


        try{
            RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.clear();
        }catch (Exception e){ }

        radioButton_nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_fecha.setVisibility(View.GONE);
                linearLayout_buscar.setVisibility(View.VISIBLE);
                obtener_registros();
                adaptador_registro.notifyDataSetChanged();
            }
        });

        radioButton_factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_fecha.setVisibility(View.GONE);
                linearLayout_buscar.setVisibility(View.VISIBLE);
                obtener_registros();
                adaptador_registro.notifyDataSetChanged();
            }
        });
        radioButton_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_fecha.setVisibility(View.VISIBLE);
                linearLayout_buscar.setVisibility(View.GONE);
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
                        month=month+1;
                        if(dayOfMonth<10){
                            fecha_desde=year+"-"+month+"-"+"0"+dayOfMonth;
                        }else{
                            fecha_desde=year+"-"+month+"-"+dayOfMonth;
                        }



                        editText_desde.setText(fecha_desde);
                        filtrar_fecha();

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
                        filtrar_fecha();
                    }
                },ano,mes,dia);
                datepickerDialogo.show();


            }
        });


        edittext_buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                obtener_registros();
                adaptador_registro.notifyDataSetChanged();
            }
        });

        return vista;

    }

    public void filtrar_fecha(){
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        if (fecha_hasta!=null & fecha_desde==null){
            consulta = BD.rawQuery(
                    "SELECT * FROM factura_detal  WHERE fecha <= '"+fecha_hasta+"' ORDER BY id_factura DESC", null);

        }else if(fecha_hasta==null&fecha_desde!=null){
            consulta = BD.rawQuery(
                    "SELECT * FROM factura_detal  WHERE fecha >= '"+fecha_desde+"' ORDER BY id_factura DESC", null);

        }else if(fecha_hasta!=null & fecha_desde!=null){
            consulta = BD.rawQuery(
                    "SELECT * FROM factura_detal  WHERE fecha BETWEEN  '"+fecha_desde+"' and '"+fecha_hasta+"' ORDER BY id_factura DESC", null);

        }

        registro.clear();
        if (consulta.moveToFirst()){
            do {
                String id_factura = consulta.getString(0);
                String id_empresa = consulta.getString(1);
                String estado = consulta.getString(2);
                String  fecha= consulta.getString(3);
                String  id_usuario = consulta.getString(4);
                String  impuesto = consulta.getString(5);
                String descuento = consulta.getString(6);
                String envio = consulta.getString(7);
                String nombre = consulta.getString(8);
                String  documento = consulta.getString(9);
                String  correo = consulta.getString(10);
                String  telefono1 = consulta.getString(11);
                String  telefono2 = consulta.getString(12);
                String  pais = consulta.getString(13);
                String  ciudad = consulta.getString(14);
                String  direccion = consulta.getString(15);

                if (nombre.equals("")){
                    nombre=getString(R.string.Anonimo);
                }

                registro.add(new RegistroModelo(id_factura, id_empresa,estado,fecha ,id_usuario ,impuesto, descuento ,envio ,nombre,documento,  correo ,telefono1,telefono2,pais,ciudad , direccion));
            }while (consulta.moveToNext());
        }
        BD.close();
        adaptador_registro.notifyDataSetChanged();


    }

    public List<RegistroModelo>obtener_registros(){

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        String buscar=edittext_buscar.getText().toString().trim();

        if(buscar.equals("")) {

            consulta = BD.rawQuery(
                    "SELECT * FROM factura_detal ORDER BY id_factura DESC", null);

        }else{
            if(radioButton_nombre.isChecked()){
                consulta = BD.rawQuery(
                        "SELECT * FROM factura_detal WHERE nombre LIKE '%"+buscar+"%' ORDER BY id_factura DESC", null);
            }
            if(radioButton_factura.isChecked()){
                consulta = BD.rawQuery(
                        "SELECT * FROM factura_detal WHERE id_factura LIKE '%"+buscar+"%' ORDER BY id_factura DESC", null);
            }
            if(radioButton_fecha.isChecked()){
              //busqueda por fecha


            }
        }
        registro.clear();
        if (consulta.moveToFirst()){
            do {
                String id_factura = consulta.getString(0);
                String id_empresa = consulta.getString(1);
                String estado = consulta.getString(2);
                String  fecha= consulta.getString(3);
                String  id_usuario = consulta.getString(4);
                String  impuesto = consulta.getString(5);
                String descuento = consulta.getString(6);
                String envio = consulta.getString(7);
                String nombre = consulta.getString(8);
                String  documento = consulta.getString(9);
                String  correo = consulta.getString(10);
                String  telefono1 = consulta.getString(11);
                String  telefono2 = consulta.getString(12);
                String  pais = consulta.getString(13);
                String  ciudad = consulta.getString(14);
                String  direccion = consulta.getString(15);

                if (nombre.equals("")){
                    nombre=getString(R.string.Anonimo);
                }

                registro.add(new RegistroModelo(id_factura, id_empresa,estado,fecha ,id_usuario ,impuesto, descuento ,envio ,nombre,documento,  correo ,telefono1,telefono2,pais,ciudad , direccion));
            }while (consulta.moveToNext());
        }
        BD.close();

        return registro;
    }

    @Override
    public void onResume() {
        super.onResume();
        adaptador_registro.notifyDataSetChanged();
        //la lista de ELEMENTOS DEL ACTIVITY
        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerViewRegistro=null;
        adaptador_registro=null;

        registro.clear();
        registro=null;
        consulta=null;
        Runtime.getRuntime().gc();
    }
}