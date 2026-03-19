package com.sinergia.logistics.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Envio {
    private Long id;
    private Cliente cliente;
    private String tipoProducto; // Ej: Electrónicos, Perecederos
    private Integer cantidad;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaEntrega;
    private BigDecimal precioEnvio;
    private BigDecimal precioDescuento;
    private String numeroGuia; // Único alfanumérico de 10 dígitos

    // Método de negocio puro: Se ejecuta antes de guardar para calcular el total
    public abstract void calcularPrecioConDescuento();
}