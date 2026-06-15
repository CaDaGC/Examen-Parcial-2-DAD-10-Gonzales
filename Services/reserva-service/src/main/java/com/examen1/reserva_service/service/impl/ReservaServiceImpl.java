package com.examen1.reserva_service.service.impl;

import com.examen1.reserva_service.dto.ReservaCreateRequest;
import com.examen1.reserva_service.dto.ReservaResponse;
import com.examen1.reserva_service.dto.ReservaUpdateRequest;
import com.examen1.reserva_service.mapper.ReservaMapper;
import com.examen1.reserva_service.model.Reserva;
import com.examen1.reserva_service.repository.ReservaRepository;
import com.examen1.reserva_service.service.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;

    @Override
    @Transactional
    public ReservaResponse crearReserva(ReservaCreateRequest request) {
        Reserva nuevaReserva = reservaMapper.toEntity(request);
        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);
        return reservaMapper.toResponse(reservaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> buscarPorDniCliente(String dniCliente) {
        List<Reserva> reservas = reservaRepository.findByDniCliente(dniCliente);
        return reservaMapper.toResponseList(reservas);
    }

    @Override
    @Transactional
    public ReservaResponse cancelarReservaPorCliente(Long id, String dniCliente) {
        // Buscamos la reserva
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La reserva con ID " + id + " no existe."));

        // Validación crítica de seguridad: El DNI del cliente debe coincidir con el DNI del dueño de la reserva
        if (!reserva.getDniCliente().equals(dniCliente)) {
            throw new RuntimeException("No tienes permisos para cancelar esta reserva.");
        }

        // Cambiamos el estado a CANCELADA en lugar de borrarla físicamente
        reserva.setEstado("CANCELADA");
        Reserva reservaActualizada = reservaRepository.save(reserva);

        return reservaMapper.toResponse(reservaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarTodasLasReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservaMapper.toResponseList(reservas);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con el ID: " + id));
        return reservaMapper.toResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaResponse actualizarReserva(Long id, ReservaUpdateRequest request) {
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con el ID: " + id));

        // Usamos nuestro método del mapper manual para volcar los cambios del DTO a la entidad
        reservaMapper.updateEntityFromDto(request, reservaExistente);

        Reserva reservaActualizada = reservaRepository.save(reservaExistente);
        return reservaMapper.toResponse(reservaActualizada);
    }

    @Override
    @Transactional
    public void eliminarReserva(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Reserva no encontrada con el ID: " + id);
        }
        reservaRepository.deleteById(id);
    }
}
