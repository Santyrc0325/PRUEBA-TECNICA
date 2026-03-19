package com.sinergia.logistics.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("MARITIMO")
@Getter
@Setter
public class EnvioMaritimoEntity extends EnvioEntity {

    @Column(name = "puerto_entrega")
    private String puertoEntrega;

    @Column(name = "numero_flota", length = 8)
    private String numeroFlota;
}