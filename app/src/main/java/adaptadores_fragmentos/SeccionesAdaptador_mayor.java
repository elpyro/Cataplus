package adaptadores_fragmentos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Frangmento_factura_mayor_inventario;

public class SeccionesAdaptador_mayor extends FragmentStateAdapter {

    public SeccionesAdaptador_mayor(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new Fragmento_factura_mayor_detalle();
        }

        return new Frangmento_factura_mayor_inventario();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}