package Silvero.EstudioArquitectura.entities.models;

// Lo que recibe la página por cada imagen (sin datos internos de la base)
public record ImagenDto(String url, String titulo, String alt, Integer ancho, Integer alto) {

    public static ImagenDto desde(Imagen imagen) {
        return new ImagenDto(imagen.getUrl(), imagen.getTitulo(), imagen.getAlt(), imagen.getAncho(), imagen.getAlto());
    }
}
