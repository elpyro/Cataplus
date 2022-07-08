package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.Fragmento_registro_editar.textView_tipo;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;


public class Fragmento_registro_editar_producto extends Fragment {


    public Fragmento_registro_editar_producto() {
        // Required empty public constructor
    }



    private EditText editText_nombre, editText_cantidad, editText_precio, editText_barras;
    private TextView textView_total;
    public static ImageView imageView_foto1;
    private String id_extra,id_factura_bd, id_producto_tabla_productos;
    private int cantidad_inicial;

    private Uri resultUri;
    public static Uri imageUri;
    private Button button_incrementar, button_disminuir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_fragmento_registro_editar_producto, container, false);



        editText_nombre=vista.findViewById(R.id.editTextTextPersonName9);
        editText_cantidad=vista.findViewById(R.id.editTextNumber4);
        editText_precio=vista.findViewById(R.id.editTextNumberDecimal2);
        textView_total=vista.findViewById(R.id.textView25);
        editText_barras=vista.findViewById(R.id.editTextTextPersonName3);
        imageView_foto1=vista.findViewById(R.id.imageView3);
        button_incrementar=vista.findViewById(R.id.button6);
        button_disminuir=vista.findViewById(R.id.button8);

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);



        button_incrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementar(null);
            }
        });
        button_disminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disminuir(null);
            }
        });


        // ACCION AL PRECIONAR IMAGEN
//        imageView_foto1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // muestra el cuadro de diálogo de selección de imagen
//
//                Intent intent = new Intent(getActivity(), Activity_simple_foto.class);
//                getActivity().startActivity(intent);
//            }
//        });

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.




        if (getArguments() != null) {
            id_extra =getArguments().getString("id_factura");

            //CONEXION A LA BASE DE DATOS
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
                SQLiteDatabase BD = admin.getWritableDatabase();

                Cursor consulta;
                consulta = BD.rawQuery("SELECT * FROM producto_factura_detal WHERE id_producto = '" + id_extra + "'", null);

                if (consulta.moveToFirst()) {
                    do {
                        String id_producto = consulta.getString(0);
                        id_factura_bd = consulta.getString(1);
                        String nombre_producto = consulta.getString(2);
                        String precio_producto = consulta.getString(3);
                        String foto1_producto = consulta.getString(4);
                        String barras_producto = consulta.getString(5);
                        String cantidad = consulta.getString(6);
                        id_producto_tabla_productos=consulta.getString(7);

                        cantidad_inicial= Integer.parseInt(cantidad);
                        double sub_total = (Double.parseDouble(precio_producto) * Double.parseDouble(cantidad));

                        editText_nombre.setText(nombre_producto);
                        editText_cantidad.setText(cantidad);
                        editText_precio.setText(precio_producto);
                        textView_total.setText(format.format(sub_total));
                        editText_barras.setText(barras_producto);

                        //                            imageUri = Uri.parse(consulta.getString(4));
//                            Uri imagenUri = Uri.parse(consulta.getString(4));
//                            imageView_foto1.setImageURI(imagenUri);


                        //cursor para colocar la foto  existente del producto en la base de datos
                        Cursor cursor=BD.rawQuery("SELECT * FROM productos WHERE id_producto = '"+id_producto_tabla_productos+"'",null);
                        if (cursor.moveToFirst()) {
                            if (!cursor.getBlob(23).equals(null)) {
                                byte[] imagen = cursor.getBlob(23);
                                ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen);
                                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                                imageView_foto1.setImageBitmap(theImage);
                            }
                        }

                    } while (consulta.moveToNext());


                }
                BD.close();


            }


        editText_cantidad.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //getContext() is for backspace
                    if (editText_cantidad.getText().toString().trim().length() < 1) {
                        editText_cantidad.setText("0");
                    }
                }
                calcular_total(null);
                return false;
            }
        });

        editText_precio.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //getContext() is for backspace
                    if (editText_precio.getText().toString().trim().length() < 1) {
                        editText_precio.setText("0");

                    }
                }
                calcular_total(null);
                return false;
            }
        });
        return vista;
    }

//    public void Eliminar(View view){
//
//        //ALERT DIALOGO
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Eliminar Producto");
//        builder.setMessage("¿Esta seguro?");
//
//        builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                //CONEXION A LA BASE DE DATOS
//                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
//                SQLiteDatabase BD = admin.getWritableDatabase();
//
//                //METODO QUE BORRAr FILAS DE LA BD
//                int fila_borrada = BD.delete("producto_factura_detal","id_producto="+id_extra,null);
//                BD.close();
//
//                if (fila_borrada>0){
//                //volver al fragemnto anterior
//                    Toast.makeText(getContext(), "Registro eliminado", Toast.LENGTH_SHORT).show();
//                    Navigation.findNavController(view).navigateUp();
//                }else{
//                    Toast.makeText(getContext(),getString(R.string.No_existe_articulo),Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//        builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        int cantidad_final = Integer.parseInt(editText_cantidad.getText().toString().trim());



    //CONEXION A LA BASE DE DATOS
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos", null, 1);
    SQLiteDatabase BD = admin.getWritableDatabase();


    Cursor cursor = BD.rawQuery("SELECT * FROM productos WHERE id_producto = '" + id_producto_tabla_productos + "'", null);

        //AJUSTAR LAS CANTIDADES DEL INVETARIO
    if (cursor.moveToFirst()) {
        int cantidad_base_datos= Integer.parseInt(cursor.getString(5));
        if (cantidad_inicial > cantidad_final) {

            cantidad_final=cantidad_inicial-cantidad_final;
            int cantidad=0;
             if (textView_tipo.getText().toString().trim().equals("Compra")) {
                  cantidad=cantidad_base_datos-cantidad_final;
             }else{
                  cantidad=cantidad_final+cantidad_base_datos;
             }


            ContentValues actualizar_cantidades = new ContentValues();
            actualizar_cantidades.put("cantidad", cantidad);
            //MODIFICAR FILASS
            int filas_modificadas = BD.update("productos", actualizar_cantidades, "id_producto=" + id_producto_tabla_productos, null);

        }else if (cantidad_inicial<cantidad_final){
            cantidad_final=cantidad_final-cantidad_inicial;

            int cantidad=0;
            if (textView_tipo.getText().toString().trim().equals("Compra")||textView_tipo.getText().toString().trim().equals("Buy")) {
                cantidad=cantidad_final+cantidad_base_datos;
            }else{
                cantidad=cantidad_base_datos-cantidad_final;
            }


            ContentValues actualizar_cantidades = new ContentValues();
            actualizar_cantidades.put("cantidad", cantidad);
            //MODIFICAR FILASS
            int filas_modificadas = BD.update("productos", actualizar_cantidades, "id_producto=" + id_producto_tabla_productos, null);
        }
    }


    //ELIMINAR DE LA FACTUAR SI EL VALOR ES 0
    int cant = Integer.parseInt(editText_cantidad.getText().toString().trim());
    if (cant < 1) {
        int fila_borrada = BD.delete("producto_factura_detal", "id_producto=" + id_extra, null);
        if (fila_borrada > 0) {
            //volver al fragemnto anterior
            Toast.makeText(getContext(), getString(R.string.Registro_eliminado), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.No_existe_articulo), Toast.LENGTH_LONG).show();
        }

    } else {

        //ACTUALIZAR EL PRODUCTO DE LA FACTURA
        ContentValues registro_producto = new ContentValues();
        registro_producto.put("nombre_producto", String.valueOf(editText_nombre.getText()).trim());
        registro_producto.put("precio_producto", String.valueOf(editText_precio.getText()));
 //       registro_producto.put("foto1_producto", String.valueOf(imageUri));
        registro_producto.put("barras_producto", String.valueOf(editText_barras.getText()));
        registro_producto.put("cantidad", String.valueOf(editText_cantidad.getText()));

        //MODIFICAR FILASS
        int filas_modificadas = BD.update("producto_factura_detal", registro_producto, "id_producto=" + id_extra, null);
        BD.close();

        if (filas_modificadas > 0) {
            Toast.makeText(getContext(), getString(R.string.Registro_modificado), Toast.LENGTH_SHORT).show();

        }
    }
}


    public  void incrementar(View v){
        if (editText_cantidad.getText()!= null){
            int valor= Integer.parseInt(editText_cantidad.getText().toString().trim());
            valor=valor+1;
            editText_cantidad.setText(valor+"");
            calcular_total(null);
        }
    }

    public  void disminuir(View v){
        if (editText_cantidad.getText()!= null){

            int valor= Integer.parseInt(editText_cantidad.getText().toString().trim());
            if (valor>0){
                valor=valor-1;
                editText_cantidad.setText(valor+"");
                calcular_total(null);
            }

        }
    }

    public void calcular_total(View v){
        int cantidad= Integer.parseInt(editText_cantidad.getText().toString().trim());
        double precio= Double.parseDouble(editText_precio.getText().toString().trim());
        double total=cantidad*precio;
        //ELIMINAR FORMATO PARA GUARDAR VALOR EN DOUBLE
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.
        textView_total.setText(format.format(total));

    }



}