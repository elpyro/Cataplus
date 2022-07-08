package com.cataplus.facturacionycatalogospdf;


import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Factura;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_factura_detal;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_inventario_detalle;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_inventario_inventario;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_registro_AgregarProducto;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_factura_mayor_inventario;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Frangmento_factura_mayor_inventario;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import arraylistas.ArrayFacturaActiva;
import fragmentos.Factura_detal;
import fragmentos.Fragmento_factura_detal;
import inventario.fragmentos.Fragmento_inventario_detalle;
import inventario.fragmentos.Fragmento_inventario_inventario;
import metodos.DecimalDigitsInputFilter;

//https://www.youtube.com/watch?v=5YcZU7tDU98&t=402s


public class Fragmento_incluir_producto extends Fragment {
    public static ImageView imagen1;
    public static Uri uri_imagen1;
    private EditText editText_carrito,edittext_nombre,  edittext_descripcion, editText_buscar, edittext_proveedor, edittext_talla, edittext_color, edittext_cantidad, edittext_detal, edittext_costo, edittext_mayor;
    public static EditText edittextbarras;
    private TextView fecha_modiciacion,usuario_modificacion, textView_id_consecutivo_producto;
    private Spinner combo_categoria, combo_medida;
    private Switch switch_visible, switch_cantidad;

    private Button button_scanner, button_eliminar;

    private LinearLayout linearlayout_ultima_modificacion, linearLayout_eliminar, linearLayout_id_producto, linearLayout_carrito;

    public static List<ProductosModelo> lalista = new ArrayList<>();



    //Permiso de la clase Constants
    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    //selección de imagen Constants
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    private static final int LEER_SCANNER = 104;

    // matrices de permisos
    private String[] cameraPermissions; // cámara y almacenamiento
    private String [] storagePermissions;// solo almacenamiento
    private String id_extra, fecha_hora, nombre_origial, codigo_original;

    private int bandera=0, bandera_escaner=0;
    private String padre="";
    private Uri imageUri;
    boolean existe=false;
    private byte[] blob;
    private View vista;




    public Fragmento_incluir_producto() {
        // Required empty public constructor
    }

    public static Fragmento_incluir_producto newInstance(String param1, String param2) {
        Fragmento_incluir_producto fragment = new Fragmento_incluir_producto();

        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hace visible el menu
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        vista= inflater.inflate(R.layout.fragment_fragmento_incluir_producto, container, false);

        edittext_nombre=vista.findViewById(R.id.editText_incluir_nombre_empresa);
        edittextbarras=vista.findViewById(R.id.editText_codigo_barras);
        combo_categoria=vista.findViewById(R.id.spinner_categoria);
        switch_visible=vista.findViewById(R.id.switch_visible);
        switch_cantidad=vista.findViewById(R.id.switch_cantidad);
        edittext_cantidad=vista.findViewById(R.id.editTextNumber_cantidad);
        combo_medida=vista.findViewById(R.id.spinner_medida);
        edittext_costo=vista.findViewById(R.id.editText_costo);
        edittext_mayor=vista.findViewById(R.id.editText_mayor);
        edittext_detal=vista.findViewById(R.id.editText_detal);
        imagen1=vista.findViewById(R.id.imageView_incluir_foto1);
        linearLayout_carrito=vista.findViewById(R.id.carrito);
        edittext_descripcion=vista.findViewById(R.id.editTextText_incluir_marca);
        edittext_proveedor=vista.findViewById(R.id.editTextText_proveedor);
        edittext_talla=vista.findViewById(R.id.editTextTextPersonName_incluir_talla);
        edittext_color=vista.findViewById(R.id.editTextTextPersonName_incluir_color);
        textView_id_consecutivo_producto=vista.findViewById(R.id.id_consecutivo_producto);
        button_scanner=vista.findViewById(R.id.button3);
        button_eliminar=vista.findViewById(R.id.button5);
        linearlayout_ultima_modificacion=vista.findViewById(R.id.linearLayout_ulitma_modificacion);
        linearLayout_eliminar=vista.findViewById(R.id.linearLayor_eliminar);
        linearLayout_id_producto=vista.findViewById(R.id.linearLayout_id);
        editText_carrito=vista.findViewById(R.id.editTest_carrito);

        usuario_modificacion=vista.findViewById(R.id.textView_actulizacion_usuario1);
        fecha_modiciacion=vista.findViewById(R.id.textView_fecha1);

        edittext_cantidad.setVisibility(View.GONE);

        editText_buscar=vista.findViewById(R.id.editTextText_buscar);

        //formato 2 decimales
        edittext_detal.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        edittext_mayor.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        edittext_costo.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        //COLOCAR FOTO POR DEFECTO FOTO VACIA EN BD
        String archivo = "android.resource://"+  getActivity().getPackageName() + "/drawable/agregar_foto_jpeg";
        uri_imagen1 = Uri.parse(archivo);

        //FECHA ACTUAL
        fecha_hora = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


        if (getArguments() != null) {
            id_extra =getArguments().getString("id_producto");

            if(getArguments().getString("padre")!=null){
                padre=getArguments().getString("padre");
            }

            //CONEXION A LA BASE DE DATOS
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
            SQLiteDatabase BD= admin.getWritableDatabase();
            Cursor cursor=BD.rawQuery("SELECT * FROM productos WHERE id_producto = '"+id_extra+"'",null);

            if (cursor.moveToFirst()){

                edittext_nombre.setText(cursor.getString(1));
                nombre_origial=cursor.getString(1);
                edittextbarras.setText(cursor.getString(2));
                codigo_original=(cursor.getString(2));

                //posisicion del combo box
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categoria, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                combo_categoria.setAdapter(adapter);
                if ((cursor.getString(3) != null)){
                    int spinnerPosition = adapter.getPosition((cursor.getString(3)));
                    combo_categoria.setSelection(spinnerPosition);
                }

                //posisicion del combo box
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.medidas, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                combo_medida.setAdapter(adapter2);
                if ((cursor.getString(6) != null)){
                    int spinnerPosition = adapter2.getPosition((cursor.getString(6)));
                    combo_medida.setSelection(spinnerPosition);
                }

                if((cursor.getString(4).equals("Si"))){
                    switch_cantidad.setChecked(true);
                    edittext_cantidad.setVisibility(View.VISIBLE);
                }

                if (cursor.getString(10).equals("Inactivo")){
                    switch_visible.setChecked(false);
                }

                edittext_cantidad.setText(cursor.getString(5));
                edittext_costo.setText(cursor.getString(7));
                edittext_mayor.setText(cursor.getString(8));
                edittext_detal.setText(cursor.getString(9));

//                if (!cursor.getString(11).equals(null)){
//                    Uri resultUri = Uri.parse(cursor.getString(11));
//                    imageUri = resultUri;
//                    imagen1.setImageURI(resultUri);
//                    uri_imagen1 = resultUri;
//                }

                //convertir imagen bit a imagen
                if (!cursor.getBlob(23).equals(null)) {
                    byte[] imagen = cursor.getBlob(23);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                    uri_imagen1=convertir_bitmap_a_uri(getContext(), theImage);
                    imagen1.setImageBitmap(theImage);

                }



                edittext_descripcion.setText(cursor.getString(18));
                edittext_proveedor.setText(cursor.getString(19));
                edittext_talla.setText(cursor.getString(20));
                edittext_color.setText(cursor.getString(21));

                linearLayout_id_producto.setVisibility(View.VISIBLE);
                String primer_caracter= String.valueOf((cursor.getString(3).charAt(0)));
                primer_caracter=primer_caracter.toUpperCase();
                textView_id_consecutivo_producto.setText("ID: "+primer_caracter+cursor.getString(0));

                linearlayout_ultima_modificacion.setVisibility(View.VISIBLE);
                fecha_modiciacion.setText(cursor.getString(16));
               // usuario_modificacion.setText(cursor.getString(17));

                //  floatingActionButton_eliminar.setVisibility(View.VISIBLE);
                linearLayout_eliminar.setVisibility(View.VISIBLE);

                existe=true;
            }
            BD.close();

        }

        if (id_extra==null){
            linearLayout_carrito.setVisibility(View.VISIBLE);
        }else {
            linearLayout_carrito.setVisibility(View.GONE);
        }


        button_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//ocultar teclado
                InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);

                Intent intent=new Intent(getContext(),Activity_scanner_codigosVisiblesInvisibles.class);
                startActivity(intent);

            }
        });

        button_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ocultar teclado
                InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
                //guardar
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(nombre_origial);
                builder.setMessage(getString(R.string.Apunto_eliminar_registro));
                builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        registro_eliminado(v);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                actualizar_recyclerviews();
            }
        });

        edittext_nombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                    input.hideSoftInputFromWindow(v.getWindowToken(), 0);

                     } } });

        edittext_cantidad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                    input.hideSoftInputFromWindow(v.getWindowToken(), 0);

                } } });



        imagen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // muestra el cuadro de diálogo de selección de imagen

                bandera=1;
                //ocultar teclado
                InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);

             Intent intent=new Intent(getContext(), Activity_simple_foto.class);
             startActivity(intent);

            }
        });

switch_cantidad.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        cantidad_visible(v);
    }
});


        return vista;


    }


    public Uri convertir_bitmap_a_uri(Context inContext, Bitmap inImage) {
        File tempFile = null;
        try {
            File tempDir= Environment.getExternalStorageDirectory();
            tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
            tempDir.mkdir();
            tempFile = File.createTempFile("Cataplus", ".jpg", tempDir);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] bitmapData = bytes.toByteArray();
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        }catch (Exception e){
            Toast.makeText(getContext(), "No se puede guardar la imagen "+e, Toast.LENGTH_SHORT).show();
        }
        return Uri.fromFile(tempFile);
    }

        //MOSTRAR TEXT DE CANTIDAD
    public void cantidad_visible(View view){

        if (switch_cantidad.isChecked()){
            edittext_cantidad.setVisibility(View.VISIBLE);

        }else{
            edittext_cantidad.setVisibility(View.GONE);
        }
    }



    public void registro_eliminado(View v){

        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        //METODO QUE BORRAr FILAS DE LA BD
        int fila_borrada = BD.delete("productos","id_producto="+id_extra,null);
        BD.close();

        if (fila_borrada>0){
            Toast.makeText(getContext(),"Registro Eliminado ",Toast.LENGTH_SHORT).show();
            //cerrar el fragmento y volver al anterior
            Navigation.findNavController(v).navigateUp();
        }else{
            Toast.makeText(getContext(),getString(R.string.No_existe_articulo),Toast.LENGTH_LONG).show();
        }
    }


    public void guardar(View view) {

//ocultar teclado
        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //buscar codigo de barras
        String buscar_codigo=edittextbarras.getText().toString().trim();

        if (!buscar_codigo.equals("")) {

            if (codigo_original ==null) {
                //CONEXION A LA BASE DE DATOS
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
                SQLiteDatabase BD = admin.getWritableDatabase();
                Cursor cursor = BD.rawQuery("SELECT * FROM productos WHERE codigo = '" + buscar_codigo + "'", null);

                if (cursor.moveToFirst()) {
                    Toast.makeText(getContext(), "Error a guardar, codigo registrado en: " + cursor.getString(1), Toast.LENGTH_LONG).show();
                    edittextbarras.setError("Duplicado");
                    edittextbarras.requestFocus();
                    return;
                }
                BD.close();
            }else{
                if (!codigo_original.equals(buscar_codigo)) {
                    //CONEXION A LA BASE DE DATOS
                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
                    SQLiteDatabase BD = admin.getWritableDatabase();
                    Cursor cursor = BD.rawQuery("SELECT * FROM productos WHERE codigo = '" + buscar_codigo + "'", null);

                    if (cursor.moveToFirst()) {
                        edittextbarras.requestFocus();
                        edittextbarras.setError("Duplicado");
                        Toast.makeText(getContext(), "Error a guardar, codigo registrado en: " + cursor.getString(1), Toast.LENGTH_LONG).show();
                        return;
                    }
                    BD.close();

                }
            }
        }

        //convertir imagen uri a bit
        Bitmap bmp = null;

        try {
            bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri_imagen1);
            ByteArrayOutputStream stream = new ByteArrayOutputStream(8);
            bmp.compress(Bitmap.CompressFormat.JPEG, 16, stream);
            blob=stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String nombre = edittext_nombre.getText().toString().trim();
        String barras = edittextbarras.getText().toString().trim();
        String categoria = combo_categoria.getSelectedItem().toString().trim();
        String visible = "Inactivo";
        String segimiento = "No";
        if (switch_cantidad.isChecked()) {
            segimiento = "Si";
        }
        if (switch_visible.isChecked()) {
            visible = "Activo";
        }
        int cantidad = 0;
        if (!edittext_cantidad.getText().toString().trim().equals("")) {
            cantidad = Integer.parseInt(edittext_cantidad.getText().toString().trim());
        }

        String medida = combo_medida.getSelectedItem().toString().trim();
        String costo = edittext_costo.getText().toString().trim();
        String mayor = edittext_mayor.getText().toString().trim();
        String detal = edittext_detal.getText().toString().trim();
       // String var_imagen1 = imagen1.toString();
        String descripcion = edittext_descripcion.getText().toString().trim();
        String proveedor = edittext_proveedor.getText().toString().trim();
        String talla = edittext_talla.getText().toString().trim();
        String color = edittext_color.getText().toString().trim();
        //String valor_uri = String.valueOf(uri_imagen1);

        if (id_extra != null){
            //MODIFICANDO BASE DE DATOS

            AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
            SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD

            if (nombre.equals("")) {
                Toast.makeText(getContext(), "El nombre del producto no puede estar vacio", Toast.LENGTH_LONG).show();
                return;
            } else {
                ContentValues registro= new ContentValues();

                registro.put("nombre", nombre);
                registro.put("codigo", barras);
                registro.put("categoria", categoria);
                registro.put("cantidad", cantidad);
                registro.put("medida", medida);
                registro.put("p_costo", costo);
                registro.put("p_mayor", mayor);
                registro.put("p_detal", detal);
                registro.put("estado", visible);
                // registro.put("foto1", valor_uri);
                //        registro.put("foto2",var_imagen2);
                //        registro.put("foto3",var_imagen3);
                //        registro.put("foto4",var_imagen4);
                registro.put("proveedor", proveedor);
                registro.put("seguimiento", segimiento);
                registro.put("oferta", 0);
                registro.put("fecha_modificacion", fecha_hora);
                registro.put("usuario_modificacion", "Administrador");
                registro.put("descripcion", descripcion);
                registro.put("talla", talla);
                registro.put("color", color);
                registro.put("imagen",blob);

                //MODIFICAR FILASS
                int filas_modificadas = BaseDeDatos.update("productos",registro,"id_producto="+id_extra,null);
                BaseDeDatos.close();

                if (filas_modificadas>0){
                    Toast.makeText(getContext(),getString(R.string.Registro_modificado),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),getString(R.string.No_existe_articulo),Toast.LENGTH_LONG).show();
                }





            }

        }else{
            //AGREGANDO NUEVOS REGISTROS

            if (nombre.equals("")) {

                edittext_nombre.setError(getString(R.string.Requerido));
                edittext_nombre.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE); imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                return;
            } else {

                if (costo.equals("")) {
                    costo = "0.00";
                }
                if (mayor.equals("")) {
                    mayor = "0.00";
                }
                if (detal.equals("")) {
                    detal = "0.00";
                }

                // CREAR OBJETO DE LA CLASE QUE CONECTA BD
                AdminSQLiteOpenHelper cn = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
                //ABRIR LA BD EN MODO ESCRITURA y lectura
                SQLiteDatabase BaseDeDatos = cn.getWritableDatabase();

                //GUARMOS LOS VALORES ENN CONTENTVALUES
                ContentValues registro = new ContentValues();

                registro.put("nombre", nombre);
                registro.put("codigo", barras);
                registro.put("categoria", categoria);
                registro.put("cantidad", cantidad);
                registro.put("medida", medida);
                registro.put("p_costo", costo);
                registro.put("p_mayor", mayor);
                registro.put("p_detal", detal);
                registro.put("estado", visible);
               // registro.put("foto1", valor_uri);
                //        registro.put("foto2",var_imagen2);
                //        registro.put("foto3",var_imagen3);
                //        registro.put("foto4",var_imagen4);
                registro.put("proveedor", proveedor);
                registro.put("seguimiento", segimiento);
                registro.put("oferta", 0);
                registro.put("fecha_modificacion", fecha_hora);
                registro.put("usuario_modificacion", "Administrador");
                registro.put("descripcion", descripcion);
                registro.put("talla", talla);
                registro.put("color", color);
                registro.put("imagen",blob);

                // INSERTAMOS EN LA BASE DE DATOS UN CONTENTVALUES CON LA INFORMACION
                BaseDeDatos.insert("productos", null, registro);

                //agregamos al carrito de compras el ultimo registro ingresado.
                if(!editText_carrito.getText().toString().trim().equals(null)||editText_carrito.getText().toString().trim().equals("0")){
                Cursor consulta;
                consulta = BaseDeDatos.rawQuery("SELECT * FROM    productos   WHERE   id_producto = (SELECT MAX(id_producto)  FROM productos);", null);
                if (consulta.moveToFirst()) {
                    String ultimo_producto = consulta.getString(0);
                        Principal.lista_inventario.add(new ArrayFacturaActiva(ultimo_producto, costo, editText_carrito.getText().toString().trim(),nombre,barras,null,costo));
                    }
                }


                BaseDeDatos.close();
                Toast.makeText(getContext(), "Registro Guardado", Toast.LENGTH_LONG).show();

            }
        }







        actualizar_recyclerviews();

        //cerrar el fragmento y volver al anterior

        try{
            imagen1=null;
            uri_imagen1=null;
            bmp=null;
            Navigation.findNavController(view).navigateUp();
        }catch (Exception e){
            // getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container_activity_scanner)).commit();
            // getFragmentManager().beginTransaction().remove(this).commit();
            getActivity().getFragmentManager().popBackStack();

        }
    }

    private void actualizar_recyclerviews() {
        // ACTULIZAMOS LAS LISTAS DE  OTROS FRAGMENTOS SI ENTAN ABIERTOS

        if (Frangmento_factura_mayor_inventario.adaptador_la_factura_mayor_inventario!=null) {
            Frangmento_factura_mayor_inventario.lalista=obtener_productos();
            Frangmento_factura_mayor_inventario.adaptador_la_factura_mayor_inventario = new RecyclerViewAdaptador_factura_mayor_inventario(Frangmento_factura_mayor_inventario.lalista);
            Frangmento_factura_mayor_inventario.recyclerViewFacturas.setAdapter(Frangmento_factura_mayor_inventario.adaptador_la_factura_mayor_inventario);
        }

        if (Fragmento_factura_mayor_detalle.adaptador_factura_mayor_detalle!=null) {
            Fragmento_factura_mayor_detalle.adaptador_factura_mayor_detalle = new RecyclerViewAdaptador_factura_mayor_detalle(Frangmento_factura_mayor_inventario.lalista);
            Fragmento_factura_mayor_detalle.recyclerViewProductos.setAdapter(Fragmento_factura_mayor_detalle.adaptador_factura_mayor_detalle);
        }

        if(Fragmento_factura_detal.adaptador_la_factura_inventario!=null) {
            Fragmento_factura_detal.lalista=obtener_productos();
            Fragmento_factura_detal.adaptador_la_factura_inventario = new RecyclerViewAdaptador_Factura(Fragmento_factura_detal.lalista);
            Fragmento_factura_detal.recyclerViewFacturas.setAdapter(Fragmento_factura_detal.adaptador_la_factura_inventario);

        }
        if (Factura_detal.adaptador_factura_detal_detalle!=null){
            Factura_detal.adaptador_factura_detal_detalle= new RecyclerViewAdaptador_factura_detal(Fragmento_factura_detal.lalista);
            Factura_detal.recyclerViewProductos.setAdapter(Factura_detal.adaptador_factura_detal_detalle);
        }

        if(Fragmento_inventario_inventario.adaptador_inventario_inventario!=null) {
            Fragmento_inventario_inventario.lalista=obtener_productos();
            Fragmento_inventario_inventario.adaptador_inventario_inventario = new RecyclerViewAdaptador_inventario_inventario( Fragmento_inventario_inventario.lalista);
            Fragmento_inventario_inventario.recyclerViewFacturas.setAdapter(Fragmento_inventario_inventario.adaptador_inventario_inventario);
        }

        if(Fragmento_inventario_detalle.adaptador_inventario_detalle!=null) {
            Fragmento_inventario_detalle.adaptador_inventario_detalle = new RecyclerViewAdaptador_inventario_detalle( Fragmento_inventario_inventario.lalista);
            Fragmento_inventario_detalle.recyclerViewProductos.setAdapter(Fragmento_inventario_detalle.adaptador_inventario_detalle);
        }


        if(Fragemento_registro_editar_agregarProducto.adaptador_registro_agregarProducto!=null) {
            Fragemento_registro_editar_agregarProducto.lalista=obtener_productos();
            Fragemento_registro_editar_agregarProducto.adaptador_registro_agregarProducto = new RecyclerViewAdaptador_registro_AgregarProducto(Fragemento_registro_editar_agregarProducto.lalista);
            Fragemento_registro_editar_agregarProducto.recyclerViewFacturas.setAdapter(Fragemento_registro_editar_agregarProducto.adaptador_registro_agregarProducto);
        }


    }

    //PARA LOS RECYCLIERVIEW DE OTROS ACTIVITIS
    public List<ProductosModelo> obtener_productos() {


        String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;
        Cursor consulta;

        if(lalista!=null)   lalista.clear();

        // CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        consulta = BD.rawQuery(
                "SELECT * FROM productos WHERE estado='Activo' ORDER BY estado, categoria, nombre", null);

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
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_detal,temp_mayor, temp_estado, temp_foto1, imagen_byte));
                }else if(padre.equals("detal")){
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));
                }else if(padre.equals("inventario")){
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_costo, temp_costo, temp_estado, temp_foto1, imagen_byte));
                }


            } while (consulta.moveToNext());
        }
        BD.close();
        return lalista;
    }



//    public void actualizar_recyclerViews(){
//        try{
//            Fragmento_factura_detal.adaptador_la_factura_inventario.notifyDataSetChanged();
//        }catch (Exception e){
//
//        }
//        try{
//            Factura_detal.adaptador_factura_detal_detalle.notifyDataSetChanged();
//
//        }catch (Exception e){
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (existe==false) {
            //mostrar teclado
            edittext_nombre.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        }else{
            //boton guardar invisibe. guarda con tecla back
        //    floatingActionButton_guardar.setVisibility(View.GONE);
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
                guardar(vista);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Runtime.getRuntime().gc();
    }

}
