package com.sinergia.logistics.infrastructure.adapter.in.web;

import com.sinergia.logistics.infrastructure.adapter.in.dto.AuthRequestDTO;
import com.sinergia.logistics.infrastructure.adapter.in.dto.AuthResponseDTO;
import com.sinergia.logistics.infrastructure.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        
        // Simulación básica de validación (Para no hacer la prueba técnica infinita)
        // En un caso real, aquí buscaríamos el usuario en la Base de Datos.
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            
            // Si la contraseña es correcta, generamos el token
            String token = jwtService.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponseDTO(token));
            
        } else {
            // Si falla, devolvemos un 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}