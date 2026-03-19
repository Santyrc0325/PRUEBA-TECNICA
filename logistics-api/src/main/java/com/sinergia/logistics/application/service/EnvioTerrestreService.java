package com.sinergia.logistics.application.service;

import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioResponseDTO;
import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioTerrestreRequestDTO;
import com.sinergia.logistics.domain.model.EnvioTerrestre;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.EnvioTerrestreEntity;
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
public class EnvioTerrestreService {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public EnvioResponseDTO crearEnvioTerrestre(EnvioTerrestreRequestDTO dto) {
        
        // Buscar al cliente en la base de datos
        // Si el cliente no existe, el proceso se detiene y lanza un error.
        ClienteEntity clienteEncontrado = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Error: El cliente con ID " + dto.getClienteId() + " no está registrado."));

        // Verificar que el numero de guia sea unico
        if (envioRepository.existsByNumeroGuia(dto.getNumeroGuia())) {
            throw new IllegalArgumentException("El número de guía ya está registrado.");
        }

        // Mapeo Manual y Logica de Negocio
        EnvioTerrestre modelo = new EnvioTerrestre();
        modelo.setCantidad(dto.getCantidad());
        modelo.setPrecioEnvio(dto.getPrecioEnvio());
        modelo.calcularPrecioConDescuento(); // Calcula el 5% si la cantidad > 10

        // Mapeo hacia la Entidad que se va a guardar en la BD
        EnvioTerrestreEntity entity = new EnvioTerrestreEntity();
        
        entity.setCliente(clienteEncontrado);
        entity.setTipoProducto(dto.getTipoProducto());
        entity.setCantidad(dto.getCantidad());
        entity.setFechaRegistro(LocalDateTime.now());
        entity.setFechaEntrega(dto.getFechaEntrega());
        entity.setPrecioEnvio(dto.getPrecioEnvio());
        entity.setPrecioDescuento(modelo.getPrecioDescuento()); 
        entity.setNumeroGuia(dto.getNumeroGuia());
        entity.setBodegaEntrega(dto.getBodegaEntrega());
        entity.setPlacaVehiculo(dto.getPlacaVehiculo());

        // Guardar todo en la base de datos
        EnvioTerrestreEntity entidadGuardada = envioRepository.save(entity);

        // Mapear la entidad guardada al ResponseDTO para devolverlo de forma segura
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
        response.setBodegaEntrega(entidadGuardada.getBodegaEntrega());
        response.setPlacaVehiculo(entidadGuardada.getPlacaVehiculo());

        return response;
    }
    

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarTodos() {
        // Busca todos, filtra solo los terrestres y los mapea a DTO
        return envioRepository.findAll().stream()
                .filter(envio -> envio instanceof EnvioTerrestreEntity)
                .map(envio -> mapearAResponse((EnvioTerrestreEntity) envio))
                .toList();
    }
    
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerPorId(Long id) {
        EnvioTerrestreEntity entidad = (EnvioTerrestreEntity) envioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El envío terrestre con ID " + id + " no existe."));
        return mapearAResponse(entidad);
    }
    
    @Transactional
    public EnvioResponseDTO actualizarEnvio(Long id, EnvioTerrestreRequestDTO dto) {
        
        // Buscamos el envío que queremos actualizar
        EnvioTerrestreEntity entidadExistente = (EnvioTerrestreEntity) envioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El envío terrestre con ID " + id + " no existe."));

        // Verificamos si cambiaron el numero de guia y si ya existe
        if (!entidadExistente.getNumeroGuia().equals(dto.getNumeroGuia()) && 
            envioRepository.existsByNumeroGuia(dto.getNumeroGuia())) {
            throw new IllegalArgumentException("El nuevo número de guía ya está registrado por otro envío.");
        }

        // Se verficia si cambiaron el cliente
        if (!entidadExistente.getCliente().getId().equals(dto.getClienteId())) {
            ClienteEntity nuevoCliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("El nuevo cliente no existe."));
            entidadExistente.setCliente(nuevoCliente);
        }

        // Se vuelve a calcular por si cambiaron la cantidad o el precio
        EnvioTerrestre modelo = new EnvioTerrestre();
        modelo.setCantidad(dto.getCantidad());
        modelo.setPrecioEnvio(dto.getPrecioEnvio());
        modelo.calcularPrecioConDescuento(); // Vuelve a aplicar el 5% si es necesario

        // Actualizamos los datos de la entidad
        entidadExistente.setTipoProducto(dto.getTipoProducto());
        entidadExistente.setCantidad(dto.getCantidad());
        entidadExistente.setFechaEntrega(dto.getFechaEntrega());
        entidadExistente.setPrecioEnvio(dto.getPrecioEnvio());
        entidadExistente.setPrecioDescuento(modelo.getPrecioDescuento());
        entidadExistente.setNumeroGuia(dto.getNumeroGuia());
        entidadExistente.setBodegaEntrega(dto.getBodegaEntrega());
        entidadExistente.setPlacaVehiculo(dto.getPlacaVehiculo());

        return mapearAResponse(envioRepository.save(entidadExistente));
    }

    @Transactional
    public void eliminarEnvio(Long id) {
        if (!envioRepository.existsById(id)) {
            throw new EntityNotFoundException("El envío con ID " + id + " no existe.");
        }
        envioRepository.deleteById(id);
    }

    // Método para no repetir el código de mapeo
    private EnvioResponseDTO mapearAResponse(EnvioTerrestreEntity entidadGuardada) {
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
        response.setBodegaEntrega(entidadGuardada.getBodegaEntrega());
        response.setPlacaVehiculo(entidadGuardada.getPlacaVehiculo());
        return response;
    }
}