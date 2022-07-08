package inventario.fragmentos;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cataplus.facturacionycatalogospdf.Fragmento_incluir_producto;
import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.ui.RecyclerView.RecyclerViewAdaptador_inventario_inventario;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import adaptadores_fragmentos.SeccionesAdaptador_inventario;


public class Contenedor_fragmentos_inventario extends Fragment {


    public Contenedor_fragmentos_inventario() {
        // Required empty public constructor
    }

    public static Contenedor_fragmentos_inventario newInstance(String param1, String param2) {
        Contenedor_fragmentos_inventario fragment = new Contenedor_fragmentos_inventario();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View vista;
    private SeccionesAdaptador_inventario adaptador;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista= inflater.inflate(R.layout.fragment_contenedor_fragmentos_inventario, container, false);


        tabLayout= vista.findViewById(R.id.tab_layout);
        viewPager2= vista.findViewById(R.id.contenedor_inventario);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.Mi_Inventario));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Detalle));

        FragmentManager fm= getActivity().getSupportFragmentManager();
        adaptador=new SeccionesAdaptador_inventario(fm,getLifecycle());
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

            Runtime.getRuntime().gc();

            Fragmento_inventario_inventario.adaptador_inventario_inventario=null;
            Fragmento_inventario_inventario.recyclerViewFacturas=null;
            Fragmento_inventario_inventario.vista=null;
            Fragmento_inventario_inventario.lalista.clear();
            Fragmento_inventario_inventario.lalista=null;
            Fragmento_inventario_inventario.consulta=null;

            Fragmento_inventario_detalle.adaptador_inventario_detalle=null;
            Fragmento_inventario_detalle.recyclerViewProductos=null;
            Fragmento_inventario_detalle.root=null;
            Fragmento_inventario_detalle.consulta=null;

            if (Fragmento_incluir_producto.lalista!=null){
                Fragmento_incluir_producto.lalista.clear();

            }


        }catch (Exception e){}
    }
}