package ma.tickethub.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Autorise le front Angular (autre origine : autre port) à appeler l'API.
 * Sans ça, le navigateur bloque les requêtes (Same-Origin Policy).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // Lu dans application.properties : en production, on change l'URL sans toucher au code
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins) // jamais "*" : on liste les origines autorisées
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location") // Angular peut lire l'URL du nouvel événement (201)
                .maxAge(3600); // le navigateur garde la réponse du preflight 1 h
    }
}
