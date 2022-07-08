package com.cataplus.facturacionycatalogospdf;

import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.mInterstitialAd;
import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import modelos.Modelo_empresa;


public class FragmentEmpresa extends Fragment {



    public FragmentEmpresa() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentEmpresa newInstance(String param1, String param2) {

        FragmentEmpresa fragment = new FragmentEmpresa();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public static ImageView imageView_incluir_foto1;
    public static Uri imageUri;

    private EditText nombre, documento, dominio, telefono1, telefono2, pais, ciudad, direccion, fecha_modificacion, usuario_modificacion, correo;
    private TextView logo;
    private String codigo_empresa, fecha_hora;
    private byte[] blob=null;
    private static final String LIST_KEY = "list_key100";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_empresa, container, false);


        nombre=root.findViewById(R.id.editText_incluir_nombre_empresa);
        documento=root.findViewById(R.id.textView_documento_empresa);
        correo=root.findViewById(R.id.editText_correo_empresa);
        dominio=root.findViewById(R.id.textView_dominio_empresa);
        telefono1=root.findViewById(R.id.editText_telefono1);
        telefono2=root.findViewById(R.id.textView_telefono2);
        pais=root.findViewById(R.id.editTest_pais_empresa);
        ciudad=root.findViewById(R.id.editText_ciudad_empresa);
        direccion=root.findViewById(R.id.editTextTextMultiLine_direccion_empresa);
        imageView_incluir_foto1=root.findViewById(R.id.imageView_incluir_foto1);
//                fecha_modificacion=findViewById(R.id.feasdfsad),
//                usuario_modificacion=findViewById(R.id.textView_documento_empresa),
        logo= root.findViewById(R.id.textView_logo);
        fecha_hora = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        //foto vacia
        String archivo = "android.resource://"+  getActivity().getPackageName() + "/drawable/agregar_foto_jpeg";
        imageUri = Uri.parse(archivo);
        imageView_incluir_foto1.setImageURI(imageUri);

        //CARGA DE PUBLICIDAD
        AdView mAdView = (AdView)root.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);


        //PUBLICIDAD https://www.youtube.com/watch?v=7WwSCZLpMNc
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(),"ca-app-pub-5390342068041092/1274252406", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

        // ACCION AL PRECIONAR IMAGEN
        imageView_incluir_foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // muestra el cuadro de diálogo de selección de imagen

                Intent  intent=new Intent(getContext(), Activity_simple_foto.class);
                startActivity(intent);
            }
        });
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "basedatos",null,1);
        SQLiteDatabase BD= admin.getWritableDatabase();
        Cursor cursor=BD.rawQuery("SELECT * FROM empresa",null);
        if (cursor.moveToFirst()){
            codigo_empresa=cursor.getString(0);
            nombre.setText(cursor.getString(1));
            documento.setText(cursor.getString(2));
            correo.setText(cursor.getString(3));
            dominio.setText(cursor.getString(4));
            telefono1.setText(cursor.getString(5));
            telefono2.setText(cursor.getString(6));
            pais.setText(cursor.getString(7));
            ciudad.setText(cursor.getString(8));
            direccion.setText(cursor.getString(9));

            //convertir imagen bit a imagen
            if (cursor.getBlob(13)!=null) {
                byte[] imagen = cursor.getBlob(13);
                ByteArrayInputStream imageStream = new ByteArrayInputStream(imagen);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                imageUri=convertir_bitmap_a_uri(getContext(), theImage);
                imageView_incluir_foto1.setImageBitmap(theImage);

            }

        }

        BD.close();

        return root;
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
    
}
        return Uri.fromFile(tempFile);
        
    }
    public byte[] imagen_uri_a_bit(Uri uri_imagen){
        //convertir imagen uri a bit
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri_imagen);
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            blob=stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blob;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mInterstitialAd != null) {
            mInterstitialAd.show((Activity) getContext());
        }

        //convertir logo a string
        String valor_uri = String.valueOf(imageUri);

        AdminSQLiteOpenHelper cn= new AdminSQLiteOpenHelper(getContext(),"basedatos",null,1);
        SQLiteDatabase BaseDeDatos = cn.getWritableDatabase(); //METODO ACTIVA LECTURA Y ESCRITURA EN BD
        if(codigo_empresa!=null) {
            //MODIFICANDO BASE DE DATOS

            if (nombre.equals("")) {
                Toast.makeText(getContext(), getString(R.string.El_nombre_empresa_no_vacio), Toast.LENGTH_SHORT).show();
                nombre.setError(getString(R.string.Requerido));
                return;
            } else {

                //GUARMOS LOS VALORES ENN CONTENTVALUES
                ContentValues registro = new ContentValues();
                registro.put("nombre", nombre.getText().toString().trim());
                registro.put("documento", documento.getText().toString().trim());
                registro.put("correo", correo.getText().toString().trim());
                registro.put("dominio", dominio.getText().toString().trim());
                registro.put("telefono1", telefono1.getText().toString().trim());
                registro.put("telefono2", telefono2.getText().toString().trim());
                registro.put("pais", pais.getText().toString().trim());
                registro.put("ciudad", ciudad.getText().toString().trim());
                registro.put("direccion", direccion.getText().toString().trim());
                registro.put("fecha_modificacion", fecha_hora);
                registro.put("usuario_modificacion", "Administrador");
              //  registro.put("logo", valor_uri);
                registro.put("imagen",imagen_uri_a_bit(imageUri));

                //MODIFICAR FILASS
                int filas_modificadas = BaseDeDatos.update("empresa", registro, "id_empresa=" + codigo_empresa, null);
                BaseDeDatos.close();
            }
        }else{

            //GUARMOS LOS VALORES ENN CONTENTVALUES
            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre.getText().toString().trim());
            registro.put("documento", documento.getText().toString().trim());
            registro.put("correo", correo.getText().toString().trim());
            registro.put("dominio", dominio.getText().toString().trim());
            registro.put("telefono1", telefono1.getText().toString().trim());
            registro.put("telefono2", telefono2.getText().toString().trim());
            registro.put("pais", pais.getText().toString().trim());
            registro.put("ciudad", ciudad.getText().toString().trim());
            registro.put("direccion", direccion.getText().toString().trim());
            registro.put("fecha_modificacion", fecha_hora);
            registro.put("usuario_modificacion", "Administrador");
          //  registro.put("logo", valor_uri);
            registro.put("imagen",imagen_uri_a_bit(imageUri));

            // INSERTAMOS EN LA BASE DE DATOS UN CONTENTVALUES CON LA INFORMACION
            BaseDeDatos.insert("empresa", null, registro);
        }

        BaseDeDatos.close();

        //ACTUALIZAR LOS DATOS DE EMPRESA PARA QUE ESTEN EN LAS PREFERENCIAS DE LA APP
        Preferencias_app.datos_empresa.clear();
        Preferencias_app.datos_empresa.add(new Modelo_empresa("0", nombre.getText().toString().trim(),documento.getText().toString().trim(), correo.getText().toString().trim(),
                dominio.getText().toString().trim(),telefono1.getText().toString().trim(), telefono2.getText().toString().trim(),
                pais.getText().toString().trim(), ciudad.getText().toString().trim(),direccion.getText().toString().trim(), valor_uri,imagen_uri_a_bit(imageUri)));

    }


}