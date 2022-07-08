package fragmentos;

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


import static com.cataplus.facturacionycatalogospdf.Principal.format;
import static com.cataplus.facturacionycatalogospdf.Principal.lista_factura_detal;
import static com.cataplus.facturacionycatalogospdf.Principal.valor_inventario;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;
import static com.cataplus.facturacionycatalogospdf.Principal.valor_factura;


import android.content.Intent;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.cataplus.facturacionycatalogospdf.Activity_scanner;
import com.cataplus.facturacionycatalogospdf.AdminSQLiteOpenHelper;

import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Factura;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import arraylistas.ArrayFacturaActiva;
import inventario.fragmentos.Fragmento_inventario_detalle;

import com.cataplus.facturacionycatalogospdf.R;

//FACTURA DETAL INVENTARIO
public class Fragmento_factura_detal extends Fragment {

  //  public static RecyclerViewAdaptador_factura_detal adaptador_factura_detal_detalle;
    public static RecyclerViewAdaptador_Factura adaptador_la_factura_inventario;
    public static RecyclerView recyclerViewFacturas;
    public static TextView monto_factura;
    static EditText editText_buscar;
    public static List<ProductosModelo> lalista ;
    static View vista;
    static Cursor consulta;

    private  final String LIST_KEY = "list_key100";



    private Button boton_scanear, button_borrar_pedido, button_buscar;
    private RadioButton radioButton_nombre, radioButton_codigo, radioButton_id;


    private String buscar_codigo = "";



    private String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;



    static TextView textView_valor_facturar;
    static EditText buscar_en_fragmento;
    private FloatingActionButton floatingActionButton_agregar;



    public Fragmento_factura_detal() {
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
         vista=inflater.inflate(R.layout.fragment_fragmento_factura_detal, container, false);
        lalista= new ArrayList<>();

        monto_factura=(TextView) vista.findViewById(R.id.textView_valor_factura);
        editText_buscar=(EditText)vista.findViewById(R.id.editTextText_buscar_fragmento);
        textView_valor_facturar=vista.findViewById(R.id.textView_valor_factura);

        boton_scanear=vista.findViewById(R.id.button_escanear);
        button_borrar_pedido=vista.findViewById(R.id.button_borrar_pedido);
        button_buscar=vista.findViewById(R.id.button_buscar);
        radioButton_nombre=vista.findViewById(R.id.radioButton_nombre);
        radioButton_codigo=vista.findViewById(R.id.radioButton_barras);
        radioButton_id=vista.findViewById(R.id.radioButton_id);

        floatingActionButton_agregar=vista.findViewById(R.id.floatingActionButton_nuevo_producto1);

        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
        recyclerViewFacturas = (RecyclerView) vista.findViewById(R.id.recycler_facturar);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewFacturas.setLayoutManager(new LinearLayoutManager(getContext()));


        adaptador_la_factura_inventario = new RecyclerViewAdaptador_Factura(obtener_productos());
       // adaptador_la_factura_inventario.setHasStableIds (true);
        //recyclerViewFacturas.setHasFixedSize(true);
        recyclerViewFacturas.setItemViewCacheSize(20);
        recyclerViewFacturas.setDrawingCacheEnabled(true);
        recyclerViewFacturas.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewFacturas.setAdapter(adaptador_la_factura_inventario);


        button_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrar(v);
            }
        });

        if (editText_buscar.getText().toString().trim().equals("")) {
//        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            String jsonString = pref.getString(LIST_KEY, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayFacturaActiva>>() {
            }.getType();
            ArrayList<ArrayFacturaActiva> listPrefer = gson.fromJson(jsonString, type);

            lista_factura_detal = (ArrayList) listPrefer.clone();

            //ELIMINAR FORMATO PARA GUARDAR VALOR EN DOUBLE
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2); //Define 2 decimales.

            //VERIFICAR EL ARRAY Y CONTAR, CANTIDADES POR LOS PRECIOS PARA TENER TOTAL DE FACTURA
            valor_factura = 0;

            for (int x = 0; x < lista_factura_detal.size(); x++) {
                ArrayFacturaActiva productos = lista_factura_detal.get(x);
                String precio_producto = productos.getPrecio_producto();
                String seleccion = productos.getSeleccion_producto();
                double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
                valor_factura = valor_factura + valorseleccion;
                format.setMaximumFractionDigits(2); //Define 2 decimales.
                textView_valor_facturar.setText(format.format(valor_factura));
            }
        }

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        floatingActionButton_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("padre","detal");
                Navigation.findNavController(v).navigate(R.id.fragmento_incluir_producto,bundle);
            }
        });

        boton_scanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear_en_activity();

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


        return vista;
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

                lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));


            } while (consulta.moveToNext());
        }

        BD.close();

        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(v.getWindowToken(), 0);
        try {
            adaptador_la_factura_inventario.notifyDataSetChanged();
        }catch (Exception e){ }




    }


    public List<ProductosModelo> obtener_productos() {

        lalista.clear();
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();


        consulta = BD.rawQuery(
                "SELECT * FROM productos  ORDER BY estado, categoria, nombre", null);

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

                lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));
            } while (consulta.moveToNext());
        }

        BD.close();

        return lalista;

    }

    public void escanear_en_activity(){
        Intent intent = new Intent(getActivity(), Activity_scanner.class);
        intent.putExtra("padre","Fragmento_factura_detal");
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
                filtrar(view_pedido);
                lista_factura_detal.clear();
                //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
                Gson gson = new Gson();
                String jsonString = gson.toJson(lista_factura_detal);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(LIST_KEY, jsonString);
                editor.apply();
                valor_factura=0.00;
                monto_factura.setText("0");

                adaptador_la_factura_inventario.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void actualizar_totales(){
        valor_factura = 0;

        for (int x = 0; x < Principal.lista_factura_detal.size(); x++) {
            ArrayFacturaActiva productos = Principal.lista_factura_detal.get(x);
            String precio_producto = productos.getPrecio_producto();
            String seleccion = productos.getSeleccion_producto();
            double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
            valor_factura = valor_factura + valorseleccion;
        }

        try {
            valor_factura=valor_factura-((valor_factura*Factura_detal.descuento)/100);
        }catch (Exception e){
        }

        try {
            valor_factura=valor_factura+((valor_factura*Factura_detal.impuesto)/100);
        }catch (Exception e){
        }

        try {
            valor_factura=valor_factura+Factura_detal.envio;
        }catch (Exception e){
        }

        monto_factura.setText(format.format(valor_factura));
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizar_totales();
        if (adaptador_la_factura_inventario!=null) adaptador_la_factura_inventario.notifyDataSetChanged();
    }




}