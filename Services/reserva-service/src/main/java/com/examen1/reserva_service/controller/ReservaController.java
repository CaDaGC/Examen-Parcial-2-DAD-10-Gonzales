package com.examen1.reserva_service.controller;

import com.examen1.reserva_service.dto.ReservaCreateRequest;
import com.examen1.reserva_service.dto.ReservaResponse;
import com.examen1.reserva_service.dto.ReservaUpdateRequest;
import com.examen1.reserva_service.service.service.ReservaService;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservas")
@Slf4j
public class ReservaController {

    private final ReservaService service;
    private final DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> findAll() {
        logRequest("GET /api/v1/reservas");
        return ResponseEntity.ok()
                .header("X-Service-Port", serverPort)
                .body(service.listarTodasLasReservas());
    }

    @GetMapping("/dni/{dniCliente}")
    public ResponseEntity<List<ReservaResponse>> findByDniCliente(@PathVariable String dniCliente) {
        logRequest("GET /api/v1/reservas/dni/" + dniCliente);
        return ResponseEntity.ok()
                .header("X-Service-Port", serverPort)
                .body(service.buscarPorDniCliente(dniCliente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> findById(@PathVariable Long id) {
        logRequest("GET /api/v1/reservas/" + id);
        return ResponseEntity.ok()
                .header("X-Service-Port", serverPort)
                .body(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ReservaCreateRequest request) {
        logRequest("POST /api/v1/reservas");
        ReservaResponse response = service.crearReserva(request);

        return ResponseEntity.created(
                URI.create("/api/v1/reservas/" + response.getId())
        ).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ReservaUpdateRequest request) {
        logRequest("PUT /api/v1/reservas/" + id);
        return ResponseEntity.ok()
                .header("X-Service-Port", serverPort)
                .body(service.actualizarReserva(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelByCliente(@PathVariable Long id,
                                                           @RequestParam String dniCliente) {
        logRequest("PATCH /api/v1/reservas/" + id + "/cancelar");
        return ResponseEntity.ok()
                .header("X-Service-Port", serverPort)
                .body(service.cancelarReservaPorCliente(id, dniCliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logRequest("DELETE /api/v1/reservas/" + id);
        service.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instance")
    public ResponseEntity<Map<String, Object>> instance() {
        logRequest("GET /api/v1/reservas/instance");
        List<ServiceInstance> instances = discoveryClient.getInstances(applicationName);

        return ResponseEntity.ok()
                .header("X-Service-Port", serverPort)
                .body(Map.of(
                        "service", applicationName,
                        "port", serverPort,
                        "registeredInstances", instances.size()
                ));
    }

    private void logRequest(String endpoint) {
        log.info("ms-reserva instance on port {} handled {}", serverPort, endpoint);
    }
}
