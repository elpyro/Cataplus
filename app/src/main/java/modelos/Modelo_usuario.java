package modelos;

public class Modelo_usuario {
    private String id, nombre, correo, fecha_registro, fecha_ultima_ingreso, premium,base_datos,fecha_base_datos;

    public String getBase_datos() {
        return base_datos;
    }

    public void setBase_datos(String base_datos) {
        this.base_datos = base_datos;
    }

    public String getFecha_base_datos() {
        return fecha_base_datos;
    }

    public void setFecha_base_datos(String fecha_base_datos) {
        this.fecha_base_datos = fecha_base_datos;
    }

    public String getFecha_ultima_ingreso() {
        return fecha_ultima_ingreso;
    }

    public void setFecha_ultima_ingreso(String fecha_ultima_ingreso) {
        this.fecha_ultima_ingreso = fecha_ultima_ingreso;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }
    @Override
    public String toString(){return nombre;}
}
