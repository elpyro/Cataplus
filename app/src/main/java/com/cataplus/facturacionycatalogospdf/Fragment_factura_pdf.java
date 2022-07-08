package com.cataplus.facturacionycatalogospdf;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;

//pdf
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


import com.cataplus.facturacionycatalogospdf.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Fragment_factura_pdf extends Fragment {

    private static final String TAG = Factura_pdf.class.getSimpleName();
    public List<String[]> temp_datos_cliente = new ArrayList<>();
    public String ultima_factura="";
    public static CheckBox checkBox_informacion_superior,checkBox_informacion_inferior,checkBoximagenes, checkBox_barras;
    public static EditText informacion_superior, informacion_inferior;
    private String id_factura;
    private Button button_generar_pdf;

    public static Fragment_factura_pdf newInstance(String param1, String param2) {
        Fragment_factura_pdf fragment = new Fragment_factura_pdf();

        return fragment;
    }

    public Fragment_factura_pdf() {
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
        View vista=inflater.inflate(R.layout.fragment_factura_pdf, container, false);

        checkBoximagenes=(CheckBox)vista.findViewById(R.id.checkBox_imagenes);
        checkBox_barras=(CheckBox)vista.findViewById(R.id.checkBox_barras);
        checkBox_informacion_inferior=(CheckBox)vista.findViewById(R.id.checkBox_inferior);
        checkBox_informacion_superior=(CheckBox)vista.findViewById(R.id.checkBox_superior);
        informacion_inferior=(EditText)vista.findViewById(R.id.editTextTextMultiLine_inferior);
        informacion_superior=(EditText)vista.findViewById(R.id.editTextTextMultiLine_superior);
        button_generar_pdf=(Button)vista.findViewById(R.id.button_generar_pdf);
        informacion_superior.setText(Preferencias_app.informacion_superior);
        informacion_inferior.setText(Preferencias_app.informacion_inferior);


        if (getArguments() != null) {
            id_factura = getArguments().getString("id_factura");

            checkBox_informacion_superior.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox_informacion_superior.isChecked()) {
                        informacion_superior.setVisibility(View.VISIBLE);
                    } else {
                        informacion_superior.setVisibility(View.GONE);
                    }
                }
            });
            checkBox_informacion_inferior.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkBox_informacion_inferior.isChecked()) {
                        informacion_inferior.setVisibility(View.VISIBLE);
                    } else {
                        informacion_inferior.setVisibility(View.GONE);
                    }
                }
            });

            button_generar_pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Principal.progressDialog = ProgressDialog.show(getContext(), getString(R.string.Creando_pdf),
                            getString(R.string.Creando_pdf)  , true);


                    Intent intent = new Intent(getContext(), Factura_pdf.class);
                    intent.putExtra("id_factura", id_factura);
                    startActivity(intent);
                }
            });

        }

        //PUBLICIDAD
        AdView mAdView = (AdView)vista.findViewById(R.id.adView2);
        AdRequest puclicidadRequest = new AdRequest.Builder().build();
        mAdView.loadAd(puclicidadRequest);
            return vista;

    }




}