package com.example.FixNow;

import static com.mapbox.maps.plugin.annotation.AnnotationsUtils.getAnnotations;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.network.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// Importaciones de Mapbox
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevaIncidenciaActivity extends AppCompatActivity {

    private Button btnRegistroIncidencia, btnMiUbicacion;
    private EditText etTitulo, etDescripcion, etDireccion;
    private RadioGroup rgTipo;

    // Variables de Mapbox y ubicación
    private MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_incidencia);

        initView();

        // Inicializar cliente de ubicación de Google
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configuración inicial de Mapbox
        // Configuración inicial de Mapbox
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {

            // 1. Inicializamos usando un método estático (se pondrá en rojo temporalmente)
            AnnotationPlugin annotationApi = getAnnotations(mapView);

            // 2. CORRECCIÓN V11: Pasamos un 'new AnnotationConfig()' en lugar de 'mapView'
            pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());

            configurarEventosMapa();
        });

        btnRegistroIncidencia.setOnClickListener(v -> guardarDatos());
        btnMiUbicacion.setOnClickListener(v -> obtenerUbicacionGPS());
    }

    private void initView(){
        btnRegistroIncidencia = findViewById(R.id.btnRegistro_Incidencia);
        btnMiUbicacion = findViewById(R.id.btnMiUbicacion);
        etTitulo = findViewById(R.id.edt_Titulo_Incidencia);
        etDescripcion = findViewById(R.id.edt_Descripcion_Incidencia);
        etDireccion = findViewById(R.id.edt_Direccion_Incidencia);
        rgTipo = findViewById(R.id.radioGroupLocation);
        mapView = findViewById(R.id.mapView);
    }

    private void configurarEventosMapa() {
        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
        gesturesPlugin.addOnMapClickListener(point -> {
            actualizarMarcador(point);
            obtenerDireccionDesdeCoordenadas(point);
            return true; // Mapbox requiere retornar un booleano aquí
        });
    }

    // --- OBTENER UBICACIÓN GPS ACTUAL ---
    private void obtenerUbicacionGPS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                Point miUbicacion = Point.fromLngLat(location.getLongitude(), location.getLatitude());

                CameraOptions cameraOptions = new CameraOptions.Builder()
                        .center(miUbicacion)
                        .zoom(16.0)
                        .build();
                mapView.getMapboxMap().setCamera(cameraOptions);

                actualizarMarcador(miUbicacion);
                obtenerDireccionDesdeCoordenadas(miUbicacion);
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación. Activa tu GPS.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- PINTAR MARCADOR EN EL MAPA ---
    private void actualizarMarcador(Point point) {
        if (pointAnnotationManager != null) {
            pointAnnotationManager.deleteAll(); // Borrar marcador anterior

            Bitmap iconBitmap = vectorToBitmap(android.R.drawable.ic_menu_mylocation);

            // CORRECCIÓN: Mapbox requiere añadir el Bitmap al estilo primero
            Style style = mapView.getMapboxMap().getStyle();
            if (style != null && iconBitmap != null) {
                style.addImage("marcador_icono", iconBitmap);
            }

            // Luego usamos el ID ("marcador_icono") como String
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage("marcador_icono");

            pointAnnotationManager.create(pointAnnotationOptions);
        }
    }

    private void obtenerDireccionDesdeCoordenadas(Point point) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(point.latitude(), point.longitude(), 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccionObj = direcciones.get(0);
                String direccionTexto = direccionObj.getAddressLine(0);
                etDireccion.setText(direccionTexto);
            }
        } catch (IOException e) {
            Log.e("MAPA", "Error al obtener dirección", e);
            Toast.makeText(this, "Error al traducir coordenadas", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap vectorToBitmap(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionGPS();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarDatos() {
        SharedPreferences sp = getSharedPreferences("sesion", MODE_PRIVATE);
        String nombreCliente = sp.getString("nombreCliente", "Cliente");
        int idCliente = sp.getInt("idCliente", 0);

        if (idCliente == 0) {
            Toast.makeText(this, "Error de sesión. Vuelve a loguearte.", Toast.LENGTH_LONG).show();
            return;
        }

        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        String tipoSeleccionado = "";
        int selectedId = rgTipo.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton rb = findViewById(selectedId);
            tipoSeleccionado = rb.getText().toString();
        } else {
            Toast.makeText(this, "Selecciona el tipo de incidencia", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Llena todos los campos y selecciona ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistroIncidencia.setEnabled(false);
        btnRegistroIncidencia.setText("Guardando...");

        Incidencia nuevaIncidencia = new Incidencia();
        nuevaIncidencia.setTitulo(titulo);
        nuevaIncidencia.setDescripcion(descripcion);
        nuevaIncidencia.setTipo(tipoSeleccionado);
        nuevaIncidencia.setDireccion(direccion);
        nuevaIncidencia.setIdCliente(idCliente);
        nuevaIncidencia.setNombreCliente(nombreCliente);

        RetrofitClient.getApiService().crearIncidencia(nuevaIncidencia).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                btnRegistroIncidencia.setEnabled(true);
                btnRegistroIncidencia.setText("Registrar Incidencia");

                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(NuevaIncidenciaActivity.this, "¡Incidencia creada correctamente!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NuevaIncidenciaActivity.this, "Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NuevaIncidenciaActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                btnRegistroIncidencia.setEnabled(true);
                btnRegistroIncidencia.setText("Registrar Incidencia");
                Log.e("API_INCIDENCIA", t.getMessage());
                Toast.makeText(NuevaIncidenciaActivity.this, "Fallo de conexión. Verifica tu internet o XAMPP.", Toast.LENGTH_LONG).show();
            }
        });
    }
}