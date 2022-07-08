package com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.ContentValues;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.AdminSQLiteOpenHelper;
import com.cataplus.facturacionycatalogospdf.ClienteModelo;
import fragmentos.Factura_detal;
import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.Preferencias_app;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_factura_mayor_detalle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arraylistas.ArrayFacturaActiva;
import metodos.DecimalDigitsInputFilter;

//Fragmento FACTURA DETAL DETALLE

import com.cataplus.facturacionycatalogospdf.R;


public class Fragmento_factura_mayor_detalle extends Fragment {
    //detalle

    public static List<ClienteModelo> cliente_mayor;
    public static RecyclerViewAdaptador_factura_mayor_detalle adaptador_factura_mayor_detalle;
    public static TextView textView_valor_factura, textView_cliente;
    public static RecyclerView recyclerViewProductos;
    public static EditText editText_descuento, editText_impuesto, editText_envio;
    public static double impuesto=0, descuento=0, envio=0;
    static List<ProductosModelo> productos;
    static Cursor consulta;
    static View root;

    private static final String PREFENCIA_MAYOR = "preferencia_mayor";

    private CheckBox checkBox_descuento, checkBox_impuesto, checkBox_envio;
    private Button button_agregar_cliente, button_borrar_cliente, facturar;





    public Fragmento_factura_mayor_detalle() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_fragmento_factura_mayor_detalle, container, false);
        cliente_mayor= new ArrayList<>();
        productos = new ArrayList<>();

        textView_valor_factura=root.findViewById(R.id.textView_valor_factura);
        textView_cliente=root.findViewById(R.id.textview_cliente_factura);
        editText_descuento=root.findViewById(R.id.editTextNumber_descuento);
        editText_impuesto=root.findViewById(R.id.editTextNumber_impuesto);
        editText_envio=root.findViewById(R.id.editTextNumber_envio);
        facturar=root.findViewById(R.id.registrar_factura);

        checkBox_descuento=root.findViewById(R.id.check_descuento);
        checkBox_impuesto=root.findViewById(R.id.check_impuesto);
        checkBox_envio=root.findViewById(R.id.check_envio);

        button_agregar_cliente=root.findViewById(R.id.button_agregar_cliente);
        button_borrar_cliente=root.findViewById(R.id.button_borrar_cliente);


        //agregando las preferencias a los elementos
        editText_descuento.setText(Preferencias_app.descuento_preferencia+"");
        editText_impuesto.setText(Preferencias_app.impuesto_preferencia+"");
        editText_envio.setText(Preferencias_app.envio_preferencia+"");

        //DOS DECIMALES
        editText_descuento.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        editText_impuesto.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        editText_envio.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});

        //RELACIONAMOS LOS ELEMENTOS DEL fragment
        recyclerViewProductos= (RecyclerView) root.findViewById(R.id.RecyclerView_factura_detal);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptador_factura_mayor_detalle = new RecyclerViewAdaptador_factura_mayor_detalle(Frangmento_factura_mayor_inventario.lalista);
      //  adaptador_factura_mayor_detalle.setHasStableIds (true);
     //   recyclerViewProductos.setHasFixedSize(true);
        recyclerViewProductos.setItemViewCacheSize(20);
        recyclerViewProductos.setDrawingCacheEnabled(true);
        recyclerViewProductos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewProductos.setAdapter(adaptador_factura_mayor_detalle);



        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)root.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);





        checkBox_descuento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox_acciones(null);
            }
        });
        checkBox_envio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox_acciones(null);
            }
        });
        checkBox_impuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox_acciones(null);
            }
        });

        //ACCIONES AL CAMBIAR EDITTEXT SELECCION
        editText_descuento.addTextChangedListener(new TextWatcher() {
            int previousLength;  boolean backSpace;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

                if (editText_descuento.getText().toString().trim().equals("")){
                    descuento=0;
                }else {
                    descuento = Double.parseDouble(editText_descuento.getText().toString().trim());
                }
                actualizar_totales();
            }
        });
        editText_impuesto.addTextChangedListener(new TextWatcher() {
            int previousLength;  boolean backSpace;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

                if (editText_impuesto.getText().toString().trim().equals("")){
                    impuesto=0;
                }else {
                    impuesto = Double.parseDouble(editText_impuesto.getText().toString().trim());
                }
                actualizar_totales();
            }
        });

        editText_envio.addTextChangedListener(new TextWatcher() {
            int previousLength;  boolean backSpace;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

                if (editText_envio.getText().toString().trim().equals("")){
                    envio=0;
                }else {
                    envio = Double.parseDouble(editText_envio.getText().toString().trim());
                }
                actualizar_totales();
            }
        });

        button_agregar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_borrar_cliente.setVisibility(View.VISIBLE);

                //AGREGAR CLIENTE DESDE CONSULTAR CLIENTE.
                Bundle bundle = new Bundle();
                bundle.putString("padre","factura mayor" );
                Navigation.findNavController(v).navigate(R.id.fragmento_consultar_clientes,bundle);


            }
        });

        button_borrar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_borrar_cliente.setVisibility(View.GONE);
                cliente_mayor.clear();
                textView_cliente.setText(R.string.cliente_anonimo);
            }
        });

        facturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facturar_producto(v);
            }
        });
        return root;
    }

    public void facturar_producto(View v){

        if(Principal.lista_factura_mayor.size()<1){
            Toast.makeText(getContext(), "No tiene productos por facturar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cliente_mayor.isEmpty()){
            cliente_mayor.add(new ClienteModelo("", "","","","","","","",""));
        }

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
        SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD

        String fecha_hora =   new SimpleDateFormat("yyyy-M-dd").format(new Date());


        //GUARMOS LOS VALORES ENN CONTENTVALUES
        ContentValues registro = new ContentValues();
        registro.put("id_empresa", Preferencias_app.datos_empresa.get(0).getId_empresa());
        registro.put("estado",getString(R.string.Mayor));
        registro.put("fecha", fecha_hora);
        registro.put("impuesto",impuesto );
        registro.put("descuento", descuento);
        registro.put("envio", envio);
       // registro.put("fecha_busqueda",fecha_hora);
        registro.put("nombre", cliente_mayor.get(0).getNombre());
        registro.put("documento", cliente_mayor.get(0).getDocumento());
        registro.put("correo", cliente_mayor.get(0).getCorreo());
        registro.put("telefono1", cliente_mayor.get(0).getTelefono1());
        registro.put("telefono2", cliente_mayor.get(0).getTelefono2());
        registro.put("pais", cliente_mayor.get(0).getPais());
        registro.put("ciudad", cliente_mayor.get(0).getCiudad());
        registro.put("direccion", cliente_mayor.get(0).getDireccion());

        BaseDeDatos.insert("factura_detal", null, registro);
        // INSERTAMOS EN LA BASE DE DATOS UNO A VARIOS (VARIOS PRODUCTOS)

        cliente_mayor.clear();
        //ULITMO CLIENTE AGREAGDO
        Cursor consulta;
        consulta = BaseDeDatos.rawQuery("SELECT id_factura FROM    factura_detal   WHERE   id_factura = (SELECT MAX(id_factura)  FROM factura_detal);", null);

        String ultima_factura="";
        if (consulta.moveToFirst()) {
            ultima_factura = consulta.getString(0);
        }
        consulta.close();
        consulta=null;
        ContentValues registro_producto = new ContentValues();
        for (int x = 0; x < Principal.lista_factura_mayor.size(); x++) {

            //AGREGAR LOS PRODUCTOS A LA FACTURA ACTUAL
            registro_producto.put("id_factura", ultima_factura );
            registro_producto.put("nombre_producto", Principal.lista_factura_mayor.get(x).getNombre_producto() );
            registro_producto.put("precio_producto", Principal.lista_factura_mayor.get(x).getPrecio_producto() );
            registro_producto.put("foto1_producto", Principal.lista_factura_mayor.get(x).getFoto1());
            registro_producto.put("barras_producto", Principal.lista_factura_mayor.get(x).getBarras() );
            registro_producto.put("cantidad", Principal.lista_factura_mayor.get(x).getSeleccion_producto());
            registro_producto.put("id_producto_tabla_productos", Principal.lista_factura_mayor.get(x).getId_producto());
            registro_producto.put("tipo","Mayor");
            registro_producto.put("p_costo",Principal.lista_factura_mayor.get(x).getPrecio_costo());
            registro_producto.put("fecha",fecha_hora);
            registro_producto.put("descuento",descuento);

            BaseDeDatos.insert("producto_factura_detal", null, registro_producto);

            //RESTAR CANTIDADES DEL LA BD
            ArrayFacturaActiva productos = Principal.lista_factura_mayor.get(x);
            ContentValues actualizar_cantidades= new ContentValues();
            consulta=BaseDeDatos.rawQuery("SELECT  cantidad  FROM productos WHERE id_producto = '"+productos.getId_producto()+"'",null);
            if (consulta.moveToFirst()) {
                int cant= Integer.parseInt(consulta.getString(0));
                actualizar_cantidades.put("cantidad", cant-Integer.parseInt(productos.getSeleccion_producto()));
            }
            consulta.close();
            consulta=null;
            //MODIFICAR FILASS
            try {
                int filas_modificadas = BaseDeDatos.update("productos",actualizar_cantidades,"id_producto="+productos.getId_producto(),null);
            }catch (Exception e){
                Toast.makeText(getContext(), getString(R.string.Error_guardando), Toast.LENGTH_LONG).show();
                Frangmento_factura_mayor_inventario.lalista.clear();
            }

        }

        BaseDeDatos.close();


        Principal.lista_factura_mayor.clear();
        //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
        Gson gson = new Gson();
        String jsonString = gson.toJson(Principal.lista_factura_mayor);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREFENCIA_MAYOR, jsonString);
        editor.apply();
        Principal.valor_factura=0.00;

//        //CERRAR FRAGMENTO
//        getActivity().onBackPressed();

        Toast.makeText(getContext(), R.string.registro_guardado, Toast.LENGTH_SHORT).show();

        //cerrar el fragmento y volver al anterior
        Navigation.findNavController(v).navigateUp();
        //abrir fragemnt
        Bundle bundle = new Bundle();
        bundle.putString("id_factura",ultima_factura );
        Navigation.findNavController(v).navigate(R.id.fragment_factura_pdf,bundle);

    }



    public  void checkbox_acciones(View view){


        if (editText_descuento.getText().toString().trim().equals("")){
            editText_descuento.setText("0");
        }
        if (editText_impuesto.getText().toString().trim().equals("")){
            editText_impuesto.setText("0");
        }
        if (editText_envio.getText().toString().trim().equals("")){
            editText_envio.setText("0");
        }

        if (checkBox_descuento.isChecked()){
            editText_descuento.setVisibility(View.VISIBLE);
            descuento= Double.parseDouble(editText_descuento.getText().toString().trim());

        }else{
            editText_descuento.setVisibility(View.INVISIBLE);

            descuento=0;
        }

        if (checkBox_impuesto.isChecked()){
            editText_impuesto.setVisibility(View.VISIBLE);
            impuesto= Double.parseDouble(editText_impuesto.getText().toString().trim());
        }else{
            editText_impuesto.setVisibility(View.INVISIBLE);
            impuesto=0;
        }

        if (checkBox_envio.isChecked()){
            editText_envio.setVisibility(View.VISIBLE);
            envio= Double.parseDouble(editText_envio.getText().toString().trim());
        }else{
            editText_envio.setVisibility(View.INVISIBLE);
            envio=0;
        }
        actualizar_totales();

    }

    public void actualizar_totales(){
        Principal.valor_factura_mayor = 0;
        for (int x = 0; x < Principal.lista_factura_mayor.size(); x++) {
            ArrayFacturaActiva productos = Principal.lista_factura_mayor.get(x);
            String precio_producto = productos.getPrecio_producto();
            String seleccion = productos.getSeleccion_producto();
            double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
            Principal.valor_factura_mayor = Principal.valor_factura_mayor + valorseleccion;
        }

        try {

            Principal.valor_factura_mayor= Principal.valor_factura_mayor-((Principal.valor_factura_mayor*Fragmento_factura_mayor_detalle.descuento)/100);
        }catch (Exception e){
        }

        try {
            Principal.valor_factura_mayor= Principal.valor_factura_mayor+((Principal.valor_factura_mayor*Fragmento_factura_mayor_detalle.impuesto)/100);
        }catch (Exception e){
        }

        try {
            Principal.valor_factura_mayor= Principal.valor_factura_mayor+Fragmento_factura_mayor_detalle.envio;
        }catch (Exception e){
        }

        try{
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2); //Define 2 decimales.
            Fragmento_factura_mayor_detalle.textView_valor_factura.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
            Frangmento_factura_mayor_inventario.monto_factura.setText(format.format(Principal.valor_factura_mayor));
            Fragmento_factura_mayor_detalle.textView_valor_factura.setText(format.format(Principal.valor_factura_mayor));
        }catch (Exception e){

        }
    }

    public List<ProductosModelo> obtener_productos() {

        String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;
        Frangmento_factura_mayor_inventario.lalista.clear();
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

                Frangmento_factura_mayor_inventario.lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_detal,temp_mayor, temp_estado, temp_foto1, imagen_byte));
            } while (consulta.moveToNext());
        }
        consulta.close();
        consulta=null;
        BD.close();

        return  Frangmento_factura_mayor_inventario.lalista;

    }
    @Override
    public void onResume() {
        super.onResume();
        try {

            obtener_productos();//necesario para eliminar cualquier filtro anteriormente colocoado
           actualizar_totales();
            adaptador_factura_mayor_detalle.notifyDataSetChanged();
            if(Principal.lista_factura_mayor.size()<1){
                facturar.setVisibility(View.GONE);
            }else{
                facturar.setVisibility(View.VISIBLE);
            }
            if (!cliente_mayor.isEmpty()){
                Fragmento_factura_mayor_detalle.textView_cliente.setText(cliente_mayor.get(0).getNombre());
                button_borrar_cliente.setVisibility(View.VISIBLE);
            }else{
                button_borrar_cliente.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }
    }

}