package com.sinergia.logistics.infrastructure.adapter.in.web;

import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioTerrestreRequestDTO;
import com.sinergia.logistics.infrastructure.adapter.in.dto.EnvioResponseDTO;
import com.sinergia.logistics.application.service.EnvioTerrestreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logistica/terrestre")
@RequiredArgsConstructor
public class EnvioTerrestreController {

    private final EnvioTerrestreService envioTerrestreService;

    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(@Valid @RequestBody EnvioTerrestreRequestDTO requestDTO) {
        
        // Si la placa o la guia tienen mal formato, Spring bloquea la peticion.
        
        var envioCreado = envioTerrestreService.crearEnvioTerrestre(requestDTO);
        
        return new ResponseEntity<>(envioCreado, HttpStatus.CREATED); // Devuelve codigo 201
    }
    
    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarEnvios() {
        return ResponseEntity.ok(envioTerrestreService.listarTodos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerEnvioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioTerrestreService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @PathVariable Long id, 
            @Valid @RequestBody EnvioTerrestreRequestDTO requestDTO) {
        
        EnvioResponseDTO envioActualizado = envioTerrestreService.actualizarEnvio(id, requestDTO);
        return ResponseEntity.ok(envioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long id) {
        envioTerrestreService.eliminarEnvio(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }
}