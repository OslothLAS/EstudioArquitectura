package Silvero.EstudioArquitectura.entities.services;

import Silvero.EstudioArquitectura.config.ImagenesProperties;
import Silvero.EstudioArquitectura.entities.models.ImagenCloudinary;
import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.api.exceptions.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Lee las carpetas de Cloudinary (una por sección) y copia sus imágenes a la base de datos.
// Estructura esperada en Cloudinary:  estudio/portada,  estudio/nosotros,  estudio/proceso, ...
@Service
public class CloudinarySyncService {

    private static final Logger log = LoggerFactory.getLogger(CloudinarySyncService.class);

    // Cloudinary entrega cada imagen en el mejor formato (WebP/AVIF) y calidad para el navegador,
    // con un ancho máximo de 2000px para no mandar originales enormes.
    private static final String TRANSFORMACION = "f_auto,q_auto,c_limit,w_2000";

    private final Cloudinary cloudinary;
    private final ImagenService imagenService;
    private final ImagenesProperties propiedades;

    public CloudinarySyncService(ObjectProvider<Cloudinary> cloudinary,
                                 ImagenService imagenService,
                                 ImagenesProperties propiedades) {
        this.cloudinary = cloudinary.getIfAvailable();
        this.imagenService = imagenService;
        this.propiedades = propiedades;
    }

    public boolean estaConfigurado() {
        return cloudinary != null;
    }

    // Se ejecuta al arrancar (a los 5 segundos) y después cada "app.imagenes.sincronizacion-ms"
    @Scheduled(initialDelay = 5000, fixedDelayString = "${app.imagenes.sincronizacion-ms}")
    public void sincronizacionProgramada() {
        if (!estaConfigurado()) {
            log.warn("CLOUDINARY_URL no está definida: se omite la sincronización de imágenes");
            return;
        }
        sincronizar();
    }

    // Devuelve cuántas imágenes quedaron en cada sección (-1 si esa sección falló)
    public synchronized Map<String, Integer> sincronizar() {
        Map<String, Integer> resumen = new LinkedHashMap<>();
        for (String seccion : propiedades.secciones()) {
            String carpeta = propiedades.carpetaRaiz() + "/" + seccion;
            try {
                List<ImagenCloudinary> imagenes = listarCarpeta(carpeta);
                imagenService.actualizarSeccion(seccion, imagenes);
                resumen.put(seccion, imagenes.size());
            } catch (Exception e) {
                // Si falla una sección no se toca lo que ya había en la base
                log.error("No se pudo sincronizar la carpeta '{}' de Cloudinary", carpeta, e);
                resumen.put(seccion, -1);
            }
        }
        log.info("Imágenes sincronizadas con Cloudinary: {}", resumen);
        return resumen;
    }

    @SuppressWarnings("unchecked")
    private List<ImagenCloudinary> listarCarpeta(String carpeta) throws Exception {
        List<Map<String, Object>> recursos = new ArrayList<>();
        String cursor = null;
        do {
            Map<String, Object> opciones = new HashMap<>();
            opciones.put("max_results", 500);
            opciones.put("context", true);
            if (cursor != null) {
                opciones.put("next_cursor", cursor);
            }
            ApiResponse respuesta;
            try {
                respuesta = cloudinary.api().resourcesByAssetFolder(carpeta, opciones);
            } catch (NotFound e) {
                // La carpeta todavía no se creó en Cloudinary: la sección queda sin imágenes
                log.warn("La carpeta '{}' no existe en Cloudinary", carpeta);
                return List.of();
            }
            recursos.addAll((List<Map<String, Object>>) respuesta.get("resources"));
            cursor = (String) respuesta.get("next_cursor");
        } while (cursor != null);

        // El orden en la página lo da el nombre del archivo: 01-fachada, 02-living, ...
        return recursos.stream()
                .filter(recurso -> "image".equals(recurso.get("resource_type")))
                .sorted(Comparator.comparing(CloudinarySyncService::nombreVisible, String.CASE_INSENSITIVE_ORDER))
                .map(this::convertir)
                .toList();
    }

    private ImagenCloudinary convertir(Map<String, Object> recurso) {
        String secureUrl = (String) recurso.get("secure_url");
        String url = secureUrl.replaceFirst("/upload/", "/upload/" + TRANSFORMACION + "/");

        // "Title" y "Description" que completás en la ficha de la imagen en Cloudinary
        Map<String, Object> contexto = contextoPersonalizado(recurso);
        String titulo = (String) contexto.get("caption");
        String alt = (String) contexto.getOrDefault("alt", titulo);

        return new ImagenCloudinary(
                (String) recurso.get("public_id"),
                nombreVisible(recurso),
                url,
                titulo,
                alt,
                aEntero(recurso.get("width")),
                aEntero(recurso.get("height"))
        );
    }

    private static String nombreVisible(Map<String, Object> recurso) {
        Object nombre = recurso.get("display_name");
        if (nombre != null) {
            return nombre.toString();
        }
        // Sin "display_name", se usa la última parte del public_id (estudio/proceso/croquis -> croquis)
        String publicId = recurso.get("public_id").toString();
        return publicId.substring(publicId.lastIndexOf('/') + 1);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> contextoPersonalizado(Map<String, Object> recurso) {
        if (recurso.get("context") instanceof Map<?, ?> contexto) {
            if (contexto.get("custom") instanceof Map<?, ?> custom) {
                return (Map<String, Object>) custom;
            }
            return (Map<String, Object>) contexto;
        }
        return Map.of();
    }

    private static Integer aEntero(Object valor) {
        return valor instanceof Number numero ? numero.intValue() : null;
    }
}
