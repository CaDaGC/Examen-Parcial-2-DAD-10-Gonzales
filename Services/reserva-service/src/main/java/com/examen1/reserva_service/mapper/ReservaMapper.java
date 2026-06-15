package com.examen1.reserva_service.mapper;

import com.examen1.reserva_service.dto.ReservaCreateRequest;
import com.examen1.reserva_service.dto.ReservaResponse;
import com.examen1.reserva_service.dto.ReservaUpdateRequest;
import com.examen1.reserva_service.model.Reserva;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservaMapper {

    public Reserva toEntity(ReservaCreateRequest request) {
        if (request == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setDniCliente(request.getDniCliente());
        reserva.setOrigen(request.getOrigen());
        reserva.setDestino(request.getDestino());
        reserva.setFechaViaje(request.getFechaViaje());
        reserva.setAsiento(request.getAsiento());
        reserva.setPrecio(request.getPrecio());

        return reserva;
    }

    public ReservaResponse toResponse(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        ReservaResponse response = new ReservaResponse();
        response.setId(reserva.getId());
        response.setDniCliente(reserva.getDniCliente());
        response.setOrigen(reserva.getOrigen());
        response.setDestino(reserva.getDestino());
        response.setFechaViaje(reserva.getFechaViaje());
        response.setFechaReserva(reserva.getFechaReserva());
        response.setAsiento(reserva.getAsiento());
        response.setPrecio(reserva.getPrecio());
        response.setEstado(reserva.getEstado());

        return response;
    }


    public List<ReservaResponse> toResponseList(List<Reserva> reservas) {
        if (reservas == null) {
            return null;
        }

        List<ReservaResponse> list = new ArrayList<>();
        for (Reserva reserva : reservas) {
            list.add(toResponse(reserva));
        }
        return list;
    }


    public void updateEntityFromDto(ReservaUpdateRequest request, Reserva reserva) {
        if (request == null || reserva == null) {
            return;
        }

        reserva.setDniCliente(request.getDniCliente());
        reserva.setOrigen(request.getOrigen());
        reserva.setDestino(request.getDestino());
        reserva.setFechaViaje(request.getFechaViaje());
        reserva.setAsiento(request.getAsiento());
        reserva.setPrecio(request.getPrecio());
        reserva.setEstado(request.getEstado());
    }
}