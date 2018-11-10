package com.example.pexu.proyecto04;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Serializable {
    //Declaración de variables, botones, imagenes, etc.
    EditText edtNombre, edtTelefono, edtEmail, edtLatitud, edtLongitud;
    Button btnElegir, btnAñadir, btnLista, btnElegirMapa;
    ImageView imageView;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();


        //Crear base de datos con el nombre de la base de datos ContactosDB.sqlite
        sqLiteHelper = new SQLiteHelper(this, "ContactosDB2.sqlite", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CONTACTOS (Id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR, telefono VARCHAR, email VARCHAR, imagen BLOB, latitud VARCHAR, longitud VARCHAR)");
        //Adjuntar un click listener a el botón btnElegir y obtener imagen desde la galería dentro de la ImageView
        btnElegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY

                );
            }
        });

        //Boton para ver la lista de contactos
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactosList.class);
                startActivity(intent);

            }
        });

    }

    //metodo imageViewToByte
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "No tienes permiso para acceder al lugar del archivo", Toast.LENGTH_LONG).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        edtNombre = (EditText) findViewById(R.id.edtNombre);
        edtTelefono = (EditText) findViewById(R.id.edtTelefono);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        btnElegir = (Button) findViewById(R.id.btnElegir);
        btnAñadir = (Button) findViewById(R.id.btnAñadir);
        btnLista = (Button) findViewById(R.id.btnLista);
        imageView = (ImageView) findViewById(R.id.imageView);
        edtLatitud = (EditText) findViewById(R.id.edtLatitud);
        edtLongitud = (EditText) findViewById(R.id.edtLongitud);



        //detecta si se trae algo de MapsActivity
        Bundle parametros = this.getIntent().getExtras();
        if (parametros != null) {
            edtLatitud.setText(getIntent().getExtras().getString("latitud"));
            edtLongitud.setText(getIntent().getExtras().getString("longitud"));
            edtNombre.setText(getIntent().getExtras().getString("nombre"));
            edtTelefono.setText(getIntent().getExtras().getString("telefono"));
            edtEmail.setText(getIntent().getExtras().getString("email"));

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);

        }


        //para el botón de elegir mapa, est{a la variable btnElegirMapa, se hace un casting (Button), y lo buscamos por su id
        btnElegirMapa = (Button) findViewById(R.id.btnElegirMapa);

        //el OnClickListener (para que haga una acción) para que al dar click al boton nos llevará al activity de Mapas
        //new crear una instancia OnClickListener, y nos completa todo el método
//        btnElegirMapa.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {                           //la clase y se le indica que es una clase
////                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
////                //y aquí para poder iniciar el activity mandamos a traer el método
////                startActivity(intent);
////            }
////        });

    }

//    public void elegirMapa(View view) {
//        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//////                //y aquí para poder iniciar el activity mandamos a traer el método
//        startActivity(intent);
//
//
//    }
    // ENVIAR A MAPSACTIVITY
    public void elegirMapa (View view) {

        String nombre = edtNombre.getText().toString();
        String telefono = edtTelefono.getText().toString();
        String email = edtEmail.getText().toString();

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);

        intent.putExtra("nombre", nombre);
        intent.putExtra("telefono", telefono);
        intent.putExtra("email", email);

        startActivity(intent);

    }

    //inserta en la base de datos los campos y luego limpia los textos
    public void insertaBD(View view) {
        try {
            sqLiteHelper.insertData(

                    edtNombre.getText().toString().trim(),
                    edtTelefono.getText().toString().trim(),
                    edtEmail.getText().toString().trim(),
                    imageViewToByte(imageView),
                    edtLatitud.getText().toString(),
                    edtLongitud.getText().toString()

            );


            Toast.makeText(getApplicationContext(), "¡Añadido exitosamente!", Toast.LENGTH_LONG).show();
            edtNombre.setText("");
            edtTelefono.setText("");
            edtEmail.setText("");
            imageView.setImageResource(R.mipmap.iconavatar);
            edtLatitud.setText("");
            edtLongitud.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "¡ERROR!", Toast.LENGTH_LONG).show();
        }
    }

}

