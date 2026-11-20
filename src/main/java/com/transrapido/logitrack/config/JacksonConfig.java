package com.transrapido.logitrack.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Permite que Jackson serialice entidades con relaciones LAZY (por ejemplo
// Ruta.camion, Ruta.conductor, Mantenimiento.camion) sin romper por el proxy
// de Hibernate, y ademas las carga completas si todavia no estaban inicializadas
// (FORCE_LAZY_LOADING), porque el informe documenta la respuesta con esos datos
// anidados (Tabla 35).
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module modulo = new Hibernate6Module();
        // Si la relacion LAZY no fue cargada todavia, que la traiga (como antes,
        // cuando no habia proxies de por medio) en vez de devolver null: el informe
        // documenta que Ruta y Mantenimiento muestran el camion/conductor completo.
        modulo.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return modulo;
    }
}
