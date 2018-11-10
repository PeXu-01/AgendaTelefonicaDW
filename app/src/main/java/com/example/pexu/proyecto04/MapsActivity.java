package com.example.pexu.proyecto04;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//implementar otra clase para el dragable donde pondrá los métodos para iniciar el arrastre del marcador, el movimiento de arrastre y el final del arrastre
public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerDragListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private Marker markerDrag; //agregar la variable de tipo Marker para un marcador personalizado arrastrable
    private Button btnGuardarGPS, btnTrazar;
    private EditText edtLatitud, edtLongitud, edtNombre, edtTelefono, edtEmail;
    private ImageView imageView;
    SQLiteHelper conn;

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Mapeando campos
        edtLatitud = (EditText) findViewById(R.id.edtLatitud);
        edtLongitud = (EditText) findViewById(R.id.edtLongitud);
        //NUEVO
        edtNombre = (EditText) findViewById(R.id.edtNombre);
        edtTelefono = (EditText) findViewById(R.id.edtTelefono);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        imageView = (ImageView) findViewById(R.id.imageView);
        //Recibiendo de MainActivity
        Bundle parametros = this.getIntent().getExtras();
        if (parametros != null) {
            edtLatitud.setText(getIntent().getExtras().getString("latitud"));
            edtLongitud.setText(getIntent().getExtras().getString("longitud"));
            edtNombre.setText(getIntent().getExtras().getString("nombre"));
            edtTelefono.setText(getIntent().getExtras().getString("telefono"));
            edtEmail.setText(getIntent().getExtras().getString("email"));

            Intent intent = new Intent(MapsActivity.this, MainActivity.class);

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //para ver si los servicios de GooglePlay están disponibles y actualizados
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS) {


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.show();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();


    }

    private void init() {

        Bundle parametros = this.getIntent().getExtras();
        if(parametros != null){
            Toast.makeText(this, getIntent().getExtras().getString("nombre"), Toast.LENGTH_LONG).show();

        }
    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Permiso para obtener mi ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this); //para ubicación personal
        mMap.setOnMyLocationClickListener(this);

        UiSettings uiSettings = mMap.getUiSettings(); //para habilitar los controles de zoom
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in Santo Tomás de Castilla and move the camera
        LatLng santo = new LatLng(15.69, -88.61767);
        mMap.addMarker(new MarkerOptions().position(santo).title("Santo Tomás de Castilla").snippet("Puerto Santo Tomás de Castilla, Puerto Barrios, Izabal").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        //marcador personal arrastrable
        LatLng personal = new LatLng(15.69, -88.61667);
        markerDrag = googleMap.addMarker(new MarkerOptions()
                .position(personal)
                .title("Marcador")
                .draggable(true)
        );
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); //para seleccionar tipo de mapa
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santo, 16)); //camara y zoom por default
        mMap.getUiSettings().setCompassEnabled(true); //para poner botones zoom al mapa

        googleMap.setOnMarkerClickListener(this); //habilitar el evento para escuchar los eventos de los marcadores (senOnMarkerClickListener)
        googleMap.setOnMarkerDragListener(this); //habilitar el método de arrastrar


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(markerDrag)) {
            String lat, lng;
            lat = Double.toString(marker.getPosition().latitude);
            lng = Double.toString(marker.getPosition().longitude);
            Toast.makeText(this, lat + ", " + lng, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Drag Listener, Inicio, moviendose el marcador y Final
    @Override
    public void onMarkerDragStart(Marker marker) { //Marcador cuando el pointer inicia a moverse
        if (marker.equals(markerDrag)) {
            Toast.makeText(this, "Iniciar Coordenadas", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMarkerDrag(Marker marker) { //Marcador cuando el pointer está en movimiento
        if (marker.equals(markerDrag)) {
            String lat, lng;
            lat = Double.toString(marker.getPosition().latitude);
            lng = Double.toString(marker.getPosition().longitude);
            ((EditText) findViewById(R.id.edtLatitud)).setText(lat);
            ((EditText) findViewById(R.id.edtLongitud)).setText(lng);
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) { //marcador al finalizar movimiento del pointer
        if (marker.equals(markerDrag)) {
            Toast.makeText(this, "Finalización", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    //Obtiene de la plantilla y la envia a los EditText de coordenadas (y de nueva a los EditText de nombre, etc. del MainActivity
    public void agregaCoordenada(View view) {

        String latitud = edtLatitud.getText().toString();
        String longitud = edtLongitud.getText().toString();
        String nombre = edtNombre.getText().toString();
        String telefono = edtTelefono.getText().toString();
        String email = edtEmail.getText().toString();

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);

        intent.putExtra("latitud", latitud);
        intent.putExtra("longitud", longitud);
        intent.putExtra("nombre", nombre);
        intent.putExtra("telefono", telefono);
        intent.putExtra("email", email);

        startActivity(intent);

            //Toast para probar si envia
//        Toast.makeText(this, "Latitud " + latitud + " Longitud " + longitud, Toast.LENGTH_LONG).show();


        //probar solución: llevar el objeto a MapsACtivity y traerlo de regreso
        //otra: activar el boton coordenadas hasta que se llene los otros campos


    }

}





