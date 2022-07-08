package com.cataplus.facturacionycatalogospdf.ui.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cataplus.facturacionycatalogospdf.R;

import java.util.List;

import modelos.ModeloProductoFactura;


public class RecyclerViewAdaptador_Registro_Producto extends RecyclerView.Adapter<RecyclerViewAdaptador_Registro_Producto.ViewHolder>{


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView_total, textView_id_producto;
        private ImageView imageView_foto1;
        private TextView editText_nombre, editText_cantidad, editText_precio;
        private LinearLayout linear_de_card;
        private Context context, context_activity;
        public ViewHolder(View itemView) {
            super(itemView);
            context=itemView.getContext();



            textView_id_producto=(TextView)itemView.findViewById(R.id.textView7);
            textView_total=(TextView)itemView.findViewById(R.id.textView5);
            editText_nombre=(TextView)itemView.findViewById(R.id.editTextTextPersonName8);
            editText_cantidad=(TextView)itemView.findViewById(R.id.editTextTextPersonName);
            editText_precio=(TextView)itemView.findViewById(R.id.editTextTextPersonName2);
            imageView_foto1=(ImageView) itemView.findViewById(R.id.imageView2);
            linear_de_card=(LinearLayout)itemView.findViewById(R.id.layout_principal);

//
//            linear_de_card.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    return false;
//                }
//            });


        }
        //AL PRECIONAR ESE ELEMENTO EJECUTAR ACCION.



        public void setOnClickListeneres() {
            linear_de_card.setOnClickListener(this);
        }

        @Override
        public void onClick(View c) {

            Bundle bundle = new Bundle();
            bundle.putString("id_factura",textView_id_producto.getText().toString().trim() );
            Navigation.findNavController(c).navigate(R.id.fragmento_registro_editar_producto,bundle);

        }


    }



    public List<ModeloProductoFactura> productoFacturaList;

    public RecyclerViewAdaptador_Registro_Producto(List<ModeloProductoFactura> productoFacturaList) {
        this.productoFacturaList = productoFacturaList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_producto,parent,false);
        RecyclerViewAdaptador_Registro_Producto.ViewHolder viewHolder= new RecyclerViewAdaptador_Registro_Producto.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdaptador_Registro_Producto.ViewHolder holder, int position) {
        //agregar elementos  de LA LISTA

        holder.textView_id_producto.setText((productoFacturaList.get(position).getId_producto()));
        holder.editText_nombre.setText(productoFacturaList.get(position).getNombre_producto());
        holder.editText_cantidad.setText(productoFacturaList.get(position).getCantidad_producto());
        holder.editText_precio.setText(productoFacturaList.get(position).getPrecio_producto());
        holder.textView_total.setText(productoFacturaList.get(position).getSub_total());
//        Uri resultUri = Uri.parse(productoFacturaList.get(position).get_foto1_producto());
        holder.imageView_foto1.setImageBitmap(productoFacturaList.get(position).getImagen());



        // EJECUTAR ACTIVITI CUANDO SELECCIONA UN ELEMENTO.
        holder.setOnClickListeneres();

    }

    @Override
    public int getItemCount() {
        return productoFacturaList.size();
    }
}
