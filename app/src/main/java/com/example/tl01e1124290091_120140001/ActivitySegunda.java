package com.example.tl01e1124290091_120140001;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    Button btnCompartir, btnImagen, btnActualizar, btnEliminar, btnVolver;
    ArrayList<String> listaInformacion;
    ArrayList<Contacto> listaContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        // Inicializar vistas
        listView = (ListView) findViewById(R.id.listView);
        buscar = (EditText) findViewById(R.id.Buscar);
        btnCompartir = (Button) findViewById(R.id.btncompartir);
        btnImagen = (Button) findViewById(R.id.btnimagen);
        btnActualizar = (Button) findViewById(R.id.btnactualizar);
        btnEliminar = (Button) findViewById(R.id.btneliminar);
        btnVolver = (Button) findViewById(R.id.btnvolver);

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
                eliminar();
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
                Compartir();
            }
        });

        // Botón ver imagen
        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Imagen();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactoSeleccionado = listaContacto.get(position);
                Toast.makeText(ActivitySegunda.this, "Seleccionado: " + contactoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Contacto contactoSeleccionado = null;
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
        }

        cursor.close();
        db.close();

        // Crear y asignar el ArrayAdapter al ListView
        ContactoAdapter adapter = new ContactoAdapter(this, listaContacto);
        listView.setAdapter(adapter);
    }
    public void eliminar() {
        if (contactoSeleccionado != null) {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            // Borrar por nombre y teléfono (o agrega un ID único en la tabla)
            int resultado = db.delete(Transacciones.TableContactos,
                    Transacciones.nombres + "=? AND " + Transacciones.telefono + "=?",
                    new String[]{contactoSeleccionado.getNombre(), contactoSeleccionado.getTelefono()});

            db.close();

            if (resultado > 0) {
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                consultarListaContactos(); // recargar lista
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecciona un contacto", Toast.LENGTH_SHORT).show();
        }
    }
    public void Compartir() {
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
    public void Imagen() {
        if (contactoSeleccionado != null && contactoSeleccionado.getFoto() != null) {
            byte[] bytes = Base64.decode(contactoSeleccionado.getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            // Abrir un Dialog para mostrar la imagen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            builder.setView(imageView);
            builder.setPositiveButton("Cerrar", null);
            builder.show();
        } else {
            Toast.makeText(this, "No hay foto disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
