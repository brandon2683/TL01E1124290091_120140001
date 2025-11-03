package com.example.tl01e1124290091_120140001;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tl01e1124290091_120140001.Configuraciones.SQLiteConexion;

public class MainActivity extends AppCompatActivity {

    EditText pais, nombres, telefono, nota;
    ImageView imageView;
    Button btnfoto, btnagregar, btncontactos;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    SQLiteConexion conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        pais = findViewById(R.id.pais);
        nombres = findViewById(R.id.nombres);
        telefono = findViewById(R.id.telefono);
        nota = findViewById(R.id.nota);
        imageView = findViewById(R.id.imageView);

        btnfoto = findViewById(R.id.btnfoto);
        btnagregar = findViewById(R.id.btnagregar);
        btncontactos = findViewById(R.id.btncontactos);

        conexion = new SQLiteConexion(this, "DBContactos", null, 1);

        // Abrir cámara
        btnfoto.setOnClickListener(v -> abrirCamara());

        // Guardar contacto en SQLite
        btnagregar.setOnClickListener(v -> AddPersona());

        // Abrir lista de contactos
        btncontactos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivitySegunda.class);
            startActivity(intent);
        });
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    // Guardar contacto en SQLite
    private void AddPersona() {
        SQLiteConexion dbHelper = new SQLiteConexion(this, "DBContactos", null, 1);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("pais", pais.getText().toString());
        valores.put("nombre", nombres.getText().toString());
        valores.put("telefono", telefono.getText().toString());
        valores.put("nota", nota.getText().toString());

        long resultado = db.insert("personas", null, valores);
        db.close();

        if (resultado != -1) {
            Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_LONG).show();
            pais.setText("");
            nombres.setText("");
            telefono.setText("");
            nota.setText("");
            imageView.setImageBitmap(null); // opcional, limpia la foto
        } else {
            Toast.makeText(this, "Error al guardar contacto", Toast.LENGTH_LONG).show();
        }
    }
}
