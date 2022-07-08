package com.cataplus.facturacionycatalogospdf;


import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;



public class Fragmento_consultar_productos extends Fragment {
    public static String buscar_codigo="";

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private ZXingScannerView vistaescaner;
    private int bandera_camara=0;

    private TextView tvCodigoLeido;

    private RecyclerView recyclerViewScores;
    private RecyclerViewAdaptador adaptadorScores;

  private FloatingActionButton floatingActionButton_agregar_producto;
    private RadioButton radioButton_nombre, radioButton_codigo, radioButton_id;

    private Button boton_buscar, button_scanear;

    private EditText editText_buscar;

    private List<ProductosModelo> score ;

    private String temp_id, temp_nombre , temp_barras, temp_cantidad, temp_categoria, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;

    private Cursor consulta;

    public Fragmento_consultar_productos() {
        // Required empty public constructor
    }

    public static Fragmento_consultar_productos newInstance(String param1, String param2) {
        Fragmento_consultar_productos fragment = new Fragmento_consultar_productos();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
         View vista=inflater.inflate(R.layout.fragment_fragmento_consultar_productos, container, false);
        score= new ArrayList<>();
        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
        recyclerViewScores= (RecyclerView) vista.findViewById(R.id.recycler_facturar);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewScores.setLayoutManager(new LinearLayoutManager(getContext()));


        adaptadorScores = new RecyclerViewAdaptador(obtener_productos());
       // adaptadorScores.setHasStableIds (true);
        //recyclerViewScores.setHasFixedSize(true);
        recyclerViewScores.setItemViewCacheSize(20);
        recyclerViewScores.setDrawingCacheEnabled(true);
        recyclerViewScores.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        recyclerViewScores.setAdapter(adaptadorScores);

        boton_buscar=vista.findViewById(R.id.button_buscar);
        button_scanear=vista.findViewById(R.id.button_escanear);
        editText_buscar=vista.findViewById(R.id.editTextText_buscar);

        radioButton_nombre=vista.findViewById(R.id.radioButton_nombre);
        radioButton_codigo=vista.findViewById(R.id.radioButton_barras);
        radioButton_id=vista.findViewById(R.id.radioButton_id);
        floatingActionButton_agregar_producto=vista.findViewById(R.id.floatingActionButton_nuevo_producto);

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        boton_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrar(v);
            }
        });

        floatingActionButton_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(v).navigate(R.id.fragmento_incluir_producto);
            }
        });

        button_scanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent=new Intent(getContext(),Activity_scanner_codigosVisiblesInvisibles.class);
             startActivity(intent);
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


        return vista;
    }



    public void filtrar(View v){

        score.clear();

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();


        if (buscar_codigo.equals("")) {
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

        }else{
            consulta = BD.rawQuery(
                    "SELECT * FROM productos WHERE codigo= '" + buscar_codigo + "' ORDER BY estado, categoria, nombre", null);
            Toast.makeText(getContext(), "Codigo: "+buscar_codigo, Toast.LENGTH_SHORT).show();
        }

        if (consulta.moveToFirst()) {
            do {
                temp_id = consulta.getString(0);
                temp_nombre = consulta.getString(1);
                temp_barras= consulta.getString(2);
                temp_seguimiento = consulta.getString(4);
                temp_cantidad = consulta.getString(5);
                temp_costo = consulta.getString(7);
                temp_mayor = consulta.getString(8);
                temp_detal = consulta.getString(9);
                temp_estado = consulta.getString(10);
                temp_foto1 = consulta.getString(11);


                byte[] imagen_byte = consulta.getBlob(23);


                score.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));
            } while (consulta.moveToNext());
        }

        BD.close();
        if (v!=null){
            InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
            input.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        adaptadorScores.notifyDataSetChanged();

    }


    public List<ProductosModelo> obtener_productos(){

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();


        consulta = BD.rawQuery(
                "SELECT * FROM productos ORDER BY estado, categoria, nombre", null);

        if (consulta.moveToFirst()){
            do {
                temp_id=consulta.getString(0);
                temp_nombre = consulta.getString(1);
                temp_categoria= consulta.getString(3);
                temp_seguimiento= consulta.getString(4);
                temp_cantidad= consulta.getString(5);
                temp_costo= consulta.getString(7);
                temp_mayor= consulta.getString(8);
                temp_detal= consulta.getString(9);
                temp_estado= consulta.getString(10);
                temp_foto1= consulta.getString(11);

                byte[] imagen_byte = consulta.getBlob(23);

                score.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal,temp_estado,temp_foto1,imagen_byte));
            }while (consulta.moveToNext());
        }

        BD.close();

        return score;

    }


    @Override
    public void onResume() {
        super.onResume();
        //recibir argumentos
        if (!buscar_codigo.equals("")) {
            filtrar(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RecyclerView recyclerViewScores=null;
        RecyclerViewAdaptador adaptadorScores=null;
        score.clear();
        score=null;
        consulta=null;

        if (Fragmento_incluir_producto.lalista!=null){
            Fragmento_incluir_producto.lalista.clear();

        }
        Runtime.getRuntime().gc();
    }
}
