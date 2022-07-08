package com.cataplus.facturacionycatalogospdf.ui.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cataplus.facturacionycatalogospdf.ClienteModelo;
import fragmentos.Factura_detal;
import com.cataplus.facturacionycatalogospdf.Fragmento_consultar_clientes;
import com.cataplus.facturacionycatalogospdf.ui.factura_mayor.fragmentos.Fragmento_factura_mayor_detalle;
import com.cataplus.facturacionycatalogospdf.R;

import java.util.List;

public class RecyclerViewAdaptador_clientes extends RecyclerView.Adapter<RecyclerViewAdaptador_clientes.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView_id, textView_nombre, textView_documento, textView_correo, textView_telefono1, textView_telefono2, textView_pais, textView_ciudad, textView_dirrecion;
        private LinearLayout linear_de_card;
        private Context context;
        public ViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();
            textView_id=(TextView)itemView.findViewById(R.id.textView_id_cliente);
            textView_nombre=(TextView)itemView.findViewById(R.id.textView_nombre_cliente1);
            textView_documento=(TextView)itemView.findViewById(R.id.textView_documento_empresa);
            textView_correo=(TextView)itemView.findViewById(R.id.textView_correo);
            textView_telefono1=(TextView)itemView.findViewById(R.id.textView_telefono1);
            textView_telefono2=(TextView)itemView.findViewById(R.id.textView_telefono2);
            textView_pais=(TextView)itemView.findViewById(R.id.textView_pais);
            textView_ciudad=(TextView)itemView.findViewById(R.id.textView_ciudad);
            textView_dirrecion=(TextView)itemView.findViewById(R.id.textView_direccion);
            linear_de_card=(LinearLayout)itemView.findViewById(R.id.linear_clientes);



            linear_de_card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(!Fragmento_consultar_clientes.padre_factura.equals(null)||!Fragmento_consultar_clientes.padre_factura.equals("")){
                        //                    //abrir fragemnt
                        Bundle bundle = new Bundle();
                        bundle.putString("id_cliente",textView_id.getText().toString().trim() );
                        Navigation.findNavController(v).navigate(R.id.fragmento_agregar_cliente,bundle);
                    }



                    return false;
                }
            });

        }
        //AL PRECIONAR ESE ELEMENTO EJECUTAR ACCION.
        public void setOnClickListeneres() {
            linear_de_card.setOnClickListener(this);
        }
        @Override
        public void onClick(View c) {



            if(Fragmento_consultar_clientes.padre_factura.equals(null)||Fragmento_consultar_clientes.padre_factura.equals("")){
                //                    //abrir fragemnt
                Bundle bundle = new Bundle();
                bundle.putString("id_cliente",textView_id.getText().toString().trim() );
                Navigation.findNavController(c).navigate(R.id.fragmento_agregar_cliente,bundle);
            }
            if (Fragmento_consultar_clientes.padre_factura.equals("factura detal")) {
                try {

                    Factura_detal.textView_cliente.setText(textView_nombre.getText().toString().trim());
                    Factura_detal.cliente_detal.clear();
                    Factura_detal.cliente_detal.add(new ClienteModelo(textView_id.getText().toString().trim(), textView_nombre.getText().toString().trim(),
                            textView_documento.getText().toString().trim(), textView_correo.getText().toString().trim(), textView_telefono1.getText().toString().trim(),
                            textView_telefono2.getText().toString().trim(), textView_pais.getText().toString().trim(), textView_ciudad.getText().toString().trim(),
                            textView_dirrecion.getText().toString().trim()));
                Fragmento_consultar_clientes.padre_factura="";
                    Navigation.findNavController(c).navigateUp();
                } catch (Exception e) {
                    return;
                }
            }

            //ENVIAR EL NOMBRE DEL CLIENTE A LA FACTUAR EN PROCESO AL SELECCIONARLO

            if (Fragmento_consultar_clientes.padre_factura.equals("factura mayor")) {
                try {

                    Fragmento_factura_mayor_detalle.textView_cliente.setText(textView_nombre.getText().toString().trim());
                    Fragmento_factura_mayor_detalle.cliente_mayor.clear();
                    Fragmento_factura_mayor_detalle.cliente_mayor.add(new ClienteModelo(textView_id.getText().toString().trim(), textView_nombre.getText().toString().trim(),
                            textView_documento.getText().toString().trim(), textView_correo.getText().toString().trim(), textView_telefono1.getText().toString().trim(),
                            textView_telefono2.getText().toString().trim(), textView_pais.getText().toString().trim(), textView_ciudad.getText().toString().trim(),
                            textView_dirrecion.getText().toString().trim()));
                Fragmento_consultar_clientes.padre_factura="";
                    Navigation.findNavController(c).navigateUp();
                } catch (Exception e) {
                    return;
                }

            }




        }

    }

    public List<ClienteModelo> clienteLista;

    public RecyclerViewAdaptador_clientes(List<ClienteModelo> clienteLista) {
        this.clienteLista = clienteLista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente,parent,false);
        RecyclerViewAdaptador_clientes.ViewHolder viewHolder= new RecyclerViewAdaptador_clientes.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdaptador_clientes.ViewHolder holder, int position) {
        //agregar elementos  de LA LISTA
        holder.textView_id.setText(clienteLista.get(position).getId());
        holder.textView_nombre.setText(clienteLista.get(position).getNombre());
        holder.textView_documento.setText(clienteLista.get(position).getDocumento());
        holder.textView_correo.setText(clienteLista.get(position).getCorreo());
        holder.textView_telefono1.setText(clienteLista.get(position).getTelefono1());
        holder.textView_telefono2.setText(clienteLista.get(position).getTelefono2());
        holder.textView_pais.setText(clienteLista.get(position).getPais());
        holder.textView_ciudad.setText(clienteLista.get(position).getCiudad());
        holder.textView_dirrecion.setText(clienteLista.get(position).getDireccion());

        // EJECUTAR ACTIVITI CUANDO SELECCIONA UN ELEMENTO.
        holder.setOnClickListeneres();
    }

    @Override
    public int getItemCount() {
        return clienteLista.size();
    }
}
