package adaptadores_fragmentos;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import inventario.fragmentos.Fragmento_inventario_detalle;
import inventario.fragmentos.Fragmento_inventario_inventario;

public class SeccionesAdaptador_inventario extends FragmentStateAdapter {

    public SeccionesAdaptador_inventario(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new Fragmento_inventario_detalle();
        }

        return new Fragmento_inventario_inventario();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}