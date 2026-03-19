package com.sinergia.logistics.application.service;

import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioResponseDTO;
import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioTerrestreRequestDTO;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.EnvioTerrestreEntity;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.repository.ClienteRepository;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnvioTerrestreServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private EnvioTerrestreService envioTerrestreService;

    @Test
    void debeAplicarDescuentoDel5PorCientoSiCantidadEsMayorA10() {
        // ARRANGE (Preparar los datos de prueba)
        Long clienteId = 1L;
        EnvioTerrestreRequestDTO requestDTO = new EnvioTerrestreRequestDTO();
        requestDTO.setClienteId(clienteId);
        requestDTO.setCantidad(15); // Mas de 10 para el descuento
        requestDTO.setPrecioEnvio(new BigDecimal("100.00"));
        requestDTO.setNumeroGuia("ABC1234567");
        requestDTO.setTipoProducto("Electrónica");
        requestDTO.setFechaEntrega(LocalDateTime.now().plusDays(5));
        requestDTO.setBodegaEntrega("Bodega Central");
        requestDTO.setPlacaVehiculo("ABC123");

        ClienteEntity clienteMock = new ClienteEntity();
        clienteMock.setId(clienteId);

        // Simulamos lo que hace la base de datos (Mockito)
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(envioRepository.existsByNumeroGuia(requestDTO.getNumeroGuia())).thenReturn(false);
        
        // Cuando el servicio intente guardar, le devolvemos una entidad simulada
        when(envioRepository.save(any(EnvioTerrestreEntity.class))).thenAnswer(invocation -> {
            EnvioTerrestreEntity entidadGuardada = invocation.getArgument(0);
            entidadGuardada.setId(100L); // Simulamos que la BD le asignó un ID
            return entidadGuardada;
        });

        // Ejecutar el método
        EnvioResponseDTO response = envioTerrestreService.crearEnvioTerrestre(requestDTO);

        // Verificar que el resultado este bien
        assertNotNull(response);
        assertEquals(0, new BigDecimal("95.00").compareTo(response.getPrecioDescuento()));
        
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(envioRepository, times(1)).save(any(EnvioTerrestreEntity.class));
    }
}