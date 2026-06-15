package com.examen1.reserva_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponse {
    private Long id;
    private String dniCliente;
    private String origen;
    private String destino;
    private LocalDateTime fechaViaje;
    private LocalDateTime fechaReserva;
    private String asiento;
    private Double precio;
    private String estado;
}
