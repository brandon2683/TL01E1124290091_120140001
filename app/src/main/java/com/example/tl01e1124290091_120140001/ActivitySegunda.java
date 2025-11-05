package com.example.tl01e1124290091_120140001;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
    ArrayList<Contacto> listaContacto;
    ContactoAdapter adapter;
    private Contacto contactoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);

        listView = findViewById(R.id.listView);
        buscar = findViewById(R.id.Buscar);

        listaContacto = new ArrayList<>();
        adapter = new ContactoAdapter(this, listaContacto);
        listView.setAdapter(adapter);

        cargarContactos();

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

    private void cargarContactos() {
        listaContacto.clear();

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Cursor cursor = db.rawQuery(Transacciones.SELECTTABLEPERSONAS, null);

        while (cursor.moveToNext()) {
            Contacto contacto = new Contacto();
            contacto.setPais(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.pais)));
            contacto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombres)));
            contacto.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono)));
            contacto.setNota(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nota)));
            contacto.setFoto(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.foto)));

            listaContacto.add(contacto);
        }

        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();
    }

    public void eliminarContacto() {
        if (contactoSeleccionado != null) {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            int resultado = db.delete(Transacciones.TableContactos,
                    Transacciones.nombres + "=? AND " + Transacciones.telefono + "=?",
                    new String[]{contactoSeleccionado.getNombre(), contactoSeleccionado.getTelefono()});
            db.close();

            if (resultado > 0) {
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                cargarContactos();
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecciona un contacto", Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarImagen() {
        if (contactoSeleccionado != null && contactoSeleccionado.getFoto() != null) {
            byte[] bytes = Base64.decode(contactoSeleccionado.getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

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

    public void compartirContacto() {
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
