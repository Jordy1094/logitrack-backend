package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.dto.LoginRequest;
import com.transrapido.logitrack.dto.LoginResponse;
import com.transrapido.logitrack.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

// Tabla 34: POST /api/auth/login.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest datos) {
        return authService.login(datos);
    }
}
