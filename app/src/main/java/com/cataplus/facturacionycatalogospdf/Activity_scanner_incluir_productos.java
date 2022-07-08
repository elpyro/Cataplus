package com.cataplus.facturacionycatalogospdf;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cataplus.facturacionycatalogospdf.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Activity_scanner_incluir_productos extends AppCompatActivity {
    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private ZXingScannerView vistaescaner;

    private String buscar_codigo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_scanner_incluir_productos);

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
                    //      tvCodigoLeido.setText(codigo);
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
        vistaescaner.setResultHandler(new zxingscanner());//OJO CAMBIA  CUANDO SE COPIA, SOLO DEBE QUEDAR: NEW ZXINGSCANNER()
        setContentView(vistaescaner);
        vistaescaner.startCamera();
    }

    //SCANER CONTINUACION
    class zxingscanner implements ZXingScannerView.ResultHandler {

        @Override
        public void handleResult(Result rawResult) {
            buscar_codigo = rawResult.getText();
            //Incluir_productos.edittextbarras.setText(buscar_codigo);
            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            vistaescaner.setFlash(false);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            vistaescaner.setFlash(false);

        }catch (Exception e){}
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            vistaescaner.setFlash(false);

        }catch (Exception e){}
    }

}