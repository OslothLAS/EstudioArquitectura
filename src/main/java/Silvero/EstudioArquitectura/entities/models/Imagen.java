package Silvero.EstudioArquitectura.entities.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

// Imagen alojada en Cloudinary. Los datos se copian acá al sincronizar,
// así la página no consulta Cloudinary en cada visita.
@Entity
@Table(name = "imagenes")
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador único de la imagen en Cloudinary
    @Column(nullable = false, unique = true)
    private String publicId;

    // Sección de la página donde se muestra (portada, nosotros, ...)
    @Column(nullable = false)
    private String seccion;

    // Nombre del archivo en Cloudinary, sirve para asociar la imagen a un elemento (ej: una etapa del proceso)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String url;

    private String titulo;
    private String alt;
    private Integer ancho;
    private Integer alto;

    // Posición dentro de la sección (según el nombre del archivo en Cloudinary)
    private int orden;

    private Instant actualizada;

    protected Imagen() {
        // Requerido por JPA
    }

    public Imagen(String publicId, String seccion) {
        this.publicId = publicId;
        this.seccion = seccion;
    }

    // Getters y Setters
    public Long getId() { return id; }

    public String getPublicId() { return publicId; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAlt() { return alt; }
    public void setAlt(String alt) { this.alt = alt; }

    public Integer getAncho() { return ancho; }
    public void setAncho(Integer ancho) { this.ancho = ancho; }

    public Integer getAlto() { return alto; }
    public void setAlto(Integer alto) { this.alto = alto; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public Instant getActualizada() { return actualizada; }
    public void setActualizada(Instant actualizada) { this.actualizada = actualizada; }
}
