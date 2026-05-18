package com.example.FixNow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

// --- IMPORTS DE TU PROYECTO ---
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.incidencia.Incidencia;
import com.example.FixNow.model.FinalizarRequest;
import com.example.FixNow.network.RetrofitClient;

// --- IMPORTS DE MAPBOX V11 ---
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import static com.mapbox.maps.plugin.annotation.AnnotationsUtils.getAnnotations;
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

public class DetalleOrdenActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTitulo, tvDescripcion, tvTipo, tvDireccion, tvCliente;
    private Button btnFinalizar;

    // Vistas de Mapbox
    private MapView mapViewSolucionador;
    private PointAnnotationManager pointAnnotationManager;

    // Datos
    private int idOrden;
    private int idIncidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_orden);

        idOrden = getIntent().getIntExtra("ID_ORDEN", -1);
        idIncidencia = getIntent().getIntExtra("ID_INCIDENCIA", -1);

        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvTipo = findViewById(R.id.tvDetalleTipo);
        tvDireccion = findViewById(R.id.tvDetalleDireccion);
        tvCliente = findViewById(R.id.tvDetalleCliente);
        btnFinalizar = findViewById(R.id.btnFinalizarOrden);

        // Inicializar el mapa
        mapViewSolucionador = findViewById(R.id.mapViewSolucionador);

        btnFinalizar.setOnClickListener(v -> finalizarTrabajo());

        if (idIncidencia != -1) {
            cargarDetalles();
        } else {
            Toast.makeText(this, "Error: ID de incidencia no válido", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void cargarDetalles() {
        RetrofitClient.getApiService().obtenerIncidencia(idIncidencia).enqueue(new Callback<ApiResponse<Incidencia>>() {
            @Override
            public void onResponse(Call<ApiResponse<Incidencia>> call, Response<ApiResponse<Incidencia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Incidencia inc = response.body().getData();
                    if (inc != null) {
                        // Llenamos los textos
                        tvTitulo.setText(inc.getTitulo());
                        tvDescripcion.setText(inc.getDescripcion());
                        tvTipo.setText(inc.getTipo());

                        String direccionDeLaApp = inc.getDireccion();
                        tvDireccion.setText("Dirección: " + direccionDeLaApp);

                        // --- NUEVO: LLAMAR AL MAPA ---
                        if (direccionDeLaApp != null && !direccionDeLaApp.isEmpty()) {
                            mostrarUbicacionEnMapa(direccionDeLaApp);
                        }

                        String nombreC = (inc.getNombreCliente() != null) ? inc.getNombreCliente() : "Cliente";
                        tvCliente.setText("Abierta por: " + nombreC);

                        String estado = inc.getStatus();

                        if ("Finalizada".equalsIgnoreCase(estado) || "Resuelta".equalsIgnoreCase(estado)) {
                            btnFinalizar.setVisibility(View.GONE);
                        } else {
                            btnFinalizar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Incidencia>> call, Throwable t) {
                Toast.makeText(DetalleOrdenActivity.this, "Error al cargar detalles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- MAGIA DE MAPBOX PARA EL SOLUCIONADOR ---
    private void mostrarUbicacionEnMapa(String direccionTexto) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Buscamos las coordenadas a partir del texto de la dirección (Traducción Inversa)
            List<Address> listaDirecciones = geocoder.getFromLocationName(direccionTexto, 1);

            if (listaDirecciones != null && !listaDirecciones.isEmpty()) {
                Address ubicacion = listaDirecciones.get(0);
                Point puntoIncidencia = Point.fromLngLat(ubicacion.getLongitude(), ubicacion.getLatitude());

                // Inicializar Mapbox
                mapViewSolucionador.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> {

                    // Desactivar gestos para que sea un mapa estático de solo lectura
                    GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapViewSolucionador);
                    gesturesPlugin.setScrollEnabled(false);
                    gesturesPlugin.setPinchToZoomEnabled(false);
                    gesturesPlugin.setRotateEnabled(false);

                    // Inicializar el gestor de marcadores (Con la lógica de v11 que ya corregimos)
                    AnnotationPlugin annotationApi = getAnnotations(mapViewSolucionador);
                    pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());

                    // Mover la cámara hacia la incidencia
                    CameraOptions cameraOptions = new CameraOptions.Builder()
                            .center(puntoIncidencia)
                            .zoom(15.0)
                            .build();
                    mapViewSolucionador.getMapboxMap().setCamera(cameraOptions);

                    // Pintar el marcador en el mapa
                    Bitmap iconBitmap = vectorToBitmap(android.R.drawable.ic_menu_mylocation);
                    if (style != null && iconBitmap != null) {
                        style.addImage("pin_incidencia", iconBitmap);
                    }

                    PointAnnotationOptions options = new PointAnnotationOptions()
                            .withPoint(puntoIncidencia)
                            .withIconImage("pin_incidencia");
                    pointAnnotationManager.create(options);
                });
            } else {
                Toast.makeText(this, "No se pudo mapear la dirección exacta", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("MAPA_SOLUCIONADOR", "Error al convertir dirección a coordenadas", e);
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

    // --- LÓGICA ORIGINAL DE FINALIZAR ---
    private void finalizarTrabajo() {
        if (idOrden == -1 || idIncidencia == -1) {
            Toast.makeText(this, "Error en los datos de la orden", Toast.LENGTH_SHORT).show();
            return;
        }

        btnFinalizar.setEnabled(false);
        btnFinalizar.setText("Finalizando...");

        FinalizarRequest req = new FinalizarRequest(idOrden, idIncidencia);

        RetrofitClient.getApiService().finalizarTrabajo(req).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(DetalleOrdenActivity.this, "Orden finalizada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        restaurarBoton("Error: " + response.body().getMessage());
                    }
                } else {
                    restaurarBoton("Error del servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                restaurarBoton("Error de conexión");
            }
        });
    }

    private void restaurarBoton(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        btnFinalizar.setEnabled(true);
        btnFinalizar.setText("Finalizar Orden");
    }
}