package inventario.fragmentos;

import static com.cataplus.facturacionycatalogospdf.Principal.format;
import static com.cataplus.facturacionycatalogospdf.Principal.lista_inventario;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;
import static com.cataplus.facturacionycatalogospdf.Principal.valor_inventario;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.Activity_scanner;
import com.cataplus.facturacionycatalogospdf.AdminSQLiteOpenHelper;
import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_inventario_inventario;
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

//CAMBIADO A COMPRAS
public class Fragmento_inventario_inventario extends Fragment {

    //  public static RecyclerViewAdaptador_factura_detal adaptador_factura_detal_detalle;
    public static RecyclerViewAdaptador_inventario_inventario adaptador_inventario_inventario;
    public static RecyclerView recyclerViewFacturas;
    public static TextView monto_factura;
    static EditText editText_buscar;
    static View vista;
    public static List<ProductosModelo> lalista;
    static Cursor consulta;
    private Button boton_scanear, button_borrar_pedido, button_buscar, button_nuevo_producto;
    private RadioButton radioButton_nombre, radioButton_codigo, radioButton_id;
    private static final String PREFERENCIA_INVENTARIO = "preferencia_inventario";






    String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;



    public Fragmento_inventario_inventario() {
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
        vista = inflater.inflate(R.layout.fragment_fragmento_inventario_inventario, container, false);

        lalista = new ArrayList<>();

     //   monto_factura = (TextView) vista.findViewById(R.id.textView_valor_inventario);
        editText_buscar = (EditText) vista.findViewById(R.id.editTextText_buscar_fragmento);
       // textView_valor_inventarior = vista.findViewById(R.id.textView_valor_inventario);
        monto_factura=vista.findViewById(R.id.textView_valor_factura);
        boton_scanear = vista.findViewById(R.id.button_escanear);
        button_borrar_pedido=vista.findViewById(R.id.button_borrar_pedido);
        button_buscar=vista.findViewById(R.id.button_buscar);
        button_nuevo_producto=vista.findViewById(R.id.nuevo_producto);
        radioButton_nombre=vista.findViewById(R.id.radioButton_nombre);
        radioButton_codigo=vista.findViewById(R.id.radioButton_barras);
        radioButton_id=vista.findViewById(R.id.radioButton_id);

        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
        recyclerViewFacturas = (RecyclerView) vista.findViewById(R.id.recycler_inventario);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewFacturas.setLayoutManager(new LinearLayoutManager(getContext()));

        adaptador_inventario_inventario = new RecyclerViewAdaptador_inventario_inventario(obtener_productos());
        recyclerViewFacturas.setAdapter(adaptador_inventario_inventario);




        if (editText_buscar.getText().toString().trim().equals("")) {
//        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            String jsonString = pref.getString(PREFERENCIA_INVENTARIO, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayFacturaActiva>>() {
            }.getType();
            ArrayList<ArrayFacturaActiva> listPrefer = gson.fromJson(jsonString, type);

            lista_inventario = (ArrayList) listPrefer.clone();

            //ELIMINAR FORMATO PARA GUARDAR VALOR EN DOUBLE
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2); //Define 2 decimales.

        }

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);

        button_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrar(v);
            }
        });
        button_nuevo_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("padre","inventario");
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

                //temp_detal y temp_mayor invertidas en el fragmento mayorista ojo
                lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_detal, temp_mayor, temp_estado, temp_foto1, imagen_byte));


            } while (consulta.moveToNext());
        }

        BD.close();
        InputMethodManager input = (InputMethodManager) (getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE));
        input.hideSoftInputFromWindow(v.getWindowToken(), 0);
        adaptador_inventario_inventario.notifyDataSetChanged();

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

                lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_detal, temp_mayor, temp_estado, temp_foto1, imagen_byte));
            } while (consulta.moveToNext());
        }

        BD.close();

        return lalista;

    }

    public void escanear_en_activity() {
        Intent intent = new Intent(getActivity(), Activity_scanner.class);
        intent.putExtra("padre", "Fragmento_inventario_inventario");
        startActivity(intent);

    }

    public void borrar_pedido(View view_pedido) {

        //ALERT DIALOGO
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.Atencion));
        builder.setMessage(getString(R.string.Esta_apunto_de_eliminar));

        builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filtrar(view_pedido);
                lista_inventario.clear();

                //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
                Gson gson = new Gson();
                String jsonString = gson.toJson(lista_inventario);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREFERENCIA_INVENTARIO, jsonString);
                editor.apply();
                valor_inventario = 0.00;
                monto_factura.setText("0");
                adaptador_inventario_inventario.notifyDataSetChanged();
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
        valor_inventario = 0;
try{
    for (int x = 0; x < Principal.lista_inventario.size(); x++) {
        ArrayFacturaActiva productos = Principal.lista_inventario.get(x);
        String precio_producto = productos.getPrecio_producto();
        String seleccion = productos.getSeleccion_producto();
        double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
        Principal.valor_inventario = Principal.valor_inventario + valorseleccion;
    }
    monto_factura.setText(format.format(valor_inventario));
}catch (Exception e){

}

    }

    @Override
    public void onResume() {
        super.onResume();
        actualizar_totales();
       if (adaptador_inventario_inventario!=null) adaptador_inventario_inventario.notifyDataSetChanged();
    }



}