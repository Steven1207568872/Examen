package com.example.examen;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import WebServices.Asynchtask;
import WebServices.WebService;

public class Detalle extends FragmentActivity implements OnMapReadyCallback, Asynchtask {

    private GoogleMap mMap;
    private JSONObject userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);

        // Recibir y parsear los datos del usuario
        try {
            String userDataString = getIntent().getStringExtra("userData");
            if (userDataString != null) {
                userData = new JSONObject(userDataString);
            } else {
                Toast.makeText(this, "No se recibieron datos del usuario", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar datos del usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar datos en las vistas
        loadUserData();

        // Cargar la bandera del país
        fetchCountryFlag();

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void loadUserData() {
        ImageView imgProfile = findViewById(R.id.imgDetailProfile);
        TextView txtName = findViewById(R.id.txtDetailName);
        TextView txtInfo = findViewById(R.id.txtDetailInfo);

        try {
            JSONObject name = userData.getJSONObject("name");
            String fullName = name.getString("title") + ". " + name.getString("first") + " " + name.getString("last");
            txtName.setText(fullName);

            String imageUrl = userData.getJSONObject("picture").getString("large");
            Glide.with(this).load(imageUrl).circleCrop().into(imgProfile);

            String email = userData.getString("email");
            String phone = userData.getString("phone");
            JSONObject location = userData.getJSONObject("location");
            String address = location.getJSONObject("street").getInt("number") + " " +
                    location.getJSONObject("street").getString("name") + ", " +
                    location.getString("city") + ", " +
                    location.getString("country");

            String fullInfo = "Email: " + email + "\n\nTeléfono: " + phone + "\n\nDirección: " + address;
            txtInfo.setText(fullInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchCountryFlag() {
        try {
            String countryName = userData.getJSONObject("location").getString("country");
            String url = "https://restcountries.com/v3.1/name/" + countryName;

            WebService ws = new WebService(url, new HashMap<String, String>(), this, this);
            ws.execute("GET");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // --- MODIFICACIÓN AÑADIDA AQUÍ ---
        // Habilitar controles y gestos para mejorar la interacción
        mMap.getUiSettings().setZoomControlsEnabled(true); // Muestra los botones de +/- para el zoom
        mMap.getUiSettings().setCompassEnabled(true);      // Muestra la brújula cuando el mapa se rota
        mMap.getUiSettings().setZoomGesturesEnabled(true); // Permite hacer zoom con los dedos
        mMap.getUiSettings().setScrollGesturesEnabled(true);// Permite mover el mapa
        mMap.getUiSettings().setRotateGesturesEnabled(true);// Permite rotar el mapa con dos dedos
        mMap.getUiSettings().setMapToolbarEnabled(true);   // Muestra botones para abrir en Google Maps o ver rutas

        try {
            // Obtener coordenadas del JSON
            JSONObject coordinates = userData.getJSONObject("location").getJSONObject("coordinates");
            double lat = coordinates.getDouble("latitude");
            double lng = coordinates.getDouble("longitude");
            String city = userData.getJSONObject("location").getString("city");

            // Crear LatLng y mover la cámara
            LatLng userLocation = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(userLocation).title(city));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15)); // Zoom urbano

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String result) throws JSONException {
        ImageView imgFlag = findViewById(R.id.imgFlag);

        try {
            JSONArray jsonResponse = new JSONArray(result);
            JSONObject countryData = jsonResponse.getJSONObject(0);
            String flagUrl = countryData.getJSONObject("flags").getString("png");
            Glide.with(this).load(flagUrl).into(imgFlag);

        } catch (JSONException e) {
            Log.e("DetailActivity", "Error al procesar la respuesta de la bandera", e);
            Toast.makeText(this, "No se pudo cargar la bandera", Toast.LENGTH_SHORT).show();
        }
    }
}