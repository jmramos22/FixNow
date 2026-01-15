package com.example.FixNow.network;
;
import com.example.FixNow.model.OrdenTrabajo;
import com.example.FixNow.model.api.ApiResponse;
import com.example.FixNow.model.cliente.Cliente;
import com.example.FixNow.model.incidencia.Incidencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // --- CLIENTES ---

    // URL final: http://10.0.2.2/fixnow/public/index.php?action=registrar_cliente
    @POST("index.php?action=registrar_cliente")
    Call<ApiResponse<Void>> registrarCliente(@Body Cliente cliente);

    @POST("index.php?action=login_cliente")
    Call<ApiResponse<Cliente>> loginCliente(@Body Cliente cliente);


    // --- INCIDENCIAS ---

    @POST("index.php?action=crear_incidencia")
    Call<ApiResponse<Void>> crearIncidencia(@Body Incidencia incidencia);

    @GET("index.php?action=listar_incidencias")
    Call<ApiResponse<List<Incidencia>>> listarIncidencias();

    @GET("index.php?action=obtener_incidencia")
    Call<ApiResponse<Incidencia>> obtenerIncidencia(@Query("id") int id);

    // LISTAR OFERTAS DE UNA INCIDENCIA
    @GET("index.php?action=listar_ofertas")
    Call<ApiResponse<List<com.example.FixNow.model.oferta.Oferta>>> listarOfertas(@Query("id_incidencia") int idIncidencia);

    @POST("index.php?action=registrar_solucionador")
    Call<ApiResponse<Void>> registrarSolucionador(@Body com.example.FixNow.model.solucionador.Solucionador solucionador);

    @POST("index.php?action=login_solucionador")
    Call<ApiResponse<com.example.FixNow.model.solucionador.Solucionador>> loginSolucionador(@Body com.example.FixNow.model.solucionador.Solucionador solucionador);
    @POST("index.php?action=crear_oferta")
    Call<ApiResponse<Void>> crearOferta(@Body com.example.FixNow.model.oferta.Oferta oferta);
    @POST("index.php?action=crear_orden")
    Call<ApiResponse<Void>> crearOrdenTrabajo(@Body OrdenTrabajo orden);

    @POST("index.php?action=eliminar_oferta")
    Call<ApiResponse<Void>> eliminarOferta(@Body com.example.FixNow.model.oferta.Oferta ofertaConId);

    @POST("index.php?action=actualizar_incidencia")
    Call<ApiResponse<Void>> actualizarEstadoIncidencia(@Body com.example.FixNow.model.incidencia.Incidencia incidenciaConEstado);
    // OBTENER SOLUCIONADOR ASIGNADO (Para mostrar quién viene en camino)
    @GET("index.php?action=obtener_asignado")
    Call<ApiResponse<com.example.FixNow.model.solucionador.Solucionador>> obtenerSolucionadorAsignado(@Query("id_incidencia") int idIncidencia);
    // CALIFICAR SERVICIO
    @POST("index.php?action=calificar_orden")
    Call<ApiResponse<Void>> calificarOrden(@Body com.example.FixNow.model.OrdenTrabajo datos);

    // 1. LISTAR TRABAJOS DEL SOLUCIONADOR
    @GET("index.php?action=listar_mis_ordenes")
    Call<ApiResponse<java.util.List<com.example.FixNow.model.OrdenSolucionador>>> listarMisOrdenes(@Query("id_solucionador") int idSolucionador);

    // 2. FINALIZAR TRABAJO
    @POST("index.php?action=finalizar_trabajo")
    Call<ApiResponse<Void>> finalizarTrabajo(@Body com.example.FixNow.model.FinalizarRequest request);

}