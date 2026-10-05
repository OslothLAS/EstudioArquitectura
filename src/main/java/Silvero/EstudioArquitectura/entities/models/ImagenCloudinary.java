package Silvero.EstudioArquitectura.entities.models;

// Datos de una imagen tal como los devuelve Cloudinary, ya normalizados
// "nombre" es el nombre del archivo que se ve en Cloudinary (ej: "plan-de-necesidades")
public record ImagenCloudinary(String publicId, String nombre, String url, String titulo, String alt, Integer ancho, Integer alto) {
}
