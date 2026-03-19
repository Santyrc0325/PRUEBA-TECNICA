package com.sinergia.logistics.infrastructure.adapter.in.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnvioResponseDTO {
    private Long id;
    private Long clienteId; // Solo devolvemos el ID, no todo el objeto Cliente
    private String tipoProducto;
    private Integer cantidad;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaEntrega;
    private BigDecimal precioEnvio;
    private BigDecimal precioDescuento;
    private String numeroGuia;
    
    // Campos específicos que pueden ser nulos si es marítimo
    private String bodegaEntrega;
    private String placaVehiculo;
}