package com.transrapido.logitrack.config;

import com.transrapido.logitrack.security.JwtAccessDeniedHandler;
import com.transrapido.logitrack.security.JwtAuthenticationEntryPoint;
import com.transrapido.logitrack.security.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// Spring Security + JWT (seccion 4.2): sin sesion en el servidor, cada peticion
// protegida se autentica con el token que agrega JwtFilter (RNF-01, RNF-02).
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final String allowedOrigins;

    public SecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint authenticationEntryPoint,
                           JwtAccessDeniedHandler accessDeniedHandler,
                           @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.allowedOrigins = allowedOrigins;
    }

    // Incidencia I-01 del Capitulo 6: sin esta configuracion el navegador bloqueaba
    // las peticiones de Angular (http://localhost:4200) hacia la API (http://localhost:8080)
    // por la politica de mismo origen. Solo se aceptan los origenes declarados en
    // app.cors.allowed-origins (RNF-05), no "*".
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origen -> !origen.isEmpty())
                .toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // HU-01 (Tabla 19): el login es el unico endpoint publico.
                .requestMatchers("/api/auth/login").permitAll()

                // Capitulo 7: la documentacion Swagger es publica para poder consultarla
                // y probar la API (los endpoints siguen pidiendo el token al ejecutarlos).
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

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

                // HU-03, RF-04: registrar, editar y dar de baja conductores es tarea del Administrador.
                .requestMatchers(HttpMethod.POST, "/api/conductores").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/api/conductores/*").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/conductores/*").hasRole("ADMINISTRADOR")

                // HU-05, HU-06: asignar y finalizar una ruta es tarea del Supervisor; el
                // Administrador, al ser el rol de mayor nivel (Tabla 5: gerente), tambien
                // puede hacerlo, igual que con el resto de la operacion.
                // Capitulo 6: el Dashboard lista las rutas (filtrando EN_CURSO) para poder finalizarlas.
                .requestMatchers(HttpMethod.GET, "/api/rutas").hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
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
