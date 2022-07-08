package modelos;

import android.graphics.Bitmap;

public class ModeloProductoFactura {
String id_producto, id_factura, nombre_producto, precio_producto,_foto1_producto,_barras_producto, cantidad_producto, sub_total;
    private Bitmap imagen;

    public ModeloProductoFactura() {
    }

    public ModeloProductoFactura(String id_producto, String id_factura, String nombre_producto, String precio_producto, String _foto1_producto, String _barras_producto, String cantidad_producto, String sub_total, Bitmap imagen) {
        this.id_producto = id_producto;
        this.id_factura = id_factura;
        this.nombre_producto = nombre_producto;
        this.precio_producto = precio_producto;
        this._foto1_producto = _foto1_producto;
        this._barras_producto = _barras_producto;
        this.cantidad_producto = cantidad_producto;
        this.sub_total = sub_total;
        this.imagen = imagen;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getId_factura() {
        return id_factura;
    }

    public void setId_factura(String id_factura) {
        this.id_factura = id_factura;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getPrecio_producto() {
        return precio_producto;
    }

    public void setPrecio_producto(String precio_producto) {
        this.precio_producto = precio_producto;
    }

    public String get_foto1_producto() {
        return _foto1_producto;
    }

    public void set_foto1_producto(String _foto1_producto) {
        this._foto1_producto = _foto1_producto;
    }

    public String get_barras_producto() {
        return _barras_producto;
    }

    public void set_barras_producto(String _barras_producto) {
        this._barras_producto = _barras_producto;
    }

    public String getCantidad_producto() {
        return cantidad_producto;
    }

    public void setCantidad_producto(String cantidad_producto) {
        this.cantidad_producto = cantidad_producto;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}
