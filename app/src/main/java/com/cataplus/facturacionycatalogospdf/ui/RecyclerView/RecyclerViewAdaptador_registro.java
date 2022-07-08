package com.cataplus.facturacionycatalogospdf.ui.RecyclerView;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cataplus.facturacionycatalogospdf.Principal;
import com.cataplus.facturacionycatalogospdf.R;
import com.cataplus.facturacionycatalogospdf.modelos.RegistroModelo;

import java.util.List;

public class RecyclerViewAdaptador_registro extends RecyclerView.Adapter<RecyclerViewAdaptador_registro.ViewHolder> {


        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView_id,textView_cliente, textView_fecha, textView_factura, textView_tipo, textView_monto;
        private LinearLayout linear_de_card;
        private Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            textView_cliente=(TextView)itemView.findViewById(R.id.textview_cliente);
            textView_fecha=(TextView)itemView.findViewById(R.id.textView_fecha);
            textView_factura=(TextView)itemView.findViewById(R.id.textView_factura);
            textView_tipo=(TextView)itemView.findViewById(R.id.textView_tipo);
            textView_monto=(TextView)itemView.findViewById(R.id.textView_monto);
            textView_id=(TextView)itemView.findViewById(R.id.textView7_id);
            linear_de_card=(LinearLayout)itemView.findViewById(R.id.layout_principal);

        }
        //AL PRECIONAR ESE ELEMENTO EJECUTAR ACCION.
        public void setOnClickListeneres() {
            linear_de_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View c) {
            //AQui ABRIR OTRO Fragmento ENVIANDO EL PARAMETRO
            Principal.progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.Creando_pdf),
                    context.getResources().getString(R.string.Creando_pdf)  , true);

            Bundle bundle = new Bundle();
            bundle.putString("id_factura",textView_id.getText().toString().trim() );
            Navigation.findNavController(c).navigate(R.id.fragmento_registro_editar,bundle);
        }
    }



    public List<RegistroModelo> registroLista;

    public RecyclerViewAdaptador_registro(List<RegistroModelo> clienteLista) {
        this.registroLista = clienteLista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro,parent,false);
        RecyclerViewAdaptador_registro.ViewHolder viewHolder= new RecyclerViewAdaptador_registro.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdaptador_registro.ViewHolder holder, int position) {
        //agregar elementos  de LA LISTA

        holder.textView_id.setText(registroLista.get(position).getId_factura());
        holder.textView_cliente.setText(registroLista.get(position).getNombre());
        holder.textView_fecha.setText(registroLista.get(position).getFecha());
        holder.textView_factura.setText(registroLista.get(position).getId_factura());
        holder.textView_tipo.setText(registroLista.get(position).getEstado());
        // EJECUTAR ACTIVITI CUANDO SELECCIONA UN ELEMENTO.
        holder.setOnClickListeneres();

    }

    @Override
    public int getItemCount() {
        return registroLista.size();
    }

}
