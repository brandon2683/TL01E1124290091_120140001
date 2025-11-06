package com.example.tl01e1124290091_120140001.Configuraciones;

public class Transacciones
{
    // Nombre de la base de datos
    public static final String DBNAME = "TL01E1";

    //Nombre de la tabla de la base de datos
    public static final String TableContactos = "Contactos";

    //Campos de la tabla personas
    public static final String id = "id";
    public static final String nombres = "nombres";
    public static final String pais = "pais";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String foto = "foto";

    // DDL
    public static final String CREATETABLECONTACTOS =
            "CREATE TABLE " + TableContactos + " ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    pais + " TEXT NOT NULL, " +
                    nombres + " TEXT NOT NULL, " +
                    telefono + " TEXT, " +
                    nota + " TEXT NOT NULL, " +
                    foto + " TEXT ) " ;

    public static final String DROPTABLECONTACTOS = "DROP TABLE IF EXISTS " + TableContactos;

    //DML - select , insert, update, delete
    public static final String SELECTTABLEPERSONAS = "SELECT * FROM " + TableContactos;
}
