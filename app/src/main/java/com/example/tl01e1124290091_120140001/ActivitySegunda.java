package com.example.tl01e1124290091_120140001;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tl01e1124290091_120140001.Configuraciones.Contacto;
import com.example.tl01e1124290091_120140001.Configuraciones.SQLiteConexion;
import com.example.tl01e1124290091_120140001.Configuraciones.Transacciones;

import java.util.ArrayList;

public class ActivitySegunda extends AppCompatActivity {

    ListView listView;
    EditText buscar;
    Button btnCompartir, btnImagen, btnActualizar, btnEliminar, btnVolver;
    ArrayList<String> listaInformacion;
    ArrayList<Contacto> listaContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        // Inicializar vistas
        listView = findViewById(R.id.listView);
        buscar = findViewById(R.id.Buscar);
        btnCompartir = findViewById(R.id.btncompartir);
        btnImagen = findViewById(R.id.btnimagen);
        btnActualizar = findViewById(R.id.btnactualizar);
        btnEliminar = findViewById(R.id.btneliminar);
        btnVolver = findViewById(R.id.btnvolver);

        consultarListaContactos();

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySegunda.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Botón eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Botón actualizar
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Botón compartir
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Botón ver imagen
        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void consultarListaContactos() {
        // Conexión a la base de datos
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        listaContacto = new ArrayList<>();
        listaInformacion = new ArrayList<>();

        // Ejecutar la consulta
        Cursor cursor = db.rawQuery(Transacciones.SELECTTABLEPERSONAS, null);

        // Recorrer los resultados
        while (cursor.moveToNext()) {
            Contacto contacto = new Contacto();

            contacto.setPais(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.pais)));
            contacto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombres)));
            contacto.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono)));
            contacto.setNota(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nota)));
            contacto.setFoto(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.foto)));

            // Agregar a la lista de objetos
            listaContacto.add(contacto);

            // Crear la información que se mostrará en el ListView
            listaInformacion.add(contacto.getNombre() + " - " + contacto.getTelefono());
        }

        cursor.close();
        db.close();

        // Crear y asignar el ArrayAdapter al ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listaInformacion);
        listView.setAdapter(adapter);
    }

}
