package com.cataplus.facturacionycatalogospdf.modelos;

public class InformeComprayventaModelo {
    String id_producto , id_factura_bd , nombre_producto ,precio_producto , foto1_producto,barras_producto, cantidad, id_producto_tabla_productos,tipo, p_costo, fecha,descuento;

    public InformeComprayventaModelo() {
    }

    public InformeComprayventaModelo(String id_producto, String id_factura_bd, String nombre_producto, String precio_producto, String foto1_producto, String barras_producto, String cantidad, String id_producto_tabla_productos, String tipo, String p_costo, String fecha, String descuento) {
        this.id_producto = id_producto;
        this.id_factura_bd = id_factura_bd;
        this.nombre_producto = nombre_producto;
        this.precio_producto = precio_producto;
        this.foto1_producto = foto1_producto;
        this.barras_producto = barras_producto;
        this.cantidad = cantidad;
        this.id_producto_tabla_productos = id_producto_tabla_productos;
        this.tipo = tipo;
        this.p_costo = p_costo;
        this.fecha = fecha;
        this.descuento = descuento;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getId_factura_bd() {
        return id_factura_bd;
    }

    public void setId_factura_bd(String id_factura_bd) {
        this.id_factura_bd = id_factura_bd;
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

    public String getFoto1_producto() {
        return foto1_producto;
    }

    public void setFoto1_producto(String foto1_producto) {
        this.foto1_producto = foto1_producto;
    }

    public String getBarras_producto() {
        return barras_producto;
    }

    public void setBarras_producto(String barras_producto) {
        this.barras_producto = barras_producto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getId_producto_tabla_productos() {
        return id_producto_tabla_productos;
    }

    public void setId_producto_tabla_productos(String id_producto_tabla_productos) {
        this.id_producto_tabla_productos = id_producto_tabla_productos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getP_costo() {
        return p_costo;
    }

    public void setP_costo(String p_costo) {
        this.p_costo = p_costo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }
}


