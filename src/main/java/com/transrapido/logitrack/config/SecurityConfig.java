package com.transrapido.logitrack.config;

import com.transrapido.logitrack.security.JwtAccessDeniedHandler;
import com.transrapido.logitrack.security.JwtAuthenticationEntryPoint;
import com.transrapido.logitrack.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Spring Security + JWT (seccion 4.2): sin sesion en el servidor, cada peticion
// protegida se autentica con el token que agrega JwtFilter (RNF-01, RNF-02).
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint authenticationEntryPoint,
                           JwtAccessDeniedHandler accessDeniedHandler) {
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // HU-01 (Tabla 19): el login es el unico endpoint publico.
                .requestMatchers("/api/auth/login").permitAll()

                // HU-11, HU-12: el conductor solo ve su propia ruta y reporta su propio camion.
                .requestMatchers(HttpMethod.GET, "/api/rutas/mia").hasRole("CONDUCTOR")
                .requestMatchers(HttpMethod.POST, "/api/camiones/*/reportes").hasRole("CONDUCTOR")

                // HU-10: el resumen del Dashboard es tarea del Administrador (el supervisor
                // no aparece en esa historia; el es quien opera, no quien evalua el resumen).
                .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasRole("ADMINISTRADOR")

                // HU-02: registrar, editar y dar de baja camiones es tarea del Administrador.
                .requestMatchers(HttpMethod.POST, "/api/camiones").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/camiones/*").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/camiones/*").hasRole("ADMINISTRADOR")

                // HU-03: registrar conductores es tarea del Administrador.
                .requestMatchers(HttpMethod.POST, "/api/conductores").hasRole("ADMINISTRADOR")

                // HU-05, HU-06: asignar y finalizar una ruta es tarea del Supervisor; el
                // Administrador, al ser el rol de mayor nivel (Tabla 5: gerente), tambien
                // puede hacerlo, igual que con el resto de la operacion.
                .requestMatchers(HttpMethod.POST, "/api/rutas").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/rutas/*/finalizar").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")

                // HU-07, HU-08, HU-09: programar, registrar ingreso/cierre y consultar el
                // historial de mantenimientos es tarea del Supervisor; el Administrador
                // tambien puede, por la misma razon.
                .requestMatchers(HttpMethod.POST, "/api/camiones/*/mantenimientos").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.GET, "/api/camiones/*/mantenimientos").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/mantenimientos/*/ingreso").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/mantenimientos/*/cierre").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")

                // El resto son consultas (listar camiones, disponibles, conductores) que no
                // tienen una HU exclusiva de un solo rol: tanto Administrador (gestiona la
                // flota, HU-02/03) como Supervisor (opera con esos datos, HU-04/05) las
                // necesitan, asi que quedan abiertas a ambos.
                .requestMatchers("/api/camiones/**", "/api/conductores/**", "/api/rutas/**", "/api/mantenimientos/**")
                    .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
