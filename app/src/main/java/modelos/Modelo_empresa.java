package modelos;

public class Modelo_empresa {

    private String id_empresa, nombre, documento, correo, dominio, telefono1, telefono2, pais, ciudad, direccion, logo;
    private byte[] imagen;

    public Modelo_empresa() {
    }

    public Modelo_empresa(String id_empresa, String nombre, String documento, String correo, String dominio, String telefono1, String telefono2, String pais, String ciudad, String direccion, String logo, byte[] imagen) {
        this.id_empresa = id_empresa;
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
        this.dominio = dominio;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.pais = pais;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.logo = logo;
        this.imagen = imagen;
    }

    public String getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(String id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
