package com.example.tl01e1124290091_120140001;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Importante para la alerta
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.tl01e1124290091_120140001.Configuraciones.SQLiteConexion;
import com.example.tl01e1124290091_120140001.Configuraciones.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText nombres, telefono, nota;
    Spinner pais;
    ImageView imageView;
    Button btnfoto, btnagregar, btncontactos;

    private File fotoFile;
    private String fotoBase64 = null;
    private static final int PERMISO_CAMARA = 101;
    ActivityResultLauncher<Intent> tomarFotoLauncher;
    Map<String, String> paisCodigo = new HashMap<>();

    // Autoridad para el FileProvider
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.tl01e1124290091_120140001.provider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        pais = (Spinner) findViewById(R.id.pais);
        nombres = (EditText) findViewById(R.id.nombres);
        telefono = (EditText) findViewById(R.id.telefono);
        nota = (EditText) findViewById(R.id.nota);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnfoto = (Button) findViewById(R.id.btnfoto);
        btnagregar = (Button) findViewById(R.id.btnagregar);
        btncontactos = (Button) findViewById(R.id.btncontactos);

        btnfoto.setOnClickListener(v -> Permisos());

        btnagregar.setOnClickListener(v -> AddPersona());

        btncontactos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivitySegunda.class);
            startActivity(intent);
        });

        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (fotoFile != null && fotoFile.exists()) {
                            try {
                                Bitmap foto = BitmapFactory.decodeFile(fotoFile.getAbsolutePath());
                                ExifInterface exif = new ExifInterface(fotoFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                int rotationInDegrees = exifToDegrees(orientation);

                                Bitmap rotatedBitmap = foto;
                                if (rotationInDegrees != 0) {
                                    Matrix matrix = new Matrix();
                                    matrix.preRotate(rotationInDegrees);
                                    rotatedBitmap = Bitmap.createBitmap(foto, 0, 0,
                                            foto.getWidth(), foto.getHeight(), matrix, true);
                                }

                                imageView.setImageBitmap(rotatedBitmap);
                                fotoBase64 = bitmapToBase64(rotatedBitmap);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Error al procesar la foto", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No se pudo obtener la foto", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        paisCodigo.put("Honduras", "504");
        paisCodigo.put("Costa Rica", "506");
        paisCodigo.put("Guatemala", "502");
        paisCodigo.put("El Salvador", "503");

        ArrayList<String> listaPaises = new ArrayList<>(paisCodigo.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaPaises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pais.setAdapter(adapter);

        pais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paisSeleccionado = (String) parent.getItemAtPosition(position);
                String codigo = paisCodigo.get(paisSeleccionado);
                if (telefono.getText().toString().length() < 4) {
                    telefono.setText(codigo);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private int exifToDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90: return 90;
            case ExifInterface.ORIENTATION_ROTATE_180: return 180;
            case ExifInterface.ORIENTATION_ROTATE_270: return 270;
            default: return 0;
        }
    }

    private void Permisos() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.CAMERA}, PERMISO_CAMARA);
        }
        else
        {
            OpenCamara();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISO_CAMARA)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                OpenCamara();
            }
            else
            {
                Toast.makeText(this, "Permiso de Camara denegado "  , Toast.LENGTH_LONG).show();
            }
        }
    }

    private File crearArchivoDeImagen() throws IOException {
        String nombreImagen = "foto_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        fotoFile = new File(storageDir, nombreImagen);
        return fotoFile;
    }


    private void OpenCamara()
    {
        try {
            fotoFile = crearArchivoDeImagen();

            Uri fotoUri = FileProvider.getUriForFile(this,
                    FILE_PROVIDER_AUTHORITY,
                    fotoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            tomarFotoLauncher.launch(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Error al abrir cámara: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Muestra un AlertDialog con un título y mensaje de error.
     * @param titulo El título de la alerta.
     * @param mensaje El mensaje de error a mostrar.
     */
    private void showAlert(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }


    // Guardar contacto en SQLite con validación de Alerta
    private void AddPersona() {
        String nombreStr = nombres.getText().toString().trim();
        String telefonoStr = telefono.getText().toString().trim();
        String notaStr = nota.getText().toString().trim();
        String paisStr = pais.getSelectedItem().toString();

        if (nombreStr.isEmpty() || telefonoStr.isEmpty() || notaStr.isEmpty()) {
            showAlert("Datos Incompletos", "Por favor, complete todos los campos de texto.");
            return;
        }

        if (fotoBase64 == null) {
            showAlert("Foto Requerida", "Debe tomar una foto para guardar el contacto.");
            return;
        }

        // Validación de longitud de teléfono (simple)
        if (telefonoStr.length() < 7) {
            showAlert("Teléfono Inválido", "El número de teléfono debe tener al menos 7 dígitos (incluyendo código de país).");
            return;
        }

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db =  conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombres, nombreStr);
        valores.put(Transacciones.pais, paisStr);
        // Usando String para evitar NumberFormatException
        valores.put(Transacciones.telefono, telefonoStr);
        valores.put(Transacciones.nota, notaStr);
        valores.put(Transacciones.foto, fotoBase64);

        long resultado = db.insert(Transacciones.TableContactos, null, valores);
        db.close();

        if (resultado != -1) {
            Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_LONG).show();
            clean();
        } else {
            showAlert("Error de Guardado", "Ocurrió un error al guardar el contacto en la base de datos.");
        }
    }

    private void clean()
    {
        pais.setSelection(0); // Restablece el Spinner a la primera posición
        nombres.setText("");
        telefono.setText(paisCodigo.get(pais.getSelectedItem().toString())); // Vuelve a poner el código de país
        nota.setText("");
        imageView.setImageDrawable(null); // Limpiar la imagen de forma segura
        fotoBase64 = null; // Limpiar la data Base64
        fotoFile = null; // Limpiar la referencia al archivo
    }
}