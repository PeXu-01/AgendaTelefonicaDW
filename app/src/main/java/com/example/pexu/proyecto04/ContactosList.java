package com.example.pexu.proyecto04;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//Creado nuevo activity y entonces mostrar toda la data en un gridview o listview
public class ContactosList extends AppCompatActivity {

    private Button btnTrazar;

    //GridView gridView;
    ListView listView;
    ArrayList<Contactos> list;
    ContactosListAdapter adapter = null;
    Button btnActualizar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactos_list_activity);

        listView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new ContactosListAdapter(this, R.layout.contactos_items, list);
        listView.setAdapter(adapter);



        // get all data from sqlite obtener todos los datos desde sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM Contactos"); //SQLiteHelper public Cursor getData
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);    //variables "nombre"
            String telefono = cursor.getString(2);
            String email = cursor.getString(3);
            byte[] imagen = cursor.getBlob(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            list.add(new Contactos(id, nombre, telefono, email, imagen, latitud, longitud)); //clase = "Contactos"
        }
        adapter.notifyDataSetChanged();

        //FASE 2
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Trazar Ruta","Actualizar", "Eliminar"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ContactosList.this);

                dialog.setTitle("Escoger una acción");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        //
                        switch(item){
                            case 0:
                            //trazar ruta
                                Cursor b = MainActivity.sqLiteHelper.getData("SELECT id FROM Contactos");
                                ArrayList<Integer> arrID = new ArrayList<Integer>();
                                while (b.moveToNext()){
                                    arrID.add(b.getInt(0));
                                }
                                Intent intent = new Intent (ContactosList.this, MapsActivity2.class);
                                startActivity(intent);
                            break;
                            case 1:
                            // actualizar
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM Contactos"); //para actualizar imagen
                            ArrayList<Integer> arrID1 = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID1.add(c.getInt(0));
                            }
                            showDialogUpdate(ContactosList.this, arrID1.get(position));
                            break;
                            case 2:
                            // eliminar
                            Cursor d = MainActivity.sqLiteHelper.getData("SELECT id FROM Contactos"); //para actualizar imagen
                            ArrayList<Integer> arrID2 = new ArrayList<Integer>();
                            while (d.moveToNext()){
                                arrID2.add(d.getInt(0));
                            }
                            showDialogDelete(arrID2.get(position));
                            break;
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

    }

    TextView txtLatitud, txtLongitud;
    //para mandar la latitud y longitud a MapsActivity2 ARREGLAR!!
    private void showDialogRoute (Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_maps2);
        dialog.setTitle("Trazar Ruta");

    }

    ImageView imageViewContactos;
    //Crear un dialogo para actualizar los datos
    private void showDialogUpdate(Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_contactos_activity); //creamos un nuevo layout y los mostramos como un dialogo
        dialog.setTitle("Actualizar");

        imageViewContactos = (ImageView) dialog.findViewById(R.id.imageViewContactos);
        final EditText edtNombre = (EditText) dialog.findViewById(R.id.edtNombre);
        final EditText edtTelefono = (EditText) dialog.findViewById(R.id.edtTelefono);
        final EditText edtEmail = (EditText) dialog.findViewById(R.id.edtEmail);
        final EditText edtLatitud = (EditText) dialog.findViewById(R.id.edtLatitud);
        final EditText edtLongitud = (EditText) dialog.findViewById(R.id.edtLongitud);
        Button btnActualizar = (Button) dialog.findViewById(R.id.btnActualizar);

        // poner anchura para dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels *1.00);
        // poner altura para dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels *0.95);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewContactos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // solicitar la librería de fotos
                ActivityCompat.requestPermissions(
                        ContactosList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888

                );
            }
        });


        btnActualizar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateData(
                            edtNombre.getText().toString().trim(),
                            edtTelefono.getText().toString().trim(),
                            edtEmail.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewContactos),
                            edtLatitud.getText().toString().trim(),
                            edtLongitud.getText().toString().trim(),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "¡Actualizado!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("Error de actualización", error.getMessage());
                }
                updateContactosList(); //fin fase 2
            }
        });
    }
    //inicio fase 3, eliminar y dialogos (ventanitas) de alerta
    private void showDialogDelete(final int idContacto){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ContactosList.this);

        dialogDelete.setTitle("¡Advertencia!");
        dialogDelete.setMessage("¿Está seguro que quiere eliminar este contacto?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(idContacto);
                    Toast.makeText(getApplicationContext(), "¡Borrado exitosamente!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                updateContactosList();

            }
        });

        dialogDelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();

    }

    // para refrescar el ListView después de actualizar
    private void updateContactosList(){

        // get all data from sqlite obtener todos los datos desde sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM Contactos"); //SQLiteHelper public Cursor getData
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);    //variables "nombre"
            String telefono = cursor.getString(2);
            String email = cursor.getString(3);
            byte[] imagen = cursor.getBlob(4);
            String latitud = cursor.getString(5);
            String longitud = cursor.getString(6);
            list.add(new Contactos(id, nombre, telefono, email, imagen, latitud, longitud)); //clase = "Contactos"
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "No tienes permiso para acceder al lugar del archivo", Toast.LENGTH_LONG).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewContactos.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
