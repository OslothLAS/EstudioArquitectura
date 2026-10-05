package Silvero.EstudioArquitectura.entities.services;


import Silvero.EstudioArquitectura.entities.models.EtapaProceso;
import Silvero.EstudioArquitectura.entities.models.ImagenDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ArquitecturaService {

    // Carpeta de Cloudinary (estudio/proceso) con una imagen o GIF por etapa
    private static final String SECCION_IMAGENES_PROCESO = "proceso";

    private final ImagenService imagenService;

    // En el futuro aquí inyectarás el repositorio:
    // private EtapaRepository etapaRepository;

    public ArquitecturaService(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    // Cada etapa lleva la imagen de Cloudinary cuyo archivo se llama igual que ella
    // (ej: "plan-de-necesidades.gif" -> "Plan de necesidades")
    public List<EtapaProceso> obtenerProcesoDeTrabajo() {
        Map<String, ImagenDto> imagenes = imagenService.obtenerPorNombre(SECCION_IMAGENES_PROCESO);
        List<EtapaProceso> etapas = obtenerEtapas();
        for (EtapaProceso etapa : etapas) {
            etapa.setImagen(imagenes.get(ImagenService.normalizarNombre(etapa.getNombre())));
        }
        return etapas;
    }

    private List<EtapaProceso> obtenerEtapas() {
        // Simulamos lo que en el futuro traerá la base de datos
        return Arrays.asList(
                new EtapaProceso(1, "Plan de necesidades", "Conocemos tu forma de vivir.",
                        "Conversamos sobre tus necesidades, gustos, hábitos y expectativas, analizamos el terreno o espacio existente y nos adaptamos a tu presupuesto.",
                        "Nos permite comprender qué necesitás realmente y establecer una base clara antes de comenzar a diseñar."),
                new EtapaProceso(2, "Croquis", "Le damos forma a las primeras ideas.",
                        "Exploramos distintas posibilidades de distribución y relación entre los espacios.",
                        "Podés visualizar las primeras propuestas, comparar alternativas y participar desde el comienzo en las decisiones del proyecto."),
                new EtapaProceso(3, "Anteproyecto", "Definimos cómo será tu espacio.",
                        "Desarrollamos la propuesta elegida, trabajando la distribución, las dimensiones, la estética y los materiales.",
                        "Te permite comprender cómo se verá y cómo funcionará el espacio antes de avanzar a la documentación definitiva."),
                new EtapaProceso(4, "Proyecto", "Convertimos la idea en una propuesta definida.",
                        "Ajustamos y definimos los ambientes, sus medidas, materiales y características principales.",
                        "Llegás a una propuesta final clara, revisada y adaptada a tus necesidades, evitando decisiones improvisadas durante la obra."),
                new EtapaProceso(5, "Proyecto ejecutivo", "Preparamos toda la información para construir.",
                        "Elaboramos los planos y detalles necesarios para ejecutar correctamente el proyecto.",
                        "Reduce errores, dudas, cambios inesperados y gastos innecesarios durante la construcción. Además, permite pedir presupuestos más claros y comparables."),
                new EtapaProceso(6, "Dirección de obra", "Acompañamos la materialización del proyecto.",
                        "Supervisamos el avance de la obra, verificamos que se respete lo proyectado y resolvemos consultas durante la ejecución.",
                        "Tenés acompañamiento profesional durante la construcción y mayor tranquilidad de que el resultado final se corresponda con lo que planificamos juntos."),
                new EtapaProceso(7, "Entrega final", "Revisamos el resultado y cerramos el proceso.",
                        "Realizamos una revisión final, hacemos los ajustes necesarios y te acompañamos también después de la obra.",
                        "Contás con una instancia de revisión y ajustes, además de acompañamiento post obra.")
        );
    }

    public String obtenerDescripcionEstudio() {
        return "Nuestra misión es crear un hogar que se sienta verdaderamente tuyo y donde encuentres paz cada vez que llegás.";
    }
}