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
import android.widget.CheckBox;
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

        ContactoAdapter adapter = new ContactoAdapter(this, listaContacto);
        listView.setAdapter(adapter);
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
                ArrayList<Contacto> seleccionados = obtenerContactosSeleccionados();
                if (!seleccionados.isEmpty()) {
                    Contacto contacto = seleccionados.get(0); // primer contacto seleccionado
                    Intent intent = new Intent(ActivitySegunda.this, ActivityActualizar.class);
                    intent.putExtra("id", contacto.getId()); // enviamos el ID
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivitySegunda.this, "Selecciona un contacto", Toast.LENGTH_SHORT).show();
                }
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
                // Obtener el contacto correspondiente
                Contacto contacto = listaContacto.get(position);

                // Cambiar el estado del CheckBox
                contacto.setSeleccionado(!contacto.isSeleccionado());

                // Actualizar el CheckBox visual dentro del item
                CheckBox checkBox = view.findViewById(R.id.checkSeleccionar);
                checkBox.setChecked(contacto.isSeleccionado());
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
        }

        cursor.close();
        db.close();

        // Crear y asignar el ArrayAdapter al ListView
        ContactoAdapter adapter = new ContactoAdapter(this, listaContacto);
        listView.setAdapter(adapter);
    }
    public void eliminar() {
        ArrayList<Contacto> seleccionados = obtenerContactosSeleccionados();

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        int eliminados = 0;

        for (Contacto c : seleccionados) {
            // Recomiendo usar ID único si tu tabla tiene
            int resultado = db.delete(
                    Transacciones.TableContactos,
                    Transacciones.nombres + "=? AND " + Transacciones.telefono + "=?",
                    new String[]{c.getNombre(), c.getTelefono()}
            );

            if (resultado > 0) eliminados++;
        }

        db.close();

        if (eliminados > 0) {
            Toast.makeText(this, eliminados + " contacto(s) eliminado(s)", Toast.LENGTH_SHORT).show();
            consultarListaContactos(); // recargar la lista
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
    public void Compartir() {
        ArrayList<Contacto> seleccionados = obtenerContactosSeleccionados();

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un contacto", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder mensaje = new StringBuilder();
        for (Contacto c : seleccionados) {
            mensaje.append("Nombre: ").append(c.getNombre()).append("\n")
                    .append("Teléfono: ").append(c.getTelefono()).append("\n")
                    .append("País: ").append(c.getPais()).append("\n")
                    .append("Nota: ").append(c.getNota()).append("\n\n");
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mensaje.toString());
        startActivity(Intent.createChooser(intent, "Compartir contactos"));
    }
    public void Imagen() {ArrayList<Contacto> seleccionados = obtenerContactosSeleccionados();

        if (!seleccionados.isEmpty()) {
            Contacto contacto = seleccionados.get(0); // tomamos el primero seleccionado

            if (contacto.getFoto() != null && !contacto.getFoto().isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(contacto.getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(bitmap);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    int padding = (int) (16 * getResources().getDisplayMetrics().density);
                    imageView.setPadding(padding, padding, padding, padding);

                    // Limitar tamaño máximo
                    int maxWidth = (int) (300 * getResources().getDisplayMetrics().density);
                    int maxHeight = (int) (300 * getResources().getDisplayMetrics().density);
                    imageView.setMaxWidth(maxWidth);
                    imageView.setMaxHeight(maxHeight);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(contacto.getNombre());
                    builder.setView(imageView);
                    builder.setPositiveButton("Cerrar", null);
                    builder.show();

                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No hay foto disponible para este contacto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecciona al menos un contacto", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList<Contacto> obtenerContactosSeleccionados() {
        ArrayList<Contacto> seleccionados = new ArrayList<>();
        for (Contacto c : listaContacto) {
            if (c.isSeleccionado()) {
                seleccionados.add(c);
            }
        }
        return seleccionados;
    }
}
