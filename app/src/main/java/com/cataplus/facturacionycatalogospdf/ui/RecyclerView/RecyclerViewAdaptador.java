package com.cataplus.facturacionycatalogospdf.ui.RecyclerView;
//https://www.youtube.com/watch?v=4ZUgK3hSYoY&t=118s
//https://www.youtube.com/watch?v=iukLnlnzdQQ ABRIR DETALLES DE ELEMENTO DEL RECICLER VIEW
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.R;

import java.io.ByteArrayInputStream;
import java.util.List;

// clase para llenar datos de lista de clientes
public class RecyclerViewAdaptador extends RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder>{



    public static class ViewHolder extends RecyclerView.ViewHolder {
        //es necesario crear el contexto porque la esta clase al ser un recyvler no la lee
        Context context;
        //DECLARACION DE VARIABLES NECESARIAS
        private TextView label_cantidad, textView_nombre,textView_categoria_id, textView_cantidad, textView_costo, textView_mayor,textView_detal, textView_id;
        private ImageView imageView_producto;
        private LinearLayout linear_de_card;


        public ViewHolder(View itemView) {
            super(itemView);
            //inicializamos context ya que no lo lee normalmente en los recyclerwiew
            context=itemView.getContext();

            textView_id=(TextView)itemView.findViewById(R.id.textView_id_invisible);
            label_cantidad=(TextView)itemView.findViewById(R.id.textView_label_cantidad);
            textView_nombre=(TextView)itemView.findViewById(R.id.textview_cliente);
            textView_cantidad= (TextView)itemView.findViewById(R.id.textView_cantidad);
            textView_costo= (TextView)itemView.findViewById(R.id.textView_fecha);
            textView_mayor= (TextView)itemView.findViewById(R.id.textView_factura);
            textView_detal= (TextView)itemView.findViewById(R.id.textView_monto);
            textView_categoria_id=(TextView)itemView.findViewById(R.id.textView_categoria_id);
            imageView_producto=(ImageView)itemView.findViewById(R.id.imageView_producto);
            linear_de_card=(LinearLayout)itemView.findViewById(R.id.layout_principal);

            linear_de_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id_producto", String.valueOf(textView_id.getText()));
                    Navigation.findNavController(v).navigate(R.id.fragmento_incluir_producto,bundle);

                }
            });

        }






    }
    public List<ProductosModelo> modeloLista;

    public RecyclerViewAdaptador(List <ProductosModelo>modeloLista){
        this.modeloLista=modeloLista;
    }

    //INFLA EL CONTENIDO
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //agregar elementos  de LA LISTA

        Uri imageUri;
        holder.textView_id.setText(modeloLista.get(position).getId());
        holder.textView_nombre.setText(modeloLista.get(position).getNombre());
        holder.textView_cantidad.setText(modeloLista.get(position).getCantidad());
        holder.textView_costo.setText(modeloLista.get(position).getP_costo());
        holder.textView_mayor.setText(modeloLista.get(position).getP_mayor());
        holder.textView_detal.setText(modeloLista.get(position).getP_detal());
        String primer_caracter= String.valueOf(modeloLista.get(position).getCategoria().charAt(0));
        primer_caracter=primer_caracter.toUpperCase();
        holder.textView_categoria_id.setText("ID: "+primer_caracter+modeloLista.get(position).getId());

//        Uri resultUri = Uri.parse(modeloLista.get(position).getFoto1());
//        imageUri = resultUri;



        ByteArrayInputStream imageStream = new ByteArrayInputStream(modeloLista.get(position).getImagen());
        Bitmap imagen = BitmapFactory.decodeStream(imageStream);

        holder.imageView_producto.setImageBitmap(imagen);



        if (modeloLista.get(position).getSeguimiento().trim().equals("No")){
            holder.textView_cantidad.setVisibility(View.GONE);
            holder.label_cantidad.setVisibility(View.GONE);
           }else {
            holder.textView_cantidad.setVisibility(View.VISIBLE);
            holder.label_cantidad.setVisibility(View.VISIBLE);
        }

        if (Integer.parseInt(modeloLista.get(position).getCantidad())<1){
            holder.textView_cantidad.setTextColor(Color.parseColor("#780202"));
        }else {
            holder.textView_cantidad.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (modeloLista.get(position).getEstado().equals("Inactivo")) {
            holder.linear_de_card.setBackgroundColor(Color.parseColor("#A8757575"));
        }else{
            holder.linear_de_card.setBackgroundColor(Color.parseColor("#0FB158FF"));
        }

        // EJECUTAR ACTIVITI CUANDO SELECCIONA UN ELEMENTO.


    }

    //METODO QUE PERMITE INDICAR AL ADAPTADOR LA CANTIDAD DE ELEMENTOS QUE SE PROCESARAN
    @Override
    public int getItemCount() {
        return modeloLista.size();
    }

}