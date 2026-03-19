package com.sinergia.logistics.infrastructure.adapter.out.persistence.repository;

import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.EnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioRepository extends JpaRepository<EnvioEntity, Long> {
    
    // Spring Data JPA crea la consulta SQL automáticamente solo con leer el nombre del método
    boolean existsByNumeroGuia(String numeroGuia);
}