package com.example.tl01e1124290091_120140001;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.appcompat.app.AlertDialog;
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

public class ActivityActualizar extends AppCompatActivity {
    EditText nombresactu, telefonoactu, notaactu;
    Spinner paisactu;
    ImageView imageViewactu;
    Button btnfotoactu, btnactulizar, btnvolver;
    private File fotoFile;
    private String fotoBase64 = null;
    private static final int PERMISO_CAMARA = 101;
    ActivityResultLauncher<Intent> tomarFotoLauncher;
    Map<String, String> paisCodigo = new HashMap<>();
    private int contactoId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actualizar);

        paisactu = (Spinner) findViewById(R.id.paisactu);
        nombresactu = (EditText) findViewById(R.id.nombresactu);
        telefonoactu = (EditText) findViewById(R.id.telefonoactu);
        notaactu = (EditText) findViewById(R.id.notaactu);
        imageViewactu = (ImageView) findViewById(R.id.imageViewactu);

        btnfotoactu = (Button) findViewById(R.id.btnfotoactu);
        btnactulizar = (Button) findViewById(R.id.btnactualizar);
        btnvolver = (Button) findViewById(R.id.btnvolver);

        contactoId = getIntent().getIntExtra("id", -1);

        if (contactoId != -1) {
            cargarContacto(contactoId);
        }

        btnfotoactu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });
        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (fotoFile != null && fotoFile.exists()) {
                            try {
                                // Cargar el bitmap desde el archivo
                                Bitmap foto = BitmapFactory.decodeFile(fotoFile.getAbsolutePath());

                                // Leer orientación EXIF
                                ExifInterface exif = new ExifInterface(fotoFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                int rotationInDegrees = exifToDegrees(orientation);

                                // Rotar bitmap si es necesario
                                Bitmap rotatedBitmap = foto;
                                if (rotationInDegrees != 0) {
                                    Matrix matrix = new Matrix();
                                    matrix.preRotate(rotationInDegrees);
                                    rotatedBitmap = Bitmap.createBitmap(foto, 0, 0,
                                            foto.getWidth(), foto.getHeight(), matrix, true);
                                }

                                // Mostrar en ImageView
                                imageViewactu.setImageBitmap(rotatedBitmap);

                                // Convertir a Base64
                                fotoBase64 = bitmapToBase64(rotatedBitmap);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ActivityActualizar.this, "Error al procesar la foto", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ActivityActualizar.this, "No se pudo obtener la foto", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        paisCodigo.put("Honduras", "+504");
        paisCodigo.put("Costa Rica", "+506");
        paisCodigo.put("Guatemala", "+502");
        paisCodigo.put("El Salvador", "+503");
        // Crear lista de países para el Spinner
        ArrayList<String> listaPaises = new ArrayList<>(paisCodigo.keySet());
        // Adapter del Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaPaises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paisactu.setAdapter(adapter);
        paisactu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paisSeleccionado = (String) parent.getItemAtPosition(position);
                // Llenar automáticamente el código del país en el EditText
                String codigo = paisCodigo.get(paisSeleccionado);
                telefonoactu.setText(codigo);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
        btnactulizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarContacto();
            }
        });
        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityActualizar.this, ActivitySegunda.class);
                startActivity(intent);
            }
        });
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
            seleccionarImagen();
        }
    }
    private void seleccionarImagen() {
        String[] opciones = {"Cámara", "Galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                // Tomar foto con cámara
                OpenCamara();
            } else if (which == 1) {
                // Seleccionar imagen de galería
                abrirGaleria();
            }
        });
        builder.show();
    }
    private void OpenCamara()
    {
        try {
            // Crear archivo temporal
            fotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "foto_" + System.currentTimeMillis() + ".jpg");
            Uri fotoUri = FileProvider.getUriForFile(this,
                    "com.example.tl01e1124290091_120140001.provider", fotoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            tomarFotoLauncher.launch(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Error al abrir cámara: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        seleccionarImagenLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> seleccionarImagenLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData(); // URI de la imagen seleccionada
                    try {
                        // Cargar bitmap desde URI
                        Bitmap foto = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                        // Mostrar en ImageView
                        imageViewactu.setImageBitmap(foto);

                        // Convertir a Base64
                        fotoBase64 = bitmapToBase64(foto);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

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
    private void showAlert(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void cargarContacto(int id) {
        SQLiteConexion conn = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();

        Cursor cursor = db.query(
                Transacciones.TableContactos,
                null,
                Transacciones.id + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            nombresactu.setText(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombres)));
            telefonoactu.setText(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono)));
            notaactu.setText(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nota)));

            String paisStr = cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.pais));
            int spinnerPos = ((ArrayAdapter) paisactu.getAdapter()).getPosition(paisStr);
            paisactu.setSelection(spinnerPos);

            String fotoStr = cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.foto));
            if (fotoStr != null && !fotoStr.isEmpty()) {
                byte[] bytes = Base64.decode(fotoStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageViewactu.setImageBitmap(bitmap);
                fotoBase64 = fotoStr; // mantener Base64 actual
            }
        }

        cursor.close();
        db.close();
    }
    private void actualizarContacto() {
        String nombreStr = nombresactu.getText().toString().trim();
        String telefonoStr = telefonoactu.getText().toString().trim();
        String notaStr = notaactu.getText().toString().trim();
        String paisStr = paisactu.getSelectedItem().toString();

        if (nombreStr.isEmpty() || telefonoStr.isEmpty() || notaStr.isEmpty()) {
            showAlert("Datos incompletos", "Completa todos los campos.");
            return;
        }

        if (fotoBase64 == null) {
            showAlert("Foto requerida", "Debes seleccionar o tomar una foto.");
            return;
        }

        SQLiteConexion conn = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombres, nombreStr);
        valores.put(Transacciones.telefono, telefonoStr);
        valores.put(Transacciones.nota, notaStr);
        valores.put(Transacciones.pais, paisStr);
        valores.put(Transacciones.foto, fotoBase64);

        int resultado = db.update(
                Transacciones.TableContactos,
                valores,
                Transacciones.id + "=?",
                new String[]{String.valueOf(contactoId)}
        );

        db.close();

        if (resultado > 0) {
            Toast.makeText(this, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
            finish(); // regresar a la lista
        } else {
            Toast.makeText(this, "Error al actualizar contacto", Toast.LENGTH_SHORT).show();
        }
    }
}