package adaptadores_fragmentos;
//https://www.youtube.com/watch?v=5-RWOvJ9oq8

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fragmentos.Factura_detal;

import fragmentos.Fragmento_factura_detal;

public class SeccionesAdaptador extends FragmentStateAdapter {

    public SeccionesAdaptador(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new Factura_detal();
         }

        return new Fragmento_factura_detal();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
