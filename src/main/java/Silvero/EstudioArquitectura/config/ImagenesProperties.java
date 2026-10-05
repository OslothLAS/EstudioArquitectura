package Silvero.EstudioArquitectura.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

// Valores "app.imagenes.*" de application.properties
@ConfigurationProperties(prefix = "app.imagenes")
public record ImagenesProperties(
        String carpetaRaiz,
        List<String> secciones,
        long sincronizacionMs,
        String tokenSincronizacion
) {
}
