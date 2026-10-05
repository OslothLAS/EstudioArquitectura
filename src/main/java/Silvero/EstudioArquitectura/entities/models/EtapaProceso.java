package Silvero.EstudioArquitectura.entities.models;

public class EtapaProceso {
    private int numero;
    private String nombre;
    private String subtitulo;
    private String descripcion;
    private String beneficio;
    // Imagen o GIF de la carpeta estudio/proceso de Cloudinary con el mismo nombre que la etapa (puede no haber)
    private ImagenDto imagen;

    public EtapaProceso(int numero, String nombre, String subtitulo, String descripcion, String beneficio) {
        this.numero = numero;
        this.nombre = nombre;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.beneficio = beneficio;
    }

    // Getters y Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getBeneficio() { return beneficio; }
    public void setBeneficio(String beneficio) { this.beneficio = beneficio; }

    public ImagenDto getImagen() { return imagen; }
    public void setImagen(ImagenDto imagen) { this.imagen = imagen; }
}
