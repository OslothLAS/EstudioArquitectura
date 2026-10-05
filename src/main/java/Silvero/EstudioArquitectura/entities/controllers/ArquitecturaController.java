package Silvero.EstudioArquitectura.entities.controllers;

import Silvero.EstudioArquitectura.entities.models.EtapaProceso;
import Silvero.EstudioArquitectura.entities.services.ArquitecturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/arquitectura")
@CrossOrigin(origins = "*") // Permite que tu HTML consulte esta API si corren en puertos distintos
public class ArquitecturaController {

    private final ArquitecturaService arquitecturaService;

    @Autowired
    public ArquitecturaController(ArquitecturaService arquitecturaService) {
        this.arquitecturaService = arquitecturaService;
    }

    // Endpoint: GET http://localhost:8080/api/arquitectura/proceso
    @GetMapping("/proceso")
    public List<EtapaProceso> obtenerProceso() {
        return arquitecturaService.obtenerProcesoDeTrabajo();
    }

    // Endpoint: GET http://localhost:8080/api/arquitectura/mision
    @GetMapping("/mision")
    public String obtenerMision() {
        return arquitecturaService.obtenerDescripcionEstudio();
    }

    // Aquí puedes agregar un endpoint para servir las imágenes más adelante
    // @GetMapping("/galeria") ...
}