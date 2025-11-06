package com.example.tl01e1124290091_120140001;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button; // Nueva Importación
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tl01e1124290091_120140001.Configuraciones.Contacto;
import com.example.tl01e1124290091_120140001.Configuraciones.ContactoAdapter;
import com.example.tl01e1124290091_120140001.Configuraciones.SQLiteConexion;
import com.example.tl01e1124290091_120140001.Configuraciones.Transacciones;

import java.util.ArrayList;

public class ActivitySegunda extends AppCompatActivity {

    ListView listView;
    EditText buscar;

    // Declaración de Botones AÑADIDOS
    Button btnEliminar, btnImagen, btnCompartir, btnActualizar, btnVolver;

    ArrayList<Contacto> listaContacto;
    ContactoAdapter adapter;
    private Contacto contactoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        // Inicialización de Vistas
        listView = findViewById(R.id.listView);
        buscar = findViewById(R.id.Buscar);

        // Inicialización de Botones AÑADIDOS
        btnEliminar = findViewById(R.id.btneliminar);
        btnImagen = findViewById(R.id.btnimagen);
        btnCompartir = findViewById(R.id.btncompartir);
        btnActualizar = findViewById(R.id.btnactualizar);
        btnVolver = findViewById(R.id.btnvolver);

        listaContacto = new ArrayList<>();
        adapter = new ContactoAdapter(this, listaContacto);
        listView.setAdapter(adapter);

        cargarContactos();

        // Eventos de Botón (Solución al posible error de referencia)
        btnEliminar.setOnClickListener(v -> eliminarContacto());
        btnImagen.setOnClickListener(v -> mostrarImagen());
        btnCompartir.setOnClickListener(v -> compartirContacto());

        btnVolver.setOnClickListener(v -> finish()); // Cierra esta Activity y vuelve a MainActivity

        btnActualizar.setOnClickListener(v -> {
            // Por ahora, solo recargar la lista
            cargarContactos();
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show();
        });


        // Evento selección de contacto
        listView.setOnItemClickListener((parent, view, position, id) -> {
            contactoSeleccionado = adapter.getItem(position);
            Toast.makeText(ActivitySegunda.this, "Seleccionado: " + contactoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
        });

        // Buscador dinámico
        buscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    // (Resto de tus métodos: cargarContactos, eliminarContacto, mostrarImagen, compartirContacto)

    private void cargarContactos() {
        listaContacto.clear();

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();

        // Usar un try-catch para manejar errores de columna faltante en la DB
        try (Cursor cursor = db.rawQuery(Transacciones.SELECTTABLEPERSONAS, null)) {
            while (cursor.moveToNext()) {
                Contacto contacto = new Contacto();
                contacto.setPais(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.pais)));
                contacto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombres)));
                contacto.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono)));
                contacto.setNota(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nota)));
                contacto.setFoto(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.foto)));

                listaContacto.add(contacto);
            }
        } catch (IllegalArgumentException e) {
            // Este error ocurre si una columna (ej: 'foto') no existe en tu tabla.
            Toast.makeText(this, "Error de base de datos: Columna faltante. " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }

        adapter.notifyDataSetChanged();
    }

    // Tu método eliminarContacto...
    public void eliminarContacto() {
        // ... (Tu código actual de eliminar)
        if (contactoSeleccionado != null) {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            int resultado = db.delete(Transacciones.TableContactos,
                    Transacciones.nombres + "=? AND " + Transacciones.telefono + "=?",
                    new String[]{contactoSeleccionado.getNombre(), contactoSeleccionado.getTelefono()});
            db.close();

            if (resultado > 0) {
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                contactoSeleccionado = null; // Desseleccionar después de eliminar
                cargarContactos();
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecciona un contacto", Toast.LENGTH_SHORT).show();
        }
    }

    // Tu método mostrarImagen...
    public void mostrarImagen() {
        // ... (Tu código actual de mostrarImagen)
        if (contactoSeleccionado != null && contactoSeleccionado.getFoto() != null && !contactoSeleccionado.getFoto().isEmpty()) {
            try {
                byte[] bytes = Base64.decode(contactoSeleccionado.getFoto(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                builder.setView(imageView);
                builder.setPositiveButton("Cerrar", null);
                builder.show();
            } catch (Exception e) {
                Toast.makeText(this, "Error al decodificar la foto.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecciona un contacto con foto.", Toast.LENGTH_SHORT).show();
        }
    }

    // Tu método compartirContacto...
    public void compartirContacto() {
        // ... (Tu código actual de compartirContacto)
        if (contactoSeleccionado != null) {
            String mensaje = "Nombre: " + contactoSeleccionado.getNombre() +
                    "\nTeléfono: " + contactoSeleccionado.getTelefono() +
                    "\nPaís: " + contactoSeleccionado.getPais() +
                    "\nNota: " + contactoSeleccionado.getNota();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, mensaje);
            startActivity(Intent.createChooser(intent, "Compartir contacto"));
        } else {
            Toast.makeText(this, "Selecciona un contacto", Toast.LENGTH_SHORT).show();
        }
    }
}