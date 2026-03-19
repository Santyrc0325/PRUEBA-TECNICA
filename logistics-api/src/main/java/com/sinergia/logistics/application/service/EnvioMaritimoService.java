package com.sinergia.logistics.application.service;

import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioMaritimoRequestDTO;
import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioResponseDTO;
import com.sinergia.logistics.domain.model.EnvioMaritimo;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.EnvioMaritimoEntity;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.repository.ClienteRepository;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.repository.EnvioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvioMaritimoService {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public EnvioResponseDTO crearEnvioMaritimo(EnvioMaritimoRequestDTO dto) {
        
        // Buscar al cliente
        ClienteEntity clienteEncontrado = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Error: El cliente con ID " + dto.getClienteId() + " no está registrado."));

        // Verificar que el numero de guia sea unico
        if (envioRepository.existsByNumeroGuia(dto.getNumeroGuia())) {
            throw new IllegalArgumentException("El número de guía ya está registrado.");
        }

        // Logica de Negocio (Calcula el 3% si la cantidad > 10)
        EnvioMaritimo modelo = new EnvioMaritimo();
        modelo.setCantidad(dto.getCantidad());
        modelo.setPrecioEnvio(dto.getPrecioEnvio());
        modelo.calcularPrecioConDescuento(); 

        // 4. Mapeo a la Entidad Maritima
        EnvioMaritimoEntity entity = new EnvioMaritimoEntity();
        entity.setCliente(clienteEncontrado); 
        entity.setTipoProducto(dto.getTipoProducto());
        entity.setCantidad(dto.getCantidad());
        entity.setFechaRegistro(LocalDateTime.now());
        entity.setFechaEntrega(dto.getFechaEntrega());
        entity.setPrecioEnvio(dto.getPrecioEnvio());
        entity.setPrecioDescuento(modelo.getPrecioDescuento()); 
        entity.setNumeroGuia(dto.getNumeroGuia());
        
        // Campos unicos en maritimos
        entity.setPuertoEntrega(dto.getPuertoEntrega());
        entity.setNumeroFlota(dto.getNumeroFlota());

        // Guardar en la BD
        EnvioMaritimoEntity entidadGuardada = envioRepository.save(entity);

        // Mapear al ResponseDTO
        EnvioResponseDTO response = new EnvioResponseDTO();
        response.setId(entidadGuardada.getId());
        response.setClienteId(entidadGuardada.getCliente().getId());
        response.setTipoProducto(entidadGuardada.getTipoProducto());
        response.setCantidad(entidadGuardada.getCantidad());
        response.setFechaRegistro(entidadGuardada.getFechaRegistro());
        response.setFechaEntrega(entidadGuardada.getFechaEntrega());
        response.setPrecioEnvio(entidadGuardada.getPrecioEnvio());
        response.setPrecioDescuento(entidadGuardada.getPrecioDescuento());
        response.setNumeroGuia(entidadGuardada.getNumeroGuia());
        
        // Mapeamos los campos maritimos
        response.setBodegaEntrega(entidadGuardada.getPuertoEntrega());
        response.setPlacaVehiculo(entidadGuardada.getNumeroFlota());

        return response;
    }
    

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarTodos() {
        return envioRepository.findAll().stream()
                .filter(envio -> envio instanceof EnvioMaritimoEntity)
                .map(envio -> mapearAResponse((EnvioMaritimoEntity) envio))
                .toList();
    }
    
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerPorId(Long id) {
        EnvioMaritimoEntity entidad = (EnvioMaritimoEntity) envioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El envío marítimo con ID " + id + " no existe."));
        return mapearAResponse(entidad);
    }

    @Transactional
    public EnvioResponseDTO actualizarEnvio(Long id, EnvioMaritimoRequestDTO dto) {
        
        EnvioMaritimoEntity entidadExistente = (EnvioMaritimoEntity) envioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El envío marítimo con ID " + id + " no existe."));

        if (!entidadExistente.getNumeroGuia().equals(dto.getNumeroGuia()) && 
            envioRepository.existsByNumeroGuia(dto.getNumeroGuia())) {
            throw new IllegalArgumentException("El nuevo número de guía ya está registrado.");
        }

        if (!entidadExistente.getCliente().getId().equals(dto.getClienteId())) {
            ClienteEntity nuevoCliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("El nuevo cliente no existe."));
            entidadExistente.setCliente(nuevoCliente);
        }
        

        EnvioMaritimo modelo = new EnvioMaritimo(); 
        modelo.setCantidad(dto.getCantidad());
        modelo.setPrecioEnvio(dto.getPrecioEnvio());
        modelo.calcularPrecioConDescuento(); 

        entidadExistente.setTipoProducto(dto.getTipoProducto());
        entidadExistente.setCantidad(dto.getCantidad());
        entidadExistente.setFechaEntrega(dto.getFechaEntrega());
        entidadExistente.setPrecioEnvio(dto.getPrecioEnvio());
        entidadExistente.setPrecioDescuento(modelo.getPrecioDescuento());
        entidadExistente.setNumeroGuia(dto.getNumeroGuia());
        entidadExistente.setPuertoEntrega(dto.getPuertoEntrega());
        entidadExistente.setNumeroFlota(dto.getNumeroFlota());

        EnvioMaritimoEntity guardado = envioRepository.saveAndFlush(entidadExistente);
        
        return mapearAResponse(guardado);
    }

    @Transactional
    public void eliminarEnvio(Long id) {
        if (!envioRepository.existsById(id)) {
            throw new EntityNotFoundException("El envío con ID " + id + " no existe.");
        }
        envioRepository.deleteById(id);
    }

    private EnvioResponseDTO mapearAResponse(EnvioMaritimoEntity entidadGuardada) {
        EnvioResponseDTO response = new EnvioResponseDTO();
        response.setId(entidadGuardada.getId());
        response.setClienteId(entidadGuardada.getCliente().getId());
        response.setTipoProducto(entidadGuardada.getTipoProducto());
        response.setCantidad(entidadGuardada.getCantidad());
        response.setFechaRegistro(entidadGuardada.getFechaRegistro());
        response.setFechaEntrega(entidadGuardada.getFechaEntrega());
        response.setPrecioEnvio(entidadGuardada.getPrecioEnvio());
        response.setPrecioDescuento(entidadGuardada.getPrecioDescuento());
        response.setNumeroGuia(entidadGuardada.getNumeroGuia());
        response.setBodegaEntrega(entidadGuardada.getPuertoEntrega()); 
        response.setPlacaVehiculo(entidadGuardada.getNumeroFlota());
        return response;
    }
}