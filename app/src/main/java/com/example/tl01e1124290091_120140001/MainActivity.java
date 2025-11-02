package com.example.tl01e1124290091_120140001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText pais, nombres, telefono, nota;
    ImageView imageView;
    Button btnfoto, btnagregar, btncontactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        pais = (EditText) findViewById(R.id.pais);
        nombres = (EditText) findViewById(R.id.nombres);
        telefono = (EditText) findViewById(R.id.telefono);
        nota = (EditText) findViewById(R.id.nota);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnfoto = (Button) findViewById(R.id.btnfoto);
        btnagregar = (Button) findViewById(R.id.btnagregar);
        btncontactos = (Button) findViewById(R.id.btncontactos);

        btnagregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPersona();
            }
        });

        btncontactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ActivitySegunda.class);
                startActivity(intent);
            }
        });

    }
    private void AddPersona()
    {
        Toast.makeText(this,"registro ingresado",Toast.LENGTH_LONG).show();
    }

}