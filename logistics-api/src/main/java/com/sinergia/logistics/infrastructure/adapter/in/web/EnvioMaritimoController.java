package com.sinergia.logistics.infrastructure.adapter.in.web;

import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioMaritimoRequestDTO;
import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioResponseDTO;
import com.sinergia.logistics.application.service.EnvioMaritimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logistica/maritima")
@RequiredArgsConstructor
public class EnvioMaritimoController {

    private final EnvioMaritimoService envioMaritimoService;

    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(@Valid @RequestBody EnvioMaritimoRequestDTO requestDTO) {
    	
        // Si el numero de flota o la guia tienen mal formato, Spring bloquea la peticion.
    	
        EnvioResponseDTO envioCreado = envioMaritimoService.crearEnvioMaritimo(requestDTO);
        
        return new ResponseEntity<>(envioCreado, HttpStatus.CREATED); // Devuelve codigo 201
    }
    
    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarEnvios() {
        return ResponseEntity.ok(envioMaritimoService.listarTodos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerEnvioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioMaritimoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @PathVariable Long id, 
            @Valid @RequestBody EnvioMaritimoRequestDTO requestDTO) {
        
        EnvioResponseDTO envioActualizado = envioMaritimoService.actualizarEnvio(id, requestDTO);
        return ResponseEntity.ok(envioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long id) {
        envioMaritimoService.eliminarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}