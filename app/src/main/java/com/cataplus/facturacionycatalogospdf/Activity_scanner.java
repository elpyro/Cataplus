package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.Principal.lista_factura_detal;
import static com.cataplus.facturacionycatalogospdf.Principal.lista_factura_mayor;
import static com.cataplus.facturacionycatalogospdf.Principal.lista_inventario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.databinding.ActivityPrincipalBinding;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Factura;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Frangmento_factura_mayor_inventario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.zxing.Result;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import arraylistas.ArrayFacturaActiva;
import fragmentos.Factura_detal;
import fragmentos.Fragmento_factura_detal;
import inventario.fragmentos.Fragmento_inventario_inventario;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Activity_scanner extends AppCompatActivity {

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private ZXingScannerView vistaescaner;

    private TextView tvCodigoLeido;
    boolean linterna = Preferencias_app.linterna;

    private RecyclerView recyclerViewFacturas;
    private String precio="";
    private RecyclerViewAdaptador_Factura adaptador_la_factura;
    public static TextView monto_factura;
    private final String LIST_KEY = "list_key100";
    private  final String PREFENCIA_MAYOR = "preferencia_mayor";
    private  final String PREFENCIA_INVENTARIO = "preferencia_inventario";
    private Toast ImageToast=null;
    private FloatingActionButton floatingActionButton_agregar_producto;

    private Button boton_buscar;

    public static EditText editText_buscar;

    private String buscar_codigo = "";

    private List<ProductosModelo> lalista = new ArrayList<>();

    private String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;

    private Cursor consulta;
    private String padre="";

    private int bandera_camara = 0;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);



        Bundle extras=getIntent().getExtras();
        if (extras!=null) {

            if (extras.getString("padre") != null) {
                padre = extras.getString("padre");
            }
        }



      escaner2(null);
    }


    public void filtrar_codigo() {
        //CONEXION A LA BASE DE DATOS

        Bitmap imagen=null;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();
        consulta = BD.rawQuery(
                "SELECT * FROM productos WHERE codigo= '" + buscar_codigo + "'", null);

        String tipo="";

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

                int cantidades_toast=0;
                if(padre.equals("Fragmento_factura_mayor_inventario")){
                    //mayorista
                    tipo= getString(R.string.Mayor);
                    byte[] imagen_byte = consulta.getBlob(23);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen_byte);
                    imagen = BitmapFactory.decodeStream(imageStream);

                    Frangmento_factura_mayor_inventario.lalista.clear();
                    Frangmento_factura_mayor_inventario.lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo,  temp_detal,temp_mayor, temp_estado, temp_foto1, imagen_byte));
                    imagen_byte=null;

                    // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
                    ArrayFacturaActiva busqueda = new ArrayFacturaActiva(temp_id, temp_detal, "", temp_nombre, temp_barras, temp_foto1,temp_costo);
                    int existe = 0;
                    int cantidad_guardada_mayor = 0;
                    for (int x = 0; x < lista_factura_mayor.size(); x++) {
                        ArrayFacturaActiva productos = lista_factura_mayor.get(x);
                        if (productos.getId_producto().equals(busqueda.getId_producto())) {
                            cantidad_guardada_mayor = Integer.parseInt(productos.getSeleccion_producto());
                            cantidad_guardada_mayor = cantidad_guardada_mayor + 1;
                            lista_factura_mayor.remove(x);

                            existe = 1;
                            break;
                        }
                    }
                    precio=temp_mayor;
                    if (existe == 0) {
                        lista_factura_mayor.add(new ArrayFacturaActiva(temp_id, temp_mayor, "1", temp_nombre, temp_barras, temp_foto1,temp_costo));
                        cantidad_guardada_mayor = 1;
                    } else {
                        lista_factura_mayor.add(new ArrayFacturaActiva(temp_id, temp_mayor, cantidad_guardada_mayor + "", temp_nombre, temp_barras, temp_foto1,temp_costo));
                    }
                    cantidades_toast=cantidad_guardada_mayor;

                    //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(lista_factura_mayor);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(PREFENCIA_MAYOR, jsonString);
                    editor.apply();


                }else if( padre.equals("Fragmento_factura_detal")) {
                    //ARRAY FACTURA DETAL
                    tipo= getString(R.string.Detal);
                    byte[] imagen_byte = consulta.getBlob(23);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen_byte);
                    imagen = BitmapFactory.decodeStream(imageStream);

                    Fragmento_factura_detal.lalista.clear();
                    Fragmento_factura_detal.lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_estado, temp_foto1, imagen_byte));
                    imagen_byte=null;

                    // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
                    ArrayFacturaActiva busqueda = new ArrayFacturaActiva(temp_id, temp_detal, "", temp_nombre, temp_barras, temp_foto1,temp_costo);
                    int existe = 0;
                    int cantidad_guardad = 0;
                    for (int x = 0; x < lista_factura_detal.size(); x++) {
                        ArrayFacturaActiva productos = lista_factura_detal.get(x);
                        if (productos.getId_producto().equals(busqueda.getId_producto())) {
                            cantidad_guardad = Integer.parseInt(productos.getSeleccion_producto());
                            cantidad_guardad = cantidad_guardad + 1;
                            lista_factura_detal.remove(x);

                            existe = 1;
                            break;
                        }
                    }
                    precio=temp_detal;
                    if (existe == 0) {
                        lista_factura_detal.add(new ArrayFacturaActiva(temp_id, temp_detal, "1", temp_nombre, temp_barras, temp_foto1,temp_costo));
                        cantidad_guardad = 1;
                    } else {
                        lista_factura_detal.add(new ArrayFacturaActiva(temp_id, temp_detal, cantidad_guardad + "",temp_nombre, temp_barras, temp_foto1,temp_costo));
                    }
                    cantidades_toast=cantidad_guardad;

                    //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(lista_factura_detal);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(LIST_KEY, jsonString);
                    editor.apply();

                }else if(padre.equals("Fragmento_inventario_inventario")){
                    //ARRAY inventario
                    tipo= getString(R.string.Inventario);

                    byte[] imagen_byte = consulta.getBlob(23);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen_byte);
                    imagen = BitmapFactory.decodeStream(imageStream);

                    Fragmento_inventario_inventario.lalista.clear();
                    Fragmento_inventario_inventario. lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad, temp_costo, temp_detal, temp_mayor, temp_estado, temp_foto1, imagen_byte));
                    imagen_byte=null;
                    // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
                    ArrayFacturaActiva busqueda = new ArrayFacturaActiva(temp_id, temp_costo, "",temp_nombre, temp_barras, temp_foto1,temp_costo);
                    int existe = 0;
                    int cantidad_guardad = 0;

                    for (int x = 0; x < lista_inventario.size(); x++) {
                        ArrayFacturaActiva productos = lista_inventario.get(x);
                        if (productos.getId_producto().equals(busqueda.getId_producto())) {
                            try{
                                cantidad_guardad = Integer.parseInt(productos.getSeleccion_producto());
                                cantidad_guardad = cantidad_guardad + 1;
                                lista_inventario.remove(x);

                                existe = 1;
                                break;
                            }catch (Exception e){

                                cantidad_guardad = cantidad_guardad + 1;
                                lista_inventario.remove(x);
                                existe = 1;
                                break;
                            }

                        }
                    }
                    precio=temp_costo;
                    if (existe == 0) {
                        lista_inventario.add(new ArrayFacturaActiva(temp_id, temp_costo, "1",temp_nombre, temp_barras, temp_foto1,temp_costo));
                        cantidad_guardad = 1;
                    } else {
                        lista_inventario.add(new ArrayFacturaActiva(temp_id, temp_costo, cantidad_guardad + "",temp_nombre, temp_barras, temp_foto1,temp_costo));
                    }
                    cantidades_toast=cantidad_guardad;

                    //  GUARDAR EN PREFERENCIAS EL ARRAYLIST  implementation 'com.google.code.gson:gson:2.8.6'
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(lista_inventario);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(PREFENCIA_INVENTARIO, jsonString);
                    editor.apply();
                }

                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);


         //       IMAGEN EN TOAST
//                Uri resultUri = Uri.parse(consulta.getString(11));
//               Uri imageUri = resultUri;

        ImageToast = new Toast(getBaseContext());
        LinearLayout toastLayout = new LinearLayout(getBaseContext());
        toastLayout.setOrientation(LinearLayout.VERTICAL);
        ImageView image = new ImageView(getBaseContext());

                TextView text = new TextView(getBaseContext());
                TextView text_espacio = new TextView(getBaseContext());
      //  image.setImageResource(R.drawable.ic_empresa);
                image.setImageBitmap(imagen);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(600, 600);
                image.setLayoutParams(layoutParams);

        text.setText( temp_nombre +"\n"+tipo+" " +precio+ "\n" +" X "+ cantidades_toast);

        text.setGravity(Gravity.CENTER);
        text.setTextColor(Color.parseColor("#000000"));
        text.setBackgroundColor(Color.parseColor("#FFFFFF"));
        text.setTextSize(15);
        toastLayout.addView(text);
        toastLayout.addView(image);

        ImageToast.setView(toastLayout);
        ImageToast.setGravity(Gravity.TOP,0 ,140 );
        ImageToast.setDuration(Toast.LENGTH_LONG);
        ImageToast.show();
        image=null;
        imagen=null;






            } while (consulta.moveToNext());
        } else {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
            Toast.makeText(this, getString(R.string.Codigo_no_exitente), Toast.LENGTH_SHORT).show();

//
//            //ALERT DIALOGO
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Este codigo no existe");
//            builder.setMessage("¿Desea crear este producto?");
//
//            builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    Intent intent = new Intent(getApplicationContext(), Incluir_productos.class);
//                    intent.putExtra("id_producto_nuevo", buscar_codigo);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            });
//
//
//            AlertDialog dialog = builder.create();
//            dialog.show();

        }
        consulta.close();
        BD.close();


    }


    // permisos de la camara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Escanear directamten solo si fue pedido desde el botón
                    if (permisoSolicitadoDesdeBoton) {
                        escanear();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    Toast.makeText(this, getString(R.string.Permiso_camara_necesario), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoCamaraConcedido = true;
            escaner2(null);
        } else {
            // Si no, pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CODIGO_PERMISOS_CAMARA);
        }
    }





    public void escaner2(View w) {

        if (!permisoCamaraConcedido) {
            //      Toast.makeText(this, "Por favor permite que la app acceda a la cámara "+permisoCamaraConcedido, Toast.LENGTH_SHORT).show();
            permisoSolicitadoDesdeBoton = true;
            verificarYPedirPermisosDeCamara();

            return;
        }
        //permito aceptado continuar con el scaner
        escanear();

    }



    //SCANNER
    private void escanear() {
        vistaescaner = new ZXingScannerView(this);
        vistaescaner.setResultHandler(new zxingscanner());
        setContentView(vistaescaner);
        vistaescaner.startCamera();
        bandera_camara = 1;
        vistaescaner.setFlash(Preferencias_app.linterna);
        vistaescaner.setAutoFocus(Preferencias_app.autofoco);
    }

    //SCANER CONTINUACION
    class zxingscanner implements ZXingScannerView.ResultHandler {

        @Override
        public void handleResult(Result rawResult) {
            buscar_codigo = rawResult.getText();
            //setContentView(R.layout.activity_facturar);
            if (ImageToast!=null){ ;
                try {
                    Thread.sleep(1 * 300);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ImageToast.cancel();
            }

            filtrar_codigo();

            vistaescaner.resumeCameraPreview(this);

//if (padre.equals("Fragmento_factura_mayor_inventario")){
//    try {
//        Frangmento_factura_mayor_inventario.adaptador_la_factura_mayor_inventario.notifyDataSetChanged();
//        Fragmento_factura_mayor_detalle.adaptador_factura_mayor_detalle.notifyDataSetChanged();
//
//    } catch (Exception e) {    }
//}else {
//
////    try {
////        Fragmento_factura_detal.adaptador_la_factura_inventario.notifyDataSetChanged();
////        Factura_detal.adaptador_factura_detal_detalle.notifyDataSetChanged();
////
////    } catch (Exception e) {    }
//}
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            vistaescaner.stopCamera();
            ImageToast.cancel();
        }catch (Exception e){}
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            vistaescaner.stopCamera();
            ImageToast.cancel();
        }catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // CREA EL MENU CON TRES PUNTICOS
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.linterna:
                vistaescaner.setFlash(!linterna);
                linterna=!linterna;
                return true;

        }
            return super.onOptionsItemSelected(item);
        }
}