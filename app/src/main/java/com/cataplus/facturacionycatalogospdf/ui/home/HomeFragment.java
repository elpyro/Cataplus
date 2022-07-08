package com.cataplus.facturacionycatalogospdf.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cataplus.facturacionycatalogospdf.Activity_scanner;
import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.databinding.FragmentHomeBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment {

    public static InterstitialAd mInterstitialAd;
    public static AdRequest puclicidadRequest;
    public static AdView mAdView;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    public static Button button_escaner_detal, button_escaner_mayor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });


        button_escaner_detal=root.findViewById(R.id.button_escaner_detal);
        button_escaner_mayor=root.findViewById(R.id.button_escaner_mayor);

        mAdView = root.findViewById(R.id.adView);
        puclicidadRequest = new AdRequest.Builder().build();

        //PUBLICIDAD https://www.youtube.com/watch?v=7WwSCZLpMNc
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

            mAdView.loadAd(puclicidadRequest);



        button_escaner_detal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Navigation.findNavController(v).navigate(R.id.contenedorFragment);


                Intent intent=new Intent(getContext(), Activity_scanner.class);
                intent.putExtra("padre","Fragmento_factura_detal");
                startActivity(intent);
                if(Principal.valor_factura>0){
                    Toast.makeText(getContext(),  getString(R.string.Factura_detal_actual)+": "+Principal.format.format(Principal.valor_factura), Toast.LENGTH_LONG).show();
                }

            }
        });

        button_escaner_mayor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Navigation.findNavController(v).navigate(R.id.contenedor_fragmento_mayor);


                Intent intent=new Intent(getContext(), Activity_scanner.class);
                intent.putExtra("padre","Fragmento_factura_mayor_inventario");
                startActivity(intent);
                if(Principal.valor_factura_mayor>0){
                    Toast.makeText(getContext(),  getString(R.string.Factura_mayor_actual)+": "+Principal.format.format(Principal.valor_factura_mayor), Toast.LENGTH_LONG).show();
                }

            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}