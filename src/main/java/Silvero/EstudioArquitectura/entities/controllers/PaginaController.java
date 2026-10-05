package Silvero.EstudioArquitectura.entities.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    // Cuando ingreses a localhost:8080/inicio, te mostrará la página
    @GetMapping("/inicio")
    public String mostrarPaginaInicio() {
        // Redirige al archivo exacto que pusiste en la carpeta 'static'
        return "redirect:/estudio_de_arquitectura.html";
    }

    // Opcional: Si querés que también cargue al entrar a localhost:8080/ (sin nada más)
    @GetMapping("/")
    public String raiz() {
        return "redirect:/estudio_de_arquitectura.html";
    }
}