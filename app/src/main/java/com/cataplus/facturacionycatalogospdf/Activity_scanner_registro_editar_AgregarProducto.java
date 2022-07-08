package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_registro_AgregarProducto.agregarProductos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Factura;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import arraylistas.ArrayFacturaActiva;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class Activity_scanner_registro_editar_AgregarProducto extends AppCompatActivity {

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private ZXingScannerView vistaescaner;


    private RecyclerView recyclerViewFacturas;
    private RecyclerViewAdaptador_Factura adaptador_la_factura;
    public static TextView monto_factura;
    private final String LIST_KEY = "list_key100";

    private FloatingActionButton floatingActionButton_agregar_producto;

    private Button boton_buscar;

    public static EditText editText_buscar;

    private String buscar_codigo = "";

    private List<ProductosModelo> lalista = new ArrayList<>();

    private String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;

    private Cursor consulta;
    private String padre;

    private int bandera_camara = 0;

    private String id_factura;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Bundle extras=getIntent().getExtras();
        if (extras!=null) {

            if (extras.getString("id_factura") != null) {
                id_factura = extras.getString("id_factura");
                padre=extras.getString("padre");
            }
        }


        escaner2(null);
    }


    public void filtrar_codigo() {
        //CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos", null, 1);
        SQLiteDatabase BD = admin.getWritableDatabase();
        consulta = BD.rawQuery(
                "SELECT * FROM productos WHERE codigo= '" + buscar_codigo + "' ORDER BY estado, categoria, nombre", null);


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


                //SE EJECUTA SI LA CAMARA SE ACTIVO DESDE AGREGAR PRODUCTO EN EDITAR REGISTRO

                int cantidad_guardada = 0;
                int existe=0;
                // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
                ArrayFacturaActiva busqueda = new ArrayFacturaActiva(temp_id, temp_detal, "", temp_nombre, temp_barras, temp_foto1,temp_costo);
                for (int x = 0; x < agregarProductos.size(); x++) {
                    ArrayFacturaActiva productos = agregarProductos.get(x);
                    if (productos.getId_producto().equals(busqueda.getId_producto())) {
                        cantidad_guardada= Integer.parseInt(productos.getSeleccion_producto());
                        cantidad_guardada=cantidad_guardada+1;
                        agregarProductos.remove(x);
                        existe=1;
                        break;
                    }
                }
                //agregar elemento al array solo si hay seleccionados
                if (padre.equals("mayor")) {
                    if (existe == 1) {
                        agregarProductos.add(new ArrayFacturaActiva(temp_id, temp_mayor, cantidad_guardada + "",temp_nombre, temp_barras, temp_foto1,temp_costo));

                    } else {
                        agregarProductos.add(new ArrayFacturaActiva(temp_id, temp_mayor, "1", temp_nombre, temp_barras, temp_foto1,temp_costo));
                        cantidad_guardada = 1;
                    }

                }else if (padre.equals(("detal"))){
                    if (existe == 1) {
                        agregarProductos.add(new ArrayFacturaActiva(temp_id, temp_detal, cantidad_guardada + "",temp_nombre, temp_barras, temp_foto1,temp_costo));

                    } else {
                        agregarProductos.add(new ArrayFacturaActiva(temp_id, temp_detal, "1",temp_nombre, temp_barras, temp_foto1,temp_costo));
                        cantidad_guardada = 1;
                    }
                } else if(padre.equals("inventario")) {
                    if (existe == 1) {
                        agregarProductos.add(new ArrayFacturaActiva(temp_id, temp_costo, cantidad_guardada + "",temp_nombre, temp_barras, temp_foto1,temp_costo));

                    } else {
                        agregarProductos.add(new ArrayFacturaActiva(temp_id, temp_costo, "1",temp_nombre, temp_barras, temp_foto1,temp_costo));
                        cantidad_guardada = 1;
                    }
                }

                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);


                //       IMAGEN EN TOAST
//                Uri resultUri = Uri.parse(consulta.getString(11));
//                Uri imageUri = resultUri;
//
//
//                Toast ImageToast = new Toast(getBaseContext());
//                LinearLayout toastLayout = new LinearLayout(getBaseContext());
//                toastLayout.setOrientation(LinearLayout.HORIZONTAL);
//                ImageView image = new ImageView(getBaseContext());
//
//                TextView text = new TextView(getBaseContext());
//                image.setImageURI(resultUri);
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(350, 350);
//                image.setLayoutParams(layoutParams);
//
//                text.setText(  temp_nombre + "\n" +" X " + cantidad_guardada + "" );
//
//                text.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                text.setTextSize(14);
//                toastLayout.addView(text);
//                toastLayout.addView(image);
//                ImageToast.setView(toastLayout);
//                ImageToast.setDuration(Toast.LENGTH_LONG);
//                ImageToast.show();
//

                 Toast.makeText(getApplicationContext(), temp_nombre + "\n" + " X " + cantidad_guardada + "", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1 * 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


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


            //ALERT DIALOGO
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.Codigo_no_exitente));
            builder.setMessage(getString(R.string.Desea_crear_producto));

            builder.setPositiveButton(getString(R.string.Si), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

//                    Intent intent = new Intent(getApplicationContext(), Incluir_productos.class);
//                    intent.putExtra("id_producto_nuevo", buscar_codigo);
//                    startActivity(intent);
//                    finish();
                }
            });
            builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

        }

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
                    permisoDeCamaraDenegado();
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


    private void permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(this, getString(R.string.Permiso_camara_necesario), Toast.LENGTH_SHORT).show();
    }


    public void escaner2(View w) {

        if (!permisoCamaraConcedido) {

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
            filtrar_codigo();

            vistaescaner.resumeCameraPreview(this);


        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            vistaescaner.setFlash(false);
           // Fragemento_registro_editar_agregarProducto.adaptador_registro_agregarProducto.notifyDataSetChanged();
        }catch (Exception e){}

    }

    public List<ProductosModelo> obtener_productos() {


        String temp_id, temp_nombre, temp_barras, temp_categoria, temp_cantidad, temp_costo, temp_mayor, temp_detal, temp_foto1, temp_seguimiento, temp_estado;
        Cursor consulta;

        lalista.clear();
        // CONEXION A LA BASE DE DATOS
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "basedatos", null, 1);
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
                    lalista.add(new ProductosModelo(temp_id, temp_nombre, temp_barras, temp_categoria, temp_seguimiento, temp_cantidad,  temp_mayor, temp_detal,temp_costo, temp_estado, temp_foto1, imagen_byte));
                }


            } while (consulta.moveToNext());
        }
        BD.close();
        return lalista;
    }
}
