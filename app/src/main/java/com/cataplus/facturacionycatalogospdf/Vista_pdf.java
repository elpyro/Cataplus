package com.cataplus.facturacionycatalogospdf;



import static com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment.mInterstitialAd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.home.HomeFragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.internal.ads.zzdmv;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Locale;

public class Vista_pdf extends AppCompatActivity {

    private FloatingActionButton floatingActionButton_compartir, floatingActionButton_whatapp, floatingActionButton_regresar;
    String bandera="";
    private PDFView pdfView;
    private File file;
    private String telefono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_pdf);
        pdfView=(PDFView)findViewById(R.id.vista_pdf);

        floatingActionButton_compartir=(FloatingActionButton)findViewById(R.id.floatingActionButton_facturar);
        floatingActionButton_whatapp=(FloatingActionButton)findViewById(R.id.floating_enviar_numero);
        floatingActionButton_regresar=(FloatingActionButton)findViewById(R.id.floating_regresar);

//        Principal.progressDialog.cancel();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            telefono= extras.getString("telefono");
        }

        //PUBLICIDAD https://www.youtube.com/watch?v=7WwSCZLpMNc
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5390342068041092/1274252406", adRequest,
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

        floatingActionButton_whatapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandera="floatingActionButton_whatapp";
                showInterstitial(getApplicationContext());



            }
        });

        floatingActionButton_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        floatingActionButton_compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandera="floatingActionButton_compartir";
                showInterstitial(getApplicationContext());

            }
        });

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            file=new File(bundle.getString("path",""));
             }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
        Toast.makeText(this, getString(R.string.PDF_guardado_con_exito)+" "+Environment.getExternalStorageDirectory().toString() + "/Reporte_CATAPLUS.pdf", Toast.LENGTH_LONG).show();
    }


        public void compartir_pdf(){
            //COMPARTIR EL PDF


            File file = new File(Environment.getExternalStorageDirectory().toString() + "/Reporte_CATAPLUS.pdf");

            Uri uri = FileProvider.getUriForFile(this, "com.mydomain.fileprovider", file);
        Intent intent2 = ShareCompat.IntentBuilder.from(this)
                .setType("application/pdf")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            this.startActivity(intent2);
        }catch (Exception e){
            Toast.makeText(this, getString(R.string.No_puede_compartir_pdf), Toast.LENGTH_SHORT).show();
        }



        }

    private void openWhatsApp() {
        String smsNumber = telefono;
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            try { Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND); sendIntent.setType("text/application/pdf");

                File file = new File(Environment.getExternalStorageDirectory().toString() + "/Reporte_CATAPLUS.pdf");
                Uri uri = FileProvider.getUriForFile(this, "com.mydomain.fileprovider", file);

                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
                //phone number without "+" prefix sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent); } catch(Exception e)
            { Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show(); } } else
                { Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                Toast.makeText(this, getString(R.string.WhatsApp_no_instalado), Toast.LENGTH_SHORT).show();
                this.startActivity(goToMarket); }
    }



    private boolean whatsappInstalledOrNot(String uri){
            PackageManager pm = this.getPackageManager();
             boolean app_installed = false;
             try {
                 pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES); app_installed = true;
            } catch (PackageManager.NameNotFoundException e)
             { app_installed = false; } return app_installed;
    }


    private void loadInterstitialAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                      //  Toast.makeText(getApplicationContext(), "onAdLoaded()", Toast.LENGTH_SHORT).show();

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAd = null;

                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAd = null;

                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        mInterstitialAd = null;


                        String error = String.format(
                                Locale.ENGLISH,
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(),
                                loadAdError.getCode(),
                                loadAdError.getMessage());

                    }
                });
    }

    private void showInterstitial(Context context) {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && this != null) {

            mInterstitialAd.show(this);

            if (bandera.equals("floatingActionButton_whatapp")) openWhatsApp();
            if (bandera.equals("floatingActionButton_compartir")) compartir_pdf();



        } else {
            //cambiar esto si no se quiere compartir hasta cargar publicidad
            if (bandera.equals("floatingActionButton_whatapp")) openWhatsApp();
            if (bandera.equals("floatingActionButton_compartir")) compartir_pdf();

            //por esto
//            Toast.makeText(this, R.string.Cargando_Publicidad, Toast.LENGTH_SHORT).show();
//            goToNextLevel(context);
        }
    }

    private void goToNextLevel(Context context) {
        // Show the next level and reload the ad to prepare for the level after.

        if (mInterstitialAd == null) {
            loadInterstitialAd(context);
        }
        //aqui el codigo a mostrar

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}