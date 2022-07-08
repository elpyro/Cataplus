package com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.cataplus.facturacionycatalogospdf.Fragmento_incluir_producto;
import com.cataplus.facturacionycatalogospdf.R;
import com.google.android.material.tabs.TabLayout;

import adaptadores_fragmentos.SeccionesAdaptador_mayor;


public class Contenedor_fragmento_mayor extends Fragment {




    View vista;
    private SeccionesAdaptador_mayor adaptador;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    public Contenedor_fragmento_mayor() {
        // Required empty public constructor
    }

    public static Contenedor_fragmento_mayor newInstance(String param1, String param2) {
        Contenedor_fragmento_mayor fragment = new Contenedor_fragmento_mayor();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_contenedor_fragmento_mayor, container, false);

        tabLayout= vista.findViewById(R.id.tab_layout);
        viewPager2= vista.findViewById(R.id.view_pager_factura_mayor);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.Mi_Inventario));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Detalle));

        FragmentManager fm= getActivity().getSupportFragmentManager();
        adaptador=new SeccionesAdaptador_mayor(fm,getLifecycle());
        viewPager2.setAdapter(adaptador);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        return vista;
    }




    public void onDestroy() {
        super.onDestroy();


        try {

            // LIBERANDO ESPACIO
            Frangmento_factura_mayor_inventario.adaptador_la_factura_mayor_inventario=null;
            Frangmento_factura_mayor_inventario.recyclerViewFacturas=null;
            Frangmento_factura_mayor_inventario.vista=null;
            Frangmento_factura_mayor_inventario.lalista.clear();
            Frangmento_factura_mayor_inventario.lalista=null;
            Frangmento_factura_mayor_inventario.consulta=null;

            Fragmento_factura_mayor_detalle.adaptador_factura_mayor_detalle=null;
            Fragmento_factura_mayor_detalle.recyclerViewProductos=null;
            Fragmento_factura_mayor_detalle.productos.clear();
            Fragmento_factura_mayor_detalle.productos=null;
            Fragmento_factura_mayor_detalle.root=null;
            Fragmento_factura_mayor_detalle.consulta=null;

            if (Fragmento_incluir_producto.lalista!=null){
                Fragmento_incluir_producto.lalista.clear();

            }
            Fragmento_factura_mayor_detalle.descuento= 0;
            Fragmento_factura_mayor_detalle.envio=0;
            Fragmento_factura_mayor_detalle.impuesto=0;
            Runtime.getRuntime().gc();

        }catch (Exception e){}
    }

}
