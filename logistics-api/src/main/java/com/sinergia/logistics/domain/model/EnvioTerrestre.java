package com.sinergia.logistics.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnvioTerrestre extends Envio {
    
    private String bodegaEntrega;
    private String placaVehiculo; // Formato: 3 letras y 3 números [cite: 45]

    @Override
    public void calcularPrecioConDescuento() {
        if (this.getCantidad() != null && this.getCantidad() > 10) {
            // Descuento del 5% en logística terrestre 
            BigDecimal descuento = this.getPrecioEnvio().multiply(new BigDecimal("0.05"));
            this.setPrecioDescuento(this.getPrecioEnvio().subtract(descuento));
        } else {
            this.setPrecioDescuento(this.getPrecioEnvio());
        }
    }
}