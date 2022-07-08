package inventario.fragmentos;


import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import static com.cataplus.facturacionycatalogospdf.Principal.format;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;
import static com.cataplus.facturacionycatalogospdf.Principal.valor_inventario;

import android.content.ContentValues;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

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

import arraylistas.ArrayFacturaActiva;
import fragmentos.Factura_detal;
import metodos.DecimalDigitsInputFilter;

import com.cataplus.facturacionycatalogospdf.Preferencias_app;
import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_inventario_detalle;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Frangmento_factura_mayor_inventario;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

//Fragmento FACTURA DETAL DETALLE

import com.cataplus.facturacionycatalogospdf.R;

//CAMBIADO A COMPRA (TODA REFERENCIA A LA PALABRA INVETARIO SE REFIERE A UNA COMPRA)
public class Fragmento_inventario_detalle extends Fragment {
    //detalle

    List<ClienteModelo> inventario_fecha ;
    public static RecyclerViewAdaptador_inventario_detalle adaptador_inventario_detalle;
    public static RecyclerView recyclerViewProductos;

    public static TextView textView_valor_inventario, textView_cliente;
    public static EditText editText_descuento, editText_impuesto, editText_envio;
    public static double impuesto=0, descuento=0, envio=0;


    public static String cliente="";
    static Cursor consulta;
    static View root;

    private static final String PREFENCIA_INVENTARIO = "preferencia_inventario";

    private Button button_agregar_cliente, button_borrar_cliente, facturar;



    public Fragmento_inventario_detalle() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_fragmento_inventario_detalle, container, false);

        inventario_fecha= new ArrayList<>();

        textView_valor_inventario=root.findViewById(R.id.textView_valor_inventario);
        button_agregar_cliente=root.findViewById(R.id.button_agregar_cliente);
        button_borrar_cliente=root.findViewById(R.id.button_borrar_cliente);
        facturar=root.findViewById(R.id.registrar_factura);


        //RELACIONAMOS LOS ELEMENTOS DEL fragment
        recyclerViewProductos= (RecyclerView) root.findViewById(R.id.RecyclerView_factura_detal);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptador_inventario_detalle = new RecyclerViewAdaptador_inventario_detalle(Fragmento_inventario_inventario.lalista);
        recyclerViewProductos.setAdapter(adaptador_inventario_detalle);

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)root.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);



        facturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facturar_producto(v);
            }
        });
        return root;
    }

    public void facturar_producto(View v){

        if(Principal.lista_inventario.size()<1){
            Toast.makeText(getContext(), "No tiene productos selecionados", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inventario_fecha.isEmpty()){
            inventario_fecha.add(new ClienteModelo("", "","","","","","","",""));
        }

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
        SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD

        String fecha_hora =   new SimpleDateFormat("yyyy-M-dd").format(new Date());


        //GUARMOS LOS VALORES ENN CONTENTVALUES
        ContentValues registro = new ContentValues();
        registro.put("id_empresa", Preferencias_app.datos_empresa.get(0).getId_empresa());
        registro.put("estado",getString(R.string.Inventario));
        registro.put("fecha", fecha_hora);
        registro.put("impuesto",impuesto );
        registro.put("descuento", descuento);
        registro.put("envio", envio);
        registro.put("nombre", getString(R.string.Inventario)+": "+fecha_hora);
        registro.put("documento", inventario_fecha.get(0).getDocumento());
        registro.put("correo", inventario_fecha.get(0).getCorreo());
        registro.put("telefono1", inventario_fecha.get(0).getTelefono1());
        registro.put("telefono2", inventario_fecha.get(0).getTelefono2());
        registro.put("pais", inventario_fecha.get(0).getPais());
        registro.put("ciudad", inventario_fecha.get(0).getCiudad());
        registro.put("direccion", inventario_fecha.get(0).getDireccion());

        BaseDeDatos.insert("factura_detal", null, registro);
        // INSERTAMOS EN LA BASE DE DATOS UNO A VARIOS (VARIOS PRODUCTOS)

        inventario_fecha.clear();
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
        for (int x = 0; x < Principal.lista_inventario.size(); x++) {

            //AGREGAR LOS PRODUCTOS A LA FACTURA ACTUAL
            registro_producto.put("id_factura", ultima_factura );
            registro_producto.put("nombre_producto", Principal.lista_inventario.get(x).getNombre_producto() );
            registro_producto.put("precio_producto", Principal.lista_inventario.get(x).getPrecio_producto() );
            registro_producto.put("foto1_producto", Principal.lista_inventario.get(x).getFoto1());
            registro_producto.put("barras_producto", Principal.lista_inventario.get(x).getBarras() );
            registro_producto.put("cantidad", Principal.lista_inventario.get(x).getSeleccion_producto());
            registro_producto.put("id_producto_tabla_productos", Principal.lista_inventario.get(x).getId_producto());
            registro_producto.put("tipo","Compra");
            registro_producto.put("p_costo",Principal.lista_inventario.get(x).getPrecio_costo());
            registro_producto.put("fecha",fecha_hora);
            registro_producto.put("descuento",descuento);

            BaseDeDatos.insert("producto_factura_detal", null, registro_producto);

            //SUMAR  CANTIDADES DEL LA BD
            ArrayFacturaActiva productos = Principal.lista_inventario.get(x);
            ContentValues actualizar_cantidades= new ContentValues();
            consulta=BaseDeDatos.rawQuery("SELECT seguimiento, cantidad FROM productos WHERE id_producto = '"+productos.getId_producto()+"'",null);
            if (consulta.moveToFirst()) {
                int cant= Integer.parseInt(consulta.getString(1));
                String seguimiento=(consulta.getString(0));
                if (seguimiento.equals("No")){
                    actualizar_cantidades.put("seguimiento", "Si");
                    actualizar_cantidades.put("cantidad", Integer.parseInt(productos.getSeleccion_producto()));
                }else{
                    actualizar_cantidades.put("cantidad", cant+Integer.parseInt(productos.getSeleccion_producto()));
                }
            }
            consulta.close();
            consulta=null;
            //MODIFICAR FILASS
            try {
                int filas_modificadas = BaseDeDatos.update("productos",actualizar_cantidades,"id_producto="+productos.getId_producto(),null);
            }catch (Exception e){
                Toast.makeText(getContext(), getString(R.string.Error_guardando), Toast.LENGTH_LONG).show();
                Fragmento_inventario_inventario.lalista.clear();
            }

        }


        BaseDeDatos.close();

        Principal.lista_inventario.clear();
        //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
        Gson gson = new Gson();
        String jsonString = gson.toJson(Principal.lista_inventario);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREFENCIA_INVENTARIO, jsonString);
        editor.apply();
        Principal.valor_inventario=0.00;

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
//                productos.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_mayor,  temp_detal, temp_costo, temp_estado, temp_foto1, imagen_byte));
//
//            }while (consulta.moveToNext());
//        }
//        BD.close();
//
//        return productos;
//    }


    

    public void actualizar_totales(){
        valor_inventario = 0;
        Fragmento_inventario_detalle.textView_valor_inventario.setText("0");
        for (int x = 0; x < Principal.lista_inventario.size(); x++) {
            ArrayFacturaActiva productos = Principal.lista_inventario.get(x);
            String precio_producto = productos.getPrecio_producto();
            String seleccion = productos.getSeleccion_producto();
            double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
            Principal.valor_inventario = Principal.valor_inventario + valorseleccion;
        }
        Fragmento_inventario_detalle.textView_valor_inventario.setText(format.format(valor_inventario));
        }



    public List<ProductosModelo> obtener_productos() {

        String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;
        Fragmento_inventario_inventario.lalista.clear();
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();


        Cursor consulta = BD.rawQuery(
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

                Fragmento_inventario_inventario.lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_detal, temp_mayor, temp_estado, temp_foto1, imagen_byte));
            } while (consulta.moveToNext());
        }
        consulta.close();
        consulta=null;

        BD.close();

        return  Fragmento_inventario_inventario.lalista;

    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if(Principal.lista_inventario.size()<1){
                facturar.setVisibility(View.GONE);
            }else{
                facturar.setVisibility(View.VISIBLE);
            }
            obtener_productos();//necesario para eliminar cualquier filtro anteriormente colocoado
            actualizar_totales();
            adaptador_inventario_detalle.notifyDataSetChanged();

        }catch (Exception e){

        }

    }

}