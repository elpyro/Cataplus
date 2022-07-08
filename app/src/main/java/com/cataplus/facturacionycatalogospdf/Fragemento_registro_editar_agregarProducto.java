package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.Intent;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_registro_AgregarProducto;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


public class Fragemento_registro_editar_agregarProducto extends Fragment {

    private String id_factura;
    public static String padre="";
    //  public static RecyclerViewAdaptador_factura_detal adaptador_factura_detal_detalle;
    public static RecyclerViewAdaptador_registro_AgregarProducto adaptador_registro_agregarProducto;
    public static RecyclerView recyclerViewFacturas;

    public static EditText editText_buscar;

    private  final String LIST_KEY = "list_key100";


    private Button boton_scanear, button_borrar_pedido, button_buscar;
    private RadioButton radioButton_nombre, radioButton_codigo, radioButton_id;

    private String buscar_codigo = "";

    public static List<ProductosModelo> lalista;

    private String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;

    private Cursor consulta;

    public static TextView textView_valor_facturar;
    private FloatingActionButton floatingActionButton_agregar_producto;

    public Fragemento_registro_editar_agregarProducto() {
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
        View vista= inflater.inflate(R.layout.fragment_fragemento_registro_editar_agregar_producto, container, false);
        lalista = new ArrayList<>();

        if (getArguments() != null) {
            id_factura =getArguments().getString("id_factura");
            padre=getArguments().getString("padre");
        }
        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
        recyclerViewFacturas = vista.findViewById(R.id.recycler_facturar);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewFacturas.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptador_registro_agregarProducto= new RecyclerViewAdaptador_registro_AgregarProducto(obtener_productos());
        //adaptador_registro_agregarProducto.setHasStableIds(true);
        //recyclerViewFacturas.setHasFixedSize(true);
        recyclerViewFacturas.setItemViewCacheSize(20);
        recyclerViewFacturas.setDrawingCacheEnabled(true);
        recyclerViewFacturas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        recyclerViewFacturas.setAdapter(adaptador_registro_agregarProducto);

        floatingActionButton_agregar_producto=(FloatingActionButton)vista.findViewById((R.id.floatingActionButton_nuevo_producto));


        editText_buscar=(EditText)vista.findViewById(R.id.editTextText_buscar_fragmento);
        boton_scanear=vista.findViewById(R.id.button_escanear);
        button_borrar_pedido=vista.findViewById(R.id.button_borrar_pedido);
        button_buscar=vista.findViewById(R.id.button_buscar);
        radioButton_nombre=vista.findViewById(R.id.radioButton_nombre);
        radioButton_codigo=vista.findViewById(R.id.radioButton_barras);
        radioButton_id=vista.findViewById(R.id.radioButton_id);


        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        floatingActionButton_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("padre",padre);
                Navigation.findNavController(v).navigate(R.id.fragmento_incluir_producto,bundle);

            }
        });

        button_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrar(v);
            }
        });

                boton_scanear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), Activity_scanner_registro_editar_AgregarProducto.class);
                        intent.putExtra("id_factura", id_factura);
                        intent.putExtra("padre",padre);
                        startActivity(intent);

                    }
                });
                button_borrar_pedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        borrar_pedido(v);

                    }
                });



        editText_buscar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    filtrar(v);
                }
                return false;
            }
        });


        return  vista;
    }

    public void filtrar(View v) {

        lalista.clear();

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        if (radioButton_nombre.isChecked()) {
            consulta = BD.rawQuery(
                    "SELECT * FROM productos WHERE nombre LIKE '%" + editText_buscar.getText().toString().trim() + "%'ORDER BY estado, categoria, nombre", null);
        }else if(radioButton_codigo.isChecked()){
            consulta = BD.rawQuery(
                    "SELECT * FROM productos WHERE codigo LIKE '%" + editText_buscar.getText().toString().trim() + "%'ORDER BY estado, categoria, nombre", null);
        }else if(radioButton_id.isChecked()){
            consulta = BD.rawQuery(
                    "SELECT * FROM productos WHERE id_producto LIKE '%" + editText_buscar.getText().toString().trim() + "%'ORDER BY estado, categoria, nombre", null);
        }

        if (consulta.moveToFirst()) {
            do {
                temp_id = consulta.getString(0);
                temp_nombre = consulta.getString(1);
                temp_barras = consulta.getString(2);
                temp_categoria = consulta.getString(3);
                temp_seguimiento = consulta.getString(4);
                temp_cantidad = consulta.getString(5);
                temp_costo = consulta.getString(7);
                temp_mayor = consulta.getString(8);
                temp_detal = consulta.getString(9);
                temp_estado = consulta.getString(10);
                temp_foto1 = consulta.getString(11);

                byte[] imagen_byte = consulta.getBlob(23);

                //array casi similares, el ulitmo valor del precio es el importante
                if(padre.equals("mayor")){
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_detal, temp_mayor, temp_estado, temp_foto1, imagen_byte));
                }else if(padre.equals("detal")) {
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_mayor, temp_detal,temp_estado, temp_foto1, imagen_byte));
                }else if(padre.equals("inventario")) {
                lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad,   temp_mayor, temp_detal, temp_costo, temp_estado, temp_foto1, imagen_byte));

            }


        } while (consulta.moveToNext());
        }

        BD.close();

        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(v.getWindowToken(), 0);

        adaptador_registro_agregarProducto.notifyDataSetChanged();

    }


    public List<ProductosModelo> obtener_productos() {

        lalista.clear();
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();


        consulta = BD.rawQuery(
                "SELECT * FROM productos   WHERE  estado='Activo' ORDER BY estado, categoria, nombre", null);

        if (consulta.moveToFirst()) {
            do {
                temp_id = consulta.getString(0);
                temp_nombre = consulta.getString(1);
                temp_barras = consulta.getString(2);
                temp_categoria = consulta.getString(3);
                temp_seguimiento = consulta.getString(4);
                temp_cantidad = consulta.getString(5);
                temp_costo = consulta.getString(7);
                temp_mayor = consulta.getString(8);
                temp_detal = consulta.getString(9);
                temp_estado = consulta.getString(10);
                temp_foto1 = consulta.getString(11);


                byte[] imagen_byte = consulta.getBlob(23);

                if(padre.equals("mayor")){
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_detal, temp_mayor, temp_estado, temp_foto1,imagen_byte));
                }else if(padre.equals("detal")) {
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_mayor, temp_detal,temp_estado, temp_foto1, imagen_byte));

                }else if(padre.equals("inventario")){
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad,   temp_mayor, temp_detal,temp_costo, temp_estado, temp_foto1, imagen_byte));
                }

            } while (consulta.moveToNext());
        }

        BD.close();

        return lalista;

    }

    public void escanear_en_activity(){
        Intent intent = new Intent(getContext(), Activity_scanner_registro_editar_AgregarProducto.class);
        intent.putExtra("id_factura",id_factura);
        intent.putExtra("padre",padre);
        startActivity(intent);


    }

    public void borrar_pedido(View view_pedido){
        //ALERT DIALOGO
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.Atencion));
        builder.setMessage(getString(R.string.Esta_apunto_de_eliminar));

        builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.clear();
                adaptador_registro_agregarProducto.notifyDataSetChanged();
                //cerrar el fragmento y volver al anterior
                Navigation.findNavController(view_pedido).navigateUp();

            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }
    @Override
    public void onResume() {
        super.onResume();
        adaptador_registro_agregarProducto.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //LIBERANDO ESPACIO
        recyclerViewFacturas=null;
        adaptador_registro_agregarProducto=null;
        lalista.clear();
        lalista=null;
        consulta=null;

        if (Fragmento_incluir_producto.lalista!=null){
            Fragmento_incluir_producto.lalista.clear();

        }

        Runtime.getRuntime().gc();
    }
}