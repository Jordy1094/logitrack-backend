package com.transrapido.logitrack.config;

import com.transrapido.logitrack.model.Conductor;
import com.transrapido.logitrack.model.Usuario;
import com.transrapido.logitrack.model.enums.RolUsuario;
import com.transrapido.logitrack.repository.ConductorRepository;
import com.transrapido.logitrack.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// La Tabla 34 solo documenta el registro de conductores (dni, nombres, apellidos,
// nroLicencia, telefono), sin correo ni contrasena: la API, tal como esta
// documentada en el informe, no expone un endpoint para crear la primera cuenta
// de usuario. Sin esa cuenta nadie podria probar POST /api/auth/login (Tabla 37).
// Este seeder crea tres cuentas de prueba una sola vez, al arrancar, solo si la
// tabla usuario esta vacia, para que existan credenciales con las que probar el
// login y los endpoints protegidos en Postman. No agrega ni reemplaza ningun
// endpoint de la Tabla 34 a 36.
//
// Mejora (Tabla 48, "Cuentas de prueba"): se puede desactivar con la variable de
// entorno SEED_DEMO_DATA=false (por ejemplo, despues de la sustentacion). Por
// defecto queda en true, asi el comportamiento actual en Render no cambia.
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ConductorRepository conductorRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${SEED_DEMO_DATA:true}")
    private boolean seedDemoData;

    public DataSeeder(UsuarioRepository usuarioRepository, ConductorRepository conductorRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.conductorRepository = conductorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedDemoData || usuarioRepository.count() > 0) {
            return;
        }

        usuarioRepository.save(new Usuario("Administrador", "admin@logitrack.com",
                passwordEncoder.encode("Admin123!"), RolUsuario.ADMINISTRADOR));

        usuarioRepository.save(new Usuario("Supervisor Demo", "supervisor@logitrack.com",
                passwordEncoder.encode("Supervisor123!"), RolUsuario.SUPERVISOR));

        Conductor conductor = new Conductor("10000001", "Luis", "Ramos", "Q12345678", "987654321");
        Usuario usuarioConductor = usuarioRepository.save(new Usuario("Luis Ramos", "conductor@logitrack.com",
                passwordEncoder.encode("Conductor123!"), RolUsuario.CONDUCTOR));
        conductor.setUsuario(usuarioConductor);
        conductorRepository.save(conductor);
    }
}
