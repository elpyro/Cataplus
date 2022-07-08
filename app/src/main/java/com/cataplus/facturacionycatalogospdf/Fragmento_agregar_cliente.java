package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class Fragmento_agregar_cliente extends Fragment {

    public Fragmento_agregar_cliente() {
        // Required empty public constructor
    }

    public static Fragmento_agregar_cliente newInstance(String param1, String param2) {
        Fragmento_agregar_cliente fragment = new Fragmento_agregar_cliente();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hace visible el menu
        setHasOptionsMenu(true);
    }
    private  View vista;
    private EditText  editText_nombre, editText_documento, editText_correo, editText_telefono1, editText_telefono2, editText_pais, editText_ciudad, editText_direccion;
    private TextView textView_fecha_modificacion, textView_usuario_modificacion;
    private String fecha_hora,id_extra;

    private Button button_eliminar;
    private LinearLayout  linearlayout_ultima_modificacion, linearLayout_eliminar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
       vista= inflater.inflate(R.layout.fragment_fragmento_agregar_cliente, container, false);


        editText_nombre=vista.findViewById(R.id.editText_incluir_nombre_empresa);
        editText_documento=vista.findViewById(R.id.textView_documento_empresa);
        editText_correo=vista.findViewById(R.id.editText_correo_empresa);
        editText_telefono1=vista.findViewById(R.id.editText_telefono1);
        editText_telefono2=vista.findViewById(R.id.textView_telefono2);
        editText_pais=vista.findViewById(R.id.editText_pais);
        editText_ciudad=vista.findViewById(R.id.editText_ciudad_empresa);
        editText_direccion=vista.findViewById(R.id.editTextTextMultiLine_direccion_empresa);

        textView_fecha_modificacion=vista.findViewById(R.id.textView_fecha1);
        textView_usuario_modificacion=vista.findViewById(R.id.textView_actulizacion_usuario1);
        linearlayout_ultima_modificacion=vista.findViewById(R.id.linearLayout_ulitma_modificacion);
        linearLayout_eliminar=vista.findViewById(R.id.linearLayor_eliminar);

        button_eliminar=vista.findViewById(R.id.button5);


        //FECHA ACTUAL
        fecha_hora = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);


        if (getArguments() != null) {
            id_extra =getArguments().getString("id_cliente");


            //CONEXION A LA BASE DE DATOS
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
            SQLiteDatabase BD= admin.getWritableDatabase();
            Cursor cursor=BD.rawQuery("SELECT * FROM clientes WHERE id_cliente = '"+id_extra+"'",null);


            if (cursor.moveToFirst()){
                editText_nombre.setText(cursor.getString(1));
                editText_documento.setText(cursor.getString(2));
                editText_correo.setText(cursor.getString(3));
                editText_telefono1.setText(cursor.getString(4));
                editText_telefono2.setText(cursor.getString(5));
                editText_pais.setText(cursor.getString(6));
                editText_ciudad.setText(cursor.getString(7));
                editText_direccion.setText(cursor.getString(8));

                linearlayout_ultima_modificacion.setVisibility(View.VISIBLE);
                textView_fecha_modificacion.setText(cursor.getString(9));
                textView_usuario_modificacion.setText(cursor.getString(10));

                linearLayout_eliminar.setVisibility(View.VISIBLE);


            }else{
                Toast.makeText(getContext(),"No existe el cliente: "+id_extra,Toast.LENGTH_LONG).show();
            }

            BD.close();

        }



        button_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar(v);
            }
        });

        return vista;
    }

    public void eliminar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(editText_nombre.getText().toString().trim());
        builder.setMessage(getString(R.string.Esta_apunto_de_eliminar));
        builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registro_eliminado(view);
            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void registro_eliminado(View view){

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        //METODO QUE BORRAr FILAS DE LA BD
        int fila_borrada = BD.delete("clientes","id_cliente="+id_extra,null);
        BD.close();

        if (fila_borrada>0){
            Toast.makeText(getContext(),"Registro Eliminado ",Toast.LENGTH_LONG).show();
            //cerrar el fragmento y volver al anterior
            Navigation.findNavController(view).navigateUp();
        }else{
            Toast.makeText(getContext(),"No existe el cliente",Toast.LENGTH_LONG).show();
        }
    }
    public void guardar_cliente(View view){

        //AGREGANDO NUEVOS REGISTROS
        String nombre = editText_nombre.getText().toString().trim();

        if (id_extra != null){
            //MODIFICANDO BASE DE DATOS

            AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
            SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD

            if (nombre.equals("")) {
                Toast.makeText(getContext(), "El nombre del cliente no puede estar vacio", Toast.LENGTH_SHORT).show();
                return;
            } else {
                ContentValues registro= new ContentValues();

                registro.put("nombre", editText_nombre.getText().toString().trim());
                registro.put("documento", editText_documento.getText().toString().trim());
                registro.put("correo", editText_correo.getText().toString().trim());
                registro.put("telefono1", editText_telefono1.getText().toString().trim());
                registro.put("telefono2", editText_telefono2.getText().toString().trim());
                registro.put("pais", editText_pais.getText().toString().trim());
                registro.put("ciudad", editText_ciudad.getText().toString().trim());
                registro.put("direccion", editText_direccion.getText().toString().trim());
                registro.put("fecha_modificacion", fecha_hora);
                registro.put("usuario_modificacion", "Administrador");


                //MODIFICAR FILASS
                int filas_modificadas = BaseDeDatos.update("clientes",registro,"id_cliente="+id_extra,null);
                BaseDeDatos.close();

                if (filas_modificadas>0){
                    Toast.makeText(getContext(),getString(R.string.Registro_modificado),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"No existe el cliente",Toast.LENGTH_LONG).show();
                }
                //cerrar el fragmento y volver al anterior
                Navigation.findNavController(view).navigateUp();
            }

        }else {

            if (nombre.equals("")) {
                Toast.makeText(getContext(), "El nombre del cliente no puede estar vacio", Toast.LENGTH_LONG).show();
                return;
            } else {

                // CREAR OBJETO DE LA CLASE QUE CONECTA BD
                AdminSQLiteOpenHelper cn = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
                //ABRIR LA BD EN MODO ESCRITURA y lectura
                SQLiteDatabase BaseDeDatos = cn.getWritableDatabase();

                //GUARMOS LOS VALORES ENN CONTENTVALUES
                ContentValues registro = new ContentValues();

                registro.put("nombre", editText_nombre.getText().toString().trim());
                registro.put("documento", editText_documento.getText().toString().trim());
                registro.put("correo", editText_correo.getText().toString().trim());
                registro.put("telefono1", editText_telefono1.getText().toString().trim());
                registro.put("telefono2", editText_telefono2.getText().toString().trim());
                registro.put("pais", editText_pais.getText().toString().trim());
                registro.put("ciudad", editText_ciudad.getText().toString().trim());
                registro.put("direccion", editText_direccion.getText().toString().trim());
                registro.put("fecha_modificacion", fecha_hora);
                registro.put("usuario_modificacion", "Administrador");


                // INSERTAMOS EN LA BASE DE DATOS UN CONTENTVALUES CON LA INFORMACION
                BaseDeDatos.insert("clientes", null, registro);
                BaseDeDatos.close();

                //cerrar el fragmento y volver al anterior
                Navigation.findNavController(view).navigateUp();
                Toast.makeText(getContext(), "Registro Guardado", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_guardar,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.guardar:
                guardar_cliente(vista);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}