package com.cataplus.facturacionycatalogospdf.ui.RecyclerView;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.cataplus.facturacionycatalogospdf.Fragemento_registro_editar_agregarProducto;
import com.cataplus.facturacionycatalogospdf.ProductosModelo;
import com.cataplus.facturacionycatalogospdf.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import arraylistas.ArrayFacturaActiva;




public class RecyclerViewAdaptador_registro_AgregarProducto extends RecyclerView.Adapter<RecyclerViewAdaptador_registro_AgregarProducto.ViewHolder>{

    public static  ArrayList<ArrayFacturaActiva> agregarProductos = new ArrayList<>();


    public  String id_producto, nombre_producto, seleccion,precio_producto, barras,foto1,precio_costo;

    public static int existe=0;
    private static int bandera=0;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private EditText editText_seleccion;
        private TextView getTextView_valor_facturar, textView_categoria_id, textView_nombre, textView_precio, textView_cantidad, textView_id_producto, textView_valor_facturar, label_cantidad, textView_cantida_original_invisible, textView_precio_costo, textView_barras, textView_foto1;
        private ImageView imageView_producto;
        private LinearLayout linear_de_card;
        private Context context;
        private Button boton_decremento;
        int conteo=0;




        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textView_id_producto = (TextView) itemView.findViewById(R.id.textView_id_producto);
            textView_nombre = (TextView) itemView.findViewById(R.id.textview_cliente);
            textView_precio = (TextView) itemView.findViewById(R.id.textView_monto);
            textView_cantidad = (TextView) itemView.findViewById(R.id.textView_cantidad);
            imageView_producto = (ImageView) itemView.findViewById(R.id.imageView_producto);
            linear_de_card = (LinearLayout) itemView.findViewById(R.id.layout_principal);
            editText_seleccion=(EditText)itemView.findViewById(R.id.textView_seleccion);
            textView_valor_facturar=(TextView)itemView.findViewById(R.id.textView_valor_factura);
            boton_decremento=(Button)itemView.findViewById((R.id.button_abajo));
            label_cantidad=(TextView)itemView.findViewById((R.id.textView_label_cantidad));
            textView_cantida_original_invisible=(TextView) itemView.findViewById(R.id.textView_cantidad_original_invisible);
            textView_precio_costo=(TextView)itemView.findViewById(R.id.precio_costo);
            textView_categoria_id=(TextView)itemView.findViewById(R.id.textView_categoria_id);
            textView_barras=(TextView)itemView.findViewById(R.id.textView_barras);
            textView_foto1=(TextView)itemView.findViewById(R.id.textView_foto1);

            linear_de_card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //ABRIR OTRO ACTIVITY ENVIANDO EL PARAMETRO PRODUCTO
                    //abrir fragemnt
                    Bundle bundle = new Bundle();
                    bundle.putString("id_producto",textView_id_producto.getText().toString().trim());
                    bundle.putString("id_producto_nuevo","");
                    bundle.putString("padre", Fragemento_registro_editar_agregarProducto.padre);
                    Navigation.findNavController(v).navigate(R.id.fragmento_incluir_producto,bundle);

                    return false;
                }
            });


//            editText_seleccion.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
//                    if(keyCode == KeyEvent.KEYCODE_DEL) {
//                        //this is for backspace
//                        if (editText_seleccion.getText().toString().trim().length() < 1) {
//                            conteo=0;
//                        }else{
//                            conteo = Integer.parseInt(editText_seleccion.getText().toString().trim());
//                        }
//                        //RESTAR LAS CANTIDADES
//                        restar_cantidades(conteo);
//
//                        //              CREAR ARRAY LIST USANDO UN MODELO ayuda arraylist https://parzibyte.me/blog/2019/09/02/buscar-elemento-arraylist-java/
//                        // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
//                        ArrayFacturaActiva busqueda = new ArrayFacturaActiva(textView_id_producto.getText().toString().trim(),  textView_precio.getText().toString().trim(), conteo + "",textView_nombre.getText().toString().trim(),textView_barras.getText().toString().trim(),textView_foto1.getText().toString().trim(),textView_precio_costo.getText().toString().trim());
//                        for (int x = 0; x < agregarProductos.size(); x++) {
//                            ArrayFacturaActiva productos = agregarProductos.get(x);
//                            if (productos.getId_producto().equals(busqueda.getId_producto())) {
//                                agregarProductos.remove(x);
//                                break;
//                            }
//                        }
//                        //agregar elemento al array solo si hay seleccionados
//                        if (conteo != 0) {
//                            agregarProductos.add(new ArrayFacturaActiva(textView_id_producto.getText().toString().trim(),  textView_precio.getText().toString().trim(), conteo + "",textView_nombre.getText().toString().trim(),textView_barras.getText().toString().trim(),textView_foto1.getText().toString().trim(),textView_precio_costo.getText().toString().trim()));
//                        }
//
//
//
//
//
//                        //SI EL LA SELECICION ESTA EN LA LISTA CAMBIAR FORMATO A SELECCIONADO
//                        if (conteo > 0) {
//                            boton_decremento.setVisibility(View.VISIBLE);
//                            linear_de_card.setBackgroundColor(Color.parseColor("#A8003335"));
//                        } else {
//                            editText_seleccion.setText("");
//                            linear_de_card.setBackgroundColor(Color.parseColor("#A8007075"));
//                        }
//
//
//
//                    }
//                    return false;
//                }
//            });
//            //ACCIONES AL CAMBIAR EDITTEXT SELECCION
            editText_seleccion.addTextChangedListener(new TextWatcher() {
                int previousLength;  boolean backSpace;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    previousLength = s.length();


                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {



                    //ACCIONES AL CAMBIAR SELECCION DE PRODUCTOS (VALORES SUPERIORES A "")
                    if (!editText_seleccion.getText().toString().trim().equals("")) {
                        //BANDERA==1 PARA SABER SI EL EDITTEXT SE CAMBIO AL PRECIONAR UN BOTON
                        if (bandera == 0) {
                            conteo = Integer.parseInt(editText_seleccion.getText().toString().trim());
                        }
                        restar_cantidades(conteo);


                        //              CREAR ARRAY LIST USANDO UN MODELO ayuda arraylist https://parzibyte.me/blog/2019/09/02/buscar-elemento-arraylist-java/
                        // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
                        ArrayFacturaActiva busqueda = new ArrayFacturaActiva(textView_id_producto.getText().toString().trim(),  textView_precio.getText().toString().trim(), conteo + "",textView_nombre.getText().toString().trim(),textView_barras.getText().toString().trim(),textView_foto1.getText().toString().trim(),textView_precio_costo.getText().toString().trim());
                        for (int x = 0; x < agregarProductos.size(); x++) {
                            ArrayFacturaActiva productos = agregarProductos.get(x);
                            if (productos.getId_producto().equals(busqueda.getId_producto())) {
                                agregarProductos.remove(x);
                                break;
                            }
                        }

                        //agregar elemento al array solo si hay seleccionados
                        if (conteo != 0) {
                            agregarProductos.add(new ArrayFacturaActiva(textView_id_producto.getText().toString().trim(),  textView_precio.getText().toString().trim(), conteo + "",textView_nombre.getText().toString().trim(),textView_barras.getText().toString().trim(),textView_foto1.getText().toString().trim(),textView_precio_costo.getText().toString().trim()));
                        }




                        //SI EL LA SELECICION ESTA EN LA LISTA CAMBIAR FORMATO A SELECCIONADO
                        if (conteo > 0) {
                            boton_decremento.setVisibility(View.VISIBLE);
                            linear_de_card.setBackgroundColor(Color.parseColor("#A8003335"));
                        } else {
                            editText_seleccion.setText("");
                            linear_de_card.setBackgroundColor(Color.parseColor("#A8007075"));
                        }
                        bandera=0;

                    }


                }
            });
        }

        public void actualizar_array(){


            // buscar en el arraylist si existe borrarlo, para agregar uno nuevo con la seleccion  actualizada
            ArrayFacturaActiva busqueda = new ArrayFacturaActiva(textView_id_producto.getText().toString().trim(),  textView_precio.getText().toString().trim(), conteo + "",textView_nombre.getText().toString().trim(),textView_barras.getText().toString().trim(),textView_foto1.getText().toString().trim(),textView_precio_costo.getText().toString().trim());
            for (int x = 0; x < agregarProductos.size(); x++) {
                ArrayFacturaActiva productos = agregarProductos.get(x);
                if (productos.getId_producto().equals(busqueda.getId_producto())) {
                    agregarProductos.remove(x);
                    break;
                }
            }
            //agregar elemento al array solo si hay seleccionados
            if (conteo != 0) {
                agregarProductos.add(new ArrayFacturaActiva(textView_id_producto.getText().toString().trim(),  textView_precio.getText().toString().trim(), conteo + "",textView_nombre.getText().toString().trim(),textView_barras.getText().toString().trim(),textView_foto1.getText().toString().trim(),textView_precio_costo.getText().toString().trim()));
            }

        }

        //RESTAR LA CANTIDAD DE PRODUCTOS DISPONIBLE
        public void restar_cantidades(int restar_seleccion){
            int restar=0;
            int restar_cantidad= Integer.parseInt(textView_cantida_original_invisible.getText().toString().trim());

            //suma cantidades si la factura es de compra
            if(Fragemento_registro_editar_agregarProducto.padre.equals("inventario")){
                restar=restar_cantidad+restar_seleccion;
            }else{
                restar=restar_cantidad-restar_seleccion;
            }

            if (restar<1){
                textView_cantidad.setTextColor(Color.parseColor("#780202"));
            }else {
                textView_cantidad.setTextColor(Color.parseColor("#FFFFFF"));
            }
            textView_cantidad.setText(restar+"");
        }

        //AL PRECIONAR ESE ELEMENTO EJECUTAR ACCION.
        public void setOnClickListeneres() {
            linear_de_card.setOnClickListener(this);
            boton_decremento.setOnClickListener(this);
        }

        @Override
        public void onClick(View c) {
            //SELECCION DE PRODUCTOS AL PRECIONAR UN BOTON
            bandera=1;

            switch (c.getId()) {
                //RESTA UNA CANTIDAD
                case R.id.button_abajo:
                    conteo =Integer.parseInt(editText_seleccion.getText().toString().trim());
                    conteo=conteo-1;
                    editText_seleccion.setText(conteo+"");
                    if (conteo==0){
                        editText_seleccion.setText("0");
                        boton_decremento.setVisibility(View.GONE);
                        linear_de_card.setBackgroundColor(Color.parseColor("#A8007075"));
                    }

                    break;
                //SUMA UN VALOR A LA SELECCION
                case R.id.layout_principal:
                    boton_decremento.setVisibility(View.VISIBLE);
                    if (editText_seleccion.getText().toString().trim().equals("")) {
                        conteo=1;
                        editText_seleccion.setText("1");
                    }else{
                        conteo =Integer.parseInt(editText_seleccion.getText().toString().trim());
                        conteo=conteo+1;
                        editText_seleccion.setText(conteo+"");
                    }
                    break;
                default:
                    break;
            }

        }
    }

    public List<ProductosModelo> productoLista;

    public RecyclerViewAdaptador_registro_AgregarProducto(List<ProductosModelo> productoLista) {
        this.productoLista = productoLista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_facturar_producto,parent,false);
        RecyclerViewAdaptador_registro_AgregarProducto.ViewHolder viewHolder= new RecyclerViewAdaptador_registro_AgregarProducto.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdaptador_registro_AgregarProducto.ViewHolder holder, int position) {

        existe=0;
        int x= 0;
        id_producto=productoLista.get(position).getId();
        nombre_producto=productoLista.get(position).getNombre();
        precio_producto=productoLista.get(position).getP_detal();
        seleccion=productoLista.get(position).getCantidad();
        barras=productoLista.get(position).getCodigo();
        foto1=productoLista.get(position).getFoto1();
        precio_costo=productoLista.get(position).getP_costo();
        holder.textView_precio_costo.setText(precio_costo);

        holder.boton_decremento.setVisibility(View.GONE);

        //CREAMOS BUSQUEDA QUE USA EL MODELO.
        ArrayFacturaActiva busqueda = new ArrayFacturaActiva(id_producto,"","","","","","");
        // Voy a buscar únicamente comparando el código
        //ESTE CODIGO GENERA UN ERROR SI NO SE USA EL "IF" TAL CUAL.
        for ( x = 0; x < agregarProductos.size(); x++) {
            ArrayFacturaActiva productos = agregarProductos.get(x);
            if (productos.getId_producto().equals(busqueda.getId_producto())) {
                //  precio_producto=productos.getPrecio_producto();
                seleccion=productos.getSeleccion_producto();
                existe = 1;
                break;
            }
        }


       // holder.editText_seleccion.setText("");
        //agregar elementos  de LA LISTA
        holder.textView_id_producto.setText(id_producto);
        holder.textView_nombre.setText(nombre_producto);
        holder.textView_cantidad.setText(productoLista.get(position).getCantidad());
        holder.textView_cantida_original_invisible.setText(productoLista.get(position).getCantidad());
        holder.textView_precio.setText(precio_producto);
        String primer_caracter= String.valueOf(productoLista.get(position).getCategoria().charAt(0));
        primer_caracter=primer_caracter.toUpperCase();
        holder.textView_categoria_id.setText("ID: "+primer_caracter+id_producto);
        holder.textView_barras.setText(barras);
        holder.textView_foto1.setText(foto1);
        holder.textView_precio_costo.setText(precio_costo);

//        Uri imageUri;
//        Uri resultUri = Uri.parse(productoLista.get(position).getFoto1());
//        imageUri = resultUri;
//        holder.imageView_producto.setImageURI(imageUri);

        ByteArrayInputStream imageStream = new ByteArrayInputStream(productoLista.get(position).getImagen());
        Bitmap imagen = BitmapFactory.decodeStream(imageStream);

        holder.imageView_producto.setImageBitmap(imagen);


        if (productoLista.get(position).getSeguimiento().trim().equals("No")){

            holder.textView_cantidad.setVisibility(View.INVISIBLE);
            holder.label_cantidad.setVisibility(View.INVISIBLE);
        }else {
            holder.textView_cantidad.setVisibility(View.VISIBLE);
            holder.label_cantidad.setVisibility(View.VISIBLE);
            int cantidad_producto;
            cantidad_producto= Integer.parseInt(holder.textView_cantidad.getText().toString().trim());
            if (cantidad_producto<1){
                holder.textView_cantidad.setTextColor(Color.parseColor("#780202"));
            }else {
                holder.textView_cantidad.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }


        if (existe==1){

            holder.editText_seleccion.setText(seleccion);
            holder.linear_de_card.setBackgroundColor(Color.parseColor("#A8003335"));
            holder.boton_decremento.setVisibility(View.VISIBLE);
        }else {
            holder.linear_de_card.setBackgroundColor(Color.parseColor("#A8007075"));
            holder.editText_seleccion.setText("");
        }

        // EJECUTAR ACTIVITI CUANDO SELECCIONA UN ELEMENTO.
        holder.setOnClickListeneres();

    }

    @Override
    public int getItemCount() {
        return productoLista.size();
    }

}

