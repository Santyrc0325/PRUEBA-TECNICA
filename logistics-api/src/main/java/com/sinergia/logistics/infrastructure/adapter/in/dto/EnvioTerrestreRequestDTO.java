package com.sinergia.logistics.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnvioTerrestreRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotBlank(message = "El tipo de producto es obligatorio")
    private String tipoProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "La fecha de entrega es obligatoria")
    @FutureOrPresent(message = "La fecha de entrega no puede ser en el pasado")
    private LocalDateTime fechaEntrega;

    @NotNull(message = "El precio de envío es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioEnvio;

    @NotBlank(message = "El número de guía es obligatorio")
    @Size(min = 10, max = 10, message = "El número de guía debe tener exactamente 10 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]{10}$", message = "El número de guía debe ser alfanumérico")
    private String numeroGuia;

    @NotBlank(message = "La bodega de entrega es obligatoria")
    private String bodegaEntrega;

    @NotBlank(message = "La placa del vehículo es obligatoria")
    @Pattern(regexp = "^[A-Za-z]{3}[0-9]{3}$", message = "La placa debe tener 3 letras y 3 números (Ej: AAA111)")
    private String placaVehiculo;
}