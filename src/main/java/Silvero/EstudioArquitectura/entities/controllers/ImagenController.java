package Silvero.EstudioArquitectura.entities.controllers;

import Silvero.EstudioArquitectura.config.ImagenesProperties;
import Silvero.EstudioArquitectura.entities.models.ImagenDto;
import Silvero.EstudioArquitectura.entities.services.CloudinarySyncService;
import Silvero.EstudioArquitectura.entities.services.ImagenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;
    private final CloudinarySyncService cloudinarySyncService;
    private final ImagenesProperties propiedades;

    public ImagenController(ImagenService imagenService,
                            CloudinarySyncService cloudinarySyncService,
                            ImagenesProperties propiedades) {
        this.imagenService = imagenService;
        this.cloudinarySyncService = cloudinarySyncService;
        this.propiedades = propiedades;
    }

    // Endpoint: GET http://localhost:8080/api/imagenes
    // Devuelve { "portada": [...], "nosotros": [...] }
    @GetMapping
    public Map<String, List<ImagenDto>> obtenerTodas() {
        return imagenService.obtenerTodas();
    }

    // Endpoint: GET http://localhost:8080/api/imagenes/portada
    @GetMapping("/{seccion}")
    public List<ImagenDto> obtenerPorSeccion(@PathVariable String seccion) {
        return imagenService.obtenerPorSeccion(seccion);
    }

    // Endpoint: POST http://localhost:8080/api/imagenes/sincronizar  (header X-Sync-Token)
    // Fuerza la sincronización sin esperar al próximo ciclo automático.
    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizar(@RequestHeader(value = "X-Sync-Token", required = false) String token) {
        String esperado = propiedades.tokenSincronizacion();
        if (esperado == null || esperado.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        if (token == null || !MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8), esperado.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!cloudinarySyncService.estaConfigurado()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("CLOUDINARY_URL no está definida");
        }
        return ResponseEntity.ok(cloudinarySyncService.sincronizar());
    }
}
