package com.sinergia.logistics.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("TERRESTRE")
@Getter
@Setter
public class EnvioTerrestreEntity extends EnvioEntity {

    @Column(name = "bodega_entrega")
    private String bodegaEntrega;

    @Column(name = "placa_vehiculo", length = 6)
    private String placaVehiculo;
}