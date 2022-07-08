package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.Principal.lista_inventario;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Registro_Producto;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_registro_AgregarProducto;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import arraylistas.ArrayFacturaActiva;
import modelos.ModeloProductoFactura;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class Fragmento_registro_editar extends Fragment {


    public Fragmento_registro_editar() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private  String id_extra;
    private EditText editText_nombre, editText_documento, editText_correo, editText_telefono1, editText_telefono2, editText_pais, editText_ciudad, editText_direccion, editText_envio, editText_descuento, editText_impuesto;
    private TextView  textView_factura, textView_fecha, textView_total, textView_descuento, textView_envio,textView_impuesto;
    public static TextView textView_tipo;
    private Button button_agregar_producto, button_crear_pdf;
    private String descuento;
    private RecyclerView recyclerView_registro_producto;
    private RecyclerViewAdaptador_Registro_Producto adaptadorRegistroProducto;
    private List<ModeloProductoFactura> producto;
    private Cursor consulta;
    private Context context;
    private double total;
    private String id_factura;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista= inflater.inflate(R.layout.fragment_fragmento_registro_editar, container, false);
        producto = new ArrayList<>();
        context=getContext();

        editText_nombre=vista.findViewById(R.id.editTextText_nombre);
        editText_documento=vista.findViewById(R.id.editTextText_documento);
        editText_correo=vista.findViewById(R.id.editTextTextPersonName5);
        editText_telefono1=vista.findViewById(R.id.editTextPhone3);
        editText_telefono2=vista.findViewById(R.id.editTextPhone2);
        editText_pais=vista.findViewById(R.id.editTextTextPersonName6);
        editText_ciudad=vista.findViewById(R.id.editTextTextPersonName7);
        editText_direccion=vista.findViewById(R.id.editTextTextMultiLine);
        editText_descuento=vista.findViewById(R.id.editTextNumber);
        editText_impuesto=vista.findViewById(R.id.editTextNumber2);
        editText_envio=vista.findViewById(R.id.editTextNumber3);
        textView_tipo=vista.findViewById(R.id.textView27);
        textView_factura=vista.findViewById(R.id.textView10);
        textView_fecha=vista.findViewById(R.id.textView12);
        textView_total=vista.findViewById(R.id.textView23);
        textView_descuento=vista.findViewById(R.id.textView20);
        textView_envio=vista.findViewById(R.id.textView22);
        textView_impuesto=vista.findViewById(R.id.textView21);
        button_crear_pdf=vista.findViewById(R.id.button_crear_pdf);
        button_agregar_producto=vista.findViewById(R.id.button);
        descuento=editText_descuento.getText().toString().trim();

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        if (getArguments() != null) {
            id_factura =getArguments().getString("id_factura");

                //CONEXION A LA BASE DE DATOS
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
                SQLiteDatabase BD = admin.getWritableDatabase();
                Cursor consulta;
                consulta = BD.rawQuery(
                        "SELECT * FROM factura_detal WHERE id_factura=  '"+id_factura+"'", null);

                if (consulta.moveToFirst()) {
                    do {

                        String id_factura_bd = consulta.getString(0);
                        String id_empresa = consulta.getString(1);
                        String estado = consulta.getString(2);
                        String fecha = consulta.getString(3);
                        String id_usuario = consulta.getString(4);
                        String impuesto = consulta.getString(5);
                        String descuento = consulta.getString(6);
                        String envio = consulta.getString(7);
                        String nombre = consulta.getString(8);
                        String documento = consulta.getString(9);
                        String correo = consulta.getString(10);
                        String telefono1 = consulta.getString(11);
                        String telefono2 = consulta.getString(12);
                        String pais = consulta.getString(13);
                        String ciudad = consulta.getString(14);
                        String direccion = consulta.getString(15);

                        textView_tipo.setText(estado);
                        textView_factura.setText(getString(R.string.Factura_)+": "+id_factura_bd);
                        textView_fecha.setText(fecha);
                        editText_nombre.setText(nombre);
                        editText_documento.setText(documento);
                        editText_correo.setText(correo);
                        editText_telefono1.setText(telefono1);
                        editText_telefono2.setText(telefono2);
                        editText_pais.setText(pais);
                        editText_ciudad.setText(ciudad);
                        editText_direccion.setText(direccion);
                        editText_descuento.setText(descuento);
                        editText_impuesto.setText(impuesto);
                        editText_envio.setText(envio);

                        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
                        recyclerView_registro_producto= (RecyclerView) vista.findViewById(R.id.RecyclerViewRegistroProducto);
                        //LO COLOCAMOS TIPO LISTA
                        recyclerView_registro_producto.setLayoutManager(new LinearLayoutManager(getContext()));
                    } while (consulta.moveToNext());
                }
           }

            editText_descuento.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                    if(keyCode == KeyEvent.KEYCODE_DEL) {
                        //this is for backspace
                        if (editText_descuento.getText().toString().trim().length() < 1) {
                            editText_descuento.setText("0");
                        }
                    }
                    calcular_total(null);
                    return false;
                }
            });

            editText_impuesto.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                    if(keyCode == KeyEvent.KEYCODE_DEL) {
                        //this is for backspace
                        if (editText_impuesto.getText().toString().trim().length() < 1) {
                            editText_impuesto.setText("0");
                        }
                    }
                    calcular_total(null);
                    return false;
                }
            });
            editText_envio.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                    if(keyCode == KeyEvent.KEYCODE_DEL) {
                        //this is for backspace
                        if (editText_envio.getText().toString().trim().length() < 1) {
                            editText_envio.setText("0");
                        }
                    }
                    calcular_total(null);
                    return false;
                }
            });
        button_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AQui ABRIR OTRO Fragmento ENVIANDO EL PARAMETRO
                Bundle bundle = new Bundle();
                bundle.putString("id_factura",id_factura );
                if(textView_tipo.getText().toString().trim().equals("Mayor")||textView_tipo.getText().toString().trim().equals("Wholesale")){
                    bundle.putString("padre","mayor" );
                }else if(textView_tipo.getText().toString().trim().equals("Detal")||textView_tipo.getText().toString().trim().equals("Retail")){
                    bundle.putString("padre","detal" );
                } else if(textView_tipo.getText().toString().trim().equals("Compra")||textView_tipo.getText().toString().trim().equals("Buy")){
                    bundle.putString("padre","inventario" );
                }
                Navigation.findNavController(v).navigate(R.id.fragemento_registro_editar_agregarProducto,bundle);
            }
        });


        button_crear_pdf .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                Bundle bundle = new Bundle();
                bundle.putString("id_factura",id_factura );
                Navigation.findNavController(v).navigate(R.id.fragment_factura_pdf,bundle);
            }
        });

        if(textView_tipo.getText().toString().trim().equals("Compra")||textView_tipo.getText().toString().trim().equals("Buy")){
            editText_documento.setVisibility(View.GONE);
            editText_correo.setVisibility(View.GONE);
            editText_telefono1.setVisibility(View.GONE);
            editText_telefono2.setVisibility(View.GONE);
            editText_pais.setVisibility(View.GONE);
            editText_ciudad.setVisibility(View.GONE);
            editText_direccion.setVisibility(View.GONE);
            editText_descuento.setVisibility(View.GONE);
            editText_impuesto.setVisibility(View.GONE);
            editText_envio.setVisibility(View.GONE);
            textView_descuento.setVisibility(View.GONE);
            textView_envio.setVisibility(View.GONE);
            textView_impuesto.setVisibility(View.GONE);

        }

        Principal.progressDialog.cancel();

        return vista;
    }


    public List<ModeloProductoFactura> obtener_productos(String id_factura){

        producto.clear();

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        consulta=BD.rawQuery("SELECT * FROM producto_factura_detal WHERE id_factura = '"+id_factura+"'",null);

        total = 0;

        if (consulta.moveToFirst()){
            do {
                String id_producto = consulta.getString(0);
                String id_factura_bd = consulta.getString(1);
                String nombre_producto = consulta.getString(2);
                String precio_producto = consulta.getString(3);
                String barras_producto=consulta.getString(5);
                String cantidad = consulta.getString(6);
                String id_producto_tabla_productos=consulta.getString(7);

                double sub_total=(Double.parseDouble(precio_producto)*Double.parseDouble(cantidad));
                total= total +sub_total;
                producto.add(new ModeloProductoFactura(id_producto,id_factura_bd,nombre_producto,precio_producto,id_producto_tabla_productos,barras_producto,cantidad,format.format(sub_total),null));
            }while (consulta.moveToNext());
            consulta.close();
            consulta=null;
            BD.close();

            calcular_total(null);
            Bitmap imagen=null;

            //COLOCAR IMAGEN GUARDADA EN LA TABLA PRODUCTOS
            AdminSQLiteOpenHelper administrador = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
            SQLiteDatabase BaseDatos= administrador.getWritableDatabase();
            for (int x = 0; x < producto.size(); x++) {
                ModeloProductoFactura productos = producto.get(x);
                // cursor para colocar la foto  existente del producto en la base de datos
                consulta =BaseDatos.rawQuery("SELECT imagen FROM productos WHERE id_producto = '"+productos.get_foto1_producto()+"'",null);

                if (consulta.moveToFirst()) {
                    byte[] imagen_byte = consulta.getBlob(0);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen_byte);
                    imagen = BitmapFactory.decodeStream(imageStream);
                    productos.setImagen(imagen);
                }
                consulta.close();
                consulta=null;
            }
            BaseDatos.close();
        }
        return producto;
    }

    public void calcular_total(View view){

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.

        double total_descontado;
        total_descontado=total;
        try {
            double descuento= Double.parseDouble(editText_descuento.getText().toString().trim());
            total_descontado=total_descontado-((total_descontado*descuento)/100);
        }catch (Exception e){
        }
        try {
            double impuesto= Double.parseDouble(editText_impuesto.getText().toString().trim());

            total_descontado=total_descontado+((total_descontado*impuesto)/100);
        }catch (Exception e){
        }
        try {
            double envio= Double.parseDouble(editText_envio.getText().toString().trim());
            total_descontado=total_descontado+envio;
        }catch (Exception e){
        }
        textView_total.setText(format.format(total_descontado));

        total_descontado=0;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

//        if (editText_nombre.equals("")){
//            Toast.makeText(getContext(),getString(R.string.Debe_ingresar_nombre_cliente),Toast.LENGTH_LONG).show();
//            editText_nombre.setError(getString(R.string.Requerido));
//            return;
//        }else {
            ContentValues registro_factura= new ContentValues();
            //       registro_producto.put("id_factura", ultima_factura );
            registro_factura.put("nombre", editText_nombre.getText().toString().trim());
            registro_factura.put("documento",editText_documento.getText().toString().trim());
            registro_factura.put("correo", editText_correo.getText().toString().trim());
            registro_factura.put("telefono1", editText_telefono1.getText().toString().trim());
            registro_factura.put("telefono2", editText_telefono2.getText().toString().trim());
            registro_factura.put("pais", editText_pais.getText().toString().trim());
            registro_factura.put("ciudad", editText_ciudad.getText().toString().trim());
            registro_factura.put("direccion", editText_direccion.getText().toString().trim());
            registro_factura.put("impuesto", editText_impuesto.getText().toString().trim());
            registro_factura.put("descuento", editText_descuento.getText().toString().trim());
            registro_factura.put("envio", editText_envio.getText().toString().trim());
            //MODIFICAR FILASS
            int filas_modificadas = BD.update("factura_detal",registro_factura,"id_factura="+id_factura,null);

            if(!descuento.equals(editText_descuento.getText().toString().trim())){
                //todo AJUSTAR EL DESCUENTO A TODOS LOS PRODUCTOS DE LA FACTURA
                Cursor cursor = BD.rawQuery("SELECT * FROM producto_factura_detal WHERE id_factura = '" + id_factura + "'", null);

                ContentValues actualizar_descuento = new ContentValues();
                if (cursor.moveToFirst()) {

                    actualizar_descuento.put("descuento", editText_descuento.getText().toString().trim());
                    BD.update("producto_factura_detal", actualizar_descuento, "id_factura=" + id_factura, null);
                }
            }


            //LIBERANDO ESPACIO
            BD.close();
            recyclerView_registro_producto=null;
            adaptadorRegistroProducto=null;

            if (producto!=null)producto.clear();
            producto=null;
            consulta=null;
            Runtime.getRuntime().gc();
        //}
    }

    @Override
    public void onResume() {
         super.onResume();


        if ( RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.size()>0){
        //ALERT DIALOGO
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.Agregar_producto));
        builder.setMessage(getString(R.string.Esta_seguro));

        builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                //CONEXION A LA BASE DE DATOS
                AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
                SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD

                String fecha_hora =   new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                ContentValues registro_producto = new ContentValues();
                for (int x = 0; x < RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.size(); x++) {
                    //AGREGAR PRODUCTOS A LA FACTURA ACTUAL
                    registro_producto.put("id_factura", id_factura );
                    registro_producto.put("id_producto_tabla_productos", RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getId_producto());
                    registro_producto.put("nombre_producto", RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getNombre_producto() );
                    registro_producto.put("precio_producto", RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getPrecio_producto() );
                    registro_producto.put("foto1_producto", RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getFoto1());
                    registro_producto.put("barras_producto", RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getBarras() );
                    registro_producto.put("cantidad", RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getSeleccion_producto());
                    registro_producto.put("tipo",textView_tipo.getText().toString().trim());
                    registro_producto.put("p_costo",RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x).getPrecio_costo() );
                    registro_producto.put("fecha",fecha_hora);
                    registro_producto.put("descuento",editText_descuento.getText().toString().trim());

                    BaseDeDatos.insert("producto_factura_detal", null, registro_producto);
                      //RESTAR PRODUCTOS AL INVENTARIO o sumar

                    ArrayFacturaActiva productos = RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.get(x);
                    ContentValues actualizar_cantidades= new ContentValues();
                    Cursor cursor=BaseDeDatos.rawQuery("SELECT * FROM productos WHERE id_producto = '"+productos.getId_producto()+"'",null);

                    if (cursor.moveToFirst()) {
                        //SUMAR CANTIDADES SI ES COMPRA
                        if (textView_tipo.getText().toString().trim().equals("Compra")){
                            //SI NO ESTA EN SEGUIMIENTO EL VALOR INCIAL ES 0
                            int cant= Integer.parseInt(cursor.getString(5));
                            String seguimiento=(cursor.getString(4));
                            if (seguimiento.equals(getString(R.string.No))){
                                actualizar_cantidades.put("seguimiento", "Si");
                                actualizar_cantidades.put("cantidad", Integer.parseInt(productos.getSeleccion_producto()));
                            }else{
                                actualizar_cantidades.put("cantidad", cant+Integer.parseInt(productos.getSeleccion_producto()));
                            }
                        }else{
                            //RESTAR CANTIDADES SI ES VENTA
                            int cant= Integer.parseInt(cursor.getString(5));
                            actualizar_cantidades.put("cantidad", cant-Integer.parseInt(productos.getSeleccion_producto()));
                        }


                    }
                    //MODIFICAR FILASS
                    int filas_modificadas = BaseDeDatos.update("productos",actualizar_cantidades,"id_producto="+productos.getId_producto(),null);


                    adaptadorRegistroProducto = new RecyclerViewAdaptador_Registro_Producto(obtener_productos(id_factura));
                //    adaptadorRegistroProducto.setHasStableIds (true);
                //    recyclerView_registro_producto.setHasFixedSize(true);
                    recyclerView_registro_producto.setItemViewCacheSize(20);
                    recyclerView_registro_producto.setDrawingCacheEnabled(true);
                    recyclerView_registro_producto.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    recyclerView_registro_producto.setAdapter(adaptadorRegistroProducto);


                }
                Toast.makeText(getContext(), getString(R.string.Productos_agregados), Toast.LENGTH_LONG).show();
                BaseDeDatos.close();
                RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.clear();


            }
        });
        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos.clear();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

                }
        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
        adaptadorRegistroProducto = new RecyclerViewAdaptador_Registro_Producto(obtener_productos(id_factura));
       // adaptadorRegistroProducto.setHasStableIds (true);
      //  recyclerView_registro_producto.setHasFixedSize(true);
        recyclerView_registro_producto.setItemViewCacheSize(20);
        recyclerView_registro_producto.setDrawingCacheEnabled(true);
        recyclerView_registro_producto.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView_registro_producto.setAdapter(adaptadorRegistroProducto);


    }


}

