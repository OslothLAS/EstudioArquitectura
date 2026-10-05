package Silvero.EstudioArquitectura;

import Silvero.EstudioArquitectura.entities.models.EtapaProceso;
import Silvero.EstudioArquitectura.entities.models.ImagenCloudinary;
import Silvero.EstudioArquitectura.entities.models.ImagenDto;
import Silvero.EstudioArquitectura.entities.repositories.ImagenRepository;
import Silvero.EstudioArquitectura.entities.services.ArquitecturaService;
import Silvero.EstudioArquitectura.entities.services.ImagenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ImagenServiceTests {

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private ArquitecturaService arquitecturaService;

    @BeforeEach
    void limpiar() {
        imagenRepository.deleteAll();
    }

    private static ImagenCloudinary nube(String publicId, String titulo) {
        String nombre = publicId.substring(publicId.lastIndexOf('/') + 1);
        return new ImagenCloudinary(publicId, nombre, "https://res.cloudinary.com/demo/" + publicId, titulo, titulo, 800, 600);
    }

    @Test
    void normalizaNombresParaAsociarlosConLasEtapas() {
        assertThat(ImagenService.normalizarNombre("Plan de necesidades")).isEqualTo("plan-de-necesidades");
        assertThat(ImagenService.normalizarNombre("01 - Plan_de_Necesidades")).isEqualTo("plan-de-necesidades");
        assertThat(ImagenService.normalizarNombre("Dirección de obra")).isEqualTo("direccion-de-obra");
        assertThat(ImagenService.normalizarNombre("proyecto-ejecutivo")).isNotEqualTo(ImagenService.normalizarNombre("Proyecto"));
    }

    @Test
    void asignaACadaEtapaLaImagenConSuNombre() {
        imagenService.actualizarSeccion("proceso", List.of(nube("estudio/proceso/02-croquis", "Croquis")));

        List<EtapaProceso> etapas = arquitecturaService.obtenerProcesoDeTrabajo();
        assertThat(etapas).filteredOn(etapa -> etapa.getNombre().equals("Croquis"))
                .singleElement().extracting(etapa -> etapa.getImagen().titulo()).isEqualTo("Croquis");
        assertThat(etapas).filteredOn(etapa -> etapa.getImagen() != null).hasSize(1);
    }

    @Test
    void agregaActualizaYBorraSegunLoQueHayEnLaNube() {
        imagenService.actualizarSeccion("nosotros", List.of(nube("a", "A"), nube("b", "B")));
        assertThat(imagenService.obtenerPorSeccion("nosotros")).extracting(ImagenDto::titulo).containsExactly("A", "B");

        // "a" se borró en Cloudinary, "b" cambió de título y apareció "c" antes que "b"
        imagenService.actualizarSeccion("nosotros", List.of(nube("c", "C"), nube("b", "B nuevo")));
        assertThat(imagenService.obtenerPorSeccion("nosotros")).extracting(ImagenDto::titulo).containsExactly("C", "B nuevo");
        assertThat(imagenRepository.count()).isEqualTo(2);
    }

    @Test
    void mueveUnaImagenDeSeccionSinDuplicarla() {
        imagenService.actualizarSeccion("nosotros", List.of(nube("x", "X")));
        imagenService.actualizarSeccion("portada", List.of(nube("x", "X")));
        imagenService.actualizarSeccion("nosotros", List.of());

        assertThat(imagenService.obtenerPorSeccion("portada")).hasSize(1);
        assertThat(imagenService.obtenerPorSeccion("nosotros")).isEmpty();
        assertThat(imagenRepository.count()).isEqualTo(1);
    }

    @Test
    void devuelveTodasLasSeccionesConfiguradas() {
        assertThat(imagenService.obtenerTodas()).containsOnlyKeys("portada", "nosotros", "proceso");
    }
}
