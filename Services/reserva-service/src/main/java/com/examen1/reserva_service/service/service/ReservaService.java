package com.examen1.reserva_service.service.service;

import com.examen1.reserva_service.dto.ReservaCreateRequest;
import com.examen1.reserva_service.dto.ReservaResponse;
import com.examen1.reserva_service.dto.ReservaUpdateRequest;

import java.util.List;

public interface ReservaService {

    ReservaResponse crearReserva(ReservaCreateRequest request);
    List<ReservaResponse> buscarPorDniCliente(String dniCliente);
    ReservaResponse cancelarReservaPorCliente(Long id, String dniCliente);

    List<ReservaResponse> listarTodasLasReservas();
    ReservaResponse obtenerPorId(Long id);
    ReservaResponse actualizarReserva(Long id, ReservaUpdateRequest request);
    void eliminarReserva(Long id);
}
