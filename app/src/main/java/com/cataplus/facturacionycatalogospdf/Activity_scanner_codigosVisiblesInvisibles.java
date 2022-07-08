package com.cataplus.facturacionycatalogospdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_Factura;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Activity_scanner_codigosVisiblesInvisibles extends AppCompatActivity {

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private ZXingScannerView vistaescaner;

    private TextView tvCodigoLeido;
    boolean linterna = Preferencias_app.linterna;
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

        escaner2(null);
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

            try {

                vistaescaner.stopCamera();
                Fragmento_incluir_producto.edittextbarras.setText(rawResult.getText());
                ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);//sonido

                finish();
                return;
            }catch (Exception e){ }

            try {
                Fragmento_consultar_productos.buscar_codigo = rawResult.getText();
            }catch (Exception e){ }


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            vistaescaner.setFlash(false);
        //    Fragemento_registro_editar_agregarProducto.adaptador_registro_agregarProducto.notifyDataSetChanged();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            vistaescaner.stopCamera();

        }catch (Exception e){}
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            vistaescaner.stopCamera();

        }catch (Exception e){}
    }

}