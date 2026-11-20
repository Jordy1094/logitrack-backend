package com.transrapido.logitrack.service;

import com.transrapido.logitrack.dto.LoginRequest;
import com.transrapido.logitrack.dto.LoginResponse;
import com.transrapido.logitrack.exception.CredencialesInvalidasException;
import com.transrapido.logitrack.model.Usuario;
import com.transrapido.logitrack.repository.UsuarioRepository;
import com.transrapido.logitrack.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// POST /api/auth/login (Tabla 34): valida credenciales y devuelve el token JWT y el rol (RF-01).
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.getCorreo())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contrasena incorrectos"));

        if (!passwordEncoder.matches(datos.getContrasena(), usuario.getContrasena())) {
            throw new CredencialesInvalidasException("Correo o contrasena incorrectos");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());
        return new LoginResponse(token, usuario.getRol(), usuario.getNombre());
    }
}
