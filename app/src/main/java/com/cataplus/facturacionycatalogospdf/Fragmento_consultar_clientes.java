package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_clientes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class Fragmento_consultar_clientes extends Fragment {


    private RecyclerView recyclerViewClientes;
    private RecyclerViewAdaptador_clientes adaptadorClientes;
    public static String padre_factura="";
    private Button button_nuevo_cliente;
    private List<ClienteModelo> cliente ;
    private Cursor consulta;
    private EditText editText_buscar_cliente;

    public Fragmento_consultar_clientes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View vista= inflater.inflate(R.layout.fragment_fragmento_consultar_clientes, container, false);
        cliente= new ArrayList<>();

        //RELACIONAMOS LOS ELEMENTOS DEL ACTIVITY
        recyclerViewClientes= (RecyclerView) vista.findViewById(R.id.recycler_facturar);
        //LO COLOCAMOS TIPO LISTA
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(getContext()));
        editText_buscar_cliente=(EditText)vista.findViewById(R.id.editTextText_buscar);

        adaptadorClientes = new RecyclerViewAdaptador_clientes(obtener_clientes());
        recyclerViewClientes.setAdapter(adaptadorClientes);

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);


        if (getArguments() != null) {
            padre_factura =getArguments().getString("padre");
        }



        button_nuevo_cliente=(Button)vista.findViewById(R.id.nuevo_cliente);
        button_nuevo_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nuevo cliente
                nuevo_cliente(v);
            }
        });

        editText_buscar_cliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //accion al precionar una tecla cualquiera
                filtrar_cliente(editText_buscar_cliente.getText().toString().trim());
            }
        });

return vista;
    }


    private void nuevo_cliente(View v) {

        Navigation.findNavController(v).navigate(R.id.fragmento_agregar_cliente);
    }

    public List<ClienteModelo>obtener_clientes(){
        cliente.clear();
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        consulta = BD.rawQuery(
                "SELECT * FROM clientes", null);

        if (consulta.moveToFirst()){
            do {

                cliente.add(new ClienteModelo(consulta.getString(0),consulta.getString(1), consulta.getString(2), consulta.getString(3), consulta.getString(4), consulta.getString(5), consulta.getString(6), consulta.getString(7), consulta.getString(8)));

            }while (consulta.moveToNext());
        }

        BD.close();

        return cliente;
    }

    public void filtrar_cliente(String nombre){
        cliente.clear();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();

        consulta = BD.rawQuery(


                "SELECT * FROM clientes WHERE nombre LIKE '%" + nombre + "%'ORDER BY nombre", null);


        if (consulta.moveToFirst()){
            do {

                cliente.add(new ClienteModelo(consulta.getString(0),consulta.getString(1), consulta.getString(2), consulta.getString(3), consulta.getString(4), consulta.getString(5), consulta.getString(6), consulta.getString(7), consulta.getString(8)));

            }while (consulta.moveToNext());
        }

        BD.close();
        adaptadorClientes.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerViewClientes=null;
        adaptadorClientes=null;
        cliente.clear();
        cliente=null;
        consulta=null;
        Runtime.getRuntime().gc();
    }
}