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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.tl01e1124290091_120140001.Configuraciones.SQLiteConexion;
import com.example.tl01e1124290091_120140001.Configuraciones.Transacciones;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    EditText pais, nombres, telefono, nota;
    ImageView imageView;
    Button btnfoto, btnagregar, btncontactos;

    private File fotoFile;
    private String fotoBase64 = null;
    private static final int PERMISO_CAMARA = 101;
    ActivityResultLauncher<Intent> tomarFotoLauncher;


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

        // Abrir cámara
        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permisos();
            }
        });

        // Guardar contacto en SQLite
        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPersona();
            }
        });

        // Abrir lista de contactos
        btncontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivitySegunda.class);
                startActivity(intent);
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
                            imageView.setImageBitmap(rotatedBitmap);

                            // Convertir a Base64
                            fotoBase64 = bitmapToBase64(rotatedBitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al procesar la foto", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No se pudo obtener la foto", Toast.LENGTH_LONG).show();
                    }
                }
            }
                );
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

    private void OpenCamara()
    {
        try {
            // Crear archivo temporal
            fotoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "foto_" + System.currentTimeMillis() + ".jpg");
            Uri fotoUri = FileProvider.getUriForFile(this,
                    "com.example.ucenm3p20251.provider", fotoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            tomarFotoLauncher.launch(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Error al abrir cámara: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Guardar contacto en SQLite
    private void AddPersona() {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.DBNAME, null, 1);
        SQLiteDatabase db =  conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(Transacciones.nombres, nombres.getText().toString());
        valores.put(Transacciones.pais, pais.getText().toString());
        valores.put(Transacciones.telefono,Integer.parseInt(telefono.getText().toString()));
        valores.put(Transacciones.nota, nota.getText().toString());
        valores.put(Transacciones.foto,fotoBase64);

        long resultado = db.insert(Transacciones.TableContactos, null, valores);

        if (resultado != -1) {
            Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_LONG).show();
            clean();
        } else {
            Toast.makeText(this, "Error al guardar contacto", Toast.LENGTH_LONG).show();
            clean();
        }
    }

    private void clean()
    {
        pais.setText("");
            nombres.setText("");
            telefono.setText("");
            nota.setText("");
            imageView.setImageBitmap(null); // opcional, limpia la foto
    }
}
