package com.sinergia.logistics.application.service;

import com.sinergia.logistics.infrastructure.adapter.in.dto.ClienteDTO;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import com.sinergia.logistics.infrastructure.adapter.out.persistence.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteDTO crearCliente(ClienteDTO dto) {
        ClienteEntity entity = new ClienteEntity();
        entity.setNombre(dto.getNombre());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        
        ClienteEntity guardado = clienteRepository.save(entity);
        
        dto.setId(guardado.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll().stream().map(entity -> {
            ClienteDTO dto = new ClienteDTO();
            dto.setId(entity.getId());
            dto.setNombre(entity.getNombre());
            dto.setEmail(entity.getEmail());
            dto.setTelefono(entity.getTelefono());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        ClienteEntity entidad = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));
        
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setEmail(entidad.getEmail());
        dto.setTelefono(entidad.getTelefono());
        return dto;
    }

    @Transactional
    public ClienteDTO actualizarCliente(Long id, ClienteDTO dto) {
        ClienteEntity entidadExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));

        entidadExistente.setNombre(dto.getNombre());
        entidadExistente.setEmail(dto.getEmail());
        entidadExistente.setTelefono(dto.getTelefono());

        ClienteEntity guardado = clienteRepository.save(entidadExistente);
        
        dto.setId(guardado.getId());
        return dto;
    }
    
    @Transactional
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("El cliente con ID " + id + " no existe.");
        }
        
        // Si este cliente ya tiene envíos creados (Terrestres o Marítimos), Hibernate lanzará una excepción de "DataIntegrityViolationException" por la llave foránea.
        clienteRepository.deleteById(id);
    }
}