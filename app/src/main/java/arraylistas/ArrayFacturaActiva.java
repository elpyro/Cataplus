package arraylistas;

public class ArrayFacturaActiva {
    private String id_producto;
    private String precio_producto;
    private String seleccion_producto;
    private String nombre_producto;
    private String barras;
    private String foto1;
    private String precio_costo;


    public ArrayFacturaActiva() {
    }

    public ArrayFacturaActiva(String id_producto, String precio_producto, String seleccion_producto, String nombre_producto, String barras, String foto1, String precio_costo) {
        this.id_producto = id_producto;
        this.precio_producto = precio_producto;
        this.seleccion_producto = seleccion_producto;
        this.nombre_producto = nombre_producto;
        this.barras = barras;
        this.foto1 = foto1;
        this.precio_costo = precio_costo;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getPrecio_producto() {
        return precio_producto;
    }

    public void setPrecio_producto(String precio_producto) {
        this.precio_producto = precio_producto;
    }

    public String getSeleccion_producto() {
        return seleccion_producto;
    }

    public void setSeleccion_producto(String seleccion_producto) {
        this.seleccion_producto = seleccion_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getBarras() {
        return barras;
    }

    public void setBarras(String barras) {
        this.barras = barras;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getPrecio_costo() {
        return precio_costo;
    }

    public void setPrecio_costo(String precio_costo) {
        this.precio_costo = precio_costo;
    }

    ////    PARA LEER LOS CARACTERES STRING
//    public String toString(){
//        return id_producto+" "+nombre_producto+" "+precio_producto+" "+cantidad_producto;
//    }
}