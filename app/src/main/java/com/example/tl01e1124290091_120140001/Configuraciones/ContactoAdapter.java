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
    private ArrayList<Contacto> contactosOriginal; // Lista completa
    private ArrayList<Contacto> contactosFiltrados; // Lista filtrada

    public ContactoAdapter(Context context, ArrayList<Contacto> contactos) {
        super(context, 0, contactos);
        this.context = context;
        this.contactosOriginal = new ArrayList<>(contactos);
        this.contactosFiltrados = contactos;
    }

    @Override
    public int getCount() {
        return contactosFiltrados.size();
    }

    @Override
    public Contacto getItem(int position) {
        return contactosFiltrados.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            item = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
        }

        Contacto contacto = contactosFiltrados.get(position);

        ImageView imgFoto = item.findViewById(R.id.imgFoto);
        TextView tvNombre = item.findViewById(R.id.tvNombre);
        TextView tvTelefono = item.findViewById(R.id.tvTelefono);

        tvNombre.setText(contacto.getNombre());
        tvTelefono.setText(contacto.getTelefono());

        if (contacto.getFoto() != null && !contacto.getFoto().isEmpty()) {
            byte[] bytes = Base64.decode(contacto.getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgFoto.setImageBitmap(bitmap);
        } else {
            imgFoto.setImageResource(R.mipmap.ic_launcher_round); // Imagen por defecto
        }

        return item;
    }

    // Método para filtrar la lista por nombre o teléfono
    public void filtrar(String texto) {
        texto = texto.toLowerCase();
        contactosFiltrados = new ArrayList<>();

        if (texto.isEmpty()) {
            contactosFiltrados.addAll(contactosOriginal);
        } else {
            for (Contacto c : contactosOriginal) {
                if (c.getNombre().toLowerCase().contains(texto) ||
                        c.getTelefono().toLowerCase().contains(texto)) {
                    contactosFiltrados.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }
}
