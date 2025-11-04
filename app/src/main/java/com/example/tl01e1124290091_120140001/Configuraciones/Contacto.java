package com.example.tl01e1124290091_120140001.Configuraciones;

public class Contacto {
    private String pais;
    private String nombre;
    private String telefono;
    private String nota;
    private String foto;

    // Constructor vacío
    public Contacto() {
    }

    // Constructor con parámetros
    public Contacto(String pais, String nombre, String telefono, String nota, String foto) {
        this.pais = pais;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.foto = foto;
    }

    // Getters y Setters
    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
