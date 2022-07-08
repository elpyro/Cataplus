package com.cataplus.facturacionycatalogospdf;





import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.databinding.ActivityPrincipalBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

import arraylistas.ArrayFacturaActiva;


public class Principal extends AppCompatActivity {

    public static TextView factura_pendiente, textView_factura_pendiente_mayor;

    public static ProgressDialog progressDialog;
    public static double valor_factura=0;
    public static double valor_factura_mayor=0;
    public static double valor_inventario=0;
    public static ArrayList<ArrayFacturaActiva> lista_factura_detal=new ArrayList<ArrayFacturaActiva>();
    public static ArrayList<ArrayFacturaActiva> lista_factura_mayor=new ArrayList<ArrayFacturaActiva>();
    public static ArrayList<ArrayFacturaActiva> lista_inventario=new ArrayList<ArrayFacturaActiva>();

    public static DecimalFormat format;

    private ImageView imageView_logo;
    private LinearLayout linearLayout_noticias;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPrincipalBinding binding;
    private  final String LIST_KEY = "list_key100";//PREFERENCIA DETAL
    private  final String PREFENCIA_MAYOR = "preferencia_mayor";
    private  final String PREFENCIA_INVENTARIO = "preferencia_inventario";

    private  static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    // matrices de permisos
    private String[] cameraPermissions; // cámara y almacenamiento
    private String [] storagePermissions;// solo almacenamiento
    public static View vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Fuente: https://www.iteramos.com/pregunta/8924/como-otener-la-vista-del-contenido-de-la-actividadb

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarPrincipal.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.fragmento_reportes,R.id.vista_pdf, R.id.fragmento_registro_editar,R.id.contenedor_fragmentos_inventario, R.id.fragmento_incluir_producto,R.id.fragmento_consultar_productos, R.id.fragmento_agregar_cliente,  R.id.fragmento_consultar_clientes, R.id.fragmentEmpresa, R.id.contenedorFragment, R.id.fragmento_Registros, R.id.contenedor_fragmento_mayor,R.id.settingsFragment, R.id.fragmento_registro_editar_producto,R.id.fragment_factura_pdf, R.id.fragemento_registro_editar_agregarProducto)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //CONEXION A LA BASE DE DATOS borrando todos los registros
//        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos", null, 1);
//        SQLiteDatabase BD = admin.getWritableDatabase();
//        BD.delete("producto_factura_detal", null, null);
//        BD.delete("factura_detal", null, null);
//        BD.close();



//        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World! hola mundo2");

        imageView_logo=findViewById(R.id.imageView4);


        // RESUELVE EL ERROR Row too big to fit into CursorWindow requiredPos=0, totalRows=1; query: CUANDO SE GUARDA BLOB EN LA BASE DE DATOS
        //PEGAR EN EL MAIN ACTIVITY O ACTIVITY INICIAL
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        //permisos de almacenamiento y camara
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!checkCameraPermission()){
            requestCameraPermission();
        }

        //ELIMINAR FORMATO PARA GUARDAR VALOR EN DOUBLE
        format = new DecimalFormat();
        format.setMaximumFractionDigits(2); //Define 2 decimales.

        //llamar las preferencias guardadas
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        Preferencias_app.obtener_preferencias(preferencias,this);



        //TRATAR DE LEER LA REFENCIA DEL ARRAY DETAL, SI NO EXISTE CREARLA
        try {
            //        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String jsonString = pref.getString(LIST_KEY, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayFacturaActiva>>() {
            }.getType();
            ArrayList<ArrayFacturaActiva> listPrefer = gson.fromJson(jsonString, type);

            lista_factura_detal = (ArrayList) listPrefer.clone();

            //VERIFICAR EL ARRAY Y CONTAR, CANTIDADES POR LOS PRECIOS PARA TENER TOTAL DE FACTURA
            valor_factura = 0;
            // Facturar.monto_factura.setText("0");
            for (int x = 0; x < lista_factura_detal.size(); x++) {
                ArrayFacturaActiva productos = lista_factura_detal.get(x);
                String precio_producto = productos.getPrecio_producto();
                String seleccion = productos.getSeleccion_producto();
                double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
                valor_factura = valor_factura + valorseleccion;
                format.setMaximumFractionDigits(2); //Define 2 decimales.
                //   Facturar.monto_factura.setText(format.format(valor_factura));
            }
        }catch (Exception e){
            //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
            Gson gson = new Gson();
            String jsonString = gson.toJson(lista_factura_detal);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(LIST_KEY, jsonString);
            editor.apply();

        }
        //TRATAR DE LEER LA REFENCIA DEL ARRAY MAYORISTA, SI NO EXISTE CREARLA
        try {
            //        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String jsonString = pref.getString(PREFENCIA_MAYOR, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayFacturaActiva>>() {
            }.getType();
            ArrayList<ArrayFacturaActiva> listPrefer = gson.fromJson(jsonString, type);

            lista_factura_mayor = (ArrayList) listPrefer.clone();

            //VERIFICAR EL ARRAY Y CONTAR, CANTIDADES POR LOS PRECIOS PARA TENER TOTAL DE FACTURA
            valor_factura_mayor = 0;
            // Facturar.monto_factura.setText("0");
            for (int x = 0; x < lista_factura_mayor.size(); x++) {
                ArrayFacturaActiva productos = lista_factura_mayor.get(x);
                String precio_producto = productos.getPrecio_producto();
                String seleccion = productos.getSeleccion_producto();
                double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
                valor_factura_mayor = valor_factura_mayor + valorseleccion;
                format.setMaximumFractionDigits(2); //Define 2 decimales.
                //   Facturar.monto_factura.setText(format.format(valor_factura));
            }
        }catch (Exception e){
            //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
            Gson gson = new Gson();
            String jsonString = gson.toJson(lista_factura_mayor);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFENCIA_MAYOR, jsonString);
            editor.apply();

        }

        //TRATAR DE LEER LA REFENCIA DEL ARRAY INVENTARIO, SI NO EXISTE CREARLA
        try {
            //        //     RECUPERAR ARRAYLIST GUARDAO EN PREFERENCIAS     implementation 'com.google.code.gson:gson:2.8.6'
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String jsonString = pref.getString(PREFENCIA_INVENTARIO, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayFacturaActiva>>() {
            }.getType();
            ArrayList<ArrayFacturaActiva> listPrefer = gson.fromJson(jsonString, type);

            lista_inventario = (ArrayList) listPrefer.clone();

            //VERIFICAR EL ARRAY Y CONTAR, CANTIDADES POR LOS PRECIOS PARA TENER TOTAL DE FACTURA
            valor_inventario = 0;
            // Facturar.monto_factura.setText("0");
            for (int x = 0; x < lista_inventario.size(); x++) {
                ArrayFacturaActiva productos = lista_inventario.get(x);
                String precio_producto = productos.getPrecio_producto();
                String seleccion = productos.getSeleccion_producto();
                double valorseleccion = Double.parseDouble(seleccion) * Double.parseDouble(precio_producto);
                valor_inventario = valor_inventario + valorseleccion;
                format.setMaximumFractionDigits(2); //Define 2 decimales.
                //   Facturar.monto_factura.setText(format.format(valor_factura));
            }
        }catch (Exception e){
            //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
            Gson gson = new Gson();
            String jsonString = gson.toJson(lista_inventario);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREFENCIA_INVENTARIO, jsonString);
            editor.apply();

        }



    }



//PERMISOS DE CAMARA Y ALMACENAMIENTO
    private boolean checkStoragePermission(){
        //comprobar si el permiso de almacenamiento está habilitado o no
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private  void requestStoragePermission(){
        // solicita el permiso de almacenamiento
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        // verifica si el permiso de la cámara está habilitado o no
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        // solicita el permiso de la cámara
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // resultado del permiso permitido / denegado
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){

                    }
                    else{
                       Toast.makeText(this, getString(R.string.Se_requieren_permisos), Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){

                    // si se permite devolver verdadero de lo contrario falso
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        // permiso de almacenamiento permitido

                    }
                    else{
                        Toast.makeText(this, getString(R.string.Se_requieren_permiso_almacenamiento), Toast.LENGTH_LONG).show();
                    }
                }

            }
            break;
        }
    }



    @Override
    public void onStart() {
        super.onStart();









//        imageView_logo.setVisibility(View.GONE);
//        linearLayout_noticias.setVisibility(View.VISIBLE);
//        if(valor_factura==0 && valor_factura_mayor==0){
//            imageView_logo.setVisibility(View.VISIBLE);
//            linearLayout_noticias.setVisibility(View.GONE);
//        }
//
//
//        textView_factura_pendiente_mayor.setText(format.format(valor_factura_mayor));
//        factura_pendiente.setText(format.format(valor_factura));
//



    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // CREA EL MENU CON TRES PUNTICOS
//        getMenuInflater().inflate(R.menu.menu_agregar, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



}