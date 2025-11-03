package com.example.tl01e1124290091_120140001;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tl01e1124290091_120140001.Configuraciones.SQLiteConexion;

import java.util.ArrayList;

public class ActivitySegunda extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<com.example.tl01e1124290091_120140001.Personas> listaContactos;
    SQLiteConexion conexion;
    com.example.tl01e1124290091_120140001.ContactoAdapter.ContactoAdapter adapter;

    EditText editTextName;
    Button btnCompartir, btnImagen, btnActualizar, btnEliminar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        editTextName = findViewById(R.id.editTextText);

        btnCompartir = findViewById(R.id.btncompartir);
        btnImagen = findViewById(R.id.btnimagen);
        btnActualizar = findViewById(R.id.btnactualizar);
        btnEliminar = findViewById(R.id.btneliminar);
        btnVolver = findViewById(R.id.btnvolver);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Conexión SQLite y lista
        conexion = new SQLiteConexion(this, "DBContactos", null, 1);
        listaContactos = new ArrayList<>();

        // Cargar contactos en la lista
        cargarContactos();

        // Configurar adapter
        adapter = new ContactoAdapter(listaContactos);
        recyclerView.setAdapter(adapter);

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());

        // Botón eliminar
        btnEliminar.setOnClickListener(v -> {
            String nombre = editTextName.getText().toString();
            eliminarContacto(nombre);
        });

        // Botón actualizar
        btnActualizar.setOnClickListener(v -> {
            String nombre = editTextName.getText().toString();
            actualizarContacto(nombre);
        });

        // Botón compartir
        btnCompartir.setOnClickListener(v -> {
            String nombre = editTextName.getText().toString();
            compartirContacto(nombre);
        });

        // Botón ver imagen
        btnImagen.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad de ver imagen aún no implementada", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarContactos() {
        listaContactos.clear();
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM personas", null);

        if (cursor.moveToFirst()) {
            do {
                Personas p = new Personas();
                p.setId(cursor.getInt(0));
                p.setPais(cursor.getString(1));
                p.setNombre(cursor.getString(2));
                p.setTelefono(cursor.getString(3));
                p.setNota(cursor.getString(4));
                listaContactos.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private void eliminarContacto(String nombre) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        int rows = db.delete("personas", "nombre=?", new String[]{nombre});
        db.close();
        if (rows > 0) {
            Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
            cargarContactos();
        } else {
            Toast.makeText(this, "No se encontró contacto", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarContacto(String nombre) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nota", "Nota actualizada"); // ejemplo de actualización
        int rows = db.update("personas", valores, "nombre=?", new String[]{nombre});
        db.close();
        if (rows > 0) {
            Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
            cargarContactos();
        } else {
            Toast.makeText(this, "No se encontró contacto", Toast.LENGTH_SHORT).show();
        }
    }

    private void compartirContacto(String nombre) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM personas WHERE nombre=?", new String[]{nombre});
        if (cursor.moveToFirst()) {
            String info = "Nombre: " + cursor.getString(2) +
                    "\nPaís: " + cursor.getString(1) +
                    "\nTeléfono: " + cursor.getString(3) +
                    "\nNota: " + cursor.getString(4);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, info);
            startActivity(Intent.createChooser(intent, "Compartir contacto"));
        } else {
            Toast.makeText(this, "No se encontró contacto", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }
}
