package Silvero.EstudioArquitectura.entities.services;

import Silvero.EstudioArquitectura.config.ImagenesProperties;
import Silvero.EstudioArquitectura.entities.models.Imagen;
import Silvero.EstudioArquitectura.entities.models.ImagenCloudinary;
import Silvero.EstudioArquitectura.entities.models.ImagenDto;
import Silvero.EstudioArquitectura.entities.repositories.ImagenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImagenService {

    private final ImagenRepository imagenRepository;
    private final ImagenesProperties propiedades;

    public ImagenService(ImagenRepository imagenRepository, ImagenesProperties propiedades) {
        this.imagenRepository = imagenRepository;
        this.propiedades = propiedades;
    }

    @Transactional(readOnly = true)
    public List<ImagenDto> obtenerPorSeccion(String seccion) {
        return imagenRepository.findBySeccionOrderByOrdenAsc(seccion).stream()
                .map(ImagenDto::desde)
                .toList();
    }

    // Todas las secciones configuradas, en el orden de application.properties
    @Transactional(readOnly = true)
    public Map<String, List<ImagenDto>> obtenerTodas() {
        Map<String, List<ImagenDto>> resultado = new LinkedHashMap<>();
        for (String seccion : propiedades.secciones()) {
            resultado.put(seccion, obtenerPorSeccion(seccion));
        }
        return resultado;
    }

    // Imágenes de una sección indexadas por su nombre normalizado (ver normalizarNombre).
    // Si dos archivos tienen el mismo nombre, queda el primero según el orden.
    @Transactional(readOnly = true)
    public Map<String, ImagenDto> obtenerPorNombre(String seccion) {
        Map<String, ImagenDto> resultado = new LinkedHashMap<>();
        for (Imagen imagen : imagenRepository.findBySeccionOrderByOrdenAsc(seccion)) {
            if (imagen.getNombre() != null) {
                resultado.putIfAbsent(normalizarNombre(imagen.getNombre()), ImagenDto.desde(imagen));
            }
        }
        return resultado;
    }

    // "01 - Plan de Necesidades", "plan_de_necesidades" y "plan-de-necesidades" -> "plan-de-necesidades"
    // (sin tildes, en minúsculas, con guiones y sin el número de orden del principio)
    public static String normalizarNombre(String nombre) {
        String sinTildes = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "")
                .replaceFirst("^\\d+-(?=[a-z])", "");
    }

    // Deja la sección igual a lo que hay en Cloudinary:
    // agrega las nuevas, actualiza las existentes y borra las que ya no están.
    @Transactional
    public void actualizarSeccion(String seccion, List<ImagenCloudinary> imagenesEnLaNube) {
        Map<String, Imagen> existentes = imagenRepository.findBySeccion(seccion).stream()
                .collect(Collectors.toMap(Imagen::getPublicId, Function.identity()));

        Instant ahora = Instant.now();
        for (int i = 0; i < imagenesEnLaNube.size(); i++) {
            ImagenCloudinary nube = imagenesEnLaNube.get(i);
            Imagen imagen = existentes.get(nube.publicId());
            if (imagen == null) {
                // Puede existir en otra sección si la moviste de carpeta en Cloudinary
                imagen = imagenRepository.findByPublicId(nube.publicId())
                        .orElseGet(() -> new Imagen(nube.publicId(), seccion));
                imagen.setSeccion(seccion);
            }
            imagen.setNombre(nube.nombre());
            imagen.setUrl(nube.url());
            imagen.setTitulo(nube.titulo());
            imagen.setAlt(nube.alt());
            imagen.setAncho(nube.ancho());
            imagen.setAlto(nube.alto());
            imagen.setOrden(i);
            imagen.setActualizada(ahora);
            imagenRepository.save(imagen);
        }

        Set<String> idsEnLaNube = imagenesEnLaNube.stream()
                .map(ImagenCloudinary::publicId)
                .collect(Collectors.toSet());
        List<Imagen> eliminadas = existentes.values().stream()
                .filter(imagen -> !idsEnLaNube.contains(imagen.getPublicId()))
                .toList();
        imagenRepository.deleteAll(eliminadas);
    }
}
