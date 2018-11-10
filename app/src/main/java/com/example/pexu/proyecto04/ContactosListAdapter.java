package com.example.pexu.proyecto04;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactosListAdapter extends BaseAdapter { //Crear un Custom Adapter y override los getView() metodos

    private Context context;
    private int layout;
    private ArrayList<Contactos> contactosList;

    public ContactosListAdapter(Context context, int layout, ArrayList<Contactos> contactosList) {
        this.context = context;
        this.layout = layout;
        this.contactosList = contactosList;
    }

    @Override
    public int getCount() {
        return contactosList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder{
        ImageView imageView;
        TextView txtNombre, txtTelefono, txtEmail, id, txtLatitud, txtLongitud;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.id = (TextView) row.findViewById(R.id.id);
            holder.txtNombre = (TextView) row.findViewById(R.id.txtNombre);
            holder.txtTelefono = (TextView) row.findViewById(R.id.txtTelefono);
            holder.txtEmail = (TextView) row.findViewById(R.id.txtEmail);
            holder.imageView = (ImageView) row.findViewById(R.id.imgContacto);
            holder.txtLatitud = (TextView) row.findViewById(R.id.txtLatitud);
            holder.txtLongitud = (TextView) row.findViewById(R.id.txtLongitud);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Contactos contactos = contactosList.get(position);

        //holder.id.setText(contactos.getId());
        holder.txtNombre.setText(contactos.getNombre());
        holder.txtTelefono.setText(contactos.getTelefono());
        holder.txtEmail.setText(contactos.getEmail());
        holder.txtLatitud.setText(contactos.getLatitud());
        holder.txtLongitud.setText(contactos.getLongitud());


        byte[] contactosImage = contactos.getImagen();
        Bitmap bitmap = BitmapFactory.decodeByteArray(contactosImage, 0, contactosImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }

}
