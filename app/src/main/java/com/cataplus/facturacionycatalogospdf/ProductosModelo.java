package com.cataplus.facturacionycatalogospdf;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

//https://www.youtube.com/watch?v=4ZUgK3hSYoY&t=118s
public class ProductosModelo {
    private String id, nombre, codigo, categoria, seguimiento, cantidad, p_costo, p_mayor, p_detal, estado, foto1;
    byte[] imagen;


    public ProductosModelo() {
    }

    public ProductosModelo(String id, String nombre, String codigo, String categoria, String seguimiento, String cantidad, String p_costo, String p_mayor, String p_detal, String estado, String foto1, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.categoria = categoria;
        this.seguimiento = seguimiento;
        this.cantidad = cantidad;
        this.p_costo = p_costo;
        this.p_mayor = p_mayor;
        this.p_detal = p_detal;
        this.estado = estado;
        this.foto1 = foto1;
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(String seguimiento) {
        this.seguimiento = seguimiento;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getP_costo() {
        return p_costo;
    }

    public void setP_costo(String p_costo) {
        this.p_costo = p_costo;
    }

    public String getP_mayor() {
        return p_mayor;
    }

    public void setP_mayor(String p_mayor) {
        this.p_mayor = p_mayor;
    }

    public String getP_detal() {
        return p_detal;
    }

    public void setP_detal(String p_detal) {
        this.p_detal = p_detal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}