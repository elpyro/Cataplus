package fragmentos;
//https://www.youtube.com/watch?v=5-RWOvJ9oq8
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.cataplus.facturacionycatalogospdf.Fragmento_incluir_producto;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import adaptadores_fragmentos.SeccionesAdaptador;
import inventario.fragmentos.Fragmento_inventario_detalle;
import inventario.fragmentos.Fragmento_inventario_inventario;


public class ContenedorFragment extends Fragment {


    View vista;
    private SeccionesAdaptador adaptador;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;


    public ContenedorFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ContenedorFragment newInstance(String param1, String param2) {
        ContenedorFragment fragment = new ContenedorFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_contenedor, container, false);
        tabLayout= vista.findViewById(R.id.tab_layout);
        viewPager2= vista.findViewById(R.id.view_pager_factura_detal);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.Mi_Inventario));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Detalle));

        FragmentManager fm= getActivity().getSupportFragmentManager();
        adaptador=new SeccionesAdaptador(fm,getLifecycle());
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
            Fragmento_factura_detal.adaptador_la_factura_inventario=null;
            Fragmento_factura_detal.recyclerViewFacturas=null;
            Fragmento_factura_detal.vista=null;
            Fragmento_factura_detal.lalista.clear();
            Fragmento_factura_detal.lalista=null;
            Fragmento_factura_detal.consulta=null;

            Factura_detal.adaptador_factura_detal_detalle=null;
            Factura_detal.recyclerViewProductos=null;
            Factura_detal.productos.clear();
            Factura_detal.productos=null;
            Factura_detal.root=null;
            Factura_detal.consulta=null;

            if (Fragmento_incluir_producto.lalista!=null){
                Fragmento_incluir_producto.lalista.clear();

            }

            Factura_detal.descuento= 0;
            Factura_detal.envio=0;
            Factura_detal.impuesto=0;
            Runtime.getRuntime().gc();

        }catch (Exception e){}
    }

}
