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
public class EnvioMaritimo extends Envio {

    private String puertoEntrega;
    private String numeroFlota; // Formato: 3 letras, 4 números y 1 letra 

    @Override
    public void calcularPrecioConDescuento() {
        if (this.getCantidad() != null && this.getCantidad() > 10) {
            // Descuento del 3% en logística marítima 
            BigDecimal descuento = this.getPrecioEnvio().multiply(new BigDecimal("0.03"));
            this.setPrecioDescuento(this.getPrecioEnvio().subtract(descuento));
        } else {
            this.setPrecioDescuento(this.getPrecioEnvio());
        }
    }
}