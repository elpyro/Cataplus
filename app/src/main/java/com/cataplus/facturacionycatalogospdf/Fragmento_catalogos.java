package com.cataplus.facturacionycatalogospdf;


import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.puclicidadRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;


import android.content.Intent;


//pdf

import androidx.appcompat.widget.AppCompatEditText;

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

public class Fragmento_catalogos extends Fragment {


    public Fragmento_catalogos() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static RadioButton radioButton_catalogo_detal;
    public static RadioButton radioButton_catalogo_mayor;
    public static CheckBox checkBox_descripcion, checkBox_proveedor, checkBox_color,checkBox_talla,checkBox_medida;

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppCompatEditText rowCount;
    private Button button1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista=inflater.inflate(R.layout.fragment_fragmento_catalogos, container, false);
        button1=vista.findViewById(R.id.button1);
        radioButton_catalogo_detal=vista.findViewById(R.id.radioButton_catalogo_detal);
        radioButton_catalogo_mayor=vista.findViewById(R.id.radioButton_catalogo_mayor);
        checkBox_descripcion=vista.findViewById(R.id.checkBox4);
        checkBox_proveedor=vista.findViewById(R.id.checkBox5);
        checkBox_color=vista.findViewById(R.id.checkBox);
        checkBox_talla=vista.findViewById(R.id.checkBox2);
        checkBox_medida=vista.findViewById(R.id.checkBox6);

        //CARGA DE PUBLICIDAD
        AdView mAdView = vista.findViewById(R.id.adView);
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
                        HomeFragment.mInterstitialAd = interstitialAd;
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        HomeFragment. mInterstitialAd = null;
                    }
                });

        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

//                //Despues de mostrar el anuncio Insterticial, realiza el intent.
//                Handler myHandler = new Handler();
//                myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 6000); //5 segundos
//
//                if (HomeFragment.mInterstitialAd != null) {
//                    HomeFragment.mInterstitialAd.show((Activity) getContext());
//                }

                Principal.progressDialog = ProgressDialog.show(getContext(), getString(R.string.Creando_pdf),
                        getString(R.string.Creando_pdf)  , true);

                Intent intent=new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        return vista;
    }




}