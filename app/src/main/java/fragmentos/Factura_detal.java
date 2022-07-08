package fragmentos;

import static com.cataplus.facturacionycatalogospdf.Principal.lista_factura_detal;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;
import static com.cataplus.facturacionycatalogospdf.Principal.valor_factura;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.cataplus.facturacionycatalogospdf.Preferencias_app;
import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_factura_detal;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Frangmento_factura_mayor_inventario;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import arraylistas.ArrayFacturaActiva;
import metodos.DecimalDigitsInputFilter;

//Fragmento FACTURA DETAL DETALLE
public class Factura_detal extends Fragment {
    //detalle

    static List<ProductosModelo> productos;
    static Cursor consulta;
    public static List<ClienteModelo> cliente_detal;
    public static RecyclerViewAdaptador_factura_detal adaptador_factura_detal_detalle;
    public static RecyclerView recyclerViewProductos;
    public static TextView textView_valor_factura, textView_cliente;
    public static EditText editText_descuento, editText_impuesto, editText_envio;
    public static double impuesto=0, descuento=0, envio=0;

    static View root;
    private static final String LIST_KEY = "list_key100";

    private CheckBox checkBox_descuento, checkBox_impuesto, checkBox_envio;
    private Button button_agregar_cliente, button_borrar_cliente,facturar;


    public Factura_detal() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_factura_detal, container, false);
        cliente_detal = new ArrayList<>();
         productos= new ArrayList<>();
        textView_valor_factura=root.findViewById(R.id.textView_valor_factura);
        textView_cliente=root.findViewById(R.id.textview_cliente_factura);
        editText_descuento=root.findViewById(R.id.editTextNumber_descuento);
        editText_impuesto=root.findViewById(R.id.editTextNumber_impuesto);
        editText_envio=root.findViewById(R.id.editTextNumber_envio);

        checkBox_descuento=root.findViewById(R.id.check_descuento);
        checkBox_impuesto=root.findViewById(R.id.check_impuesto);
        checkBox_envio=root.findViewById(R.id.check_envio);

        button_agregar_cliente=root.findViewById(R.id.button_agregar_cliente);
        button_borrar_cliente=root.findViewById(R.id.button_borrar_cliente);
        facturar=root.findViewById(R.id.registrar_factura);

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

        adaptador_factura_detal_detalle = new RecyclerViewAdaptador_factura_detal(Fragmento_factura_detal.lalista);
      //  adaptador_factura_detal_detalle.setHasStableIds (true);
    //    recyclerViewProductos.setHasFixedSize(true);
        recyclerViewProductos.setItemViewCacheSize(20);
        recyclerViewProductos.setDrawingCacheEnabled(true);
        recyclerViewProductos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewProductos.setAdapter(adaptador_factura_detal_detalle);


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

                //AGREGAR CLIENTE DESDE FRAGMENTO
                button_borrar_cliente.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("padre","factura detal" );
                Navigation.findNavController(v).navigate(R.id.fragmento_consultar_clientes,bundle);

            }
        });

        button_borrar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_borrar_cliente.setVisibility(View.GONE);
                cliente_detal.clear();
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

        if(lista_factura_detal.size()<1){
            Toast.makeText(getContext(), "No tiene productos por facturar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cliente_detal.isEmpty()){
            cliente_detal.add(new ClienteModelo("", "","","","","","","",""));
        }

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
        SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD

        String fecha_hora =   new SimpleDateFormat("yyyy-M-dd").format(new Date());

        //GUARMOS LOS VALORES ENN CONTENTVALUES
        ContentValues registro = new ContentValues();
        registro.put("id_empresa", Preferencias_app.datos_empresa.get(0).getId_empresa());
        registro.put("estado",getString(R.string.Detal));
        registro.put("fecha", fecha_hora);
        registro.put("impuesto",impuesto );
        registro.put("descuento", descuento);
        registro.put("envio", envio);
        registro.put("nombre", cliente_detal.get(0).getNombre());
        registro.put("documento", cliente_detal.get(0).getDocumento());
        registro.put("correo", cliente_detal.get(0).getCorreo());
        registro.put("telefono1", cliente_detal.get(0).getTelefono1());
        registro.put("telefono2", cliente_detal.get(0).getTelefono2());
        registro.put("pais", cliente_detal.get(0).getPais());
        registro.put("ciudad", cliente_detal.get(0).getCiudad());
        registro.put("direccion", cliente_detal.get(0).getDireccion());

        BaseDeDatos.insert("factura_detal", null, registro);
        // INSERTAMOS EN LA BASE DE DATOS UNO A VARIOS (VARIOS PRODUCTOS)

        cliente_detal.clear();
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
        for ( int x = 0; x < lista_factura_detal.size(); x++) {
//AGREGAR PRODUCTOS A LA FACTURA
            registro_producto.put("id_factura", ultima_factura );
            registro_producto.put("nombre_producto", lista_factura_detal.get(x).getNombre_producto() );
            registro_producto.put("precio_producto", lista_factura_detal.get(x).getPrecio_producto() );
            registro_producto.put("foto1_producto", lista_factura_detal.get(x).getFoto1());
            registro_producto.put("barras_producto", lista_factura_detal.get(x).getBarras() );
            registro_producto.put("cantidad", lista_factura_detal.get(x).getSeleccion_producto());
            registro_producto.put("id_producto_tabla_productos", lista_factura_detal.get(x).getId_producto());
            registro_producto.put("tipo","Detal");
            registro_producto.put("p_costo", lista_factura_detal.get(x).getPrecio_costo());
            registro_producto.put("fecha",fecha_hora);
            registro_producto.put("descuento",descuento);
            BaseDeDatos.insert("producto_factura_detal", null, registro_producto);

//RESTAR CANTIDADES DE LA BASE DE DATOS
            ArrayFacturaActiva productos = lista_factura_detal.get(x);
            ContentValues actualizar_cantidades= new ContentValues();

             consulta=BaseDeDatos.rawQuery("SELECT cantidad FROM productos WHERE id_producto = '"+productos.getId_producto()+"'",null);

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
                Fragmento_factura_detal.lalista.clear();
            }
        }

        BaseDeDatos.close();

        lista_factura_detal.clear();
        //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
        Gson gson = new Gson();
        String jsonString = gson.toJson(lista_factura_detal);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(LIST_KEY, jsonString);
        editor.apply();
        valor_factura=0.00;

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

//    public List<ProductosModelo>obtener_productos(){
//
//        //CONEXION A LA BASE DE DATOS
//        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
//        SQLiteDatabase BD= admin.getWritableDatabase();
//
//        consulta = BD.rawQuery(
//                "SELECT * FROM productos WHERE estado='Activo' ORDER BY estado, categoria, nombre", null);
//
//        if (consulta.moveToFirst()){
//            do {
//                String temp_id = consulta.getString(0);
//                String temp_nombre = consulta.getString(1);
//                String temp_barras = consulta.getString(2);
//                String  temp_categoria= consulta.getString(3);
//                String  temp_seguimiento = consulta.getString(4);
//                String temp_cantidad = consulta.getString(5);
//                String temp_costo = consulta.getString(7);
//                String temp_mayor = consulta.getString(8);
//                String  temp_detal = consulta.getString(9);
//                String  temp_estado = consulta.getString(10);
//                String  temp_foto1 = consulta.getString(11);
//
//                byte[] imagen_byte = consulta.getBlob(23);
//                ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen_byte);
//                Bitmap imagen = BitmapFactory.decodeStream(imageStream);
//
//                productos.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));
//
//            }while (consulta.moveToNext());
//        }
//        BD.close();
//
//        return productos;
//    }


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
        valor_factura = 0;
        for (int x = 0; x < lista_factura_detal.size(); x++) {
            ArrayFacturaActiva productos = lista_factura_detal.get(x);
            String precio_producto = productos.getPrecio_producto();
            String seleccion = productos.getSeleccion_producto();
            double valorseleccion;
            try{
                valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
            }catch (Exception e){
                valorseleccion=0.0;
            }
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

        try{
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2); //Define 2 decimales.
            Factura_detal.textView_valor_factura.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
            Fragmento_factura_detal.monto_factura.setText(format.format(valor_factura));
            Factura_detal.textView_valor_factura.setText(format.format(valor_factura));

        }catch (Exception e){

        }

    }

    public List<ProductosModelo> obtener_productos() {

        String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;
        Fragmento_factura_detal.lalista.clear();
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();


        consulta = BD.rawQuery(
                "SELECT * FROM productos   ORDER BY estado, categoria, nombre", null);

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

                Fragmento_factura_detal.lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));
            } while (consulta.moveToNext());
        }
        consulta.close();
        consulta=null;
        BD.close();

        return  Fragmento_factura_detal.lalista;

    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if(lista_factura_detal.size()<1){
                facturar.setVisibility(View.GONE);
            }else{
                facturar.setVisibility(View.VISIBLE);
            }
            obtener_productos();//necesario para eliminar cualquier filtro anteriormente colocoado
            actualizar_totales();



            adaptador_factura_detal_detalle.notifyDataSetChanged();
            if (!cliente_detal.isEmpty()) {
                textView_cliente.setText(cliente_detal.get(0).getNombre());
                button_borrar_cliente.setVisibility(View.VISIBLE);
            } else {
                button_borrar_cliente.setVisibility(View.GONE);
            }
        }catch (Exception e){
        }

    }

}