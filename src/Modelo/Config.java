
package Modelo;

public class Config {
    private int id;
    private String dni_empresa;
    private String nombre;
    private String telefono;
    private String direccion;
    private String mensaje;
    
    public Config(){
        
    }

    public Config(int id, String dni_empresa, String nombre, String telefono, String direccion, String mensaje) {
        this.id = id;
        this.dni_empresa = dni_empresa;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.mensaje = mensaje;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni_empresa() {
        return dni_empresa;
    }

    public void setDni_empresa(String dni_empresa) {
        this.dni_empresa = dni_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
