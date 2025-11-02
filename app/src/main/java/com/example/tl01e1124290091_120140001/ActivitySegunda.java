package com.example.tl01e1124290091_120140001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivitySegunda extends AppCompatActivity {

    Button btnvolver,btncompartir, btnimagen, btneliminar, btnactualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_segunda);

        btnvolver = (Button) findViewById(R.id.btnvolver);
        btncompartir = (Button) findViewById(R.id.btncompartir);
        btnimagen = (Button) findViewById(R.id.btnimagen);
        btneliminar = (Button) findViewById(R.id.btneliminar);
        btnactualizar = (Button) findViewById(R.id.btnactualizar);

        btnvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySegunda.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}