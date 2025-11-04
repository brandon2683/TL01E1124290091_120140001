package com.example.tl01e1124290091_120140001.Configuraciones;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tl01e1124290091_120140001.R;

import java.util.ArrayList;

public class ContactoAdapter extends ArrayAdapter<Contacto> {

    private Context context;
    private ArrayList<Contacto> contactos;

    public ContactoAdapter(Context context, ArrayList<Contacto> contactos) {
        super(context, 0, contactos);
        this.context = context;
        this.contactos = contactos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            item = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
        }

        Contacto contacto = contactos.get(position);

        ImageView imgFoto = item.findViewById(R.id.imgFoto);
        TextView tvNombre = item.findViewById(R.id.tvNombre);
        TextView tvTelefono = item.findViewById(R.id.tvTelefono);

        tvNombre.setText(contacto.getNombre());
        tvTelefono.setText(contacto.getTelefono());

        // Convertir Base64 a Bitmap
        if (contacto.getFoto() != null && !contacto.getFoto().isEmpty()) {
            byte[] bytes = Base64.decode(contacto.getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgFoto.setImageBitmap(bitmap);
        } else {
            imgFoto.setImageResource(R.mipmap.ic_launcher_round); // Imagen por defecto
        }

        return item;
    }
}
